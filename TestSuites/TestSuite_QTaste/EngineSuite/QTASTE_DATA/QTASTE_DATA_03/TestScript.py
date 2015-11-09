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
# QTaste Data driven test: Check the default TIMEOUT value.
# <p>
# This test case has the goal to verify that the TIMEOUT data value is defined to 60 seconds if it is not defined in the CSV file.<p>
# This test will execute a test case that takes more time than the default TIMEOUT data.
# @preparation None
##

from qtaste import *

def Step1():
    """
    @step      Call the verb neverReturn()
    @expected  Test is "Failed", reason: <i>Test execution timeout.</i><p>
               Script call stack is reported.<p>
               Elapsed time is more or less 60 seconds.
    """
    testAPI.getEngineTest().neverReturn()

doStep(Step1)

