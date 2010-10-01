@echo off

setlocal
set TESTSUITE_DIR=%CD%
cd %~dp0\..\..
call bin\qtaste_start -testsuite %TESTSUITE_DIR% -testbed Testbeds\enginetest.xml %*
start reports\index.html
endlocal
