#!/bin/bash

#remove previous python compilation classes.
pushd ../tools/jython/lib/Lib/
rm -f *.class
popd

mvn clean -P qtaste-install-3rd-artifacts || exit 1
mvn install -P qtaste-build-kernel-first || exit 1

# restore pom.xml if modified
mv -f pom.xml.bak pom.xml >& /dev/null

exit 0
