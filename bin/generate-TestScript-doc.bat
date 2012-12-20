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

setlocal

set TEST_SCRIPT=%~f1
set TEST_SCRIPT_DIR=%~dp1
echo Test script: %TEST_SCRIPT%

if not exist %TEST_SCRIPT% (
  echo File %TEST_SCRIPT% doesn't exist
  goto end
)

call %~dp0\setenv

rem generate Test steps modules doc from top-level TestSuites directory
call %QTASTE_ROOT%\bin\generate-TestStepsModules-doc TestSuites

set FORMATTER_DIR="%QTASTE_ROOT%\tools\TestScriptDoc"

echo.
echo Generating Test script XML doc...
java -cp %JYTHON_HOME%/jython.jar;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar;testapi\target\qtaste-testapi-deploy.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %JYTHON_HOME%\Lib\pythondoc.py -f -s -Otestscriptdoc_xmlformatter %TEST_SCRIPT%
echo.

if exist %TEST_SCRIPT_DIR%\TestScript-doc.xml (
  echo Converting Test script XML doc to HTML...
  java -cp %QTASTE_ROOT%/kernel/target/ate-kernel-deploy.jar org.apache.xalan.xslt.Process -XSLTC -XT -IN %TEST_SCRIPT_DIR%\TestScript-doc.xml -XSL %FORMATTER_DIR%\testscriptdoc_xml2html.xsl -OUT %TEST_SCRIPT_DIR%\TestScript-doc.html
  del %TEST_SCRIPT_DIR%\TestScript-doc.xml
) else (
  echo XML test script doc has not been generated for %TEST_SCRIPT%!
  del !TEST_SCRIPT_DIR!\TestScript-doc.html 2>NUL
)

endlocal

:end
