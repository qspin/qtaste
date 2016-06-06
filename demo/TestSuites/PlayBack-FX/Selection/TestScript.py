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
javaguiMI = testAPI.getJavaGUIFX(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

def step1():
    """
    @step      Description of the actions done for this step
    @expected  Description of the expected result
    """

    javaguiMI.selectTabId("TABBED_PANE", testData.getValue("TAB_ID"))
    subtitler.setSubtitle(testData.getValue("COMMENT"))
    component = testData.getValue("COMPONENT_NAME")
    value = testData.getIntValue("INDEX")
    if value != -1:
        try:
            javaguiMI.selectIndex(component, value)
        except:
            testAPI.stopTest(Status.FAIL, "Fail to select index " + testData.getValue("INDEX") + " in " + component + "'")
    else:
        value = testData.getValue("VALUE")
        try:
            javaguiMI.selectValue(component, value)
        except:
            testAPI.stopTest(Status.FAIL, "Fail to select value '" + value + "' in " + component + "'")
        actualSelection = javaguiMI.getSelectedValue(component)
        if value != actualSelection:
            testAPI.stopTest(Status.FAIL, "Expected to see '" + value + "' selected in '" + component + "' but got '" + actualSelection + "'")

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
