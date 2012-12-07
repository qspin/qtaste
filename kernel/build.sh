#!/bin/bash

#remove previous python compilation classes.
pushd ../tools/jython/lib/Lib/
rm -f *.class
popd

# build using maven
mvn clean install assembly:single

# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
