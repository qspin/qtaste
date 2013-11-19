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
	occurence = testData.getIntValue("OCCURENCE")
	columnName = testData.getValue("COLUMN_NAME")
	columnValue = testData.getValue("COLUMN_VALUE")
	
	if javaguiMI.countTableRows(component, columnName, columnValue) < occurence:
		testAPI.stopTest(Status.FAIL, "Not enough occurences in the table")

	if occurence == -1:
		javaguiMI.selectInTable(component, columnName, columnValue)
	else:
		javaguiMI.selectInTable(component, columnName, columnValue, occurence)


doStep(step1)
