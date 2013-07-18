@echo off

rem  # Relases a new qtaste version by executing the following steps:
rem  # 1) Check that there are no uncommitted changes in the sources
rem  # 2) Check that there are no SNAPSHOT dependencies
rem  # 3) Change the version in the POMs from x-SNAPSHOT to a new version (you will be prompted for the versions to use)
rem  # 4) Transform the SCM information in the POM to include the final destination of the tag
rem  # 5) Run the project tests against the modified POMs to confirm everything is in working order
rem  # 6) Commit the modified POMs
rem  # 7) Tag the code in the SCM with a version name (this will be prompted for)
rem  # 8) Modify the version in the POMs to a new value y-SNAPSHOT (these values will also be prompted for)
rem  # 9) Commit the modified POMs
rem  # --------------------------------------------------------------------------------------------------------------------
rem  # usage: releaseAll.sh [-snapshot]
rem  # optional arg: [-snapshot] only perform the above step 8) - SNAPSHOT version iterates without prompt -


if [%1] == [-help] (
    echo usage: releaseAll.cmd [-snapshot]
) else if [%1] == [-snapshot] (
    call mvn release:clean release:update-versions -P qtaste-all-modules-release
) else (
    call mvn release:clean release:prepare -P qtaste-all-modules-release,qtaste-skip-for-release
)
