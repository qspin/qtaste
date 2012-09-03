#! /bin/bash

# build the kernel
pushd kernel
./build.sh || exit 1
popd

# build other
mvn install || exit 1

# build plugins
pushd plugins
./build.sh || exit 1
popd

#build demonstrations
pushd demo
./build.sh || exit 1
popd
