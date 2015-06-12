# encoding= utf-8

##
# Tooltip.
# <p>
# Test used to validate the getToolTip() method.
#
# @data INSTANCE_ID [String] instance.
##

from qtaste import *

importTestScript("TabbedPaneSelection")

javaGUI = testAPI.getJavaGUI(INSTANCE_ID=testData.getValue("JAVAGUI_INSTANCE_NAME"))

def checkToolTip():
	"""
	@step      Check the component tooltip is the expected one.
	@expected  The component's tooltip is the same as the one from testData
	"""
	current = javaGUI.getToolTip(testData.getValue("COMPONENT_ID"))
	expected = testData.getValue("EXPECTED_TOOLTIP")
	if current != expected:
		testAPI.stopTest(Status.FAIL, "The returned tooltip (" + current + ") is not the expected one (" + expected + ")...")
	pass

doSteps(TabbedPaneSelection.changeTabById)
doStep(checkToolTip)
