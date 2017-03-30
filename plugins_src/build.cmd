@echo off

if not exist "..\plugins\SUT" (
    mkdir ..\plugins\SUT
)

pushd javagui
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd javagui-fx
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd sikuli
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd opcua
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd