@echo off
rem QTASTE library
set QTASTE_ROOT=%~dp0\..
set CLASSPATH=%QTASTE_ROOT%\kernel\target\qtaste-kernel-deploy.jar
set JYTHON_HOME="%QTASTE_ROOT%\tools\jython\lib"
set QTASTE_JYTHON_LIB=%QTASTE_ROOT%\iba-extension\target\*