#! /bin/bash

# Relases a new qtaste version by executing the following steps:
# 1) Check that there are no uncommitted changes in the sources
# 2) Check that there are no SNAPSHOT dependencies
# 3) Change the version in the POMs from x-SNAPSHOT to a new version (you will be prompted for the versions to use)
# 4) Transform the SCM information in the POM to include the final destination of the tag
# 5) Run the project tests against the modified POMs to confirm everything is in working order
# 6) Commit the modified POMs
# 7) Tag the code in the SCM with a version name (this will be prompted for)
# 8) Modify the version in the POMs to a new value y-SNAPSHOT (these values will also be prompted for)
# 9) Commit the modified POMs
# --------------------------------------------------------------------------------------------------------------------
# usage: releaseAll.sh [-snapshot]
# optional arg: [-newSnapshot] only perform the above step 8) - SNAPSHOT version iterates without prompt -

# Install kernel 3rd party artifacts
pushd kernel
mvn clean -P qtaste-install-3rd-artifacts || exit 1
popd

if [ "$1" == "-help" ]; then
    echo "Usage: releaseAll.sh [-snapshot] "
    exit
elif [ "$1" == "-newSnapshot" ]; then
    mvn release:clean release:update-versions -P qtaste-all-modules-release || exit 1
elif [ "$1" == "-deploySnapshot" ]; then
	# Generate PGP Signatures With Maven
	mvn clean verify -P qtaste-all-modules-release,qtaste-generate-signature-artifacts || exit 1

	# Deploy Snapshots QTaste  
	mvn clean deploy -P qtaste-all-modules-release
else
    mvn release:clean release:prepare -P qtaste-all-modules-release,qtaste-skip-for-release || exit 1
    
    # Generate PGP Signatures With Maven
	mvn clean verify -P qtaste-all-modules-release,qtaste-generate-signature-artifacts || exit 1
	
    mvn release:perform -P qtaste-all-modules-release || exit 1
fi
