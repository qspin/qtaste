@echo off

if not exist "..\plugins\SUT" (
    mkdir ..\plugins\SUT
)

pushd javagui
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd
