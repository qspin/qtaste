##
# PopupControl.
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *

import time

javaguiMI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))
subtitler = testAPI.getSubtitler()

importTestScript("TabbedPaneSelection")

def displayFirstPopup():
	"""
	@step      Click on the button to create the first popup
	@expected  a popup exist
	"""
	doSubSteps(TabbedPaneSelection.changeTab)
	subtitler.setSubtitle("Click on the <span style=\"color:red;\">Start</span> button", 1.5)
	javaguiMI.clickOnButton("START_BUTTON")
	time.sleep(1)
	if (javaguiMI.isPopupDisplayed() == False):
		testAPI.stopTest(Status.FAIL, "No popup created.")
	
	pass

def setPopupValue():
	"""
	@step      Set the popup value ant click on OK
	@expected  If the value is numeric another popup is opened ELSE no opened popup.
	"""
	popupValue = testData.getValue("POPUP_VALUE");
	subtitler.setSubtitle("Set the value '" + popupValue +"' and click on the <span style=\"color:red;\">OK</span> button", 1.5)
	javaguiMI.setPopupValue(popupValue)
	javaguiMI.clickOnPopupButton("OK");
	
	time.sleep(1)
	shouldHavePopup = testData.getBooleanValue("IS_POPUP_VALUE_NUMERIC");
	if (javaguiMI.isPopupDisplayed() != shouldHavePopup ):
		if shouldHavePopup:
			testAPI.stopTest(Status.FAIL, "Popup should have been created.")
		else:
			testAPI.stopTest(Status.FAIL, "Popup should not have been created.")
	pass

def valueValidation():
	"""
	@step      Check the displayed message and click on OK
	@expected  the message contains the correct value.
	"""
	popupValue = testData.getValue("POPUP_VALUE");
	expectedMessage = "Are you sure you want to display " + popupValue + " popup(s)?"
	subtitler.setSubtitle("Check the popup message is '" + expectedMessage + "'")
	time.sleep(2)
	currentMessage = javaguiMI.getPopupText()
	
	if ( expectedMessage != currentMessage ):
		testAPI.stopTest(Status.FAIL, "The message is not the expected one. get '" + currentMessage + "' but expects '" + expectedMessage +"'.")
	
	if ( testData.getBooleanValue("CONFIRM") ):
		subtitler.setSubtitle("Click on the <span style=\"color:red;\">Yes</span> button")
		time.sleep(1)
		javaguiMI.clickOnPopupButton("Yes")
	else:
		subtitler.setSubtitle("Click on the <span style=\"color:red;\">No</span> button", 1.5)
		time.sleep(1)
		javaguiMI.clickOnPopupButton("No")
	pass

def countPopupAndClose():
	"""
	@step      Check the number of displayed popup and close them.
	@expected  there is/are the @POPUP_VALUE popup().
	"""
	if ( testData.getBooleanValue("CONFIRM") ):
		popupValue = testData.getIntValue("POPUP_VALUE")
	else:
		popupValue = 0
	time.sleep(1)
	
	subtitler.setSubtitle("Check the number of opened popup")
	time.sleep(1)
	current = len(javaguiMI.getAllPopupText())
	if ( current != popupValue ):
		testAPI.stopTest(Status.FAIL, str(popupValue) + " popup(s) expected but only " + str(current) + " popup(s) displayed!")
	
	i = 0
	while ( i<popupValue):
		subtitler.setSubtitle("Click on the <span style=\"color:red;\">OK</span> button", 1.5)
		javaguiMI.clickOnPopupButton("OK")
		time.sleep(1)
		i += 1
		
	pass
	

doStep(displayFirstPopup)
doStep(setPopupValue)
if (testData.getBooleanValue("IS_POPUP_VALUE_NUMERIC")):
	doStep(valueValidation)
	countPopupAndClose()
	
