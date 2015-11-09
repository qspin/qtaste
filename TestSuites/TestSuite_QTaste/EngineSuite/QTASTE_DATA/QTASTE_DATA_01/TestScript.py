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
# QTaste Data driven test: Check data passing from CSV file.
# <p>
# The goal of this test is to check the passing of test data from CSV file for each data type, and check test data get/set/remove/contains methods in the script<p>
# The test consists to put string, int, double, double with precision and boolean test data the testdata.csv file,
# check that the get methods in the script return the defined values,
# use the set methods to change the values and check that the get methods return the new values,
# check the return value of the contains method for existent and non-existent test data,
# check that the remove method actually remove the test data value,
# @preparation None
# @data INT_DATA [Integer] integer data
# @data STRING_DATA [String] string data
# @data DOUBLE_DATA [Double] double data
# @data DOUBLE_WITH_PRECISION_DATA [DoubleWithPrecision] double with precision data
# @data BOOLEAN_DATA [Boolean] boolean data
##

from qtaste import *

def Step1():
    """
    @step      In the CSV, define STRING_DATA to "string", INT_VALUE to 2, DOUBLE_VALUE to 3.3, DOUBLE_WITH_PRECISION_DATA to 5(0.1) and BOOLEAN_VALUE to true
    @expected  None
    """
    pass

def Step2():
    """
    @step      Check that data values are as defined in CSV using testData.getValue(), testData.getIntValue(), testData.getDoubleValue(), testData.getDoubleWithPrecisionValue() and testData.getBooleanValue()
    @expected  Check is successful
    """
    if testData.getValue('STRING_DATA') != 'string':
        testAPI.stopTest(Status.FAIL, "String data value doesn't match.")
    if testData.getIntValue('INT_DATA') != 2:
        testAPI.stopTest(Status.FAIL, "Integer data value doesn't match.")
    if testData.getDoubleValue('DOUBLE_DATA') != 3.3:
        testAPI.stopTest(Status.FAIL, "Double data value doesn't match.")
    if testData.getDoubleWithPrecisionValue('DOUBLE_WITH_PRECISION_DATA') != DoubleWithPrecision(5,0.1):
        testAPI.stopTest(Status.FAIL, "Double with precision data value doesn't match.")
    if testData.getBooleanValue('BOOLEAN_DATA') != True:
        testAPI.stopTest(Status.FAIL, "Boolean data value doesn't match.")

def Step3():
    """
    @step      Modify data values and add a new one, using testData.setValue(), testData.setIntValue(), testData.setDoubleValue(), testData.setDoubleWithPrecisionValue() and testData.setBooleanValue()
    @expected  None
    """
    testData.setValue('STRING_DATA', 'other')
    testData.setIntValue('INT_DATA', 5)
    testData.setDoubleValue('DOUBLE_DATA', 8.6)
    testData.setDoubleWithPrecisionValue('DOUBLE_WITH_PRECISION_DATA', DoubleWithPrecision(10,0.5))
    testData.setBooleanValue('BOOLEAN_DATA', False)
    testData.setValue('NEW_DATA', 'new')

def Step4():
    """
    @step      Check that data values are as modified in step 3 using testData.getValue(), testData.getIntValue(), testData.getDoubleValue(), testData.getDoubleWithPrecisionValue() and testData.getBooleanValue()
    @expected  Check is successful
    """
    if testData.getValue('STRING_DATA') != 'other':
        testAPI.stopTest(Status.FAIL, "String data value doesn't match.")
    if testData.getIntValue('INT_DATA') != 5:
        testAPI.stopTest(Status.FAIL, "Integer data value doesn't match.")
    if testData.getDoubleValue('DOUBLE_DATA') != 8.6:
        testAPI.stopTest(Status.FAIL, "Double data value doesn't match.")
    if testData.getDoubleWithPrecisionValue('DOUBLE_WITH_PRECISION_DATA') != DoubleWithPrecision(10,0.5):
        testAPI.stopTest(Status.FAIL, "Double with precision data value doesn't match.")
    if testData.getBooleanValue('BOOLEAN_DATA') != False:
        testAPI.stopTest(Status.FAIL, "Boolean data value doesn't match.")
    if testData.getValue('NEW_DATA') != 'new':
        testAPI.stopTest(Status.FAIL, "New data value doesn't match.")

def Step5():
    """
    @step      Check that testData.contains() returns true for an existing test data and false for an inexistent test data
    @expected  Check is successful
    """
    if not testData.contains('STRING_DATA'):
        testAPI.stopTest(Status.FAIL, "testData.contains('STRING_DATA') should return true")
    if testData.contains('INEXISTENT_DATA'):
        testAPI.stopTest(Status.FAIL, "testData.contains('INEXISTENT_DATA') should return false")

def Step6():
    """
    @step      Check that after calling testData.remove(), test data doesn't contain the value anymore
    @expected  Check is successful
    """
    testData.remove('STRING_DATA')
    if testData.contains('STRING_DATA'):
        testAPI.stopTest(Status.FAIL, "testData.remove() didn't remove the test data 'STRING_DATA'")

doStep(Step1)
doStep(Step2)
doStep(Step3)
doStep(Step4)
doStep(Step5)
doStep(Step6)
