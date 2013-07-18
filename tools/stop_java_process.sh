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
   for(( i = 0 ; i < 5 ; i++ )) do
      kill $PID
      sleep 1s
      INFO=$(ps -fu $USER | grep "$JP_NAME" | grep -v "grep" | grep -v "stop_java_process")
      set -- $INFO
      PID=$2
      if [ -z $PID ]; then
         echo "Java process killed"
	 break
      fi
   done
   if [ ! -z $PID ]; then
      kill -9 $PID
   fi
fi
