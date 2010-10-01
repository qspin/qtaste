@echo off
rem start a process and check that it is started
rem usage: start_process <executable> <arguments> [-checkAfter <process_start_time>] [-title <title>]

setlocal enableDelayedExpansion 

rem if not "%3" == "-dir" goto usage
goto continue

:usage
echo Usage: start_process ^<path_to_executable> ^<arguments> -dir ^<start_command_dir^> [-checkAfter ^<process_start_time^>] [-title ^<window_title_name^>]
exit 1


:continue
set EXECUTABLE=%~1
set EXECUTABLE=%EXECUTABLE:""="%
set ARGS=%~2
set ARGS=%ARGS:""="%
set START_COMMAND_DIR=%4
set PROCESS_START_TIME=0
set TITLE=

:getArgs
if [%5] == [] goto noMoreArgs
if [%5] == [-checkAfter] (
  set PROCESS_START_TIME=%6
) else if [%5] == [-title] (
  set TITLE=%6
) else goto usage
rem shift arguments to get next 2 arguments
shift /5
shift /5
goto getArgs

:noMoreArgs

echo Starting executable program "%EXECUTABLE% %ARGS%"...

cd %START_COMMAND_DIR%
start %TITLE% /min %EXECUTABLE% %ARGS%

echo Waiting %PROCESS_START_TIME% seconds for program to start...
call %~dp0\sleep %PROCESS_START_TIME%

rem check that Process is still started
for /F %%i in ('%~dp0\find_process "%EXECUTABLE:"=""%" "%ARGS:"=""%"') do (
  echo Program "%EXECUTABLE% %ARGS%" started successfully
  goto end
)

echo Program "%EXECUTABLE% %ARGS%" not started
exit 1

:end
endlocal
