#!/bin/bash
# special jython launcher for Komodo
# remove -u argument which is not supported by jython

ARGS=`echo $* | sed 's/-u //'`
java -Dpython.path=../lib/jython.jar:../lib/lib -cp ../build/jython-engine.jar:../lib/jython.jar:$CLASSPATH org.python.util.jython $ARGS