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

    javaguiMI.selectTabId("TABBED_PANE", "TABLE_PANEL")
    subtitler.setSubtitle(testData.getValue("COMMENT"))

    component = testData.getValue("COMPONENT_NAME")
    occurence = testData.getIntValue("OCCURENCE")
    columnName = testData.getValue("COLUMN_NAME")
    columnValue = testData.getValue("COLUMN_VALUE")
    try:
        javaguiMI.countTableRows("INVALID_PURPOSE", columnName, columnValue)
        testAPI.stopTest(Status.FAIL, "countTableRows should fails if name of the component is invalid")
    except:
        pass
    try:
        javaguiMI.countTableRows(component, "INVALID_COLUMN", columnValue)
        testAPI.stopTest(Status.FAIL, "countTableRows should fails if column name of the component is invalid")
    except:
        pass
    try:
        javaguiMI.countTableRows(component, columnName, -10)
        testAPI.stopTest(Status.FAIL, "countTableRows should fails if value is invalid")
    except:
        pass

    if javaguiMI.countTableRows(component, columnName, columnValue) < occurence:
        testAPI.stopTest(Status.FAIL, "Not enough occurences in the table")

    if occurence == -1:
        javaguiMI.selectInTable(component, columnName, columnValue)
    else:
        javaguiMI.selectInTable(component, columnName, columnValue, occurence)


doStep(step1)
