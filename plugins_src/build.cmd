@echo off

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
copy target\*-deploy.jar ..\..\plugins\SUT\
popd

pushd ControlScriptBuilderAddOn
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\SUT\
popd
