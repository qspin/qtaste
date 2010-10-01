#!/bin/bash

# build using maven
mvn clean install assembly:single

# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null