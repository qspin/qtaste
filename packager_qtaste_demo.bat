@echo off
set QTASTE_ROOT=%~dp0
rem delete testapi target directory because mvn cannot always delete it when run from jython
rmdir /s /q demo\testapi\target 2>NUL
call %QTASTE_ROOT%\tools\jython\bin\jython.bat %QTASTE_ROOT%\packager_qtaste_demo.py
pause