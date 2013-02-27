##
# QtDemo/Choose test.
# <p>
# Example of test to control and check a Qt GUI.
##

from qtaste import *
from org.sikuli.script import *
from time import sleep

def test_checkbox():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	myApp = App("qtapp")
	myApp.open("/home/remy/QSpin/workspace/qtaste_svn/branches/qt-demo/qt-demo/qtapp/bin/qtapp")
	sleep(2)
	myApp.focus()
	sleep(1)
	
	myApp.window().click("res/Choosepanel.png")
	myApp.window().click("res/radio.png")
	
	if not myApp.window().exists("res/1361794353635.png"):
		testAPI.stopTest(Status.FAIL, "Fail to check the checkbox")

	myApp.close()

def test_radio():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""

	myApp = App("qtapp")
	myApp.open("/home/remy/QSpin/workspace/qtaste_svn/branches/qt-demo/qt-demo/qtapp/bin/qtapp")
	sleep(2)
	myApp.focus()
	sleep(1)
	
	myApp.window().click("res/Choosepanel.png")
	myApp.window().click("res/checkbox.png")

	if not myApp.window().exists("res/1361794367109.png"):
		testAPI.stopTest(Status.FAIL, "Fail to check the radio button")
	
	myApp.close()
		
doStep(test_checkbox)
doStep(test_radio)
