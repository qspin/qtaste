@echo off

pushd tools
call mvn clean install assembly:single
popd

pushd javagui
call mvn clean install assembly:single
popd

pushd recorder
call mvn clean install assembly:single
popd

pushd qtaste
pushd testapi
call mvn clean install assembly:single
popd
popd