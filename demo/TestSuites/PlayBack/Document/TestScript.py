##
# Playback/Document test.
# <p>
# Description of the test.
#
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
	
	text = testData.getValue("VALUE")
	component = testData.getValue("COMPONENT_NAME")
	try:
	    javaguiMI.setText(component, text) != testData.getBooleanValue("COMMAND_RESULT")
	except:
		testAPI.stopTest(Status.FAIL, "Fail to insert " + text)
	
	time.sleep(1)
	
	if testData.getBooleanValue("COMMAND_RESULT"):
		result = javaguiMI.getText(component)
		text = testData.getValue("FIELD_CONTENT")
		if result != text:
			testAPI.stopTest(Status.FAIL, "Fail to insert " + text + " but insert '" + result + "'")
			

def reset():
	"""
	@step      Reset component state
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("COMPONENT_NAME")
	value = ""
	javaguiMI.setText(component, value)

doStep(step1)
doStep(reset)
