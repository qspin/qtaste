#!/bin/bash

pushd testapi

# build using maven
mvn clean install assembly:single || exit 1

popd
pushd SUT

# build using maven
mvn clean install assembly:single || exit 1

popd
pushd SUT-FX
mvn clean install assembly:single || exit 1
popd
