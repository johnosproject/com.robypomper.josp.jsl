#!/usr/bin/env bash

# JOSP_DIR: customize the JOSP project dir; by default current working dir

JOSP_DIR=${JOSP_DIR:-$(pwd)}

# check $JOSP_DIR is com.robypomper.josp project dir
[ ! -d "$JOSP_DIR/scripts" ] && echo "Wrong working dir, please chdir to 'com.robypomper.josp' project's main dir." && exit
JOSP_ENV_DIR="$JOSP_DIR/envs"


# ##### #
# Clean #
# ##### #

echo "# STATUS-ALL"

# java JCP services
echo "Java JCP services:"
JAVA_EXECS=$(jps -lm | grep "com.robypomper.josp.jcp" | awk -F ' ' '{print $2}')
for e in $JAVA_EXECS;
do
  EXEC_PID=$(jps -lm | grep "$e" | awk -F ' ' '{print $1}')
  echo "- $e (PID:'$EXEC_PID')"
done;

# gradle tasks
echo "Gradle tasks:"
TASKS=$(jps -ml | grep "GradleWrapperMain" | grep -E "josp|jcp" | awk -F ' ' '{print $2}')
for t in $TASKS;
do
  TASK_PID=$(jps -ml | grep "$t" | awk -F ' ' '{print $1}')
  echo "- $t (PID:'$TASK_PID')"
done;
echo ""


# docker containers
echo "Docker containers:"
CONTAINERS=$(docker ps --all --format '{{.Names}}' | grep -E "jcp|josp")
for c in $CONTAINERS;
do
  echo "- $c"
done;
echo ""

# envs directories
[ -d "$JOSP_ENV_DIR" ] && echo "JOSP project env dir: not empty" || echo "JOSP project env dir: empty"
