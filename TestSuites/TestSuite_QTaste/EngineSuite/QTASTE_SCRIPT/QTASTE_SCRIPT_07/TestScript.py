# coding=utf-8

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
# The goal of this test case is to verify that variables defined in the execution of a test script are not kept upon execution of further tests.
# <p>
# In a first run (row 1 of CSV file), the test will set a variable to some value, in a second run (row 2 of CSV file), the test will check that the variable is not defined.
# @preparation None
##

from qtaste import *

def step1():
    """
    @step Check that variable myVariable is not defined
    @expected Variable myVariable is not defined
    """
    try:
        myVariable
    except NameError:
        pass
    else:
        testAPI.stopTest(Status.FAIL, "Variable myVariable is already defined.")

def step2():
    """
    @step Define variable myVariable with value "some value"
    @expected Variable myVariable is defined
    """
    global myVariable
    myVariable = "some value"

    # check that myVariable is defined
    try:
        myVariable
    except NameError:
        testAPI.stopTest(Status.FAIL, "Variable myVariable is not defined")


doStep(step1)
doStep(step2)
