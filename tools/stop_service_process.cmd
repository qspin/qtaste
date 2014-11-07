@echo off
rem stop a  process and wait for its end
rem usage: stop_service_process <serviceName> 

setlocal enableDelayedExpansion 

set SERVICE_NAME="%~1"

net stop %SERVICE_NAME%

call %~dp0\get_service_status %SERVICE_NAME%

if %errorlevel% == 0 (
	echo Service %SERVICE_NAME% is stopped
	exit 0
) else (
	echo Service %SERVICE_NAME% has not been stopped
	exit 1
)
