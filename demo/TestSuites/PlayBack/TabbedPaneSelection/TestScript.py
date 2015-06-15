# encoding= utf-8

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

component = testData.getValue("TAB_COMPONENT_NAME")

def testChangeTabByIndex():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """
    subtitler.setSubtitle("Select the tab by index", 1)
    index = testData.getIntValue("TAB_IDX")
    javaguiMI.selectTab(component, index)

def testChangeTabByTitle():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """
    subtitler.setSubtitle("Select the tab by title", 1)
    title = testData.getValue("TAB_TITLE")

    if title != '':
        javaguiMI.selectTabTitled(component, title)
	
def testChangeTabById():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """
    subtitler.setSubtitle("Select the tab by id", 1)
    id = testData.getValue("TAB_ID")

    if id != '':
        javaguiMI.selectTabId(component, id)

def reset():
    """
    @step      Unselect tab
    @expected  Description of the expected result
    """
    subtitler.setSubtitle("Unselect the tab", 1)
    index = -1
    javaguiMI.selectTab(component, index)
    time.sleep(1)

changeTabByTitle=[(1, testChangeTabByTitle)]
changeTabById=[(1, testChangeTabById)]

doStep(reset)
doStep(testChangeTabByIndex)
time.sleep(1)
doStep(reset)
doStep(testChangeTabByTitle)
time.sleep(1)
doStep(reset)
doStep(testChangeTabById)
time.sleep(1)
doStep(reset)
