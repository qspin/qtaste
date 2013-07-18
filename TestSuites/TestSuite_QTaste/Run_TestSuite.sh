#!/bin/bash

TESTSUITE_DIR=`pwd`
cd ../..
bin/qtaste_start.sh -testsuite $TESTSUITE_DIR -testbed Testbeds/enginetest.xml $*
firefox `pwd`/reports/index.html
