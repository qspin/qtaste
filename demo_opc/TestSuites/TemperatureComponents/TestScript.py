##
# OPC central integer components.
#
# @data Temperature [int] the temperature value to set.
# @data DELAY [int] time in second before the check.
##

from qtaste import *
from Common import *
from CommonData import *
import time
import string

opc = testAPI.getOPC(INSTANCE_ID='OpcDemo')

def testOPCVariableSync():
    """
    @step      Set the temperature of the central heating
    @expected  Check the temperature of the central heating has changed and readched the expected value.
    """
    spVariable, spValue, readVariable, delay, precision = getIntTestData();
    checkVarIntSync(opc, spVariable, spValue, spValue, readVariable, delay, precision)

def testOPCVariableAsync():
    """
    @step      Set the temperature of the central heating
    @expected  Check the temperature of the central heating has changed and readched the expected value.
    """
    spVariable, spValue, readVariable, delay, precision = getIntTestData();
    opc.addVariableSubscription(readVariable)
    checkVarIntAsync(opc, spVariable, spValue, spValue, readVariable, delay, precision)


doStep(testOPCVariableSync)
doStep(testOPCVariableAsync)
