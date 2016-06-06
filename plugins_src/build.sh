#! /bin/bash

PLUGINS="javagui javagui-fx sikuli"

targetDir=../plugins/SUT
if [ ! -d "$targetDir" ]
then
    mkdir $targetDir
fi

for PLUGIN in $PLUGINS
do
	pushd $PLUGIN
	mvn clean install assembly:single || exit 1
	cp target/*-deploy.jar ../$targetDir
	popd
done

PLUGINS=""
#PLUGINS="RemoteAgentManager"

targetDir=../plugins/

for PLUGIN in $PLUGINS
do
	pushd $PLUGIN
	mvn clean install assembly:single || exit 1
	cp target/*-deploy.jar ../$targetDir
	popd
done
