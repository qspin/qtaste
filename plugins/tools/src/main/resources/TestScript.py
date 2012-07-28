##
# [$TEST_NAME].
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *
import time

# update in order to cope with the javaGUI extension declared in your testbed configuration.
javaguiMI = testAPI.getJAVAGUI()

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	pass

doStep(step1)
