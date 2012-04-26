#!/bin/bash

# stop a Java process and wait for its end
# usage: stop_java_process <main_class_and_args> 

JAVA_MAIN_AND_ARGS=$1
JAVA_MAIN_AND_ARGS_NO_MULTISPACES=`echo $1 | sed 's/  */ \+/g'`

PID=`pgrep -f "^java .*$JAVA_MAIN_AND_ARGS_NO_MULTISPACES\$"`

if [ -n "$PID" ]; then
   # terminate the process gracefully
   echo Terminating java program \"$JAVA_MAIN_AND_ARGS\"...
   kill $PID 2>/dev/null

   # wait max 5 seconds for the process to shutdown
   COUNT=50
   while kill -0 $PID 2>/dev/null && [ $COUNT -gt 0 ]; do 
      sleep 0.1
      let COUNT=$COUNT-1
   done

   # check if process has shutdown
   if kill -0 $PID 2>/dev/null; then
      echo Couldn\'t terminate java program \"$JAVA_MAIN_AND_ARGS\" gracefully
      # kill the process forcefully
      echo Killing java program \"$JAVA_MAIN_AND_ARGS\" forcefully...
      kill -9 $PID 2>/dev/null
   fi
fi

echo Java program \"$JAVA_MAIN_AND_ARGS\" is stopped

