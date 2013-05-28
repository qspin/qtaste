@echo off

setlocal

set QTASTE_ROOT=%~dp0\..
set PATH=%PATH%;%QTASTE_ROOT%\tools\GnuWin32\bin
set QTASTEDOC=%QTASTE_ROOT%\doc

rem generate documentation using maven
call mvn pre-site

IF EXIST %QTASTEDOC%\target\docbkx (

  rem delete previously generated doc folders
  FOR /D /R %%X IN (%QTASTEDOC%\pdf*) DO RD /S /Q "%%X"
  FOR /D /R %%X IN (%QTASTEDOC%\html*) DO RD /S /Q "%%X"

  rem copy generated doc folders
  move %QTASTEDOC%\target\docbkx %QTASTEDOC%\

  rem delete generated doc folders
  FOR /D /R %%X IN (%QTASTEDOC%\target*) DO RD /S /Q "%%X"

)

endlocal
