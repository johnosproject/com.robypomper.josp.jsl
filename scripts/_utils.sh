#!/usr/bin/env bash

execute_startup_init() {
  [ -z "$1" ] && echo "Missing SCRIPT_DIR param" && return 1
  [ -z "$2" ] && echo "Missing JOSP_DIR param" && return 1
  [ -z "$3" ] && echo "Missing JCP_HOST param" && return 1
  [ -z "$4" ] && echo "Missing JCP_AUTH_PORT param" && return 1
  [ -z "$5" ] && echo "Missing JCP_APIS_PORT param" && return 1
  [ -z "$6" ] && echo "Missing JCP_FE_PORT param" && return 1

  SCRIPT_DIR="$1"
  SCRIPT_DIR_NAME=$(basename "$SCRIPT_DIR")
  FORCE_CLEAN_ALL="${FORCE_CLEAN_ALL:-true}"
  JOSP_DIR="$2"
  JOSP_ENV_DIR="$JOSP_DIR/envs"
  JCP_HOST="$3"
  JCP_AUTH_PORT=$4
  JCP_APIS_PORT=$5
  JCP_FE_PORT=$6
  TEST_ID=$(date +'%Y%m%d-%H%M')
  TEST_LOG_DIR="$JOSP_ENV_DIR/scripts/$SCRIPT_DIR_NAME/$TEST_ID"
  return 0
}

execute_startup_pre_clean() {
  [ -z "$1" ] && echo "Missing FORCE_CLEAN_ALL param" && return 1

  FORCE_CLEAN_ALL=$1
  LOG_FILE=${2:-/dev/null}
  if $FORCE_CLEAN_ALL;
  then
    # echo "FORCE_CLEAN_ALL: enabled"

    # java JCP services
    JAVA_EXECS=$(jps -lm | grep "com.robypomper.josp.jcp" | awk -F ' ' '{print $2}')
    for e in $JAVA_EXECS;
    do
      EXEC_PID=$(jps -lm | grep "$e" | awk -F ' ' '{print $1}')
      echo "Kill java exec '$e' (PID:'$EXEC_PID')"
      ! kill -9 "$EXEC_PID" >> "$LOG_FILE" 2>&1 && echo "Error on 'kill \"$EXEC_PID\"' command to kill '$t' java exec, skipp." && return 1
    done;
    sleep 1

    # gradle tasks
    TASKS=$(jps -lm | grep "GradleWrapperMain" | grep -E "josp|jcp" | awk -F ' ' '{print $2}')
    for t in $TASKS;
    do
      TASK_PID=$(jps -m | grep "$t" | awk -F ' ' '{print $1}')
      echo "Kill gradle task '$t' (PID:'$TASK_PID')"
      ! kill -9 "$TASK_PID" >> "$LOG_FILE" 2>&1 && echo "Error on 'kill \"$TASK_PID\"' command to kill '$t' gradle task, skipp." && return 1
    done;

    # docker containers
    CONTAINERS=$(docker ps --all --format '{{.Names}}' | grep -E "jcp|josp")
    for c in $CONTAINERS;
    do
      echo "Remove docker container '$c'"
      ! docker container stop "$c" >> "$LOG_FILE" 2>&1 && echo "Error on 'docker stop \"$c\"' command, skipp." && return 1
      ! docker container rm "$c" >> "$LOG_FILE" 2>&1 && echo "Error on 'docker rm \"$c\"' command, skipp." && return 1
    done;

    # envs directories
    if [ -d "$JOSP_ENV_DIR/dockers" ]; then ! sudo rm -r "$JOSP_ENV_DIR/dockers" && echo "Error on 'rm -r \"$JOSP_ENV_DIR/dockers\"' command, exit." && return 1; fi
    if [ -d "$JOSP_ENV_DIR/runnables" ]; then ! sudo rm -r "$JOSP_ENV_DIR/runnables" && echo "Error on 'rm -r \"$JOSP_ENV_DIR/runnables\"' command, exit." && return 1; fi

  else
    # echo "FORCE_CLEAN_ALL: disabled"

    # java JCP services
    jps -lm | grep "com.robypomper.josp.jcp" >> "$LOG_FILE" 2>&1 && echo "Some java exec from JOSP/JCP eco system still running, please stop it manually." && return 1

    # gradle tasks
    jps -m | grep -E "GradleWrapperMain" | grep -E "josp|jcp" >> "$LOG_FILE" 2>&1 && echo "Some gradle task from JOSP/JCP eco system still running, please stop it manually." && return 1

    # docker containers
    docker ps --format '{{.Names}}' | grep -E "jcp|josp" >> "$LOG_FILE" 2>&1 && echo "Some docker container from JOSP/JCP eco system still running, please stop it manually." && return 1

    # envs directories
    [ -d "$JOSP_ENV_DIR/dockers" ] && echo "Temporary files still present on JOSP project's dir. Please delete the '$JOSP_ENV_DIR/dockers' dir manually (sudo required)." && return 1
    [ -d "$JOSP_ENV_DIR/runnables" ] && echo "Temporary files still present on JOSP project's dir. Please delete the '$JOSP_ENV_DIR/runnables' dir manually (sudo required)." && return 1
  fi
  return 0
}

