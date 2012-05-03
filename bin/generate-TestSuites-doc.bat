@echo off

if not [%1] == [] (
  echo Usage: %~n0
  goto end
)

setlocal

call %~dp0\setenv

set TEST_SUITES_DIR=TestSuites

rem generate Test steps modules doc from top-level TestSuites directory
call %QTASTE_ROOT%\bin\generate-TestStepsModules-doc "%TEST_SUITES_DIR%"

for /D %%D in ("%TEST_SUITES_DIR%\*") do (
  call %~dp0\generate-TestSuite-doc "%%~fD"
  echo.
  echo --------------------------------------------------------------------------------
  echo.
)

endlocal

pause

:end
