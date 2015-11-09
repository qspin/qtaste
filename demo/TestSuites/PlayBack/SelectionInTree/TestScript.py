# encoding= utf-8

##
# Playback/SelectionInTree test
# <p>
# Test the node selection in a tree.
##

from qtaste import *

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

subtitler = testAPI.getSubtitler()
subtitler.setSubtitle(testData.getValue("COMMENT"))

# select the tab with tree components
javaguiMI.selectTabTitled("TABBED_PANE", "TREE_LIST_PANEL")

# get test data
component = testData.getValue("COMPONENT_NAME")
value     = testData.getValue("VALUE")

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
    javaguiMI.selectNode(component, value, ".")
    actualSelection = javaguiMI.getSelectedNode(component, ".")

    if actualSelection is None :
        testAPI.stopTest(Status.FAIL, "Unable to get the selected node. No node is selected.")
    elif actualSelection != value:
        testAPI.stopTest(Status.FAIL, "Expected to see value '" + value + "' selected in " + component + "' but got '" + actualSelection + "'")

doStep(reset)
doStep(step1)
