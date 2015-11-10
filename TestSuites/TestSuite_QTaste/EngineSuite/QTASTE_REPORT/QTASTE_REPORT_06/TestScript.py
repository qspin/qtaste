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
# QTaste Test result management: Check reporting of invalid data format use.
# <p>
# This test case has the goal to verify that when a script tries to use a test data with an invalid format, it is reported as "Not available" in the test report.
# @preparation None
##

from qtaste import *

engineTest = testAPI.getEngineTest()

def Step1():
    """
    @step      TestData Row 1: call testData.getIntValue('DATA') with DATA defined to "1.5"
    @expected  test is set to "Not available" with following reason:<p>
               <i>java.lang.NumberFormatException: For input string: "1.5" while parsing integer data DATA. </i><p>
               Script call stack is reported.
    """
    x = testData.getIntValue('DATA')

def Step2():
    """
    @step      TestData Row 2: call testData.getDoubleValue('DATA') with DATA defined to "bad"
    @expected  test is set to "Not available" with following reason:<p>
               <i>java.lang.NumberFormatException: For input string: "two" while parsing double data DATA. </i><p>
               Script call stack is reported.
    """
    x = testData.getDoubleValue('DATA')

def Step3():
    """
    @step      TestData Row 3: call testData.getBooleanValue('DATA') with DATA defined to "bad"
    @expected  test is set to "Not available" with following reason:<p>
               <i>Error while parsing boolean data DATA for input string: "bad" </i><p>
               Script call stack is reported.
    """
    x = testData.getBooleanValue('DATA')


type = testData.getValue("TYPE")
if type == "integer":
    doStep(Step1)
elif type == "double":
    doStep(Step2)
elif type == "boolean":
    doStep(Step3)

