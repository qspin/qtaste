# encoding= utf-8

##
# PlayBack/TabbedPaneSelection.
# <p>
# Test tabbed pane selection.
#
# @data COMMENT [String] Comment for the test
# @data JAVAGUI_INSTANCE_NAME [String] instance of the JavaGUI to control
# @data TAB_COMPONENT_NAME [String] The ID of the TAB selector to be controlled
# @data TAB_IDX [Int] The indice of the tab to be selected (-1 means unselect tab)
# @data TAB_TITLE [String] The title of the tab to select
# @data TAB_ID [String] The id of the tab to select
##

from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

component = testData.getValue("TAB_COMPONENT_NAME")

def testChangeTabByIndex():
    """
    @step      select a new tab by index
    @expected  the tab with a the specified index is selected
    """
    subtitler.setSubtitle("Select the tab by index", 1)
    index = testData.getIntValue("TAB_IDX")
    javaguiMI.selectTab(component, index)
    selectedIndex = javaguiMI.getSelectedTabIndex(component)

    if (index != selectedIndex):
        testAPI.stopTest(Status.FAIL, "Expected selected tab index : '" + index + "' but got : '" + selectedIndex + "'")

    time.sleep(1)

def testChangeTabByTitle():
    """
    @step      select a new tab by title
    @expected  the tab with a the specified title is selected
    """
    subtitler.setSubtitle("Select the tab by title", 1)
    title = testData.getValue("TAB_TITLE")

    if title != '':
        javaguiMI.selectTabTitled(component, title)
        selectedTitle = javaguiMI.getSelectedTabTitle(component)

        if (title != selectedTitle):
            testAPI.stopTest(Status.FAIL, "Expected selected tab title : '" + title + "' but got : '" + selectedTitle + "'")

    time.sleep(1)

def testChangeTabById():
    """
    @step      select a new tab by ID
    @expected  the tab with a the specified ID is selected
    """
    subtitler.setSubtitle("Select the tab by id", 1)
    id = testData.getValue("TAB_ID")

    if id != '':
        javaguiMI.selectTabId(component, id)

        selectedId = javaguiMI.getSelectedTabId(component)

        if (id != selectedId):
            testAPI.stopTest(Status.FAIL, "Expected selected tab title : '" + id + "' but got : '" + selectedId + "'")

    time.sleep(1)

def unselectTab():
    """
    @step      Unselect tab
    @expected  No tab is selected
    """
    subtitler.setSubtitle("Unselect the tab", 1)
    index = -1
    javaguiMI.selectTab(component, index)
    selectedIndex = javaguiMI.getSelectedTabIndex(component)

    if (index != selectedIndex):
        testAPI.stopTest(Status.FAIL, "Expected selected tab index : '" + index + "' but got : '" + selectedIndex + "'")

    time.sleep(1)

changeTabByTitle=[(1, testChangeTabByTitle)]
changeTabById=[(1, testChangeTabById)]

doStep(unselectTab)
doStep(testChangeTabByIndex)
doStep(unselectTab)
doStep(testChangeTabByTitle)
doStep(unselectTab)
doStep(testChangeTabById)
doStep(unselectTab)
