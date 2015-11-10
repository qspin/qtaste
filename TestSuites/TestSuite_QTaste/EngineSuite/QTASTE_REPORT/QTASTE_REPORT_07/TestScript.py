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
# QTaste Test result management: Check base QTasteException handling.
# <p>
# This test case has the goal to verify that when a verb throws a base QTasteException, it is reported as "Not available" in the test report.
# @preparation None
##

from qtaste import *

def Step1():
    """
    @step Define a test script using the verb throwQTasteException()
    @expected Test is set to "Not available" with following reason:<p>
              <i>Generic QTasteException.</i><p>
              Script call stack is reported.
    """
    testAPI.getEngineTest().throwQTasteException()

doStep(Step1)
testAPI.stopTest(Status.FAIL, "This should not be executed.")
