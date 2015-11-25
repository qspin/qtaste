# encoding= utf-8

##
# Playback/Choose test.
# <p>
# Example of test to control and check a java GUI.
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

def step1():
    """
    @step      Description of the actions&nbsp;done for this step
    @expected  Description of the expected result
    """

    javaguiMI.selectTabId("TABBED_PANE", "CHOOSE_PANEL")
    subtitler.setSubtitle(testData.getValue("COMMENT"))

    component = testData.getValue("COMPONENT_NAME")
    value = testData.getBooleanValue("VALUE")

    result = True
    try:
        javaguiMI.selectComponent(component, value)
        #javaguiMI.clickOnButton("TOGGLE_BUTTON")
    except:
        result = False

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
