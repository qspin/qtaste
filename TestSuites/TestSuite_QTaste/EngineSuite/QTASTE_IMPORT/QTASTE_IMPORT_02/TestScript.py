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
importTestScript("../QTASTE_DATA/QTASTE_DATA_01")

##
#  QTaste import test script feature:
# <p>
# This test case has the goal to verify that steps of an imported test script are accessible.
##


def Step1():
    """
    @step Check that NEW_DATA test data doesn't exist
    @expected NEW_DATA test data doesn't exist
    """
    if testData.contains('NEW_DATA'):
        testAPI.stopTest(Status.FAIL, "the test data 'NEW_DATA' already exists")


def Step2():
    """
    @step Execute Step3 of QTASTE_DATA_01
    @expected NEW_DATA test data exists and has value 'new'
    """
    doSubStep(QTASTE_DATA_01.Step3)

    if testData.getValue('NEW_DATA') != 'new':
        testAPI.stopTest(Status.FAIL, "the test data 'NEW_DATA' doesn't have 'new' value")


doStep(Step1)
doStep(Step2)
