# coding=utf-8

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
#

from qtaste import *
from lib import *

##
#  QTaste documentation management:
# <p>
# This test case has the goal to verify that lib is correctly imported for each test execution and that when a test step is imported, its documentation is well imported too.
##

def Step2():
    """
    @step Check value of DATA test data during import vs value in test script
    @expected Both values are equal
    """
    if data != testData.getValue('DATA'):
        testAPI.stopTest(Status.FAIL, "DATA value doesn't match in test script and imported lib.")


doStep(Step1FromLib)
doStep(Step2)
