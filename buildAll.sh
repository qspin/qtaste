#! /bin/bash

# usage: buildAll.sh [-u <username>] [-p <password>]
# optional args: [-u] Github username
#                [-p] Password

if [ "$1" == "-help" ]; then
    echo "usage: buildAll.sh [-u <username>] [-p <password>]"
    exit
fi

find . -name "*.sh" | xargs chmod +x

# remove previous python compilation classes
pushd tools/jython/lib/Lib/
rm -f *.class
popd

# build qtaste
mvn clean install -P qtaste-build-kernel-first || exit 1

# build plugins
pushd plugins_src
./build.sh || exit 1
popd

# build demonstrations
pushd demo
./build.sh || exit 1
popd

# generate documentation
pushd doc
./generateDocs.sh $@ || exit 1
popd

# create installer
pushd izpack
./createInstaller.sh || exit 1
popd
