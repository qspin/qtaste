#!/bin/bash

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
