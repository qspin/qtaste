#!/usr/bin/env python
# -*- coding: utf-8 -*-

##
# QTaste aggregated test cases documentation generator for test procedure document.
# Usage: jython tools/TestProcedureDoc/generateTestCampaignDoc.py <test_campaign_file.xml>
# Output: test_campaign_file-doc.html, in same directory as test_campaign_file.xml
# Precondition: must be run from QTaste root directory and qtaste-kernel-deploy.jar must be in CLASSPATH
##

import sys, re, os.path, os.sep
from sets import Set
try:
    import xml.etree.ElementTree as et
    from xml.etree.HTMLTreeBuilder import HTMLTreeBuilder
except ImportError:
    import elementtree.ElementTree as et
    from elementtree.HTMLTreeBuilder import HTMLTreeBuilder

from com.qspin.qtaste.config import StaticConfiguration
from com.qspin.qtaste.config import TestEngineConfiguration
from org.apache.log4j import PropertyConfigurator
from com.qspin.qtaste.kernel.campaign import CampaignManager
from com.qspin.qtaste.testsuite.impl import MetaTestSuite
from org.apache.log4j import Logger, Level

# conditional expression
IF = lambda a,b,c:(a and [b] or [c])[0]

# regular expression patterns
REMOVE_HTML_HEADERS_PATTERN = re.compile('^.*<body>(.*)</body>.*$', re.IGNORECASE | re.DOTALL)
HTML_HEADING_TAG_PATTERN = re.compile('(</?h)([23])>', re.IGNORECASE)
TEST_STEPS_TABLE_PATTERN = re.compile('<h3>Steps</h3>(<table[^>]*>.*</table>)(?=<h3>Test data</h3>)', re.IGNORECASE | re.DOTALL)
TEST_DATA_TABLE_PATTERN = re.compile('<h3>Test data</h3>(<table[^>]*>.*</table>)', re.IGNORECASE | re.DOTALL)
TABLE_ROW_PATTERN = re.compile('<tr>(.*?)</tr>', re.IGNORECASE | re.DOTALL)
TABLE_HEADING_PATTERN = re.compile('<th>(?:<font[^>]*>)?(?:<code>)?(.*?)(?:</code>)?(?:</font>)?</th>', re.IGNORECASE | re.DOTALL)
TABLE_DATA_PATTERN = re.compile('<td>(.*?)</td>', re.IGNORECASE | re.DOTALL)
TABLE_HEADING_OR_DATA_PATTERN = re.compile('<t[hd]>(?:<font[^>]*>)?(?:<code>)?(.*?)(?:</code>)?(?:</font>)?</t[hd]>', re.IGNORECASE | re.DOTALL)

# configuration parameters
REMOVE_STEP_NAME_COLUMN = TestEngineConfiguration.getInstance().getBoolean('reporting.test_campaign_doc.remove_step_name_column', False)
ADD_STEP_RESULT_COLUMN = TestEngineConfiguration.getInstance().getBoolean('reporting.test_campaign_doc.add_step_result_column', False)
DUPLICATE_STEPS_PER_TEST_DATA_ROW = TestEngineConfiguration.getInstance().getBoolean('reporting.test_campaign_doc.duplicate_steps_per_test_data_row', False)


##
# Read an QTaste test campaign file and generate the aggregated test cases doc file.
# @param campaignFileName the test campaign file name
# @return the aggregated test cases doc file name
def generateTestCasesDoc(campaignFileName):
    testCasesElem = generateTestCasesTree(campaignFileName)

    # don't use unique parent directories
    rootElem = testCasesElem
    while len(rootElem) == 1 and len(rootElem[0]):
        rootElem = rootElem[0]

    # generate aggregated doc file
    aggregatedDocFileName = os.path.splitext(campaignFileName)[0] + '-doc.html'
    aggregatedDocFile = open(aggregatedDocFileName, 'wb')
    aggregatedDocFile.write('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">\n')
    aggregatedDocFile.write('<html xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">\n')
    aggregatedDocFile.write('<head><META http-equiv="Content-Type" content="text/html; charset=UTF-8"><title>Aggregated test cases documentation for test campaign %s</title></head>\n<body>\n' % os.path.splitext(os.path.basename(campaignFileName))[0])
    visit(rootElem, aggregatedDocFile)
    aggregatedDocFile.write('</body>\n</html>')
    aggregatedDocFile.close()

    return aggregatedDocFileName

