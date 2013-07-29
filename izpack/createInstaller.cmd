@echo off

set QTASTE_ROOT=%~dp0\..
set QTASTEIZPACK=%QTASTE_ROOT%\izpack

mvn clean resources:resources package

IF EXIST %QTASTEIZPACK%\installer (

  rem delete previously generated doc folders
  FOR /D /R %%X IN (%QTASTEDOC%) DO RD /S /Q "%%X"
)
