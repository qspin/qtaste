#!/bin/bash

# kill a  process
# usage: stop_process <process> 

JP_NAME=$1
echo "Stopping $JP_NAME"

INFO=$(ps -fu $USER | grep "$JP_NAME" | grep -v "grep" | grep -v "stop_process")

set -- $INFO

PID=$2

if [ -z $PID ]; then
   echo "Process not found"
else
   kill $PID
   sleep 5s
   INFO=$(ps -fu $USER | grep "$JP_NAME" | grep -v "grep" | grep -v "stop_process")
   set -- $INFO
   PID=$2
   if [ -z $PID ]; then
      echo "Process killed"
   else
      kill -9 $PID
   fi
fi
