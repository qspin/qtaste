#!/bin/bash

cd testapi

# build using maven
mvn clean install assembly:single

cd ..