#!/bin/bash

source "$(dirname "$0")/common.sh"

java -Xms64m -Xmx512m -cp $QTASTE_ROOT/plugins/*:$QTASTE_ROOT/kernel/target/qtaste-kernel-deploy.jar com.qspin.qtaste.util.GenerateTestCampaignDoc 2>&1 "$@"
