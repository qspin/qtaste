@echo off

if [%1] == [] (
  set ARG="TestSuites"
) else (
  set ARG=%1
)

if not [%2] == [] (
  goto usage
)

goto begin

:usage
echo Usage: %~n0 [TestStepsModuleFile ^| BaseDirectory]
echo   Default base directory is TestSuites
goto end

:begin

setlocal enableDelayedExpansion 

call %~dp0\setenv

set FORMATTER_DIR="%QTASTE_ROOT%\tools\TestScriptDoc"

echo.
echo Generating Test steps module XML doc...
java -cp %JYTHON_HOME%\jython.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %FORMATTER_DIR%\stepsmoduledoc_xmlformatter.py %ARG%

endlocal

:end
