# encoding= utf-8

##
# Playback/FileChooser test.
# <p>
# Open a file with a JFileChooser and check the selected file.
#
##

from qtaste import *


import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

def openFileChooser():
    """
    @step      Open the JFileChooser by clicking on the open button.
    @expected  The JFileChooser is displayed (not tested).
    """

    javaguiMI.selectTabId("TABBED_PANE", "COMPLEX_JAVA_COMP")
    javaguiMI.clickOnButton("OPEN_FILECHOOSER")
    time.sleep(1)

def selectFile():
    """
    @step Base on test data, set the file to select and click on the "Open" button.
    @expected The result text field contains the file name.
    """
    fileName = testData.getValue("FILENAME")
    component = testData.getValue("COMPONENT_NAME")
    javaguiMI.selectFileThroughFileChooser(component, fileName)
    value = javaguiMI.getText("FILECHOOSER_RESULT")
    time.sleep(1)
    if ( value == fileName):
        pass
    else:
        testAPI.stopTest(Status.FAIL, "The expected value is " + fileName + " but the current is : " + value)

def reset():
    """
    @step      Reset component state
    @expected  The result text field is empty.
    """

    component = "FILECHOOSER_RESULT"
    value = ""
    javaguiMI.setText(component, value)

doStep(openFileChooser)
doStep(selectFile)
doStep(reset)
