@echo off
set QTASTE_ROOT=%~dp0
cd %QTASTE_ROOT%
rem delete kernel/target directory because mvn cannot always delete it when run from jython
rmdir /s /q kernel\target 2>NUL
call tools\jython\bin\jython.bat packager_qtaste_kernel.py
pause