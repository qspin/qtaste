#!/bin/bash

source "$(dirname "$0")/common.sh"

java -Xms64m -Xmx512m -cp $QTASTE_ROOT/plugins/*:$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar:testapi/target/qtaste-testapi-deploy.jar com.qspin.qtaste.util.GenerateTestSuiteDoc 2>&1 "$@"
