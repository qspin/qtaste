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
# QTaste Test result management: Check successful test reporting.
# <p>
# This test case has the goal to verify that when a test is executed successfully without any error, it is reported as "Passed" in the test report.
# @preparation None
##

from qtaste import *

def Step1():
    """
    @step      Invoke the verb throwNoException()
    @expected  Step result is "Passed" and QTaste proceeds to next step.
    """
    testAPI.getEngineTest().throwNoException()

def Step2():
    """
    @step      Invoke the verb throwQTasteTestFailException(False) and catch the QTasteTestFailException
    @expected  Step result is "Passed" and QTaste proceeds to next step.
    """
    try:
        testAPI.getEngineTest().throwQTasteTestFailException(False)
    except QTasteTestFailException:
        pass

def Step3():
    """
    @step      Invoke the verb throwQTasteDataException() and catch the QTasteDataException
    @expected  Step result is "Passed" and QTaste proceeds to next step.
    """
    try:
        testAPI.getEngineTest().throwQTasteDataException()
    except QTasteDataException:
        pass

def Step4():
    """
    @step      Invoke the verb throwQTasteException() and catch the QTasteException
    @expected  Step result is "Passed" and QTaste proceeds to next step.
    """
    try:
        testAPI.getEngineTest().throwQTasteException()
    except QTasteException:
        pass

def Step5():
    """
    @step      Invoke the verb throwQTasteTestFailException(False) and catch the base QTasteException
    @expected  Step result is "Passed" and QTaste proceeds to next step.
    """
    try:
        testAPI.getEngineTest().throwQTasteTestFailException(False)
    except QTasteException:
        pass

def Step6():
    """
    @step      Invoke the verb throwQTasteDataException() and catch the base QTasteException
    @expected  Step result is "Passed" and at the end, QTaste reports test as "Passed".
    """
    try:
        testAPI.getEngineTest().throwQTasteDataException()
    except QTasteException:
        pass

doStep(Step1)
doStep(Step2)
doStep(Step3)
doStep(Step4)
doStep(Step5)
doStep(Step6)
