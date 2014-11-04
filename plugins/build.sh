#! /bin/bash

PLUGINS="javagui"

for PLUGIN in $PLUGINS
do
	pushd $PLUGIN
	mvn clean install assembly:single || exit 1
	popd
done
