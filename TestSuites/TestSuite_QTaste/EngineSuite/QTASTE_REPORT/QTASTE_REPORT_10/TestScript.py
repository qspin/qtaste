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
# QTaste Test result management: Check testAPI.stopTest() function with NOT_AVAILABLE status.
# <p>
# This test case has the goal to verify that when a script asks to stop the test by calling testAPI.stopTest(Status.NOT_AVAILABLE, message), it is reported as "Not Available" in the test report with the given message.
# @preparation None
##

from qtaste import *

def Step1():
	"""
	@step Define a test script calling testAPI.stopTest(Status.FAIL, 'Test result must be not available.')
	@expected Test is set to "Not Available" with following reason:<p>
			  <i>Test result must be not available.</i><p>
			  Script call stack is reported.
	"""
	testAPI.stopTest(Status.NOT_AVAILABLE, 'Test result must be not available.')

def Step2():
	"""
	@step Call the verb throwQTasteDataException()
	@expected This should not be executed.
	"""
	# this should not be executed
	testAPI.getEngineTest().throwQTasteTestFailException()

doStep(Step1)
doStep(Step2)
