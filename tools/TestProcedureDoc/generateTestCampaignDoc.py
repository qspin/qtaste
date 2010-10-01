import os.path
##
# QTaste aggregated test cases documentation generator for test procedure document.
# Usage: jython tools/TestProcedureDoc/generateTestCampaignDoc.py <test_campaign_file.xml>
# Output: test_campaign_file-doc.html, in same directory as test_campaign_file.xml
# Precondition: must be run from QTaste root directory and qtaste-kernel-deploy.jar must be in CLASSPATH
##

import sys
import os
import re

try:
    import xml.etree.ElementTree as et
except ImportError:
    import elementtree.ElementTree as et

from com.qspin.qtaste.config import StaticConfiguration
from org.apache.log4j import PropertyConfigurator
from com.qspin.qtaste.kernel.campaign import CampaignManager
from com.qspin.qtaste.testsuite.impl import MetaTestSuite

# conditional expression
IF = lambda a,b,c:(a and [b] or [c])[0]

# regular expression patterns
REMOVE_HTML_HEADERS_PATTERN = re.compile('^.*<body>(.*)</body>.*$', re.IGNORECASE | re.DOTALL)
HTML_HEADING_TAG_PATTERN = re.compile('(</?h)([23])>', re.IGNORECASE)
TEST_DATA_TABLE_PATTERN = re.compile('<h3>Test data</h3>(<table[^>]*>.*</table>)', re.IGNORECASE | re.DOTALL)
TABLE_ROW_PATTERN = re.compile('<tr>(.*?)</tr>', re.IGNORECASE | re.DOTALL)
TABLE_HEADING_PATTERN = re.compile('<th>(?:<font[^>]*>)?(.*?)(?:</font>)?</th>', re.IGNORECASE | re.DOTALL)
TABLE_DATA_PATTERN = re.compile('<td>(.*?)</td>', re.IGNORECASE | re.DOTALL)
TABLE_HEADING_OR_DATA_PATTERN = re.compile('<t[hd]>(?:<font[^>]*>)?(.*?)(?:</font>)?</t[hd]>', re.IGNORECASE | re.DOTALL)


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
    aggregatedDocFile = open(aggregatedDocFileName, 'w')
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
# leaf nodes represent testcases and have a 'testCaseDir' attribute which is the full test case directory path
# and a 'testbeds' attribute containing the comma-separated list of testbeds names
# @param campaignFileName  the test campaign file name
# @return a 'testcases' Element tree
def generateTestCasesTree(campaignFileName):
    testCasesElem = et.Element('testcases')
    campaign = CampaignManager.getInstance().readFile(campaignFileName)
    for run in campaign.getRuns():
        testbed = os.path.splitext(run.getTestbed())[0]
        for testScript in MetaTestSuite(testbed, run.getTestsuites()).getTestScripts():
            addTestCase(testScript.getTestCaseDirectory(), testbed, testCasesElem)
    return testCasesElem

##
# Add a test case to the test cases tree.
# @param testCaseDir the full test case directory path
# @param testbed the testbed on which the test case is run
# @param testCasesElem the test cases Element tree root node
def addTestCase(testCaseDir, testbed, testCasesElem):
    # add test case dir element
    testCaseDirPath = os.path.normpath(testCaseDir).split(os.sep)
    elem = testCasesElem
    for dir in testCaseDirPath:
        dirElem = elem.find(dir)
        if dirElem is None:
            elem = et.SubElement(elem, dir)
        else:
            elem = dirElem
    # set testCaseDir attribute
    elem.attrib['testCaseDir'] = testCaseDir
    # update testbeds attribute
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
            aggregateTestCaseDoc(dirName, elem.get('testCaseDir'), elem.get('testbeds').split(','), level, aggregatedDocFile)
        index = index + 1

