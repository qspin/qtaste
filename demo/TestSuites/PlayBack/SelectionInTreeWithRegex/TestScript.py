# encoding= utf-8

##
# Playback/SelectionInTreeWithRegex test
# <p>
# Test the node selection in a tree using regex.
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
component 	  = testData.getValue("COMPONENT_NAME")
value 		  = testData.getValue("VALUE")
expectedValue = testData.getValue("EXPECTED_VALUE")

def reset():
    """
    @step      clear the node selection
    @expected no node should be selected
    """
    javaguiMI.clearNodeSelection(component)

def step1():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """
    javaguiMI.selectNodeRe(component, value, "!")
    actualSelection = javaguiMI.getSelectedNode(component, "!")
    
    if actualSelection is None :
        testAPI.stopTest(Status.FAIL, "Unable to get the selected node. No node is selected.")
    elif actualSelection != expectedValue:
        testAPI.stopTest(Status.FAIL, "Expected to see value '" + expectedValue + "' selected in " + component + "' but got '" + actualSelection + "'")
    
    time.sleep(1)

doStep(reset)
doStep(step1)
