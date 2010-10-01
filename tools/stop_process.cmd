@echo off
rem stop a  process and wait for its end
rem usage: stop_process <executable_and_args> 

setlocal enableDelayedExpansion 

set EXECUTABLE=%~1
set EXECUTABLE=%EXECUTABLE:""="%
set ARGS=%~2
set ARGS=%ARGS:""="%

for /F %%i in ('%~dp0\find_process "%EXECUTABLE:"=""%" "%ARGS:"=""%"') do (
  set PID=%%i

  rem terminate the process gracefully
  echo Terminating executable program "%EXECUTABLE% %ARGS%"...
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
    echo Couldn't terminate executable program "%EXECUTABLE% %ARGS%" gracefully
    rem kill the process forcefully
    echo Killing executable program "%EXECUTABLE% %ARGS%" forcefully...
    taskkill /f /pid !PID! >NUL 2>NUL

    rem wait 1 second after process end to free binded ports
    echo Waiting 1 second for binded ports to be freed...
    call %~dp0\sleep 1
  )
)

echo Executable program "%EXECUTABLE% %ARGS%" is stopped

endlocal