##
# Read an QTaste test campaign file and return an Element tree of test cases directory hierarchy.
# The root node is named 'testcases', other nodes are named with the directory basename,
# leaf nodes represent testcases and have a 'testCaseDir' attribute which is the full test case directory path,
# a 'selectedRows' attribute containing the comma-separated list of selected data rows,
# a 'selectedRowsForTestbedX' where X is a testbed name attribute containing the comma-separated list of selected data rows for the given testbed,
# and a 'testbeds' attribute containing the comma-separated list of testbeds names
# @param campaignFileName  the test campaign file name
# @return a 'testcases' Element tree
def generateTestCasesTree(campaignFileName):
    testCasesElem = et.Element('testcases')
    campaign = CampaignManager.getInstance().readFile(campaignFileName)
    for run in campaign.getRuns():
        testbed = os.path.splitext(run.getTestbed())[0]
        for testScript in MetaTestSuite.createMetaTestSuite(testbed, run.getTestsuites()).getTestScripts():
            dataSet = testScript.getTestDataSet()
            selectedRows = dataSet.getSelectedRows();
            addTestCase(testScript.getTestCaseDirectory(), dataSet.size(), selectedRows, testbed, testCasesElem)
    return testCasesElem

##
# Add a test case to the test cases tree.
# @param testCaseDir the full test case directory path
# @param numberRows total number of test data rows
# @param selectedRows SortedSet of selected test data rows or None if all rows selected
# @param testbed the testbed on which the test case is run
# @param testCasesElem the test cases Element tree root node
def addTestCase(testCaseDir, numberRows, selectedRows, testbed, testCasesElem):
    # add test case dir element
    testCaseDirPath = os.path.normpath(testCaseDir).split(os.sep)
    elem = testCasesElem
    for dir in testCaseDirPath:
        dirElem = elem.find(dir)
        if dirElem is None:
            # insert child keeping natural order
            index = 0
            for child in list(elem):
               if child.tag > dir:
                  break
               index = index + 1
            dirElem = et.Element(dir)
            elem.insert(index, dirElem)
        elem = dirElem
    # set testCaseDir attribute
    elem.attrib['testCaseDir'] = testCaseDir

    # set/update selectedRows and selectedRowsForTestbedX attributes
    if selectedRows is None:
        selectedRows = Set(range(1, numberRows+1))
        elem.attrib['selectedRowsForTestbed' + testbed] = ','.join([str(x) for x in selectedRows])
    else:
        selectedRows = Set(selectedRows)
        elem.attrib['selectedRowsForTestbed' + testbed] = ','.join([str(x) for x in selectedRows])
        alreadySelectedRows = elem.get('selectedRows')
        if not alreadySelectedRows is None:
            alreadySelectedRows = Set([int(x) for x in alreadySelectedRows.split(',')])
            selectedRows = selectedRows.union(alreadySelectedRows)
    elem.attrib['selectedRows'] = ','.join([str(x) for x in selectedRows])

    # set/update testbeds attribute
    testbeds = elem.get('testbeds')
    if testbeds is None:
        testbeds = testbed
    else:
        testbeds = testbeds + ',' + testbed
    elem.attrib['testbeds'] = testbeds

##
# Visit a test cases tree node and append its doc to the aggregated doc file.
# @param nodeElem the test cases tree node to visit
# @param aggregatedDocFile the aggregated doc file
# @param level the node level, starting at 1 for the first directory to handle
# @param prefix the prefix to add to the string to print
def visit(nodeElem, aggregatedDocFile, level=1, prefix=''):
    index = 1
    for elem in nodeElem:
        dirName = elem.tag
        print '%s%d. %s' % (prefix, index, dirName)
        if len(elem):
            aggregatedDocFile.write('<h%d>%s</h%d>' % (level+1, dirName, level+1))
            visit(elem, aggregatedDocFile, level+1, '  ' + prefix + str(index) + '.' )
        else:
            testCaseDir = elem.get('testCaseDir')
            selectedRows = Set([int(x) for x in elem.get('selectedRows').split(',')])
            testbeds = elem.get('testbeds').split(',')
            selectedRowsForTestbeds = {}
            for testbed in testbeds:
                selectedRowsForTestbeds[testbed] = Set([int(x) for x in elem.get('selectedRowsForTestbed' + testbed).split(',')])
            aggregateTestCaseDoc(dirName, testCaseDir, selectedRows, selectedRowsForTestbeds, testbeds, level, aggregatedDocFile)
        index += 1

