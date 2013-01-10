@echo off
rem special jython launcher for Komodo
rem remove -u argument which is not supported by jython

set ARGS=%*
set ARGS=%ARGS:-u =%
set JYTHON_HOME=%~dp0\..

java -Dpython.path=%JYTHON_HOME%/lib/jython.jar;%JYTHON_HOME%/lib/lib -cp %JYTHON_HOME%/build/jython-engine.jar;%JYTHON_HOME%/lib/jython.jar;%CLASSPATH% org.python.util.jython %ARGS%
