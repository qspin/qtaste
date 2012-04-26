#!/bin/bash
JYTHON_HOME=`dirname $0 | xargs dirname`
java -Dpython.path=$JYTHON_HOME/lib/jython.jar:$QTASTE_JYTHON_LIB:$JYTHON_HOME/lib/Lib -cp $JYTHON_HOME/build/jython-engine.jar:$JYTHON_HOME/lib/jython.jar:$CLASSPATH org.python.util.jython $*