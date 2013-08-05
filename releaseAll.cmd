@echo off

rem  # Requirements:
rem  # - A GPG client must is installed on your command line path.
rem  #   Please download GPG from http://www.gnupg.org/download/, 
rem  #  follow the instructions and install it to your system. 

rem  # Relases a new qtaste version by executing the following steps:
rem  # Prepare for Release:
rem  # 1) Check that there are no uncommitted changes in the sources
rem  # 2) Check that there are no SNAPSHOT dependencies
rem  # 3) Change the version in the POMs from x-SNAPSHOT to a new version (you will be prompted for the versions to use)
rem  # 4) Transform the SCM information in the POM to include the final destination of the tag
rem  # 5) Run the project tests against the modified POMs to confirm everything is in working order
rem  # 6) Commit the modified POMs
rem  # 7) Tag the code in the SCM with a version name (this will be prompted for)
rem  # 8) Modify the version in the POMs to a new value y-SNAPSHOT (these values will also be prompted for)
rem  # 9) Commit the modified POMs
rem  # Perform Release:
rem  # 1) Checkout from the SCM URL (tag prompted in step 7)
rem  # 2) Run the predefined Maven goals to release the project:
rem  #	 -This will stage QTaste (from tag version prompted in step 7) into sonatype, being prepared to be released.
rem  # To finally release QTaste (from stageg to released) follow step 8a) of tutorial:
rem  # https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
rem  # --------------------------------------------------------------------------------------------------------------------
rem  # usage: releaseAll.sh [-snapshot]
rem  # optional arg: [-newSnapshot] only perform the above step 8) -> SNAPSHOT version iterates without prompt <-
rem  #               [-deploySnapshot] Deploy snapshot QTaste artifacts into 
rem  #    repository https://oss.sonatype.org/content/repositories/snapshots 
rem  #
rem  # Note: Only projects owners have rights to deploy QTaste.
rem  #		 When deploying a Snapshot or Release a Passphrase
rem  #       will be promped.

rem Install kernel 3rd party artifacts
pushd kernel
call mvn clean -P qtaste-install-3rd-artifacts
popd

if [%1] == [-help] (
    echo usage: releaseAll.cmd [-snapshot]
) else if [%1] == [-newSnapshot] (
    call mvn release:clean release:update-versions -P qtaste-all-modules-release

) else if [%1] == [-deploySnapshot] (
	rem Generate PGP Signatures With Maven
	call mvn clean verify -P qtaste-all-modules-release,qtaste-generate-signature-artifacts

	rem Deploy Snapshots QTaste  
	call mvn clean deploy -P qtaste-all-modules-release
) else (
    call mvn release:clean release:prepare -P qtaste-all-modules-release,qtaste-skip-for-release
    
    rem Generate PGP Signatures With Maven
	call mvn clean verify -P qtaste-all-modules-release,qtaste-generate-signature-artifacts
	
    call mvn release:perform -P qtaste-all-modules-release || exit 1
)
