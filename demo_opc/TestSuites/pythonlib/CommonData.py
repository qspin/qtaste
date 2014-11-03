##
# OPC Data Common
#
##

from qtaste import *
import time
import string


def getIntTestData():
    factorValue = testData.getIntValue("CONVERSION_FACTOR")
    spValue = testData.getIntValue("VALUE") * factorValue
    spVariable = testData.getValue("SP_VARIABLE")
    delay = testData.getIntValue("DELAY")
    readVariable = testData.getValue("READ_VARIABLE")
    precision = testData.getIntValue("PRECISION") * factorValue

    return spVariable, spValue, readVariable, delay, precision

def getDoubleTestData():
    factorValue = testData.getDoubleValue("CONVERSION_FACTOR")
    spValue = testData.getDoubleValue("VALUE") * factorValue
    spVariable = testData.getValue("SP_VARIABLE")
    delay = testData.getIntValue("DELAY")
    readVariable = testData.getValue("READ_VARIABLE")
    precision = testData.getDoubleValue("PRECISION") * factorValue

    return spVariable, spValue, readVariable, delay, precision

def getBooleanTestData():
    spVariable = testData.getValue("SP_VARIABLE")
    readVariable = testData.getValue("READ_VARIABLE")
    delay = testData.getIntValue("DELAY")

    return spVariable, readVariable, delay
