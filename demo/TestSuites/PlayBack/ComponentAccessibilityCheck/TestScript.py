##
# ComponentAccessibilityCheck.
# <p>
# Ensure a component is accessible or not before executing an action on it.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *

importTestScript("PopupControl")

javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

def CheckAccessibility():
	"""
	@step      Try to click on the START button
	@expected  An exception is thrown cause a popup is displayed and it's not possible to click on the button
	"""
	try:
		javaguiMI.clickOnButton("START_BUTTON")
		testAPI.stopTest(Status.FAIL, "the component should not be accessible!")
	except:
		javaguiMI.clickOnPopupButton("Cancel")
		pass

doStep(PopupControl.displayFirstPopup)
doStep(CheckAccessibility)
