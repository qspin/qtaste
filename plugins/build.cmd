@echo off

pushd javagui
call mvn clean install assembly:single
popd

