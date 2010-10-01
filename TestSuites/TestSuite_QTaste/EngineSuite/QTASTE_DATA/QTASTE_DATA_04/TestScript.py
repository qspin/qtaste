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
# QTaste Data driven test: Check the file loading.
# <p>
# This test case has the goal to verify that QTaste load a file as defined in the testdata for variables begining by "FILE_".
# QTaste supports the filename specified in absolute + relative.
# @preparation None
# @data FILE_DATA [String] name of the file (relative or absolute)
# @data FILECONTENT [String] Expected file content of the file.
##

from qtaste import *

expectedFileContent = testData.getValue('FILECONTENT')

def Step1(): 
	"""
	@step      extract the content of the file
	@expected  - no exception thrown (FileNotFound for example) <p>
			   - File content must be the same as in the variable FILECONTENT
	"""
	try:
		filecontent = testData.getFileContentAsString("FILE_DATA")
	except QTasteDataException, e:
		logger.error("exception thrown: " + e.getMessage())
		testAPI.stopTest(Status.FAIL, "Exception thrown: " + e.getMessage())
	if filecontent != expectedFileContent:
		testAPI.stopTest(Status.FAIL, 'File content is different from expected.\n File content is %s and expected content is %s' % (filecontent, expectedFileContent))

doStep(Step1)