execute_startup_java_exec_via_script_daemon() {
  [ -z "$1" ] && echo "Missing SCRIPT param" && return 1
  [ -z "$2" ] && echo "Missing PROCES_FILTER param" && return 1

  SCRIPT=$1
  PROCES_FILTER=$2
  LOG_FILE=${3:-/dev/null}
  LOG_DIR="$(dirname "${LOG_FILE}")"
  LOG_NAME="$(basename "${LOG_FILE}")"
  mkdir -p "$LOG_DIR"
  bash "$SCRIPT" >> "$LOG_FILE" 2>&1 &
  # ToDo obtain PID for execute_startup_java_exec_via_script_daemon process
  #sleep 1
  #P_ID=$(jps -lm | grep "$PROCES_FILTER" | awk -F ' ' '{print $1}')
  #[ -z "$P_ID" ] && echo "Error on java exec '$PROCES_FILTER', exit" && return 1
  #PID_FILE="$LOG_DIR/${LOG_NAME%.*}.pid"
  #echo "$P_ID" > "$PID_FILE"
  return 0
}

execute_startup_gradle_task() {
  [ -z "$1" ] && echo "Missing TASK_NAME param" && return 1

  TASK_NAME=$1
  LOG_FILE=${2:-/dev/null}
  ! ./gradlew "$TASK_NAME" >> "$LOG_FILE" 2>&1 && echo "Error on gradle task '$TASK_NAME', exit" && return 1
  return 0
}

execute_startup_gradle_task_daemon() {
  [ -z "$1" ] && echo "Missing TASK_NAME param" && return 1

  TASK_NAME=$1
  LOG_FILE=${2:-execute_startup_gradle_task_daemon-$TASK_NAME.log}
  LOG_DIR="$(dirname "${LOG_FILE}")"
  LOG_NAME="$(basename "${LOG_FILE}")"
  mkdir -p "$LOG_DIR"
  ./gradlew "$TASK_NAME" >> "$LOG_FILE" 2>&1 &
  PID_FILE="$LOG_DIR/${LOG_NAME%.*}.pid"
  P_ID=""
  COUNTER=0
  while [  $COUNTER -lt 30 ]; do
    ((COUNTER=COUNTER+1))
    P_ID=$(jps -lm | grep "GradleWrapperMain $TASK_NAME" | awk -F ' ' '{print $1}')
    grep "BUILD FAILED" "$LOG_FILE" && echo "Error executing task '$TASK_NAME'" && return 1
    [ -z "$P_ID" ] && sleep 1
  done
  [ -z "$P_ID" ] && echo "Warning can't obtain task '$TASK_NAME''s PID" && return 0
  echo "$P_ID" > "$PID_FILE"
  return 0
}

execute_startup_docker_container_via_script() {
  [ -z "$1" ] && echo "Missing SCRIPT param" && return 1
  [ -z "$2" ] && echo "Missing CONT_NAME_FILTER param" && return 1

  SCRIPT=$1
  CONT_NAME_FILTER=$2
  LOG_FILE=${3:-/dev/null}
  bash "$SCRIPT" >> "$LOG_FILE" 2>&1
  CONTAINER_NAME=$(docker ps --all --format '{{.Names}}' | grep "$CONT_NAME_FILTER")
  [ -z "$CONTAINER_NAME" ] && echo "Error on docker container '$CONT_NAME_FILTER', exit" && return 1
  return 0
}

