#!/bin/bash

pushd testapi

# build using maven
mvn clean install assembly:single || exit 1

popd
