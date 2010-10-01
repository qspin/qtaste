@echo off
rem rlogin client using the com.iba.ate.tcom.rlogin.RLogin class
rem Usage: <command> <remote_host> [-user <user>] [-reboot | -command <command>] [-logOutput] [-wait [seconds]] [-log4jconf <properties_file>]

setlocal

cd %~dp0\..
java -cp kernel/target/ate-kernel-deploy.jar com.iba.ate.tcom.rlogin.RLogin %*|| exit 1

endlocal
