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
# TestCalculator - Test the windows calculator.
# <p>
# Start the Windows calculator, perform simple computations and check results.
# This demo required the "demo_gui" testbed configuration
##

from qtaste import *

calculator = testAPI.getWindows()
number1 = testData.getValue("NUMBER1")
number2 = testData.getValue("NUMBER2")
expectedResult = testData.getValue("RESULT")
operator = testData.getValue("OPERATOR")

def pressDigitButtons(numberStr):
    for i in numberStr:
        calculator.pressButton("Calculator", "_" + i)

def testCalculator():
    """
    @step      Perform a simple computation using the Windows calculator
    @expected  Compare the result provided by the calculator with the expected results
    """
    sessionID = calculator.startApplication("calc")
    calculator.pressButton("Calculator", "CE")
    pressDigitButtons(number1)
    calculator.pressButton("Calculator", "_" + operator)
    pressDigitButtons(number2)
    calculator.pressButton("Calculator", "_=")
    text = calculator.getText("Calculator", "Edit").strip()
    text = text.replace(".",",")
    expectedResult = testData.getValue("RESULT")
    if text != expectedResult:
        testAPI.stopTest(Status.FAIL, "Expected to get " + expectedResult + " but got " + text)
    calculator.stopApplication()

doStep(testCalculator)
