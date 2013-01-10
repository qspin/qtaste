@echo off

pushd tools
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\
popd

pushd javagui
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\
popd

pushd recorder
call mvn clean install assembly:single
copy target\*-deploy.jar ..\..\plugins\
popd
