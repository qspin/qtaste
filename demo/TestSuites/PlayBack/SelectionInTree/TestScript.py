##
# Playback/Selection test.
# <p>
# Description of the test.
#
##

from qtaste import *


import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID="PlaybackApp")

importTestScript("TabbedPaneSelection")

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	doSubSteps(TabbedPaneSelection.changeTab)
	
	component = testData.getValue("COMPONENT_NAME")
	value = testData.getValue("VALUE")
# 	try:
	javaguiMI.selectNode(component, value, "\!")
# 	except:
# 		testAPI.stopTest(Status.FAIL, "Fail to select value '" + value + "' in " + component + "'")
			
	time.sleep(1)


doStep(step1)
