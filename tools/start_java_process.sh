#!/bin/bash

# start a process and check that it is started
# usage: start_java_process.sh [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>] [-priority low|belownormal|normal|abovenormal|high|realtime]

function setArg {
   if [ "$1" = "-cp" ]; then
      CP_ARG="-cp $2"
   elif [ "$1" = "-vmArgs" ]; then
      VM_ARGS=$2
   elif [ "$1" = "-jmxPort" ]; then
      JMX_PORT=$2
   elif [ "$1" = "-checkAfter" ]; then
      PROCESS_START_TIME=$2
   elif [ "$1" = "-title" ]; then
      TITLE=$2
      OUTPUT="$2.out"
   elif [ "$1" = "-dir" ]; then
      WORKINGDIR=$2
   elif [ "$1" = "-priority" ]; then
      PRIORITY=`echo $2 | tr [:upper:] [:lower:]`
   fi
}

function setJMXcommand {
   if [ -z "$JMX_PORT" ]; then
      JMX_ARGS=""
   else
      JMX_ARGS="-Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
   fi
}

function setNiceCommand {
   NICE="";
   case $PRIORITY in
      low)
         NICE="nice -n 19";;
      belownormal)
         NICE="nice -n 10";;
      normal)
         NICE="";;
      abovenormal)
         NICE="nice -n -5";;
      high)
         NICE="nice -n -10";;
      realtime)
         NICE="nice -n -20";;
   esac
}

WORKINGDIR="."
PROCESS_START_TIME=0
CP_ARG=""
VM_ARGS=""
JMX_PORT=""
TITLE=""
PRIORITY=""
OUTPUT="/dev/null"


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
   setArg ${15} "${16}"
   setJMXcommand
   setNiceCommand

   cd $WORKINGDIR
   nohup $NICE java $CP_ARG $VM_ARGS $JMX_ARGS -jar $2 &>"$OUTPUT" &
else
   setArg $2 "$3"
   setArg $4 "$5"
   setArg $6 "$7"
   setArg $8 "$9"
   setArg ${10} "${11}"
   setArg ${12} "${13}"
   setJMXcommand
   setNiceCommand
   
   cd $WORKINGDIR
   nohup $NICE java $CP_ARG $VM_ARGS $JMX_ARGS $1 &>"$OUTPUT" &
fi

PID=$!

sleep $PROCESS_START_TIME

ps -p $PID >/dev/null || (echo "Process is not started"; exit -1)
