@echo off

set ROOT=%~dp0\..\
set RECORDER_PATH=%ROOT%\plugins\recorder
set FILTER_PATH="=recordingFilter.xml"

java -javaagent:%RECORDER_PATH%\target\qtaste-recorder-deploy.jar%FILTER_PATH% -cp testapi\target\qtaste-testapi-deploy.jar com.qspin.qtaste.sutuidemo.Interface || exit 1

echo "An XML file named spyRepport_*.xml has been created with all recorded events. You can use the tools plugin to convert it into a python script usable with QTaste in order to reproduce your actions."
