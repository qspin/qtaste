#!/bin/bash
export QTASTE_ROOT=`dirname $0`/..
export PATH=$PATH:$QTASTE_ROOT/lib
java -Xms64m -Xmx512m -cp $QTASTE_ROOT/plugins/*:$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar com.qspin.qtaste.util.GenerateTestSuitesDoc 2>&1 $*
