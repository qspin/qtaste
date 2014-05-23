#!/bin/bash

# kill a  process
# usage: stop_process <process> 

P_NAME=$1

# trim leading and trailing whitespaces
shopt -s extglob
P_NAME="${P_NAME##*( )}"
P_NAME="${P_NAME%%*( )}"
shopt -u extglob

echo "Stopping $P_NAME"

INFO=$(ps -fu $USER | grep "$P_NAME" | grep -v "grep" | grep -v "stop_process")

set -- $INFO

PID=$2

if [ -z $PID ]; then
   echo "Process not found"
else
for(( i = 0 ; i < 5 ; i++ )) do
      kill $PID
      sleep 1s
      INFO=$(ps -fu $USER | grep "$P_NAME" | grep -v "grep" | grep -v "stop_process")
      set -- $INFO
      PID=$2
      if [ -z $PID ]; then
         echo "Process killed"
	 break
      fi
   done
   if [ ! -z $PID ]; then
      kill -9 $PID
   fi
fi
