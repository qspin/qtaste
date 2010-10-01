@echo off
setlocal
set JYTHON_HOME=%~dp0\..
java -Dpython.path=%JYTHON_HOME%/lib/jython.jar;%JYTHON_HOME%/lib/Lib -cp "%JYTHON_HOME%/build/jython-engine.jar;%JYTHON_HOME%/lib/jython.jar;%CLASSPATH%" org.python.util.jython %*
endlocal