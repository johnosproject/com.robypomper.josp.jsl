#!/usr/bin/env bash

# JOSP_DIR: customize the JOSP project dir; by default current working dir

JOSP_DIR=${JOSP_DIR:-$(pwd)}

# check $JOSP_DIR is com.robypomper.josp project dir
[ ! -d "$JOSP_DIR/scripts" ] && echo "Wrong working dir, please chdir to 'com.robypomper.josp' project's main dir." && exit
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

! source "$SCRIPT_DIR/../../_utils.sh" && echo "Error can't find '_utils.sh' source file" && exit
! execute_shutdown_init "$SCRIPT_DIR" "$JOSP_DIR" && echo "Error initializing shutdown script" && exit
! mkdir -p "$TEST_LOG_DIR" && echo "Error, can't create dir '$TEST_LOG_DIR', exit." && exit
echo "Script logs available at '$TEST_LOG_DIR'"


# ##### #
# Clean #
# ##### #

echo "SHUTDOWN"

echo "* Shutdown JCP All killing gradle task..."
! execute_shutdown_gradle_tasks "jcpAll_Start" "$TEST_LOG_DIR/execute_shutdown_gradle_tasks-jcpAll_Start.log" && echo "- No JCP All java exec, continue."

echo "* Shutdown and remove JCP Auth as docker container (not via gradle task)..."
! execute_shutdown_docker_container "josp_auth_auth_josp_1" "$TEST_LOG_DIR/execute_shutdown_docker_container-josp_auth_auth_josp_1.log" && echo "- No JCP Auth docker container, continue."
echo "* Shutdown and remove JCP DBMS as docker container (not via gradle task)..."
! execute_shutdown_docker_container "josp_dbms_dbms_josp_1" "$TEST_LOG_DIR/execute_shutdown_docker_container-josp_dbms_dbms_josp_1.log" && echo "- No JCP DBMS docker container, continue."

execute_shutdown_print_end
exit 0
