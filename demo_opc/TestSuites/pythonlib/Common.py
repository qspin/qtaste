##
# OPC Common
#
##

from qtaste import *
import time
import string

def checkVarIntSync(opc, spVariable, spValue, expectedValue, readVariable, delay, precision=0):
    opc.write(spVariable, str(spValue))
    time.sleep(delay)
    actualValue = opc.readInt(readVariable)
    if not((actualValue >= (expectedValue - precision)) and (actualValue  <= (expectedValue + precision))):
        testAPI.stopTest(Status.FAIL,"Got "+ str(actualValue) +" for variable " + readVariable + " but expected value is " + str(expectedValue))

def checkVarDoubleSync(opc, spVariable, spValue, expectedValue, readVariable, delay, precision=0.0):
    opc.write(spVariable, str(spValue))
    time.sleep(delay)
    actualValue = opc.readDouble(readVariable)
    if not((actualValue >= (expectedValue - precision)) and (actualValue  <= (expectedValue + precision))):
        testAPI.stopTest(Status.FAIL,"Got "+ str(actualValue) +" for variable " + readVariable + " but expected value is " + str(expectedValue))

def checkVarIntAsync(opc, spVariable, spValue, expectedValue, readVariable, delay, precision=0):
    opc.write(spVariable, str(spValue))
    opc.checkValueInt(readVariable, expectedValue, delay, precision)

def checkVarDoubleAsync(opc, spVariable, spValue, expectedValue, readVariable, delay, precision=0):
    opc.write(spVariable, str(spValue))
    opc.checkValueDouble(readVariable, expectedValue, delay, precision)

def checkVarBoolAsync(opc, spVariable, spValue, expectedValue, readVariable, delay):
    opc.write(spVariable, str(spValue))
    opc.checkValueBool(readVariable, expectedValue, delay)
