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
