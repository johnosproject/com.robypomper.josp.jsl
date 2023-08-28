#!/usr/bin/env bash

# JOSP_DIR: customize the JOSP project dir; by default current working dir
# FORCE_CLEAN_ALL: if 'true', clean any service or data still present before executing the new environment; by default 'true'

JOSP_DIR=${JOSP_DIR:-$(pwd)}

# check $JOSP_DIR is com.robypomper.josp project dir
[ ! -d "$JOSP_DIR/scripts" ] && echo "Wrong working dir, please chdir to 'com.robypomper.josp' project's main dir." && exit
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

! source "$SCRIPT_DIR/../../_utils.sh" && echo "Error can't find '_utils.sh' source file" && exit
! execute_startup_init "$SCRIPT_DIR" "$JOSP_DIR" "localhost" 8998 9000 9000 && echo "Error initializing startup script" && exit
! mkdir -p "$TEST_LOG_DIR" && echo "Error, can't create dir '$TEST_LOG_DIR', exit." && exit
echo "Script logs available at '$TEST_LOG_DIR'"


# ##### #
# Clean #
# ##### #

$FORCE_CLEAN_ALL && echo "CLEAN-ALL" || echo "CLEAN"
! execute_startup_pre_clean "$FORCE_CLEAN_ALL" "$TEST_LOG_DIR/execute_startup_pre_clean.log" && echo "Error on check/clean execution environment, please clean it manually " && exit


# ##### #
# Build #
# ##### #

echo "BUILD"

echo "* Build and publish all JOSP artifacts..."
! execute_startup_gradle_task "all_PublishToLocal" "$TEST_LOG_DIR/execute_startup_gradle_task-all_PublishToLocal.log" && echo "Error on build and publish JOSP Artifacts, exit" && exit


echo "EXECUTE"

echo "* Startup JCP DBMS as docker container via gradle task..."
! execute_startup_gradle_task "dbms_Up" "$TEST_LOG_DIR/execute_startup_gradle_task-dbms_Up.log"  && echo "Error on startup JCP DBMS, exit" && exit
echo "* Startup JCP Auth as docker container via gradle task..."
! execute_startup_gradle_task "auth_Up" "$TEST_LOG_DIR/execute_startup_gradle_task-auth_Up.log"  && echo "Error on startup JCP Auth, exit" && exit

echo "* Startup JCP All as gradle task..."
! execute_startup_gradle_task_daemon "jcpAll_Start" "$TEST_LOG_DIR/execute_startup_gradle_task_daemon-jcpAll_Start.log" && echo "Error on startup JCP All, exit" && exit

execute_startup_print_end "$TEST_LOG_DIR" "$SCRIPT_DIR_NAME" "$JCP_HOST" "$JCP_AUTH_PORT" "$JCP_APIS_PORT" "$JCP_FE_PORT"
exit 0
