#!/bin/bash
# Execute a remote command using rsh and check its success which must be indicated by "SUCCESS" as last output line
# Usage: rsh_with_result.sh <rsh arguments>

TEMPFILE=`mktemp` || exit 1
rsh $* | tee $TEMPFILE || (rm $TEMPFILE; exit 1)
RESULT=`tail -n 1 $TEMPFILE`
rm $TEMPFILE
echo $RESULT > result.txt
if [ "$RESULT" = "SUCCESS" ] 
then 
	exit 0 
else	
	exit -1
fi
