@echo off
rem start a process and check that it is started
rem usage: start_java_process [-jar] <java_main_class_or_jar_and_arguments> -dir <start_command_dir> [-cp <classpath>] [-vmArgs <vm_args>] [-jmxPort <jmx_port>] [-checkAfter <process_start_time>] [-title <title>] [-priority low|belownormal|normal|abovenormal|high|realtime]

setlocal enableDelayedExpansion

if [%1] == [-jar] (
  set JAR=-jar
  shift /1
)
if not "%2" == "-dir" goto usage


goto continue

:usage
echo Usage: start_java_process [-jar] ^<java_main_class_or_jar_and_arguments^> -dir ^<start_command_dir^> [-cp ^<classpath^>] [-vmArgs ^<vm_args^>] [-jmxPort ^<jmx_port^>] [-checkAfter ^<process_start_time^>] [-title ^<window_title_name^>] [-priority low^|belownormal^|normal^|abovenormal^|high^|realtime]
exit 1


:continue
set JAVA_MAIN_AND_ARGS=%~1
set JAVA_MAIN_AND_ARGS=%JAVA_MAIN_AND_ARGS:""="%
set START_COMMAND_DIR=%3
set CLASSPATH=
set PROCESS_START_TIME=0
set TITLE=
set PRIORITY=
For /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%b_%%a_%%c)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a_%%b)
set NOW=%mydate%-%mytime%

:getArgs
if [%4] == [] goto noMoreArgs
if [%4] == [-cp] (
  set CLASSPATH=%~5
) else if [%4] == [-vmArgs] (
  set VM_ARGS=%~5
) else if [%4] == [-jmxPort] (
  set JMX_PORT=%5
) else if [%4] == [-checkAfter] (
  set PROCESS_START_TIME=%5
) else if [%4] == [-title] (
  set TITLE=%5
) else if [%4] == [-priority] (
  set PRIORITY=/%5
) else goto usage
rem shift arguments to get next 2 arguments
shift /4
shift /4
goto getArgs

:noMoreArgs

if not "%JMX_PORT%" == "" set JMX_ARGS=-Dcom.sun.management.jmxremote.port=%JMX_PORT% -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
echo Starting java program "%JAVA_MAIN_AND_ARGS%"...

cd %START_COMMAND_DIR%
if [%JAR%] == [] (
    set JAVA_CMD=java %VM_ARGS% %JMX_ARGS% -cp %CLASSPATH% %JAVA_MAIN_AND_ARGS%
) else (
    set JAVA_CMD=java %VM_ARGS% %JMX_ARGS% %JAR% %JAVA_MAIN_AND_ARGS%
)
start /min %PRIORITY% /b cmd /c (%JAVA_CMD%)>%TITLE%.%NOW%.out 2>&1

echo Waiting %PROCESS_START_TIME% seconds for program to start...
call %~dp0\sleep %PROCESS_START_TIME%

rem check that Process is still started
for /F %%i in ('%~dp0\find_java_process "%JAVA_MAIN_AND_ARGS:"=""%"') do (
  echo Java program "%JAVA_MAIN_AND_ARGS%" started successfully
  goto end
)

echo Java program "%JAVA_MAIN_AND_ARGS%" not started
exit 1

:end
endlocal
