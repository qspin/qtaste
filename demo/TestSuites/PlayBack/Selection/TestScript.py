##
# Playback/Selection test.
# <p>
# Description of the test.
#
##

from qtaste import *


import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

importTestScript("TabbedPaneSelection")

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	doSubSteps(TabbedPaneSelection.changeTab)
	subtitler.setSubtitle(testData.getValue("COMMENT"))
	component = testData.getValue("COMPONENT_NAME")
	value = testData.getIntValue("INDEX")
	if value != -1:
		try:
			javaguiMI.selectIndex(component, value)
		except:
			testAPI.stopTest(Status.FAIL, "Fail to select index " + testData.getValue("INDEX") + " in " + component + "'")
	else:
		value = testData.getValue("VALUE")
		try:
			javaguiMI.selectValue(component, value)
		except:
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
