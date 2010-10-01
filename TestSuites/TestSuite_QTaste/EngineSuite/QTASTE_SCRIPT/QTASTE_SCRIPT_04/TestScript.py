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
# QTaste scripting language test: Check doStep function.
# <p>
# The goal of this test case is to verify that functions executed by the doStep function are correctly reported as test steps.
# <p>
# The test will execute a first step function taking more or less 1 second, then execute a second step function that will fail.<p>
#
# @preparation Define step1 and step2 functions as following:
# <pre>
# def step1():
#   logger.trace('-- begin of step 1 --')
#   time.sleep(1)
#   logger.trace('-- end of step 1 --')
# 
# def step2():
#   logger.trace('-- begin of step 2 --')
#   status = testData.getValue('STATUS')
#   if status == 'SUCCESS':
#     pass
#   elif status == 'FAIL':
#     testAPI.stopTest(Status.FAIL, 'Fail')
#   elif status == 'NOT_AVAILABLE':
#     testAPI.stopTest(Status.NOT_AVAILABLE, 'Not available')
#   logger.trace('-- end of step 2 --')
# </pre>
# @data STATUS [String] Expected test result status: "SUCCESS", "FAIL" or "NOT_AVAILABLE"
##

import time
from qtaste import *

def step1():
	"""
	@step Call doStep(step1)
	@expected step1 function is called as step 1.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 1 (step1)</td></tr>
			  <tr><td>TRACE</td><td>-- begin of step 1 --</td></tr>
			  <tr><td>TRACE</td><td>-- end of step 1 --</td></tr>
			  <tr><td>INFO</td><td>End of step 1 (step1) - status: SUCCESS - elapsed time: 1.015 seconds</td></tr></table><p>
			  where 1.015 can vary but is more or less 1 second.
	"""
	logger.trace('-- begin of step 1 --')
	time.sleep(1)
	logger.trace('-- end of step 1 --')

def step3():
	"""
	@step Call doStep(step3)
	@expected step3 function is called as step 3.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 3 (step3)</td></tr>
			  <tr><td>TRACE</td><td>-- begin of step 3 --</td></tr>
			  <tr><td>TRACE</td><td>-- end of step 3 --</td></tr>
			  <tr><td>INFO</td><td>End of step 3 (step3) - status: SUCCESS - elapsed time: 0.100 seconds</td></tr></table><p>
			  where 0.100 can vary but is more or less 0.1 second.
	"""
	logger.trace('-- begin of step 3 --')
	time.sleep(0.1)
	logger.trace('-- end of step 3 --')

def step4a():
	"""
	@step Call doStep(step4a)
	@expected step4a function is called as step 4a.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 4a (step4a)</td></tr>
			  <tr><td>TRACE</td><td>-- begin of step 4a --</td></tr>
			  <tr><td>TRACE</td><td>-- end of step 4a --  <i>(only if STATUS is "SUCCESS")</i></td></tr>
			  <tr><td>INFO</td><td>End of step 4a (step4a) - status: &lt;STATUS&gt; - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- begin of step 4a --')
	status = testData.getValue('STATUS')
	if status == 'SUCCESS':
		pass
	elif status == 'FAIL':
		testAPI.stopTest(Status.FAIL, 'Fail')
	elif status == 'NOT_AVAILABLE':
		testAPI.stopTest(Status.NOT_AVAILABLE, 'Not available')
	logger.trace('-- end of step 4a --')

doStep(step1)
doStep(3, step3)
doStep('4a', step4a)
