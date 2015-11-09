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
# QTaste Multiple Instances Component test: Check get of multiple instances component instances.
# <p>
# This test case has the goal to verify that a script get the requested instance of a Multiple Instances Component.
# @preparation Add in the testbed configuration file 2 Multiple Instances component with id 1 and 2 containing an instance of MultipleInstancesTest
##

from qtaste import *

def Step1():
    """
    @step Create 2 instances of MultipleInstancesTest with id 1 and 2
    @expected 2 instances of MultipleInstancesTest are created without any errors.
    """
    global instance1, instance2
    instance1 = testAPI.getMultipleInstancesTest(INSTANCE_ID="1")
    instance2 = testAPI.getMultipleInstancesTest(INSTANCE_ID="2")

def Step2():
    """
    @step Check that the id of the instances correspond to 1 for instance1 and 2 for instance2 using the verb checkTreatmentRoomId
    @expected The check returns without error
    """
    instance1.checkInstanceId("1")
    instance2.checkInstanceId("2")

doStep(Step1)
doStep(Step2)
