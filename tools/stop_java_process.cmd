@echo off
rem stop a Java process and wait for its end
rem usage: stop_java_process <main_class_and_args> 

setlocal enableDelayedExpansion 

set JAVA_MAIN_AND_ARGS=%~1
set JAVA_MAIN_AND_ARGS=%JAVA_MAIN_AND_ARGS:""="%

for /F %%i in ('%~dp0\find_java_process "%JAVA_MAIN_AND_ARGS:"=""%"') do (
  set PID=%%i

  rem terminate the process gracefully
  echo Terminating java program "%JAVA_MAIN_AND_ARGS%"...
  taskkill /pid !PID! >NUL 2>NUL

  rem wait max 5 seconds for the process to shutdown
  set /a TIMEOUT=5
  :wait
  tasklist /nh /fi "pid eq !PID!" 2>NUL | findstr /c:!PID! >NUL 2>NUL
  if "%errorlevel%" == "0" (
    call %~dp0\sleep 1
    set /a TIMEOUT-=1
    if not "%TIMEOUT%" == "0" goto wait
  )

  rem check if process has shutdown
  tasklist /nh /fi "pid eq !PID!" 2>NUL | findstr /c:!PID! >NUL 2>NUL
  if "%errorlevel%" == "0" (
    echo Couldn't terminate java program "%JAVA_MAIN_AND_ARGS%" gracefully
    rem kill the process forcefully
    echo Killing java program "%JAVA_MAIN_AND_ARGS%" forcefully...
    taskkill /f /pid !PID! >NUL 2>NUL

    rem wait 1 second after process end to free binded ports
    echo Waiting 1 second for binded ports to be freed...
    call %~dp0\sleep 1
  )
)

echo Java program "%JAVA_MAIN_AND_ARGS%" is stopped

endlocal