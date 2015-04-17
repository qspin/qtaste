#!/bin/bash
#
# Start QTaste with its GUI with the following settings :
# - initial memory allocation pool size (Xms) : 64M
# - maximum memory allocation pool size (Xmx) : 1024M
#

source "$(dirname "$0")/common.sh"

java -Xms64m -Xmx1024m -cp $QTASTE_CLASSPATH:"$QTASTE_ROOT/plugins/*":"$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar":testapi/target/qtaste-testapi-deploy.jar com.qspin.qtaste.ui.MainPanel $*
