#! /bin/bash
# set -x

ROOT=`pwd`/../
RECORDER_PATH=$ROOT/plugins/recorder

echo 
java -javaagent:$RECORDER_PATH/target/qtaste-recorder-deploy.jar -cp testapi/target/qtaste-testapi-deploy.jar com.qspin.qtaste.testapi.ui.Interface || exit 1

echo "An XML file named spyRepport_*.xml has been created with all recorded events. You can use the tools plugin to convert it into a python script usable with QTaste in order to reproduce your actions."
