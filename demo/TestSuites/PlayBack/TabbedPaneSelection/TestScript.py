##
# PlayBack/TabbedPaneSelection.
# <p>
# Description of the test.
#
# @data COMMENT [String] Comment for the test
# @data TAB_COMPONENT_NAME [String] The ID of the TAB selector to be controlled
# @data TAB_IDX [Int] The indice of the tab to be selected
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

def testChangeTab():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	
	subtitler.setSubtitle("Select the tab", 1)
	component = testData.getValue("TAB_COMPONENT_NAME")
	title = testData.getValue("TAB_TITLE")
	if title is None or len(title) == 0:
		index = testData.getIntValue("TAB_IDX")
		javaguiMI.selectTab(component, index)
	else:
		javaguiMI.selectTabTitled(component, title)

def reset():
	"""
	@step      Unselect tab
	@expected  Description of the expected result
	"""
	
	component = testData.getValue("TAB_COMPONENT_NAME")
	index = -1
	javaguiMI.selectTab(component, index)
	time.sleep(1)
	
changeTab=[(1, testChangeTab)]

doStep(testChangeTab)
time.sleep(1)
doStep(reset)
