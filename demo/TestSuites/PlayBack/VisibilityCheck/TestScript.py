##
# VisibilityCheck.
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *
from qtaste import *

import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJavaGUI(INSTANCE_ID="PlaybackApp")

importTestScript("TabbedPaneSelection")

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	doSubSteps(TabbedPaneSelection.changeTab)
	javaguiMI.clickOnButton("VISIBILITY_BUTTON")
	time.sleep(3)
	javaguiMI.setText("VISIBILITY_TEXT", "pas bien")
	time.sleep(3)
	javaguiMI.clickOnButton("VISIBILITY_BUTTON")
	pass

doStep(step1)
