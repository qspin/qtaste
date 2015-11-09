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
# QTaste Test result management: Check reporting of inexistent test data use.
# <p>
# This test case has the goal to verify that when the script tries to use an inexistent test data, it is reported as "Not available" in the test report.
# @preparation None
##

from qtaste import *

def Step1():
    """
    @step      Call testData.getValue("INEXISTENT")
    @expected  test is set to "Not available" with following reason:<p>
               <i>TestData doesn't contain value for data INEXISTENT.</i><p>
               Script call stack is reported.
    """
    x = testData.getValue("INEXISTENT")

doStep(Step1)
