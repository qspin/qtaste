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
# QTaste Multiple Instances Component test: Check use of undefined component.
# <p>
# This test case has the goal to verify that scripts which get instances of a Multiple Instances Component not defined in the Testbed configuration will be reported as "Not Available".
# @preparation Prepare a testbed configuration file without any MultipleInstancesTest with id=3
##

from qtaste import *

def Step1():
    """
    @step Create 1 instance of MultipleInstancesTest with id 3
    @expected None
    """
    global instance3
    instance3 = testAPI.getMultipleInstancesTest(INSTANCE_ID="3")

def Step2():
    """
    @step Check that the instance create can be tested using a "if" command.
    @expected The check returns without error
    """
    #  Check if the tr_room3 is available
    if instance3:
        testAPI.stopTest(Status.FAIL, "An instance of an not defined component is expected to be false truth value")

def Step3():
    """
    @step Inovke the verb checkInstanceId on the instance created
    @expected QTaste reports test as "Not available" with following reason:<p>
              "ComponentNotPresentException: Component MultipleInstancesTest is not present in testbed"<p>
              Script call stack is reported.
    """
    #  the next line should throw an error saying that the Component is not present
    instance3.checkInstanceId("3")

doStep(Step1)
doStep(Step2)
doStep(Step3)
