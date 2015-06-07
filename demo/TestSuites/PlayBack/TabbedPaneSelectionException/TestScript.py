# encoding= utf-8

##
# PlayBack/TabbedPaneSelectionException.
# <p>
# Test error cases of the tab selection.
#
# @data COMMENT [String] Comment for the test
# @data TAB_COMPONENT_NAME [String] The ID of the TAB selector to be controlled
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

component  = testData.getValue("TAB_COMPONENT_NAME")
testCaseId = testData.getValue("TEST_CASE_ID")

def reset():
    """
    @step      Unselect tab
    @expected  no tab is selected
    """
    subtitler.setSubtitle("Unselect the tab", 1)
    javaguiMI.selectTab(component, -1)
    time.sleep(1)
    
def test_TabByIndex_outOfLowBound():
    """
    @step      Select a tab by index with an index lower than the lower bound
    @expected  a QTasteTestFailException with a message
    """
    wrongIndex = -2
    expectedMessage = 'Tab index ' + str(wrongIndex) + ' out of bounds.'

    subtitler.setSubtitle("Select a tab with the index " + str(wrongIndex), 1)

    try:
        javaguiMI.selectTab(component, wrongIndex)
    except QTasteTestFailException, e:
        if e.message != expectedMessage:
            testAPI.stopTest(Status.FAIL, "Expected to get the exception message'" + expectedMessage + "' but got '" + e.message + "'")
    except e:
        testAPI.stopTest(Status.FAIL, "Unexpected exception : '" + repr(e) + "'")

    time.sleep(1)

def test_TabByIndex_outOfHighBound():
    """
    @step      Select a tab by index with an index upper than the lower bound
    @expected  a QTasteTestFailException with a message
    """
    wrongIndex = 1234
    expectedMessage = 'Tab index ' + str(wrongIndex) + ' out of bounds.'

    subtitler.setSubtitle("Select a tab with the index " + str(wrongIndex), 1)

    try:
        javaguiMI.selectTab(component, wrongIndex)
    except QTasteTestFailException, e:
        if e.message != expectedMessage:
            testAPI.stopTest(Status.FAIL, "Expected to get the exception message'" + expectedMessage + "' but got '" + e.message + "'")
    except e:
        testAPI.stopTest(Status.FAIL, "Unexpected exception : '" + repr(e) + "'")

    time.sleep(1)
    
def test_TabByTitle_wrongTitle():
    """
    @step      Select a tab by title with a wrong title
    @expected  a QTasteTestFailException with a message
    """
    wrongTitle = 'TOTOTUTU'
    expectedMessage = "Unable to find tab titled '" + wrongTitle + "'"

    subtitler.setSubtitle("Select a tab with the title '" + wrongTitle + "'", 1)

    try:
        javaguiMI.selectTabTitled(component, wrongTitle)
    except QTasteTestFailException, e:
        if e.message != expectedMessage:
            testAPI.stopTest(Status.FAIL, "Expected to get the exception message'" + expectedMessage + "' but got '" + e.message + "'")
    except e:
        testAPI.stopTest(Status.FAIL, "Unexpected exception : '" + repr(e) + "'")

    time.sleep(1)
    
def test_TabById_wrongId():
    """
    @step      Select a tab by ID with a wrong ID
    @expected  a QTasteTestFailException with a message
    """
    wrongID = 'A12'
    expectedMessage = "Unable to find the component named '" + wrongID + "'"

    subtitler.setSubtitle("Select a tab with the ID '" + wrongID + "'", 1)

    try:
        javaguiMI.selectTabId(component, wrongID)
    except QTasteTestFailException, e:
        if e.message != expectedMessage:
            testAPI.stopTest(Status.FAIL, "Expected to get the exception message'" + expectedMessage + "' but got '" + e.message + "'")
    except e:
        testAPI.stopTest(Status.FAIL, "Unexpected exception : '" + repr(e) + "'")

    time.sleep(1)

doStep(reset)

if testCaseId == "1":
    doStep(test_TabByIndex_outOfLowBound)

if testCaseId == "2":
    doStep(test_TabByIndex_outOfHighBound)

if testCaseId == "3":
    doStep(test_TabByTitle_wrongTitle)

if testCaseId == "4":
    doStep(test_TabById_wrongId)
	
time.sleep(1)
