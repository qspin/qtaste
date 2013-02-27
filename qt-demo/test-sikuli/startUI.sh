#!/bin/bash
# bin subdirectory of qtaste root must be in path
export PATH=$PATH:../../bin
export QTASTE_CLASSPATH=$QTASTE_CLASSPATH:/usr/share/java/sikuli-script.jar
qtasteUI_start.sh