##
# Append a test case doc to the aggregated doc file.
# @param testCaseName the test case name
# @param testCaseDir the full test case directory path
# @param selectedRows the Set of selected rows
# @param selectedRowsForTestbedsDict the dictionary mapping testbed names to selected rows Sets
# @param testbedsList the list of testbeds on which the test case is run
# @param level the node level, starting at 1 for the first directory to handle
# @param aggregatedDocFile the aggregated doc file
def aggregateTestCaseDoc(testCaseName, testCaseDir, selectedRows, selectedRowsForTestbedsDict, testbedsList, level, aggregatedDocFile):
    if len(testbedsList) == 1:
        testbedsText = 'This test script is run on the testbed <i>' + testbedsList[0] + '</i>.'
    else:
        testbedsWithRowsSelection = []
        for testbed in testbedsList:
            selectedRowsForTestbed = selectedRowsForTestbedsDict.get(testbed)
            if selectedRowsForTestbed == selectedRows:
                testbedsWithRowsSelection.append(testbed)
            else:
                testbedsWithRowsSelection.append(testbed + ' (' + IF(len(selectedRowsForTestbed) > 1, 'rows ' , 'row ') + ', '.join([str(x) for x in selectedRowsForTestbed]) + ')')
        testbedsText = 'This test script is run on the following testbeds: <i>' + '</i>, <i>'.join(testbedsWithRowsSelection) + '</i>.'

    testScriptDocFileName = testCaseDir + os.sep + StaticConfiguration.TEST_SCRIPT_DOC_HTML_FILENAME
    testScriptDocFile = None
    try:
        testScriptDocFile = open(testScriptDocFileName, 'rb')
        content = testScriptDocFile.read()
        content = REMOVE_HTML_HEADERS_PATTERN.match(content).group(1)
        testStepsTableMatch = TEST_STEPS_TABLE_PATTERN.search(content)
        testStepsTable = testStepsTableMatch.group(1)
        if REMOVE_STEP_NAME_COLUMN or ADD_STEP_RESULT_COLUMN:
            htmlTreeBuilder = HTMLTreeBuilder(encoding = 'utf-8')
            htmlTreeBuilder.feed(testStepsTable)
            testStepsTableHtmlTree = htmlTreeBuilder.close()
            for trElem in testStepsTableHtmlTree.findall('tr'):
                if REMOVE_STEP_NAME_COLUMN:
                    del trElem[1]
                if ADD_STEP_RESULT_COLUMN:
                    tag = trElem[0].tag
                    if tag == "th":
                        et.SubElement(trElem, tag, {'width':'3%'}).text = 'Result'
                    elif tag == "td":
                        et.SubElement(trElem, tag).text = ' ' # non-breakable space
            testStepsTable = et.tostring(testStepsTableHtmlTree, 'utf-8')
        if DUPLICATE_STEPS_PER_TEST_DATA_ROW:
            contentBeforeSteps = content[:testStepsTableMatch.start(0)]
            testDataContent = content[testStepsTableMatch.end(1):]
            testDataTableMatch = TEST_DATA_TABLE_PATTERN.search(testDataContent)
            testDataTable = testDataTableMatch.group(1)
            dataNames, dataValuesList = getTestData(testDataTable)
            if len(dataValuesList) == 0:
                # no test data
                duplicatedStepsContent = '<h3>Steps</h3>' + testStepsTable
            else:
                duplicatedStepsContent = ''
                for rowId in range(1, len(dataValuesList)+1):
                    if rowId in selectedRows:
                        dataValues = dataValuesList[rowId-1]
                        testDataSection = '<b>Test data:</b><br><table cellspacing="0" cellpadding="0">'
                        for dataIndex in range(len(dataNames)):
                            testDataSection += '<tr><td width="20">&nbsp;</td><td><b><code>' + dataNames[dataIndex] + '</code></b></td><td width="20">&nbsp;</td><td>' + dataValues[dataIndex]+ '</td></tr>'
                        testDataSection += '</table>'
                        putTestDataBeforeSteps = True
                        try:
                            comment = dataValues[dataNames.index('COMMENT')]
                            if len(dataValuesList) > 1:
                                stepsTitle = '<h3>Testcase %d - &ldquo;<i>%s</i>&rdquo;</h3>' % (rowId, comment)
                            else:
                                stepsTitle = '<h3>Testcase &ldquo;<i>%s</i>&rdquo;</h3>' % comment
                        except ValueError:
                            if len(dataValuesList) > 0:
                                stepsTitle = '<h3>Testcase %d</h3>' % rowId
                            else:
                                # there is no test data row defined
                                stepsTitle = '<h3>Steps</h3>'
                                putTestDataBeforeSteps = False
                        if putTestDataBeforeSteps:
                            duplicatedStepsContent = duplicatedStepsContent + stepsTitle + testDataSection + '<br><p>' + testStepsTable
                        else:
                            duplicatedStepsContent = duplicatedStepsContent + stepsTitle + testStepsTable + '<br><p>' + testDataSection
            content = contentBeforeSteps + duplicatedStepsContent
        else:
            content = content[:testStepsTableMatch.start(1)] + testStepsTable + content[testStepsTableMatch.end(1):]
            testDataTableMatch = TEST_DATA_TABLE_PATTERN.search(content)
            testDataTable = testDataTableMatch.group(1)
            testDataSection = getTransformedTestDataSection(testDataTable, selectedRows)
            content = content[:testDataTableMatch.start(1)] + testDataSection + content[testDataTableMatch.end(1):]
        content = content.replace('</h2>', '</h2><h3>Testbeds</h3><p>' + testbedsText + '</p>', 1)
        content = HTML_HEADING_TAG_PATTERN.sub(lambda m: m.group(1) + str(level+int(m.group(2))-1) + '>', content)
        aggregatedDocFile.write(content)
        aggregatedDocFile.write('\n\n')
        testScriptDocFile.close()
    except:
        print 'Warning: error while reading', testScriptDocFileName
        print 'Exception:', sys.exc_info()[0], sys.exc_info()[1]
        print sys.exc_info()[2]
        raise
        aggregatedDocFile.write('<h%d>%s</h%d><p>Couldn\'t read test script doc file %s.</p>\n\n' % (level+1, testCaseName, level+1, testScriptDocFileName))
        if testScriptDocFile:
            testScriptDocFile.close()

