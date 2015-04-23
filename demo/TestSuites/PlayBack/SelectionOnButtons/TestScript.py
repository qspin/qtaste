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

importTestScript("TabbedPaneSelection")

def step1():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """

    doSubSteps(TabbedPaneSelection.changeTabById)
    subtitler.setSubtitle(testData.getValue("COMMENT"))

    component = testData.getValue("COMPONENT_NAME")
    expectedValue = testData.getValue("EXPECTED_VALUE")

    actualSelection = javaguiMI.getSelectedValue(component)

    if actualSelection != expectedValue:
        testAPI.stopTest(Status.FAIL, "Expected value:" + expectedValue + " got value:" + actualSelection)

    time.sleep(1)

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