execute_startup_print_end() {
  [ -z "$1" ] && echo "Missing TEST_LOG_DIR param" && return 1
  [ -z "$2" ] && echo "Missing SCRIPT_DIR_NAME param" && return 1
  [ -z "$3" ] && echo "Missing JCP_HOST param" && return 1
  [ -z "$4" ] && echo "Missing JCP_AUTH_PORT param" && return 1
  [ -z "$5" ] && echo "Missing JCP_APIS_PORT param" && return 1
  [ -z "$6" ] && echo "Missing JCP_FE_PORT param" && return 1

  TEST_LOG_DIR=$1
  SCRIPT_DIR_NAME=$2
  JCP_HOST=$3
  JCP_AUTH_PORT=$4
  JCP_APIS_PORT=$5
  JCP_FE_PORT=$6

  echo "####### # ####### # ####### # ####### # ####### # ####### # ####### # ####### #"
  echo ""
  echo "John Cloud Platform up and running"
  echo "   Logs available on '$TEST_LOG_DIR' dir."
  echo "   JCP Front End              @   https://$JCP_HOST:$JCP_FE_PORT/frontend"
  echo "   JCP Front End - WebAdmin   @   https://$JCP_HOST:$JCP_FE_PORT/frontend/jcp"
  echo "   JCP Auth - WebAdmin        @   https://$JCP_HOST:$JCP_AUTH_PORT/auth"
  echo "   JCP APIs - Swagger Docs    @   https://$JCP_HOST:$JCP_APIS_PORT/swagger-ui.html"
  echo ""
  echo "Now you can run your tests with JCP, after that shutdown it with following command:"
  echo "$ bash scripts/jcp/$SCRIPT_DIR_NAME/execute_shutdown.sh "
  echo ""
  echo "####### # ####### # ####### # ####### # ####### # ####### # ####### # ####### #"
  echo ""
  echo "To allows JOD and JSL clients connect to current JCP instance please update their configs as specified:"
  echo "   - jcp.url.apis: $JCP_HOST:$JCP_APIS_PORT   >> src/jospJOD/configs/jod_default.yml"
  echo "   - jcp.url.auth: $JCP_HOST:$JCP_AUTH_PORT   >> src/jospJSL/configs/jsl_default.yml"
  echo ""
  echo "Run JOD manually with command:"
  echo "$ ./gradlew javaJODRun"
  echo ""
  echo "Run JSL Shell manually with command:"
  echo "$ ./gradlew javaJSLRun"
  return 0
}



execute_shutdown_init() {
  [ -z "$1" ] && echo "Missing SCRIPT_DIR param" && return 1
  [ -z "$2" ] && echo "Missing JOSP_DIR param" && return 1

  SCRIPT_DIR="$1"
  SCRIPT_DIR_NAME=$(basename "$SCRIPT_DIR")
  JOSP_DIR="$2"
  JOSP_ENV_DIR="$JOSP_DIR/envs"
  TEST_ID=$(date +'%Y%m%d-%H%M')
  TEST_LOG_DIR="$JOSP_ENV_DIR/scripts/$SCRIPT_DIR_NAME/$TEST_ID"
  return 0
}

execute_shutdown_java_exec() {
  [ -z "$1" ] && echo "Missing PROCES_FILTER param" && return 1

  PROCES_FILTER=$1
  LOG_FILE=${2:-/dev/null}
  TASK_PID=$(jps -ml | grep "$PROCES_FILTER" | awk -F ' ' '{print $1}')
  [ -z "$TASK_PID" ] && echo "No java executable matching PROCES_FILTER param '$PROCES_FILTER'" && return 1
  ! kill -9 "$TASK_PID" >> "$LOG_FILE" 2>&1 && echo "Error on 'kill \"$TASK_PID\"' command to kill 'jcpFE_Start' gradle task, ignore."
  return 0
}

execute_shutdown_gradle_tasks() {
  execute_shutdown_java_exec "$@"
  return $?
}

execute_shutdown_docker_container() {
  [ -z "$1" ] && echo "Missing CONT_NAME_FILTER param" && return 1

  CONT_NAME_FILTER=$1
  LOG_FILE=${2:-/dev/null}
  CONTAINER_NAME=$(docker ps --all --format '{{.Names}}' | grep "$CONT_NAME_FILTER")
  [ -z "$CONTAINER_NAME" ] && echo "No docker container matching CONT_NAME_FILTER param '$CONT_NAME_FILTER'" && return 1
  ! docker container stop "$CONTAINER_NAME" >> "$LOG_FILE" 2>&1 && echo "Error on 'docker stop \"$CONTAINER_NAME\"' command, ignore."
  ! docker container rm "$CONTAINER_NAME" >> "$LOG_FILE" 2>&1 && echo "Error on 'docker rm \"$CONTAINER_NAME\"' command, ignore."
  return 0
}

execute_shutdown_print_end() {
  echo "####### # ####### # ####### # ####### # ####### # ####### # ####### # ####### #"
  echo ""

  echo "John Cloud Platform shutdown successfully"

  echo ""
  echo "####### # ####### # ####### # ####### # ####### # ####### # ####### # ####### #"

  echo "Now you can run delete latest eco-system data manually with following command:"
  echo "$ sudo rm -r \"$JOSP_ENV_DIR/dockers\""
  echo "$ sudo rm -r \"$JOSP_ENV_DIR/runnables\""
}
