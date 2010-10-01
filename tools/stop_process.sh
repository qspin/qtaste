#!/bin/bash

# kill a  process
# usage: stop_process <process> 

echo "Stopping $1"

INFO=$(ps -fu $USER | grep "$1" | grep -v "grep" | grep -v "stop_process")

set -- $INFO

PID=$2

if [ -z $PID ]; then
   echo "Process not found"
else
   echo $PID
   kill $PID
   sleep 5s
fi