##
# Append a test case doc to the aggregated doc file.
# @param testCaseName the test case name
# @param testCaseDir the full test case directory path
# @param testbedsList the list of testbeds on which the test case is run
# @param level the node level, starting at 1 for the first directory to handle
# @param aggregatedDocFile the aggregated doc file
def aggregateTestCaseDoc(testCaseName, testCaseDir, testbedsList, level, aggregatedDocFile):
    if len(testbedsList) == 1:
        testbedsText = 'This test case is run on the testbed <i>' + testbedsList[0] + '</i>'
    else:
        testbedsText = 'This test case is run on the following testbeds: <i>' + '</i>, <i>'.join(testbedsList) + '</i>'

    testScriptDocFileName = testCaseDir + os.sep + StaticConfiguration.TEST_SCRIPT_DOC_HTML_FILENAME
    testScriptDocFile = None
    try:
        testScriptDocFile = open(testScriptDocFileName)
        content = open(testScriptDocFileName).read()
        content = REMOVE_HTML_HEADERS_PATTERN.match(content).group(1)
        testDataTableMatch = TEST_DATA_TABLE_PATTERN.search(content)
        testDataTable = testDataTableMatch.group(1)
        testDataSection = getTransformedTestDataSection(testDataTable)
        content = content[:testDataTableMatch.start(1)] + testDataSection + content[testDataTableMatch.end(1):]
        content = content.replace('</h2>', '</h2><h3>Testbeds</h3><p>' + testbedsText + '</p>', 1)
        content = HTML_HEADING_TAG_PATTERN.sub(lambda m: m.group(1) + str(level+int(m.group(2))-1) + '>', content)
        aggregatedDocFile.write(content)
        aggregatedDocFile.write('\n\n')
        testScriptDocFile.close()
    except:
        print 'Warning: error while reading', testScriptDocFileName
        raise
        aggregatedDocFile.write('<h%d>%s</h%d><p>Couldn\'t read test script doc file %s.</p>\n\n' % (level+1, testCaseName, level+1, testScriptDocFileName))
        if testScriptDocFile:
            testScriptDocFile.close()

##
# Return transformed test data section, where test data with values common for all rows extracted of the table.
# @param testDataTable the original test data HTML table string
# @return the transformed test data section string, where test data with values common for all rows extracted of the table
def getTransformedTestDataSection(testDataTable):
    # extract test data with values common for all rows
    firstRow = True
    dataValuesList = []
    for rowMatch in TABLE_ROW_PATTERN.finditer(testDataTable):
        row = rowMatch.group(1)
        if firstRow:
            dataNames = TABLE_HEADING_PATTERN.findall(row)[1:] # skip 'Row' column
            firstRow = False
        else:
            dataValues = TABLE_DATA_PATTERN.findall(row)[1:] # skip 'Row' column
            dataValuesList.append(dataValues)
    if len(dataValuesList) == 0:
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
            testDataSection = '<table>'
            for dataIndex in sameDataValuesIndexes:
                testDataSection += '<tr><td><font size="-1"><b>' + dataNames[dataIndex] + '</b></font></td><td>' + dataValuesList[0][dataIndex]+ '</td></tr>'
            testDataSection += '</table>'
            if (len(sameDataValuesIndexes) < len(dataNames)):
                # remove test data with values for all rows
                updateTestDataTable = '<br><table border="1" cellPadding="2">'
                for rowMatch in TABLE_ROW_PATTERN.finditer(testDataTable):
                    row = rowMatch.group(1)
                    updatedRow = ''
                    dataIndex = -1
                    for dataNameOrValueMatch in TABLE_HEADING_OR_DATA_PATTERN.finditer(row):
                        if not dataIndex in sameDataValuesIndexes:
                            updatedRow += dataNameOrValueMatch.group(0)
                        dataIndex = dataIndex + 1
                    updateTestDataTable += '<tr>' + updatedRow + '</tr>'
                updateTestDataTable += '</table>'
                testDataSection += updateTestDataTable
        else:
            testDataSection = testDataTable

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