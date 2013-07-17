#! /bin/bash

# execute Process
pushd demo
echo "Running All Linux Demo Campaign"
#../bin/qtaste_campaign_start.sh ./TestCampaigns/AllLinuxDemo.xml || exit 1
# ../bin/qtaste_start.sh -testsuite ./TestSuites/Process/ -testbed ./Testbeds/process.xml NOK - xterm need to be installed

../bin/qtaste_start.sh -testsuite ./TestSuites/TestTranslate/ -testbed ./Testbeds/demo_web.xml
../bin/qtaste_start.sh -testsuite ./TestSuites/PlayBack/ -testbed ./Testbeds/playback.xml
popd
