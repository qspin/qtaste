##
# MultiProcess.
# <p>
# <u><b>Test only available on LINUX.</b></u>
# <p>
# Try to launch a "xterm" process and a "xterm top" process and then stop the created processes.
#
##

from qtaste import *
from com.qspin.qtaste.testapi.api import ProcessStatus
import time


process1 = testAPI.getLinuxProcess(INSTANCE_ID="p1")
process2 = testAPI.getLinuxProcess(INSTANCE_ID="p2")

def startProcess1():
	"""
	@step      Starts the first process
	@expected  The first process status is RUNNING
	"""
	checkStatus(process1.getStatus(), ProcessStatus.UNDEFINED)
	params = ["xterm"]
	process1.initialize(None, ".", params)
	checkStatus(process1.getStatus(), ProcessStatus.READY_TO_START)
	process1.start()
	time.sleep(2)
	checkStatus(process1.getStatus(), ProcessStatus.RUNNING)
	
def startProcess2():
	"""
	@step      Starts the second process
	@expected  The second process status is RUNNING
	"""
	checkStatus(process2.getStatus(), ProcessStatus.UNDEFINED)
	params = ["xterm", "top"]
	process2.initialize(None, ".", params)
	checkStatus(process2.getStatus(), ProcessStatus.READY_TO_START)
	process2.start()
	time.sleep(2)
	checkStatus(process2.getStatus(), ProcessStatus.RUNNING)

def killBoth():
	"""
	@step      kill the two processes
	@expected  Process's status are STOPPED
	"""
	process1.stop()
	process2.stop()
	time.sleep(2)
	checkStatus(process1.getStatus(), ProcessStatus.STOPPED)
	checkStatus(process2.getStatus(), ProcessStatus.STOPPED)
	pass
	
def checkStatus(currentStatus, expectedStatus):
	if currentStatus != expectedStatus:
		testAPI.stopTest(Status.FAIL, "The process has not the expected status. Current status is " + currentStatus.name() + " in state of " + expectedStatus.name())
	


doStep(startProcess1)
doStep(startProcess2)
doStep(killBoth)
