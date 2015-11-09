# encoding= utf-8

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

def step1():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """

    javaguiMI.selectTabId("TABBED_PANE", testData.getValue("TAB_ID"))
    subtitler.setSubtitle(testData.getValue("COMMENT"))
    component = testData.getValue("COMPONENT_NAME")

    if len(testData.getValue("VALUE")) > 0:
        test_with_string_value(component)
    elif len(testData.getValue("INT_VALUE")) > 0:
        test_with_int_value(component)
    elif len(testData.getValue("BOOLEAN_VALUE")) > 0:
        test_with_boolean_value(component)
    else:
        testAPI.stopTest(Status.FAIL, "No value to select....")

def test_with_int_value(componentName):
    value = testData.getIntValue("INT_VALUE")
    javaguiMI.selectValue(componentName, value)
    if javaguiMI.getSelectedValue(componentName) != value:
        testAPI.stopTest(Status.FAIL, "Fail to select the value '" + str(value) + "' in " + componentName + "'")

def test_with_boolean_value(componentName):
    value = testData.getBooleanValue("BOOLEAN_VALUE")
    javaguiMI.selectComponent(componentName, value)
    if (javaguiMI.getSelectedValue(componentName) == "true" and value) or \
       (javaguiMI.getSelectedValue(componentName) == "false" and not value):
        pass
    else:
        testAPI.stopTest(Status.FAIL, "Fail to change the selection state of '" + componentName + "' to " + str(value))

def test_with_string_value(componentName):
    value = testData.getValue("VALUE")

    javaguiMI.selectValue(componentName, value)
    if javaguiMI.getSelectedValue(componentName) != value:
        testAPI.stopTest(Status.FAIL, "Fail to change the selection of '" + componentName + "' to " + str(value))


def checkList():
    """
    @step      Check the content of the list
    @expected  Description of the expected result
    """
    actualList = javaguiMI.getListContent("LIST")
    if len(actualList) != 5:
        testAPI.stopTest(Status.FAIL, "Expected to get 5 elements in the list but got %d" %(len(actualList)))
    expectedList = ["Mickey Mouse (70)", "Tintin Milou (40)", "Louis XVII (30)", "Elisabeth II (80)", "Milou Tintin (40)"]
    i = 0
    for e in expectedList:
        if actualList[i] != expectedList[i]:
            testAPI.stopTest(Status.FAIL, "Expected to get %s at indice %d in the list but got %s" %(expectedList[i], i, actualList[i]))
        i = i + 1

def reset():
    """
    @step      Reset component state
    @expected  Description of the expected result
    """

    component = testData.getValue("COMPONENT_NAME")

doStep(step1)
doStep(checkList)
doStep(reset)
