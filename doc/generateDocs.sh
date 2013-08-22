#!/bin/bash

# usage: generateDocs.sh [-u <username>] [-p <password>]
# optional args: [-u] Github username
#                [-p] Password

# generate release notes
pushd src/docbkx/scripts/
./generateReleaseNotes.py $@ || exit 1
popd

# generate documentation using maven
mvn pre-site

if [ -d "target/docbkx/" ]; then

    # delete previously generated doc folders
    rm -rf ./pdf ./html

    # copy generated doc folders
    mv target/docbkx/* ./

    # delete generated doc folders
    rm -rf ./target
fi
