#!/bin/bash

# start a process and check that it is started
# usage: start_java_process.sh [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>]

function setArg {
   if [ "$1" = "-cp" ]; then
      CLASSPATH=$2
   elif [ "$1" = "-vmArgs" ]; then
      VM_ARGS=$2
   elif [ "$1" = "-jmxPort" ]; then
      JMX_PORT=$2
   elif [ "$1" = "-checkAfter" ]; then
      PROCESS_START_TIME=$2
   elif [ "$1" = "-title" ]; then
      TITLE=$2
   elif [ "$1" = "-dir" ]; then
      WORKINGDIR=$2
   fi
}

function setJMXcommand {
   if [ -z "$JMX_PORT" ]; then
      JMX_ARGS=""
   else
      JMX_ARGS="-Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
   fi
}

WORKINGDIR="."
PROCESS_START_TIME=0
CLASSPATH=""
VM_ARGS=""
JMX_PORT=""
TITLE=""


if [ $# -lt 1 ]; then
 echo "Usage: start_java_process.sh [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>]"
 exit
fi

echo $JMX_ARGS

if [ "$1" = "-jar" ]
then
   setArg $3 "$4"
   setArg $5 "$6"
   setArg $7 "$8"
   setArg $9 "${10}"
   setArg ${11} "${12}"
   setArg ${13} "${14}"
   setJMXcommand

   cd $WORKINGDIR
   nohup java $VM_ARGS $JMX_ARGS -jar $2 &>/dev/null &
else
   setArg $2 "$3"
   setArg $4 "$5"
   setArg $6 "$7"
   setArg $8 "$9"
   setArg ${10} "${11}"
   setArg ${12} "${13}"
   setJMXcommand
   
   cd $WORKINGDIR
   nohup java -cp $CLASSPATH $VM_ARGS $JMX_ARGS $1 &>/dev/null &
fi

PID=$!

sleep $PROCESS_START_TIME

ps -p $PID >/dev/null || (echo "Process is not started"; exit -1)
