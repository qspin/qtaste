# coding=utf-8

##
# Playback/GuiStructureAnalyser test.
# <p>
# Open a Java GUI App and print the name of all the components to a XML file.
#
##

from qtaste import *
import os

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

outputXmlFile = testData.getValue("XML_FILE_PATH")

def printGuiStructureToFile():
    """
    @step      Open a GUI and print the name of all components to XML file.
    @expected  The XML file with the structure of the selected GUI is created.
    """

    javaguiMI.selectTabId("TABBED_PANE", "COMPLEX_JAVA_COMP")
    javaguiMI.analyzeStructure(outputXmlFile)
    if not (os.path.isfile(outputXmlFile)):
        testAPI.stopTest(Status.FAIL, "No XML file was created at location [" + outputXmlFile + "]")

doStep(printGuiStructureToFile)
