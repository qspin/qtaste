@echo off
Rem Find java  based on the command-line arguments
Rem Usage: find_java_process <command_line_arguments>
Rem Result: print the process id of each matching process

if [%2] == [] (
  echo Usage: find_process ^<command_line_arguments^>
  exit 1
)

setlocal

set ARGUMENT1=%1
set ARGUMENT1=%ARGUMENT1:""=''%

set ARGUMENT2=%2
set ARGUMENT2=%ARGUMENT2:""=''%

cscript %~dp0\find_process.vbs %ARGUMENT1% %ARGUMENT2% //NoLogo

endlocal
