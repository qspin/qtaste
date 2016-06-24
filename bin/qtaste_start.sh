#!/bin/bash
#
# Start QTaste in console mode with the following settings :
# - initial memory allocation pool size (Xms) : 64M
# - maximum memory allocation pool size (Xmx) : 512M
#

source "$(dirname "$0")/common.sh"

java -Xms64m -Xmx512m -cp "$QTASTE_CLASSPATH:$QTASTE_ROOT/plugins/*:$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar:testapi/target/qtaste-testapi-deploy.jar" com.qspin.qtaste.kernel.engine.TestEngine 2>&1 "$@"
exit $?
