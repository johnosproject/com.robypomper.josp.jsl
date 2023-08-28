

# JOSP_DIR: customize the JOSP project dir; by default current working dir

JOSP_DIR=${JOSP_DIR:-$(pwd)}

# check $JOSP_DIR is com.robypomper.josp project dir
[ ! -d "$JOSP_DIR/scripts" ] && echo "Wrong working dir, please chdir to 'com.robypomper.josp' project's main dir." && exit
JOSP_ENV_DIR="$JOSP_DIR/envs"


# ##### #
# Clean #
# ##### #

echo "# CLEAN-ALL"

# java JCP services
JAVA_EXECS=$(jps -lm | grep "com.robypomper.josp.jcp" | awk -F ' ' '{print $3}')
for e in $JAVA_EXECS;
do
  EXEC_PID=$(jps -lm | grep "$e" | awk -F ' ' '{print $1}')
  echo "Kill java exec '$e' (PID:'$EXEC_PID')"
  ! kill -9 "$EXEC_PID" > /dev/null 2>&1 && echo "Error on 'kill \"$EXEC_PID\"' command to kill '$t' java exec, skipp." && continue
done;

# gradle tasks
TASKS=$(jps -m | grep "GradleWrapperMain" | grep -E "josp|jcp" | awk -F ' ' '{print $3}')
for t in $TASKS;
do
  TASK_PID=$(jps -m | grep "$t" | awk -F ' ' '{print $1}')
  echo "Kill gradle task '$t' (PID:'$TASK_PID')"
  ! kill -9 "$TASK_PID" > /dev/null 2>&1 && echo "Error on 'kill \"$TASK_PID\"' command to kill '$t' gradle task, skipp." && continue
done;


# docker containers
CONTAINERS=$(docker ps --all --format '{{.Names}}' | grep -E "jcp|josp")
for c in $CONTAINERS;
do
  echo "Remove docker container '$c'"
  ! docker container stop "$c" > /dev/null 2>&1 && echo "Error on 'docker stop \"$c\"' command, skipp." && continue
  ! docker container rm "$c" > /dev/null 2>&1 && echo "Error on 'docker rm \"$c\"' command, skipp." && continue
done;

# envs directories
if [ -d "$JOSP_ENV_DIR/dockers" ]; then ! sudo rm -r "$JOSP_ENV_DIR/dockers" && echo "Error on 'rm -r \"$JOSP_ENV_DIR/dockers\"' command, exit." && exit; fi
if [ -d "$JOSP_ENV_DIR/runnables" ]; then ! sudo rm -r "$JOSP_ENV_DIR/runnables" && echo "Error on 'rm -r \"$JOSP_ENV_DIR/runnables\"' command, exit." && exit; fi
