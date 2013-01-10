@echo off
setlocal enableDelayedExpansion 

call %~dp0\setenv
echo Generating documentation of test suites included in test campaign
echo.

set TEMP_FILE=%TEMP%\generateTestCampaignDoc.tmp
del %TEMP_FILE% >NUL 2>&1
for /f %%i in ('type %1 ^| find "<testsuite " ^| %QTASTE_ROOT%\tools\GnuWin32\bin\sed -r -e "s/^.*testsuite\s*directory=\"^(TestSuites^(\/^^^|\\^)[^^^^/\\]*^).*\"\s*\/?>\s*$/\1/" ^| sort ') do @(if not "%%i" == "!previous!" (echo %%i) & set previous=%%i) >> %TEMP_FILE%
echo Found test suites:
type %TEMP_FILE%
echo.
echo.
for /f %%i in (%TEMP_FILE%) do call %~dp0\generate-TestSuite-doc %%i > log.txt

echo.
call %QTASTE_ROOT%\tools\jython\bin\jython %QTASTE_ROOT%\tools\TestProcedureDoc\generateTestCampaignDoc.py %1
endlocal
