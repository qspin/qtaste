##
# TabbedPaneSelection.
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getPlayback()

def testChangeTab():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("TAB_COMPONENT_NAME")
	index = testData.getIntValue("TAB_IDX")
	result = javaguiMI.selectTab(component, index)
	
	time.sleep(1)
	
	if result != 1 :
		testAPI.stopTest(Status.FAIL, "Fail to set tab " + testData.getValue("TAB_IDX") + " for " + component )
	pass

def reset():
	"""
	@step      Unselect tab
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("TAB_COMPONENT_NAME")
	index = -1
	javaguiMI.selectTab(component, index)
	
	time.sleep(1)
	pass
	
changeTab=[(1, testChangeTab)]

doStep(testChangeTab)
doStep(reset)
