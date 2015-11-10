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
# QTaste Data driven test: QTaste double with precision data equality test.
# <p>
# The goal of this test is to check the equals method of DoubleWithPrecision.
# @preparation None
# @data DOUBLE_WITH_PRECISION_DATA [DoubleWithPrecision] double with precision data
##

from qtaste import *

def Step1():
    """
    @step      In the CSV, define DOUBLE_WITH_PRECISION_DATA to 10(0.5)
    @expected  None
    """
    pass

def Step2():
    """
    @step      Check the equals() method of DoubleWithPrecision with differents values
    @expected  Check is successful
    """
    doubleWithPrecisionValue = testData.getDoubleWithPrecisionValue('DOUBLE_WITH_PRECISION_DATA')
    if not doubleWithPrecisionValue.equals(10):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should be equal to 10")
    if not doubleWithPrecisionValue.equals(10.2):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should be equal to 10.2")
    if not doubleWithPrecisionValue.equals(10.5):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should be equal to 10.5")
    if not doubleWithPrecisionValue.equals(9.5):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should be equal to 9.5")
    if doubleWithPrecisionValue.equals(9.49):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should not be equal to 9.49")
    if doubleWithPrecisionValue.equals(10.51):
        testAPI.stopTest(Status.FAIL, "Double with precision value 10(0.5) should not be equal to 10.51")

doStep(Step1)
doStep(Step2)
