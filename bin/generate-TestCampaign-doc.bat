@echo off
setlocal enableDelayedExpansion 

set QTASTE_ROOT=%~dp0\..
java -Xms64m -Xmx512m -cp %QTASTE_ROOT%\plugins\*;%QTASTE_ROOT%\kernel\target\qtaste-kernel-deploy.jar com.qspin.qtaste.util.GenerateTestCampaignDoc 2>&1 %*

endlocal
