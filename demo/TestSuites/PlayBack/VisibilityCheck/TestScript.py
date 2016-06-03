# coding=utf-8

##
# VisibilityCheck.
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
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
    javaguiMI.selectTabTitled("TABBED_PANE", "MISCELLANEOUS")
    subtitler.setSubtitle("Click on the button to make the component invisible")
    time.sleep(1)
    javaguiMI.clickOnButton("VISIBILITY_BUTTON")
    time.sleep(1)
    if javaguiMI.isVisible("VISIBILITY_TEXT") != False:
        testAPI.stop(Status.FAIL, "The component should not be visible")

    try:
        subtitler.setSubtitle("Try to insert a value in the invible text field", 10)
        javaguiMI.setText("VISIBILITY_TEXT", "pas bien")
        testAPI.stop(Status.FAIL, "The component should not be visible and the setText() should failed")
    except :
        javaguiMI.clickOnButton("VISIBILITY_BUTTON")

doStep(step1)
