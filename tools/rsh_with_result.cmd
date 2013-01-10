@echo off
rem Execute a remote command using rsh and check its success which must be indicated by "SUCCESS" as last output line
rem Usage: call rsh_with_result.cmd <rsh arguments>

setlocal

for /f "tokens=*" %%l in ('rsh %*') do (echo %%l & set RESULT=%%l)
if not "%RESULT%" == "SUCCESS" exit 1

endlocal