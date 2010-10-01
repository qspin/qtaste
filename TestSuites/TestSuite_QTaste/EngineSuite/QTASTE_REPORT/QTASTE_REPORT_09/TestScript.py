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
# QTaste Test result management: Check testAPI.stopTest() function with FAIL status.
# <p>
# This test case has the goal to verify that when a script asks to stop the test by calling testAPI.stopTest(Status.FAIL, message), it is reported as "Failed" in the test report with the given message.
# @preparation None
##

from qtaste import *

def Step1():
	"""
	@step Define a test script calling testAPI.stopTest(Status.FAIL, 'This test must fail.')
	@expected Test is set to "Failed" with following reason:<p>
			  <i>This test must fail.</i><p>
			  Script call stack is reported.
	"""
	testAPI.stopTest(Status.FAIL, 'This test must fail.')

def Step2():
	"""
	@step Call the verb throwQTasteDataException()
	@expected This should not be executed.
	"""
	# this should not be executed
	testAPI.getEngineTest().throwQTasteDataException()

doStep(Step1)
doStep(Step2)
