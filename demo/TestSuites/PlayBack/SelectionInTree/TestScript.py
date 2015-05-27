# encoding= utf-8

##
# Playback/Selection test.
# <p>
# Description of the test.
#
# @SHOULD_FAIL [Boolean] : if true, the qtaste exception will be catch and the test will pass
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
    value = testData.getValue("VALUE")
    
    javaguiMI.clearNodeSelection(component)
    javaguiMI.selectNode(component, value, ".")
    actualSelection = javaguiMI.getSelectedNode(component, ".")
    
    if actualSelection is None :
        if not testData.getBooleanValue("SHOULD_FAIL"):
            testAPI.stopTest(Status.FAIL, "Unable to get the selected node. No node is selected.")
    elif actualSelection != value:
        testAPI.stopTest(Status.FAIL, "Expected to see value '" + value + "' selected in " + component + "' but got '" + actualSelection + "'")
    
    time.sleep(1)

doStep(step1)
