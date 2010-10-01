@echo off
set QTASTE_ROOT=%~dp0
rem delete testapi target directory because mvn cannot always delete it when run from jython
call %QTASTE_ROOT%\tools\jython\bin\jython.bat %QTASTE_ROOT%\packager_qtaste_demo_source.py
pause