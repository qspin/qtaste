##
# Playback test.
# <p>
# Example of test to control and check a java GUI.
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
	value = testData.getBooleanValue("VALUE")
	
	result = javaguiMI.selectComponent(component, value)
	
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
