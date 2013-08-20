@echo off

setlocal

set QTASTE_ROOT=%~dp0\..
set PATH=%PATH%;%QTASTE_ROOT%\tools\GnuWin32\bin

pushd ..\tools\jython\lib\Lib\
del /q *.class
popd

rem build using maven
call mvn clean -P qtaste-intall-3rd-artifacts || set FAILED=1
call mvn install assembly:single || set FAILED=1


rem pause if build failed
if "%FAILED%" == "1" pause

endlocal
