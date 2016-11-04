#!/bin/bash

# start a process and check that it is started
# usage: start_java_process.sh [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>] [-priority low|belownormal|normal|abovenormal|high|realtime] [-restart true]

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
   elif [ "$1" = "-restart" -a "$2" = "true" ]; then
      RESTART=true
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
RESTART=false
NOW=$(date +"%m_%d_%Y-%H_%M_%S")

if [ $# -lt 1 ]; then
 echo "Usage: start_java_process.sh [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>] [-priority low|belownormal|normal|abovenormal|high|realtime]"
 exit
fi


if [ "$1" = "-jar" ]
then
   setArg $3 "$4"
   setArg $5 "$6"
   setArg $7 "$8"
   setArg $9 "${10}"
   setArg ${11} "${12}"
   setArg ${13} "${14}"
   setArg ${15} "${16}"
   setArg ${17} "${18}"
   setJMXcommand
   setNiceCommand

   cd $WORKINGDIR

   # if starting again (restart) then backup log file
   if [ $RESTART -a -f "$OUTPUT" ]; then
      cp "$OUTPUT" "$TITLE.$NOW.out"
   fi

   COMMAND="java $CP_ARG $VM_ARGS $JMX_ARGS -jar $2"
else
   setArg $2 "$3"
   setArg $4 "$5"
   setArg $6 "$7"
   setArg $8 "$9"
   setArg ${10} "${11}"
   setArg ${12} "${13}"
   setArg ${14} "${15}"
   setJMXcommand
   setNiceCommand

   cd $WORKINGDIR

   # if starting again (restart) then backup log file
   if [ $RESTART = true -a -f "$OUTPUT" ]; then
      cp "$OUTPUT" "$TITLE.$NOW.out"
   fi

   COMMAND="java $CP_ARG $VM_ARGS $JMX_ARGS $1"
fi

exec bash <<EOFF
nohup $NICE $COMMAND &>"$OUTPUT" &
#nohup $NICE $COMMAND &>"$TITLE.$NOW.out" &
#nohup $NICE $COMMAND 2>&1 | tee "$TITLE.$NOW.out" > "$OUTPUT" &
PID=\$!

sleep $PROCESS_START_TIME

ps -p \$PID >/dev/null || (echo "Process is not started"; exit -1)
EOFF
