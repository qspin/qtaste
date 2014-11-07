@echo off
rem start a service
rem usage: start_service_process <serviceName> 

setlocal enableDelayedExpansion 

set SERVICE_NAME="%~1"

net start %SERVICE_NAME%

call %~dp0\get_service_status %SERVICE_NAME%

if %errorlevel% == 1 (
	echo Service %SERVICE_NAME% is started
	exit 0
) else (
	echo Service %SERVICE_NAME% has not been started
	exit 1
)
