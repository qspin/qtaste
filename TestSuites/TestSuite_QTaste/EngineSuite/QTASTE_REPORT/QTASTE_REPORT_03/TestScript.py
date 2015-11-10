# encoding= utf-8

#    Copyright 2007-2009 QSpin - www.qspin.be
#
#    This file is part of QTaste framework.
#
#    QTaste is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Lesser General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    QTaste is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Lesser General Public License for more details.
#
#    You should have received a copy of the GNU Lesser General Public License
#    along with QTaste. If not, see <http://www.gnu.org/licenses/>.

##
# QTaste Test result management: Check test timeout reporting.
# <p>
# This test case has the goal to verify that when test execution duration exceeds the TIMEOUT value, this is reported as "Failed" in the test report.
# @preparation None
##

import time
from qtaste import *

engineTest = testAPI.getEngineTest()

def Step1():
    """
    @step      In CSV file, define TIMEOUT to 5 for 4 rows
    @expected  None
    """
    pass

def Step2():
    """
    @step      Call the verb sleep(3000), then log an error message and call testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")
    @expected  After +-5 seconds, QTaste reports test as "Failed", reason:<i>Test execution timeout.</i><p>
               Script call stack is reported.<p>
               The error message doesn't appear in the log.
    """
    engineTest.sleep(3000)
    # this should not be executed
    logger.error('The script continued to execute after timeout!');
    testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")


def Step3():
    """
    @step      Call the verb neverReturn(), then log an error message and call testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")
    @expected  After +-5 seconds, QTaste reports test as "Failed", reason:<i>Test execution timeout.</i><p>
               Script call stack is reported.<p>
               The error message doesn't appear in the log.
    """
    engineTest.neverReturn()
    # this should not be executed
    logger.error('The script continued to execute after timeout!');
    testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")


def Step4():
    """
    @step      Do a never ending loop:<p>
               <b>while True: <br>
               &nbsp;&nbsp;&nbsp;&nbsp;pass</b><p>
               then log an error message and call testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")
    @expected  After +-5 seconds, QTaste reports test as "Failed", reason:<i>Test execution timeout.</i><p>
               Script call stack is reported.<p>
               The error message doesn't appear in the log.
    """
    while True:
        pass
    # this should not be executed
    logger.error('The script continued to execute after timeout!');
    testAPI.stopTest(Status.NOT_AVAILABLE, "This should not be executed.")


doStep(Step1)
if testData.getBooleanValue('IN_VERB'):
    if testData.getBooleanValue('IN_SLEEP'):
        doStep(Step2)
    else:
        doStep(Step3)
else:
    if testData.getBooleanValue('IN_SLEEP'):
        doStep(Step2)
    else:
        doStep(Step4)

