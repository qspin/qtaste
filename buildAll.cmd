@echo off

rem usage: buildAll.sh [-u <username>] [-p <password>]
rem optional args: [-u] Github username
rem                [-p] Password

if [%1] == [-help] (
    echo usage: buildAll.sh [-u <username>] [-p <password>]
    exit 0
)

rem remove previous python compilation classes
pushd ..\tools\jython\lib\Lib\
del /q *.class
popd

rem install kernel 3rd party artifacts
pushd kernel
call mvn clean -P qtaste-install-3rd-artifacts
popd

echo Building qtaste ...
call mvn clean install -P qtaste-build-kernel-first

echo Compiling plugins ...
pushd plugins_src
call build.cmd
popd

echo Compiling demos ...
pushd demo
call build.cmd
popd

echo generating documentation ...
pushd doc
call generateDocs.cmd %*
popd

echo creation of the installer ...
pushd izpack
call createInstaller.cmd
popd
