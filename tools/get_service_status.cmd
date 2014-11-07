@echo off
Rem Find the status of a service
Rem Usage: get_service_status <serviceName>
Rem Result: return -1 if the service is unknown
Rem			return  0 if the service is stopped
Rem			return  1 if the service is started

if [%1] == [] (
  echo Usage: get_service_status ^<command_line_arguments^>
  print %0
  set EXIT_CODE=-2
  GOTO END
)
set SERVICE_NAME="%~1"

call sc query %SERVICE_NAME%

if %errorlevel% ==1060 (
	rem "Unknown service name!"
	set EXIT_CODE=-1
	GOTO END
) else (
	for /F %%i in ('sc query %SERVICE_NAME% ^| findstr "STOPPED"') do (
		rem not running
		set EXIT_CODE=0
	GOTO END
	)
	rem running
	set EXIT_CODE=1
	GOTO END
)

:END
exit /B %EXIT_CODE%
