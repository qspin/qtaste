#! /bin/bash

PLUGINS="tools javagui recorder"

for PLUGIN in $PLUGINS
do
	pushd $PLUGIN
	mvn clean install assembly:single || exit 1
	cp target/*-deploy.jar ../../plugins/
	popd
done
