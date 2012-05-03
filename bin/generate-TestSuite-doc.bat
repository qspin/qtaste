::@echo off

if [%1] == [] (
  goto usage
)
if not [%2] == [] (
  goto usage
)

goto begin

:usage
echo Usage: %~n0 ^<TestSuiteDirectory^>
goto end

:begin

setlocal enableDelayedExpansion 

set TEST_SUITE_DIR="%~f1"
echo Test suite directory: %TEST_SUITE_DIR%

if not exist %TEST_SUITE_DIR% (
  echo Directory %TEST_SUITE_DIR% doesn't exist
  goto end
)

call %~dp0\setenv

rem generate Test steps modules doc from top-level TestSuites directory if not called from generate-TestSuites-doc.bat (in which case it is already done)
if "%TEST_SUITES_DIR%"=="" (
  call %QTASTE_ROOT%\bin\generate-TestStepsModules-doc TestSuites
)

rem set all test scripts files in TEST_SCRIPTS variable
set TEST_SCRIPTS=
set NUMBER_SCRIPTS=0
for /R %TEST_SUITE_DIR% %%F in (*.py) do if "%%~nxF" == "TestScript.py" (
  set TEST_SCRIPTS=!TEST_SCRIPTS! "%%~F"
  set /a NUMBER_SCRIPTS+=1
)
echo.
if "%NUMBER_SCRIPTS%"=="0" (
  echo No test script found.
  goto end
)
echo Test scripts: !TEST_SCRIPTS!

set FORMATTER_DIR="%QTASTE_ROOT%\tools\TestScriptDoc"
set FORMATTER_DIR=%FORMATTER_DIR:\=\\%

echo.
echo Generating Test scripts and Test suite XML doc...
java -cp %JYTHON_HOME%/jython.jar;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %JYTHON_HOME%\Lib\pythondoc.py -f -s -Otestscriptdoc_xmlformatter -Dtestsuite_dir=%TEST_SUITE_DIR% !TEST_SCRIPTS!
echo.

set TEST_SCRIPT_DIRS=
for /R %TEST_SUITE_DIR% %%F in (*.py) do if "%%~nxF" == "TestScript.py" (
  set TEST_SCRIPT_DIR=%%~dpF
  if exist !TEST_SCRIPT_DIR!\TestScript-doc.xml (
    set TEST_SCRIPT_DIRS=!TEST_SCRIPT_DIRS! !TEST_SCRIPT_DIR!
  ) else (
    echo XML test script doc has not been generated for %%F!
    del !TEST_SCRIPT_DIR!\TestScript-doc.html 2>NUL
  )
)

jrunscript -cp %QTASTE_JYTHON_LIB%;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar -e "for (i in arguments) { println('Converting Test script XML doc to HTML for ' + arguments[i]); xmlDocFile = arguments[i] + '\\TestScript-doc.xml'; org.apache.xalan.xslt.Process.main(['-XSLTC', '-XT', '-IN', xmlDocFile, '-XSL', '%FORMATTER_DIR%\\testscriptdoc_xml2html.xsl', '-OUT', arguments[i] + '\\TestScript-doc.html']); java.io.File(xmlDocFile).deleteOnExit() }" !TEST_SCRIPT_DIRS!

if exist %TEST_SUITE_DIR%\TestSuite-doc.xml (
  echo.
  echo Converting Test suite XML doc to HTML list and summary...
  jrunscript -cp %QTASTE_JYTHON_LIB%;%QTASTE_ROOT%/kernel/target/qtaste-kernel-deploy.jar -e "org.apache.xalan.xslt.Process.main(['-XSLTC', '-XT', '-IN', arguments[0] + '\\TestSuite-doc.xml', '-XSL', '%FORMATTER_DIR%\\testsuitedoc_list_xml2html.xsl', '-OUT', arguments[0] + '\\TestSuite-doc-list.html']); org.apache.xalan.xslt.Process.main(['-XSLTC', '-XT', '-IN', arguments[0] + '\\TestSuite-doc.xml', '-XSL', '%FORMATTER_DIR%\\testsuitedoc_summary_xml2html.xsl', '-OUT', arguments[0] + '\\TestSuite-doc-summary.html'])" %TEST_SUITE_DIR%

  echo.
  echo Creating Test suite frameset...
  echo ^<HTML^>^<HEAD^>^<FRAMESET cols="15%%,85%%"^>^<FRAME src="TestSuite-doc-list.html" name="listFrame" title="List of all Test suite scripts"/^>^<FRAME src="TestSuite-doc-summary.html" name="testScriptFrame" title="Test script documentation" scrolling="yes"/^>^</FRAMESET^>^</HEAD^>^</HTML^> > %TEST_SUITE_DIR%\TestSuite-doc.html
  
  del %TEST_SUITE_DIR%\TestSuite-doc.xml
) else (
  echo XML test suite doc has not been generated for %TEST_SUITE_DIR%!
  del %TEST_SUITE_DIR%\TestSuite-doc-list.html 2>NUL
  del %TEST_SUITE_DIR%\TestSuite-doc-summary.html 2>NUL
  del %TEST_SUITE_DIR%\TestSuite-doc.html 2>NUL
)

endlocal

:end
