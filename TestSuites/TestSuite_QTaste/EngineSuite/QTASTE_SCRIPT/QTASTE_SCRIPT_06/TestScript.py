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
# QTaste scripting language test: Check doStep and doSteps functions with undefined function or table.
# <p>
# The goal of this test case is to verify that trying to use an undefined step function or steps table in the doStep or doSteps functions
# produces a NameError Python exception and that the test result is then reported as not available.
#
# @preparation Define the following steps functions and table:
# <pre>
# def step1():
#   logger.trace('-- step 1 --')
#
# stepsWithUndefinedStep = [ (1, step1),
#                            (2, undefinedStep)
#                          ]
# </pre>
#
# Note: only define stepsWithUndefinedStep following the test data values. <br>
# Call doSteps(undefinedSteps), doSteps(stepsWithUndefinedStep) or doStep(undefinedStep) following the test data values
#
# @data USE_DO_STEPS [Boolean] True for testing doSteps,
#                              False for testing doStep with an undefined step function
# @data UNDEFINED_STEPS_TABLE [Boolean] True for testing doSteps with an undefined steps table,
#                                       False for testing it with a steps table containing an undefined step function
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

if testData.getBooleanValue('USE_DO_STEPS'):
	if testData.getBooleanValue('UNDEFINED_STEPS_TABLE'):
		doSteps(undefinedSteps)
	else:
		stepsWithUndefinedStep = [ (1, step1),
								   (2, undefinedStep)
								 ]
		doSteps(stepsWithUndefinedStep)
else:
	doStep(undefinedStep)
