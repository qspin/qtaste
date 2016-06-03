# coding=utf-8

##
# Playback/Document test.
# <p>
# Description of the test.
#
##

from qtaste import *


import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

def step1():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """

    javaguiMI.selectTabId("TABBED_PANE", "DOCUMENT_PANEL")
    subtitler.setSubtitle(testData.getValue("COMMENT") + " the value '" + testData.getValue("VALUE") + "'")
    time.sleep(1)
    text = testData.getValue("VALUE")
    component = testData.getValue("COMPONENT_NAME")
    try:
        javaguiMI.setText(component, text) != testData.getBooleanValue("COMMAND_RESULT")
    except:
        testAPI.stopTest(Status.FAIL, "Fail to insert " + text)

    time.sleep(1)

    if testData.getBooleanValue("COMMAND_RESULT"):
        result = javaguiMI.getText(component)
        text = testData.getValue("FIELD_CONTENT")
        if result != text:
            testAPI.stopTest(Status.FAIL, "Fail to insert " + text + " but insert '" + result + "'")


def reset():
    """
    @step      Reset component state
    @expected  Description of the expected result
    """

    component = testData.getValue("COMPONENT_NAME")
    value = ""
    javaguiMI.setText(component, value)

doStep(step1)
doStep(reset)