##
# Get the test data.
# @param testDataTable the original test data HTML table string
# @return (list of data names, list of lists of data values)
def getTestData(testDataTable):
    firstRow = True
    dataNames = []
    dataValuesList = []
    for rowMatch in TABLE_ROW_PATTERN.finditer(testDataTable):
        row = rowMatch.group(1)
        if firstRow:
            dataNames = TABLE_HEADING_PATTERN.findall(row)[1:] # skip 'Row' column
            firstRow = False
        else:
            dataValues = TABLE_DATA_PATTERN.findall(row)[1:] # skip 'Row' column
            dataValuesList.append(dataValues)
    return (dataNames, dataValuesList)

##
# Return transformed test data section, where test data with values common for all rows extracted of the table.
# @param testDataTable the original test data HTML table string
# @param selectedRows the Set of selected rows
# @return the transformed test data section string, where test data with values common for all rows extracted of the table
def getTransformedTestDataSection(testDataTable, selectedRows):
    # extract test data with values common for all rows
    dataNames, dataValuesList = getTestData(testDataTable)
    if len(dataValuesList) == 0:
        # no test data
        testDataSection = testDataTable
    else:
        sameDataValuesIndexes = []
        for dataIndex in range(len(dataNames)):
            dataValue = dataValuesList[0][dataIndex]
            sameDataValues = True
            for dataValues in dataValuesList[1:]:
                if dataValues[dataIndex] != dataValue:
                    sameDataValues = False
            if sameDataValues:
                sameDataValuesIndexes.append(dataIndex)
        if sameDataValuesIndexes:
            testDataSection = '<table cellspacing="0" cellpadding="0">'
            for dataIndex in sameDataValuesIndexes:
                testDataSection += '<tr><td><code><b>' + dataNames[dataIndex] + '</code></b></td><td width="20">&nbsp;</td><td>' + dataValuesList[0][dataIndex]+ '</td></tr>'
            testDataSection += '</table>'
            if (len(sameDataValuesIndexes) < len(dataNames)):
                # remove test data with values for all rows
                updateTestDataTable = '<br><table border="1" cellSpacing="0" cellPadding="2">'
                rowId = 0
                for rowMatch in TABLE_ROW_PATTERN.finditer(testDataTable):
                    if rowId == 0 or rowId in selectedRows:
                        row = rowMatch.group(1)
                        updatedRow = ''
                        dataIndex = -1
                        for dataNameOrValueMatch in TABLE_HEADING_OR_DATA_PATTERN.finditer(row):
                            if not dataIndex in sameDataValuesIndexes:
                                updatedRow += dataNameOrValueMatch.group(0)
                            dataIndex += 1
                        updateTestDataTable += '<tr>' + updatedRow + '</tr>'
                    rowId += 1
                updateTestDataTable += '</table>'
                testDataSection += updateTestDataTable
        else:
            updateTestDataTable = '<table border="1" cellSpacing="0" cellPadding="2">'
            rowId = 0
            for rowMatch in TABLE_ROW_PATTERN.finditer(testDataTable):
                if rowId == 0 or rowId in selectedRows:
                    updateTestDataTable += rowMatch.group(0)
                rowId += 1
            updateTestDataTable += '</table>'
            testDataSection = updateTestDataTable

    return testDataSection

# main
if __name__ == '__main__':
    if len(sys.argv) != 2:
        print 'Usage: jython tools/TestProcedureDoc/generateTestCampaignDoc.py <test_campaign_file.xml>'
        print 'Output: test_campaign_file-doc.html, in same directory as test_campaign_file.xml'
        print 'Precondition: must be run from ATE root directory and ATE.jar must be in CLASSPATH'
        sys.exit(1)
    else:
        campaignFileName = sys.argv[1]
        print 'Generation of aggregated test cases documentation for test campaign', campaignFileName
        print
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");
        aggregatedDocFileName = generateTestCasesDoc(campaignFileName)
        print
        print 'Generated', aggregatedDocFileName, 'successfully.'
