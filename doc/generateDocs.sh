#!/bin/bash

# usage: generateDocs.sh [-u <username>] [-p <password>]
# optional args: [-u] Github username
#                [-p] Password

if [ "$1" == "-help" ]; then
    echo "usage: generateDocs.sh [-u <username>] [-p <password>] [-r <release_name>]"
    exit
fi

# generate release notes
pushd src/docbkx/scripts/
./generateReleaseNotes.py $@
popd

# generate documentation using maven
mvn clean pre-site || exit 1

if [ -d "target/docbkx/" ]; then

    # delete previously generated doc folders
    rm -rf ./pdf ./html

    # copy generated doc folders
    mv target/docbkx/* ./

    # delete generated doc folders
    rm -rf ./target
fi
