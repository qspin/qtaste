@echo off
setlocal
set JYTHON_HOME=%~dp0\..
java -Dpython.path=%JYTHON_HOME%/lib/jython.jar;%QTASTE_JYTHON_LIB%;%JYTHON_HOME%/lib/Lib;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar -cp "%JYTHON_HOME%/build/jython-engine.jar;%JYTHON_HOME%/lib/jython.jar;%CLASSPATH%" org.python.util.jython %*
endlocal