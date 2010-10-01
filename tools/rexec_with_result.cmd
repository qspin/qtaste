@echo off
rem Execute a remote command using T4eRexec and check its success which must be indicated by "SUCCESS" as last output line
rem Usage: call rexec_with_result.cmd <T4eRexec arguments>

setlocal

set PATH=%~dp0\tools4ever;%PATH%

rem skip 5 first lines which are T4eRexec header
for /f "skip=5 tokens=*" %%l in ('T4eRexec -c %*') do (echo %%l & set RESULT=%%l)
if not "%RESULT%" == "SUCCESS" exit 1

endlocal