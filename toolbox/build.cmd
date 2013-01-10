@echo off
setlocal

cd testapi
call mvn clean install assembly:single || pause
cd ..

endlocal