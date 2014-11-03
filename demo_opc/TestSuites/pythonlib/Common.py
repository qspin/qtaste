##
# OPC Common
#
##

from qtaste import *
import time
import string

def goToMaintenanceMode(opc):
    opc.write("SUP_WR_BIN_AC_MAINTENANCE", "1")
    actualValue = opc.readInt("SUP_RO_INT_ST_GENERAL_STATUS")
    if not (actualValue == 0):
        testAPI.stopTest(Status.FAIL,"Cannot go to maintenance Mode: got "+ str(actualValue) +" for variable SUP_RO_INT_ST_GENERAL_STATUS")

def switchOnCompressionAir(opc):
    opc.write("SUP_WR_BIN_DO_COMP_AIR_SUPPLY", "1")
    time.sleep(1)    
    actualValue = opc.readInt("IO_DO_COMP_AIR_SUPPLY")
    if not (actualValue == 1):
        testAPI.stopTest(Status.FAIL,"Cannot switch ON Compression Air!")

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
