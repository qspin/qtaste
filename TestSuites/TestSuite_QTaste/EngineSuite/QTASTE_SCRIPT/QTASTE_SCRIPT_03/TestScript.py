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
# QTaste scripting language test: Check verbs return values.
# <p>
# The goal of this test case is to verify that values returned by verbs called in a test script are correctly passed to the script.
# <p>
# The test will call verbs returning string, integer, double and boolean values respectively and check that the returned values are the expected ones
# @preparation None
##

from qtaste import *

engineTest = testAPI.getEngineTest()

def Step1():
	"""
	@step Check that value returned by the verb returnDataAsString('text') is 'text'
	@expected The check is successful
	"""
	if engineTest.returnDataAsString('text') != 'text':
		testAPI.stopTest(Status.FAIL, "String return value does'nt match.")

def Step2():
	"""
	@step Check that value returned by the verb returnDataAsInteger(4) is 4
	@expected The check is successful
	"""
	if engineTest.returnDataAsInteger(4) != 4:
		testAPI.stopTest(Status.FAIL, "Integer return value does'nt match.")

def Step3():
	"""
	@step Check that value returned by the verb returnDataAsDouble(3.14) is 3.14
	@expected The check is successful
	"""
	if engineTest.returnDataAsDouble(3.14) != 3.14:
		testAPI.stopTest(Status.FAIL, "Double return value does'nt match.")

def Step4():
	"""
	@step Check that value returned by the verb returnDataAsBoolean(True) is True
	@expected The check is successful<p>
			  Test result is "Passed"
	"""
	if engineTest.returnDataAsBoolean(True) != True:
		testAPI.stopTest(Status.FAIL, "Boolean return value does'nt match.")

doStep(Step1)
doStep(Step2)
doStep(Step3)
doStep(Step4)
	
