# encoding= utf-8

##
# Sikuli_02.
# <p>
# Run a sikuli script create with the Sikuli IDE
#
##

from qtaste import *
import os

sikuli = testAPI.getSikuli(INSTANCE_ID="Sikuli")
SCRIPT_PATH = os.getcwd() + "/TestSuites/Sikuli/Sikuli_02/"

def execute():
    """
    @step      Run a script created with the Sikuli IDE
    @expected  The test is executed.
    """
    sikuli.openAndRunScript(SCRIPT_PATH + testData.getValue("SCRIPT_NAME"))

doStep(execute)
