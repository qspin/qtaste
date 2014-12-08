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
# QTaste Test result management: check QTasteTestFailException handling.
# <p>
# This test case has the goal to verify that when a verb reports a failure by throwing an QTasteTestFailException, it is reported as "Failed" in the test report.
# @preparation None
##

from qtaste import *

def Step1():
	"""
	@step      Define a test script using the verb throwQTasteTestFailException()
	@expected  QTaste reports test as "Failed", reason:<p>
			   This verb always fails!<p>
			   Script call stack is reported.
	"""
	testAPI.getEngineTest().throwQTasteTestFailException()

def Step2():
	"""
	@step      Define a test script using the verb throwQTasteDataException()
	@expected  This verb is never called
	"""
	logger.fatal('This step cannot be executed -> TEST FAILED')
	testAPI.getEngineTest().throwQTasteDataException()

doStep(Step1)
doStep(Step2)	
