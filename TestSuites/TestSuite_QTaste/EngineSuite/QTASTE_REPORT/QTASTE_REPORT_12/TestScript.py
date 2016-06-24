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
importTestScript("../QTASTE_IMPORT/QTASTE_IMPORT_01")

##
#  QTaste Test result management: Check reporting of import test script failure.
# <p>
# This test case has the goal to verify that failure during import of a test script is correctly reported.
##


def Step1():
    """
    @step Do nothing
    """
    pass


doStep(Step1)
