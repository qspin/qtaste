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
# QTaste scripting language test: Check doSteps function.
# <p>
# The goal of this test case is to verify that the doSteps function works as expected.
#
# @preparation Define the following steps functions and table:
# <pre>
# def step1():
#   logger.trace('-- step 1 --')
#
# def step1a():
#   logger.trace('-- step 1a --')
#
# def step2():
#   logger.trace('-- step 2 --')
#
# def step3():
#   logger.trace('-- step 3 --')
#
# def step4():
#   logger.trace('-- step 4 --')
#
# def step5():
#   logger.trace('-- step 5 --')
#
# steps = [ (1, step1),
#          ('1a', step1a),
#          (2, step2),
#          (3, step3),
#          (4, step4),
#          (5, step5)
#        ]
# </pre>
# call doSteps with the steps table and the selector from the test data (None if empty)
#
# @data SELECTOR [String] Steps selector to be used by the doSteps function, may be empty to use no selector
##

import time
from qtaste import *

def step1():
	"""
	@step Call step1 function as a step
	@expected step1 function is called as step 1.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 1 (step1)</td></tr>
			  <tr><td>TRACE</td><td>-- step 1 --</td></tr>
			  <tr><td>INFO</td><td>End of step 1 (step1) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 1 --')

def step1a():
	"""
	@step Call step1a function as a step
	@expected step1a function is called as step 1a.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 1a (step1a)</td></tr>
			  <tr><td>TRACE</td><td>-- step 1a --</td></tr>
			  <tr><td>INFO</td><td>End of step 1a (step1a) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 1a --')

def step2():
	"""
	@step Call step2 function as a step
	@expected step2 function is called as step 2.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 2 (step2)</td></tr>
			  <tr><td>TRACE</td><td>-- step 2 --</td></tr>
			  <tr><td>INFO</td><td>End of step 2 (step2) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 2 --')

def step3():
	"""
	@step Call step3 function as a step
	@expected step3 function is called as step 3.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 3 (step3)</td></tr>
			  <tr><td>TRACE</td><td>-- step 3 --</td></tr>
			  <tr><td>INFO</td><td>End of step 3 (step3) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 3 --')

def step4():
	"""
	@step Call step4 function as a step
	@expected step4 function is called as step 4.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 4 (step4)</td></tr>
			  <tr><td>TRACE</td><td>-- step 4 --</td></tr>
			  <tr><td>INFO</td><td>End of step 4 (step4) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 4 --')

def step5():
	"""
	@step Call step5 function as a step
	@expected step5 function is called as step 5.<p>
			  Log contains following informations:<p>
			  <table cols="2" border="1" cellspacing="0"><tr><td>INFO</td><td>Begin of step 5 (step5)</td></tr>
			  <tr><td>TRACE</td><td>-- step 5 --</td></tr>
			  <tr><td>INFO</td><td>End of step 5 (step5) - status: SUCCESS - elapsed time: 0.000 seconds</td></tr></table><p>
			  where 0.000 can vary but is more or less 0 second.
	"""
	logger.trace('-- step 5 --')


steps = [ (1, step1),
          ('1a', step1a),
          (2, step2),
          (3, step3),
          (4, step4),
          (5, step5)
        ]

selector = testData.getValue('SELECTOR')
if selector:
	doSteps(steps, selector)
else:
	doSteps(steps)
