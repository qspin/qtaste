@echo off
rem sleep given number of seconds

if [%1] == [] (
  echo Usage: sleep ^<number_of_seconds^>
  exit 1
)

setlocal enableDelayedExpansion 

set /a TIME_PLUS_1=%1+1
ping 0.0.0.0 -n !TIME_PLUS_1! >NUL

endlocal