@echo off

if [%1] == [] (
  goto usage
)
if not [%2] == [] (
  goto usage
)

goto begin

:usage
echo Usage: %~n0 ^<TestScriptFile^>
goto end

:begin

set QTASTE_ROOT=%~dp0\..
java -Xms64m -Xmx512m -cp %QTASTE_ROOT%\plugins\*;%QTASTE_ROOT%\kernel\target\qtaste-kernel-deploy.jar;testapi\target\qtaste-testapi-deploy.jar com.qspin.qtaste.util.GenerateTestScriptDoc %*
