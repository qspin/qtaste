@echo off
setlocal
set QTASTE_ROOT=%~dp0\..\..\..
set QTASTE_KERNEL=%QTASTE_ROOT%\kernel\target\qtaste-kernel-deploy.jar
set JYTHON_HOME=%QTASTE_ROOT%\tools\jython
java -Dpython.path="%QTASTE_KERNEL%;%QTASTE_JYTHON_LIB%;%JYTHON_HOME%\lib\Lib" -cp "%QTASTE_KERNEL%;%CLASSPATH%" org.python.util.jython %*
endlocal
