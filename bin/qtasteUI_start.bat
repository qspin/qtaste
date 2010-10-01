@echo off
setlocal
set QTASTE_ROOT=%~dp0\..
set PATH=%PATH%;%QTASTE_ROOT%\lib
java -Xms64m -Xmx1024m -cp %QTASTE_ROOT%/plugins/*;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar;testapi/target/qtaste-testapi-deploy.jar com.qspin.qtaste.ui.MainPanel %*
endlocal
