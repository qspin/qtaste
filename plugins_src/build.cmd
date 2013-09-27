@echo off

if not exist "..\plugins\SUT" (
    mkdir ..\plugins\SUT
)

pushd tools
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd javagui
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd recorder
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd AddonDemo
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\
popd

pushd ControlScriptBuilderAddOn
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\
popd
