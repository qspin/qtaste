#!/bin/bash

# kill a Java process
# usage: stop_java_process <main_class_and_args> 

echo "Stopping $1"

INFO=$(ps -fu $USER | grep "$1" | grep -v "grep" | grep -v "stop_java_process")

set -- $INFO

PID=$2

if [ -z $PID ]; then
   echo "Java process not found"
else
   echo $PID
   kill $PID
   sleep 5s
   if [ -z $PID ]; then
      echo "Java process still running - use kill -9"
   else
      kill -9 $PID
      sleep 5s
   fi
fi
