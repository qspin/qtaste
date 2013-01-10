#!/bin/bash
java -Dpython.path=../lib/jython.jar:$QTASTE_JYTHON_LIB:../lib/Lib -cp ../build/jython-engine.jar:../lib/jython.jar:$CLASSPATH org.python.util.jython $*