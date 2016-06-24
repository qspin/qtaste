# coding=utf-8

##
# TestProcess.
# <p>
# <u><b>Test only available on LINUX.</b></u>
# This demo requires the process testbed.
# <p>
# Try to launch the xterm command and then stop the created process.
#
##

from qtaste import *
from com.qspin.qtaste.testapi.api import ProcessStatus
import time


process = testAPI.getLinuxProcess(INSTANCE_ID="p1")

def initializeProcess():
    """
    @step      Initialize the xterm process
    @expected  The process status is READY_TO_START
    """
    logger.info(process.getStatus())
    checkStatus(process.getStatus(), ProcessStatus.UNDEFINED)
    params = ["xterm"]
    process.initialize(None, ".", params)
    checkStatus(process.getStatus(), ProcessStatus.READY_TO_START)

def launchProcess():
    """
    @step      Launch the xterm process
    @expected  The process status is RUNNING
    """
    logger.info(process.getStatus())
    process.start()
    time.sleep(2)
    logger.info(process.getStatus())
    checkStatus(process.getStatus(), ProcessStatus.RUNNING)
    logger.info("Process pid : " + str(process.getPid()))

def stopProcess():
    """
    @step      Stop the process
    @expected  The process status is STOPPED
    """
    process.stop()
    time.sleep(2)
    logger.info(process.getStatus())
    checkStatus(process.getStatus(), ProcessStatus.STOPPED)
    for line in process.getStdOut():
        logger.info(line)
    for line in process.getStdErr():
        logger.error(line)

def checkStatus(currentStatus, expectedStatus):
    if currentStatus != expectedStatus:
        testAPI.stopTest(Status.FAIL, "The process has not the expected status. Current status is " + currentStatus.name() + " in state of " + expectedStatus.name())

doStep(initializeProcess)
doStep(launchProcess)
doStep(stopProcess)
