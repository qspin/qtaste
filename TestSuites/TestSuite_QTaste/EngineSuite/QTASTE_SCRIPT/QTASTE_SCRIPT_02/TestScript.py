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
# QTaste scripting language test: Check call to inexistent verb.
# <p>
# The goal of this test case is to verify the QTaste behavior when the script contains a call to an undefined test API verb
# <p>
# Execute a test script containing a call to an undefined test API verb, QTaste will set the test result to "Not available" with the details of the error.
# @preparation None
##

from qtaste import *

def Step1():
    """
    @step Define a script calling the verb nonExistentVerb()
    @expected Test result set to "Not available" with the following reason:<p>
              <i>AttributeError: nonExistentVerb.</i><p>
              Script call stack is reported.
    """
    testAPI.getEngineTest().nonExistentVerb()

doStep(Step1)
