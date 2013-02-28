#!/bin/bash

# kill a Java process
# usage: stop_java_process <main_class_and_args> 

JP_NAME=$1
echo "Stopping $JP_NAME"

INFO=$(ps -fu $USER | grep "$JP_NAME" | grep -v "grep" | grep -v "stop_java_process")

set -- $INFO

PID=$2

if [ -z $PID ]; then
   echo "Java process not found"
else
   kill $PID
   sleep 5s
   INFO=$(ps -fu $USER | grep "$JP_NAME" | grep -v "grep" | grep -v "stop_java_process")
   set -- $INFO
   PID=$2
   if [ -z $PID ]; then
      echo "Java process killed"
   else
      kill -9 $PID
   fi
fi
