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
# QTaste Test result management: Check reporting of invalid verb call.
# <p>
# This test case has the goal to verify that when a script tries to call a verb with an invalid argument type or with a wrong number of arguments, it is reported as "Not available" in the test report.
# @preparation None
##

from qtaste import *

engineTest = testAPI.getEngineTest()

def Step1():
	"""
	@step      TestData Row 1: call engineTest.checkDataIsInteger(1.0)
	@expected  test is set to "Not available" with following reason:<p>
	           <i>Invalid argument(s): checkDataIsInteger(): 1st arg can't be coerced to int</i><p>
	           Script call stack is reported.
	"""
	engineTest.checkDataIsInteger(1.0)

def Step2():
	"""
	@step      TestData Row 2: call engineTest.checkData1To5(1,2,3,4)
	@expected  test is set to "Not available" with following reason:<p>
	           <i>Invalid argument(s): checkData1To5(): expected 5 args; got 4</i>, if backward compatibility is disabled<p>
	           <i>TestData doesn't contain value for data DATA5</i>, if backward compatibility is enabled<p>
	           Script call stack is reported.
	"""
	engineTest.checkData1To5(1,2,3,4)



row = testData.getIntValue("ROW")
if row == 1:
	doStep(Step1)
elif row == 2:
	doStep(Step2)
		
