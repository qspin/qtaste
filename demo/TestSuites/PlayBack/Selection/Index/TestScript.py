# encoding= utf-8

##
# Playback/Select based on an index.
# <p>
# Select a value in a combo box or in a list. The selection is based on the index, the position of the item in the component data container.
# The validation is done through the getSelectedIndex method which has to return the value used for the selection.
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

def step1():
    """
    @step      Select the pane and select a value in the component.
    @expected  The getSelectedIndex returns the same value as the one used for the selection.
    """

    javaguiMI.selectTabId("TABBED_PANE", testData.getValue("TAB_ID"))
    subtitler.setSubtitle(testData.getValue("COMMENT"))
    component = testData.getValue("COMPONENT_NAME")
    value = testData.getIntValue("INDEX")
    try:
        javaguiMI.selectIndex(component, value)
        if value != javaguiMI.getSelectedIndex(component):
            testAPI.stopTest(Status.FAIL, "Fail to select index " + testData.getValue("INDEX") + " in " + component + "'")
    except:
        testAPI.stopTest(Status.FAIL, "Fail to select index " + testData.getValue("INDEX") + " in " + component + "'")
    time.sleep(1)

def reset():
    """
    @step      Reset component state
    @expected  Description of the expected result
    """
    component = testData.getValue("COMPONENT_NAME")
    javaguiMI.selectIndex(component, -1)
    
doStep(step1)
doStep(reset)
