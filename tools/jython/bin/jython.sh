#!/bin/bash

QTASTE_ROOT="$(dirname "$0")/../../.."
QTASTE_KERNEL="$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar"
JYTHON_HOME=$QTASTE_ROOT/tools/jython
java -Dpython.path=$QTASTE_KERNEL:$QTASTE_JYTHON_LIB:$JYTHON_HOME/lib/Lib -cp $QTASTE_KERNEL:$CLASSPATH org.python.util.jython "$@"
