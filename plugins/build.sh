#! /bin/bash

PLUGINS="tools javagui recorder qtaste/testapi"

for PLUGIN in $PLUGINS
do
	pushd $PLUGIN
	mvn clean install assembly:single || exit 1
	popd
done
