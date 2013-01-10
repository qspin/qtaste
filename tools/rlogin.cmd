@echo off
rem rlogin client using the com.qspin.qtaste.tcom.rlogin.RLogin class
rem Usage: <command> <remote_host> [-user <user>] [-reboot | -command <command>] [-logOutput] [-wait [seconds]] [-log4jconf <properties_file>]

setlocal

cd %~dp0\..
java -cp kernel/target/qtaste-kernel-deploy.jar com.qspin.qtaste.tcom.rlogin.RLogin %*|| exit 1

endlocal
