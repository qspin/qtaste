##
# TestProcess.
# <p>
# Description of the test.
#
# @data INSTANCE_ID [String] instance id
##

from qtaste import *
from com.qspin.qtaste.testapi.api import ProcessStatus
import time

def step1():
	"""
	@step      Description of the actions done for this step
	@expected  Description of the expected result
	"""
	process = testAPI.getLinuxProcess(INSTANCE_ID="p1")
	logger.info(process.getStatus())
	checkStatus(process.getStatus(), ProcessStatus.UNDEFINED)
	params = ["java","-cp", "/home/sjansse/workspaces/qtaste/demo/testapi/target/qtaste-testapi-deploy.jar","com.qspin.qtaste.sutuidemo.Interface"]
	process.initialize(params)
	checkStatus(process.getStatus(), ProcessStatus.READY_TO_START)
	logger.info(process.getStatus())
	process.start()
	time.sleep(2)
	logger.info(process.getStatus())
	checkStatus(process.getStatus(), ProcessStatus.RUNNING)
	logger.info("Process pid : " + str(process.getPid()))
	process.stop()
	time.sleep(2)
	logger.info(process.getStatus())
	checkStatus(process.getStatus(), ProcessStatus.STOPPED)
	for line in process.getStdOut():
		logger.info(line)
	for line in process.getStdErr():
		logger.error(line)
	pass

def checkStatus(currentStatus, expectedStatus):
	if currentStatus != expectedStatus:
		testAPI.stopTest(Status.FAIL, "The process has not the expected status. Current status is " + currentStatus.name() + " in state of " + expectedStatus.name())
	
doStep(step1)
