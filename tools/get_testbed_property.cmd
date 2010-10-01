@echo off
Rem Get testbed property value from testbed config file specified by the TESTBED environment variable,
Rem and set it in a given variable
Rem Usage: get_testbed_property <XML_property_path> <variable_name>

if [%2] == [] (
  echo Usage: get_testbed_property ^<XML_property_path^> ^<variable_name^>
  exit 1
)

rem compute temporary file name
:setTMP_OUTPUT_FILE
set /a TMP_OUTPUT_FILE=%RANDOM%+100000  
set TMP_OUTPUT_FILE=%TEMP%\tmp%TMP_OUTPUT_FILE:~-5%.tmp
if exist %TMP_OUTPUT_FILE% goto setTMP_OUTPUT_FILE  

cscript %~dp0\get_xml_property.vbs %TESTBED% %1 //NoLogo>%TMP_OUTPUT_FILE% || (del %TMP_OUTPUT_FILE% & set TMP_OUTPUT_FILE= & exit 1)
set /p %2=<%TMP_OUTPUT_FILE%
del %TMP_OUTPUT_FILE%
set TMP_OUTPUT_FILE=
