# encoding= utf-8

##
# PlayBack/TabbedPaneSelectionException.
# <p>
# Test error cases of the tab selection.
#
# @data COMMENT [String] Comment for the test
# @data SELECTION_METHOD [String] The selection method
# @data SELECTION_VALUE [String] The value used to select the tab
# @data EXPECTED_MSG [String] The expected exception message
# @data TAB_COMPONENT_NAME [String] The ID of the TAB selector to be controlled
# @data JAVAGUI_INSTANCE_NAME [String] The name of the Java GUI instance to control
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

component  = testData.getValue("TAB_COMPONENT_NAME")

def unselectTab():
    """
    @step      Unselect tab
    @expected  no tab is selected
    """
    subtitler.setSubtitle("Unselect the tab", 1)
    javaguiMI.selectTab(component, -1)
    selectedIndex = javaguiMI.getSelectedTabIndex(component)

    if (selectedIndex != -1):
        testAPI.stopTest(Status.FAIL, "Expected selected tab index : '-1' but got : '" + selectedIndex + "'")

    time.sleep(1)
    
def step1():
    """
    @step      Select a tab using the selection method and selection value defined in test data
    @expected  a QTasteTestFailException with a message
    """

    selectionMethod = testData.getValue("SELECTION_METHOD")
    selectionValue  = testData.getValue("SELECTION_VALUE")
    expectedMessage = testData.getValue("EXPECTED_MSG")

    subtitler.setSubtitle("Select a tab with the " + selectionMethod + " equals to '" + selectionValue + "'", 1)
    time.sleep(1)

    try:
        if selectionMethod == "index":
            javaguiMI.selectTab(component, testData.getIntValue("SELECTION_VALUE"))
        elif selectionMethod == "title":
            javaguiMI.selectTabTitled(component, selectionValue)
        elif selectionMethod == "id":    
            javaguiMI.selectTabId(component, selectionValue)
        else:
            testAPI.stopTest(Status.FAIL, "Invalid selection method (check your test data) !")

        testAPI.stopTest(Status.FAIL, "No exception")

    except QTasteTestFailException, e:
        if e.message != expectedMessage:
            testAPI.stopTest(Status.FAIL, "Expected to get the exception message'" + expectedMessage + "' but got '" + e.message + "'")
    except:
        testAPI.stopTest(Status.FAIL, "Unexpected exception")


doStep(unselectTab)
doStep(step1)
