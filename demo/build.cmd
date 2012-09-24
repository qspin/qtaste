@echo off

setlocal enabledelayedexpansion

pushd testapi

set PATH=%PATH%;%QTASTE_ROOT%\tools\GnuWin32\bin

rem build using maven
call mvn clean install assembly:single || set FAILED=1

rem pause if build failed
if "%FAILED%" == "1" pause

popd

endlocal