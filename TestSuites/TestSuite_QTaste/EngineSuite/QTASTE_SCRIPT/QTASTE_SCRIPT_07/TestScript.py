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
# QTaste scripting language test: Check unpersistence of variables.
# <p>
# The goal of this test case is to verify that variables defined in the execution of a test script are not kept upon execution of further scripts.
# <p>
# In a first run (row 1 of CSV file), the test will set a variable to some value, in a second run (row 2 of CSV file), the test will check that the variable is not defined.
# @preparation None
##

from qtaste import *

def Step1():
	"""
	@step Check that variable myVariable is not defined
	@expected The check is successful
	"""
	try:
		myVariable
	except NameError:
		pass
	else:
		testAPI.stopTest(Status.FAIL, "Variable is persistent.")

def Step2():
	"""
	@step Define variable myVariable with value 5
	@expected None
	"""
	pass

def Step3():
	"""
	@step Repeat step1 and step 2 for a second row of test data
	@expected Test result is "Passed"
	"""
	pass

doStep(Step1)
myVariable = 5
doStep(Step2)
doStep(Step3)
