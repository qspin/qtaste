##
# [$TEST_NAME].
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *


import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getPlayback()

importTestScript("TabbedPaneSelection")

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	doSubSteps(TabbedPaneSelection.changeTab)
	
	component = testData.getValue("COMPONENT_NAME")
	value = testData.getIntValue("INDEX")
	if value != -1:
		if javaguiMI.selectIndex(component, value) == False:
			testAPI.stopTest(Status.FAIL, "Fail to select index " + testData.getValue("INDEX") + " in " + component + "'")
	else:
		value = testData.getValue("VALUE")
		if javaguiMI.selectValue(component, value) == False:
			testAPI.stopTest(Status.FAIL, "Fail to select value '" + value + "' in " + component + "'")
			
	time.sleep(1)

def reset():
	"""
	@step      Reset component state
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("COMPONENT_NAME")

doStep(step1)
doStep(reset)
