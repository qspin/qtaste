#!/bin/bash
# Execute a remote command using rexec and check its success which must be indicated by "SUCCESS" as last output line
# Usage: rexec_with_result.sh <rexec arguments>

TEMPFILE=`mktemp` || exit 1
rexec $* | tee $TEMPFILE || (rm $TEMPFILE; exit 1)
RESULT=`tail -n 1 $TEMPFILE`
rm $TEMPFILE
if [ "$RESULT" = "SUCCESS" ]; then 
	exit 0 
else	
	exit -1
fi
