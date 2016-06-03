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

##
# QTaste Data driven test: Check the file loading.
# <p>
# This test case has the goal to verify that QTaste load a file as defined in the testdata for variables begining by "FILE_".
# QTaste supports the filename specified in absolute + relative.
# @preparation None
# @data FILE_DATA_UTF_8 [String] name of the UTF-8 data file (relative or absolute)
# @data FILE_DATA_ISO_8859_1 [String] name of the ISO-8859-1 data file (relative or absolute)
# @data FILECONTENT [String] Expected file content of the file.
##

from qtaste import *

expectedFileContent = testData.getValue('FILECONTENT')

def Step1():
    """
    @step      extract the content of the files
    @expected  - no exception thrown (FileNotFound for example) <p>
               - File content must be the same as in the variable FILECONTENT
    """
    try:
        fileContent = testData.getFileContentAsString("FILE_DATA_UTF_8")
        fileContentUtf8 = testData.getFileContentAsString("FILE_DATA_UTF_8", "UTF-8")
        fileContentLatin1 = testData.getFileContentAsString("FILE_DATA_ISO_8859_1", "ISO-8859-1")
    except QTasteDataException, e:
        testAPI.stopTest(Status.FAIL, "Exception thrown: " + e.getMessage())

    if fileContent != expectedFileContent:
        testAPI.stopTest(Status.FAIL, 'File content is different from expected.\n File content is %s and expected content is %s' % (fileContent, expectedFileContent))
    if fileContentUtf8 != expectedFileContent:
        testAPI.stopTest(Status.FAIL, 'File content (UTF-8) is different from expected.\n File content is %s and expected content is %s' % (fileContentUtf8, expectedFileContent))
    if fileContentLatin1 != expectedFileContent:
        testAPI.stopTest(Status.FAIL, 'File content (ISO-8859-1) is different from expected.\n File content is %s and expected content is %s' % (fileContentLatin1, expectedFileContent))

doStep(Step1)
