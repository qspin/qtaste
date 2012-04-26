@echo off
Rem Find java process based on the command-line arguments
Rem Usage: find_java_process <command_line_arguments>
Rem Result: print the process id of each matching process

if [%1] == [] (
  echo Usage: find_java_process ^<command_line_arguments^>
  exit 1
)
setlocal

set ARGUMENT=%1
set ARGUMENT=%ARGUMENT:""=''%
cscript %~dp0\find_java_process.vbs %ARGUMENT% //NoLogo

endlocal
