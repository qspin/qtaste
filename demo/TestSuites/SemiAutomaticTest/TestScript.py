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
# Semi-Automatic Test
# <p>
# Example to perform semi automated test, requiring user input confirmation.
#
##


from qtaste import *

utility = testAPI.getUtility()

def step1():
    """
    @step      This semi test will show some message dialog and will expect user input
    @expected  User input dialogs will be presented to user
    """
    utility.showMessageDialog("Message Dialog", "This is a semi automated test where user need to confirm the test output.")

    ok = utility.getUserConfirmation("User confirmation", "Is Test output as expected?")
    if (not ok):
        testAPI.stopTest(Status.FAIL, "Test Failed: Test output is not as expected!")

    value = utility.getUserStringValue("Please insert the output value (e.g Hardware component measure) resulting in this test?", "<Default Value>")
 
doStep(step1)
