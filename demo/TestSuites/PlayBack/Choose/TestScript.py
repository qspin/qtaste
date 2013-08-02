##
# Playback/Choose test.
# <p>
# Example of test to control and check a java GUI.
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

importTestScript("TabbedPaneSelection")

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	doSubSteps(TabbedPaneSelection.changeTab)
	
	component = testData.getValue("COMPONENT_NAME")
	value = testData.getBooleanValue("VALUE")
	
	result = True
	try:		
		javaguiMI.selectComponent(component, value)
	except:
		result = False
	
	time.sleep(1)
	
	if result != value:
		testAPI.stopTest(Status.FAIL, "Fail change selection")
		
def reset():
	"""
	@step      Reset component state
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("COMPONENT_NAME")
	value = testData.getBooleanValue("NOT_VALUE")
	
	javaguiMI.selectComponent(component, value)

doStep(step1)
doStep(reset)
