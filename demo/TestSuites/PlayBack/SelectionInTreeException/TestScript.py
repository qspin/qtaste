# encoding= utf-8

##
# Playback/SelectionInTreeException test
# <p>
# Test error cases for the node selection in a tree.
#
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

subtitler = testAPI.getSubtitler()
subtitler.setSubtitle(testData.getValue("COMMENT"))

# select the tab with tree components
javaguiMI.selectTabTitled("TABBED_PANE", "TREE_LIST_PANEL")

# get test data
component   = testData.getValue("COMPONENT_NAME")
value       = testData.getValue("VALUE")
separator   = testData.getValue("SEPARATOR")
expectedMsg = testData.getValue("EXPECTED_MESSAGE")

def reset():
    """
    @step      clear the node selection
    @expected no node should be selected
    """
    javaguiMI.clearNodeSelection(component)

def step1():
    """
    @step      select a node according to test data
    @expected  a QTasteTestFailException with a message
    """
    exception = False

    try:
        javaguiMI.selectNode(component, value, separator)
    except QTasteTestFailException, e:
        exception = True
        if e.message != expectedMsg:
            testAPI.stopTest(Status.FAIL, "Expected message : '" + expectedMsg + "' but got : '" + e.message + "'")
    except e:
        exception = True
        testAPI.stopTest(Status.FAIL, "Unexpected exception : " + repr(e))
    
    if not exception:
        testAPI.stopTest(Status.FAIL, "No exception")

doStep(reset)
doStep(step1)
time.sleep(1)
