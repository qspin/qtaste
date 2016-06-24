##
# QTaste Test script Pythondoc XML formatter.
# Usage: pythondoc.py -f -Otestscriptdoc-xmlformatter [-Dencoding=<charset>] [-Dtestsuite_dir=<TestSuiteDir>] <all_py_scripts_in_test_suite>
# If testsuite_dir is defined, a TestSuite-doc.xml file will be generated in this directory
##

import string, os, re, codecs, java.lang
from com.qspin.qtaste.config import StaticConfiguration
from com.qspin.qtaste.util import OS

try:
    import xml.etree.ElementTree as et
    from xml.etree.ElementTree import XMLTreeBuilder as TreeBuilder
except ImportError:
    import elementtree.ElementTree as et
    from elementtree.SimpleXMLTreeBuilder import TreeBuilder


# conditional expression
IF = lambda a,b,c:(a and [b] or [c])[0]


def relpath(path, reldir):
    """Returns 'path' relative to 'reldir'."""

    # use normpath to ensure path separators are uniform
    path = os.path.normpath(path)

    # find length of reldir as prefix of path (or zero if it isn't)
    prelen = len(os.path.commonprefix((
    os.path.normcase(path),
    # add a separator to get correct prefix length
    # (normpath removes trailing separators)
    os.path.normcase(os.path.normpath(reldir)) + os.sep
    )))
    return path[prelen:]


class PythonDocGenerator:

    tags = ('name', 'version', 'preparation', 'requirement', 'data', 'step', 'expected')

    def __init__(self, options):
        self.encoding = options.get('encoding')
        if not self.encoding:
            self.encoding = 'utf-8'
        self.createTestSuiteDoc = options.has_key('testsuite_dir')
        if self.createTestSuiteDoc:
            self.testSuiteDir = os.path.abspath(options.get('testsuite_dir'))
            self.testSuite = et.Element('testsuite')
            self.testSuite.set('name', os.path.basename(self.testSuiteDir))
            self.currentNodePath = [self.testSuite]
            self.currentDirectory = ''

        self.rootTestSuitesDir = "TestSuites"
        if options.has_key('rootTestSuiteDir'):
            self.rootTestSuitesDir = options.get('rootTestSuiteDir')
        self.allImportedModulesStepsDoc = {}
        self.allImportedModulesStepsTables = {}
        self.importTestScriptPattern = re.compile('importTestScript\(("|\')([\w.]+(?:\s*[\w.]+|\.|\/)*("|\'))\)')
        self.importModulePattern = re.compile('import\s+([\w.]+(?:\s*,\s*[\w.]+)*)')
        self.importSymbolsPattern = re.compile('from\s+([\w.]+)\s+import\s+(\*|\w+(?:\s*,\s*\w+)*)')
        self.doStepPattern = re.compile('doStep\(\s*[\'"]?([\w.]+)[\'"]?(?:\s*,\s*([\w.]+))?\s*\)')
        self.doStepsPattern = re.compile('doSteps\(\s*([\w.]+)(?:\s*,\s*[\'"]\s*\[(\w*)(?:\s*-\s*(\w*))?\]\s*[\'"]\s*)?\s*\)')
        self.stepsTableDefPattern = re.compile('(\w+)\s*=\s*[\(\[]\s*(\(\s*[\'"]?\w+[\'"]?\s*,\s*\w+\s*\)(?:\s*,\s*\(\s*[\'"]?\w+[\'"]?\s*,\s*\w+\s*\))*)\s*[\)\]]')

    def save(self, module, filename):
        testScriptFileName = module.get('filename')
        self._parseTestScriptFile(testScriptFileName)
        filename = os.path.splitext(testScriptFileName)[0] + '-doc.xml'
        try:
            testScriptName = module.find('info/name').text
        except:
            testScriptName = os.path.basename(os.path.dirname(filename))
        testScript = et.Element('testscript', {'name':testScriptName, 'filename':testScriptFileName})
        if self.createTestSuiteDoc:
            relativeFilename = relpath(filename, self.testSuiteDir)
            # handle test suite directories
            directory = os.path.dirname(os.path.dirname(relativeFilename))
            self._handleDirectories(directory)
            # add name, relative filename and summary to testsuite
            testSuiteScript = et.SubElement(self.currentNodePath[-1], 'testscript', {'name':testScriptName, 'docfilename':relativeFilename})
            infoSummary = module.find('info/summary')
            if infoSummary is not None:
                testSuiteScript.append(infoSummary)
        # split data names, types and descriptions
        for elem in module.getiterator('data'):
            try:
                # check if text is included in a unique <p> element
                if (elem.text is None) and (len(elem.getchildren()) == 1):
                    p_elem = elem.find('p')
                    elem.text = p_elem.text
                    elem.tail = p_elem.tail
                    for child in p_elem.getchildren():
                        elem.append(child)
                    elem.remove(p_elem)
                (name, type, descr) = string.split(elem.text, " ", 2)
            except ValueError:
                print "Invalid data tag format: ", elem.text
                return None
            if ((len(type) > 2) and (type[0] == '[') and (type[-1] == ']')):
                type = type[1:-1]
            else:
                print "Invalid", name, "data tag type format: ", type
                return None
            descr = string.lstrip(descr)
            elem.set('name', name)
            elem.set('type', type)
            elem.text =  descr
        # add infos to testscript element
        info = module.find('info')
        if info is None:
            info = et.SubElement(testScript, 'info')
        else:
            testScript.append(info)
        # add test script version info
        et.SubElement(info, 'version').text = self._getVersion(testScriptFileName)
        # create steps functions dictionary
        for elem in module.getiterator('function'):
            if elem.find('info/step') is not None:
                name = elem.findtext('info/name')
                step = elem.find('info')
                step.tag = 'step'
                step.set('name', name)
                step.remove(step.find('description'))
                step.find('step').tag = 'description'
                self.declaredSteps[name] = step
        # add steps as executed using doStep()
        steps = et.SubElement(testScript, 'steps')
        for i in range(len(self.executedSteps)):
            stepId, stepName = self.executedSteps[i]
            declaredStep = self.declaredSteps.get(stepName)
            if declaredStep is not None:
                self._addStep(steps, stepId, declaredStep)
            else:
                print 'Warning: function step ' + stepName + ' of test script ' + testScriptName + ' is used in doStep() but not declared or not documented with @step tag'
                self._addUndefinedStep(steps, stepId, stepName)
        self._addTestData(testScriptFileName, testScript)
        self._addTestRequirement(testScriptFileName, testScript)
        tree = et.ElementTree(testScript)
        with open(filename, 'wb') as file:
            tree.write(file, self.encoding)
        return filename

    def done(self):
        if self.createTestSuiteDoc:
            filename = self.testSuiteDir + r'%sTestSuite-doc.xml' % os.sep
            print 'Saving', filename
            tree = et.ElementTree(self.testSuite)
            with open(filename, 'wb') as file:
                tree.write(file, self.encoding)
            return filename

    # parse test script file for modules imported from pythonlib directories (self.importedModulesStepsDoc)
    # and executed steps (self.executedSteps)
    def _parseTestScriptFile(self, filename):
        import re
        with open(filename, 'r') as file:
            self.executedSteps = []
            self.declaredSteps = {}
            self.declaredStepsTables = {}
            stepsTablesParsed = False
            pythonLibDirectories = self._getPythonLibDirectories(filename)
            content = ''
            for line in file:
                if line:
                    line = line.split('#', 1)[0]  # remove comment
                    if line:
                        content += line
                        match = self.importSymbolsPattern.match(line)
                        if match:
                            module = match.group(1)
                            modulePath = module.replace(".", os.sep)
                            symbols = re.split('\s*,\s*', match.group(2))
                            for symbol in symbols :
                                if symbol:
                                    if symbol == '*':
                                        self._addFullyImportedModuleStepsDocAndTables(modulePath, pythonLibDirectories)
                                    else:
                                        self._addImportedStepDocOrTable(symbol, modulePath, pythonLibDirectories)
                        else:
                            for match in self.importModulePattern.finditer(line):
                                modules = re.split('\s*,\s*', match.group(1))
                                for module in modules:
                                    modulePath = module.replace(".", os.sep)
                                    self._addImportedModuleStepsDocAndTables(modulePath, pythonLibDirectories)

                        if self.importTestScriptPattern.match(line):
                            modulePath = line.split('(')[1].split(')')[0].replace('"', '')
                            target = filename.replace("TestScript.py", "") + "../" + modulePath
                            target = target.replace("/",os.sep)
                            self._addImportedTestScriptModuleStepsDocAndTables(target.split(os.sep)[-1], target)

                        for match in self.doStepPattern.finditer(line):
                            if match.group(2):
                                stepName = match.group(2)
                                stepId = match.group(1)
                            else:
                                stepName = match.group(1)
                                stepId = str(len(self.executedSteps)+1)
                            self.executedSteps.append((stepId, stepName))
                        for match in self.doStepsPattern.finditer(line):
                            # if not yet done parse content for steps table declarations
                            if not stepsTablesParsed:
                                for defMatch in self.stepsTableDefPattern.finditer(content):
                                    stepsTableName = defMatch.group(1)
                                    stepsIdAndNames = re.split('\W+', defMatch.group(2))[1:-1]
                                    stepsId = stepsIdAndNames[::2]
                                    stepsNames = stepsIdAndNames[1::2]
                                    self.declaredStepsTables[stepsTableName] = zip(stepsId, stepsNames)
                                stepsTablesParsed = True

                            stepsTableName = match.group(1)
                            selectorStart = match.group(2)
                            selectorEnd = match.group(3)
                            if selectorEnd is None:
                                selectorEnd = selectorStart
                            stepsTable = self.declaredStepsTables.get(stepsTableName)
                            if stepsTable:
                                self._addExecutedSteps(stepsTable, selectorStart, selectorEnd)
                            else:
                                print 'Warning: steps table ' + stepsTableName + ' of test script ' + filename + ' is used in doSteps() but not declared'

    # get python libs directories for given test script
    def _getPythonLibDirectories(self, filename):
        directory = os.path.realpath(filename)
        testSuitesDirectory = os.path.realpath(self.rootTestSuitesDir)
        pythonLibDirectories = []
        previous = ""
        while directory != testSuitesDirectory and directory != previous:
            previous = str(directory)
            directory = os.path.dirname(directory)
            pythonLibDir = directory + os.sep + "pythonlib"
            if (os.path.isdir(pythonLibDir)):
                pythonLibDirectories.append(directory + os.sep + "pythonlib")
        return pythonLibDirectories

    def _addImportedModuleStepsDocAndTables(self, moduleName, pythonLibDirectories):
        stepsDocDict, stepsTablesDict = self._getModuleStepsDocAndTables(moduleName, pythonLibDirectories)
        if stepsDocDict:
            for stepName in stepsDocDict:
                self.declaredSteps[moduleName + '.' + stepName] = stepsDocDict[stepName]
        if stepsTablesDict:
            for stepsTableName in stepsTablesDict:
                self.declaredStepsTables[moduleName + '.' + stepsTableName] = [(stepId, moduleName + '.' + stepName) for stepId, stepName in stepsTablesDict[stepsTableName]]

    def _addImportedTestScriptModuleStepsDocAndTables(self, moduleName, directory):
        #create the step-doc.xml file for the imported test script
        testScriptFilePath = directory.replace("/", os.sep) + os.sep + StaticConfiguration.TEST_SCRIPT_FILENAME
        shellScriptExtension = IF(OS.getType() == OS.Type.WINDOWS, ".bat", ".sh")
        generatorScript = "generate-TestStepsModules-doc" + shellScriptExtension
        command = StaticConfiguration.QTASTE_ROOT + os.sep + "bin" + os.sep + generatorScript + ' "' + testScriptFilePath + '"'
        os.system(command)

        stepsDocDict, stepsTablesDict = self._getModuleStepsDocAndTables("TestScript", [directory])
        if stepsDocDict:
            for stepName in stepsDocDict:
                self.declaredSteps[moduleName + '.' + stepName] = stepsDocDict[stepName]
        if stepsTablesDict:
            for stepsTableName in stepsTablesDict:
                self.declaredStepsTables[moduleName + '.' + stepsTableName] = [(stepId, moduleName + '.' + stepName) for stepId, stepName in stepsTablesDict[stepsTableName]]

    def _addFullyImportedModuleStepsDocAndTables(self, moduleName, pythonLibDirectories):
        stepsDocDict, stepsTablesDict = self._getModuleStepsDocAndTables(moduleName, pythonLibDirectories)
        if stepsDocDict:
            for stepName in stepsDocDict:
                self.declaredSteps[stepName] = stepsDocDict[stepName]
        if stepsTablesDict:
            for stepsTableName in stepsTablesDict:
                self.declaredStepsTables[stepsTableName] = stepsTablesDict[stepsTableName]

    def _addImportedStepDocOrTable(self, symbol, moduleName, pythonLibDirectories):
        stepsDocDict, stepsTablesDict = self._getModuleStepsDocAndTables(moduleName, pythonLibDirectories)
        if stepsDocDict:
            stepDoc = stepsDocDict.get(symbol)
            if stepDoc:
                self.declaredSteps[symbol] = stepDoc
        if stepsTablesDict:
            stepsTable = stepsTablesDict.get(symbol)
            if stepsTable:
                self.declaredStepsTables[symbol] = [(stepId, moduleName + '.' + stepName) for stepId, stepName in stepsTable]
                # also automatically import used steps
                for stepId, stepName in stepsTable:
                    self.declaredSteps[moduleName + '.' + stepName] = stepsDocDict.get(stepName)

    def _getModuleStepsDocAndTables(self, moduleName, pythonLibDirectories):
        for pythonLibDirectory in pythonLibDirectories:
            stepsDocFileName = pythonLibDirectory + os.sep + moduleName + "-steps-doc.xml"
            stepsDocDict = self.allImportedModulesStepsDoc.get(stepsDocFileName)
            stepsTablesDict = self.allImportedModulesStepsTables.get(stepsDocFileName)
            if stepsDocDict and stepsTablesDict:
                return (stepsDocDict, stepsTablesDict)
            else:
                if os.path.exists(stepsDocFileName):
                    stepsModuleDocTree = et.parse(stepsDocFileName, TreeBuilder())
                    stepsDocDict = {}
                    stepsDocTree = stepsModuleDocTree.find('steps')
                    if stepsDocTree is not None:
                        for stepElem in stepsDocTree.getiterator('step'):
                            stepName = stepElem.get('name')
                            stepsDocDict[stepName] = stepElem
                    stepsTablesDict = {}
                    stepsTablesTree = stepsModuleDocTree.find('stepsTables')
                    if stepsTablesTree is not None:
                        for stepsTableElem in stepsTablesTree.getiterator('stepsTable'):
                            stepsTableName = stepsTableElem.get('name')
                            stepsTable = []
                            for stepElem in stepsTableElem.getiterator('step'):
                                stepId = stepElem.get('id')
                                stepName = stepElem.get('name')
                                stepsTable.append((stepId, stepName))
                            stepsTablesDict[stepsTableName] = stepsTable
                    # store doc dicts to avoid parsing them again
                    self.allImportedModulesStepsDoc[stepsDocFileName] = stepsDocDict
                    self.allImportedModulesStepsTables[stepsDocFileName] = stepsTablesDict
                    return (stepsDocDict, stepsTablesDict)
        return (None, None)

    def _addExecutedSteps(self, stepsTable, selectorStart, selectorEnd):
        isSelected = not selectorStart
        for (stepId, stepName) in stepsTable:
            if selectorStart == stepId:
                isSelected = True
            if isSelected:
                self.executedSteps.append((stepId, stepName))
            if selectorEnd == stepId:
                break

    # add a step to steps with given id
    def _addStep(self, steps, stepId, declaredStep):
        stepName = declaredStep.get('name')
        stepElement = et.SubElement(steps, 'step' , {'id':stepId, 'name':stepName})
        description = declaredStep.find('description')
        expected = declaredStep.find('expected')
        stepElement.append(description)
        if expected is not None:
            stepElement.append(expected)

    # add a undefined step to steps with given id, and name
    def _addUndefinedStep(self, steps, stepId, stepName):
        stepElement = et.SubElement(steps, 'step' , {'id':str(stepId), 'name':stepName})
        et.SubElement(stepElement, 'description').text = 'Undefined'

    def _handleDirectories(self, newDirectory):
        if not newDirectory == self.currentDirectory:
            commonDirPrefix = os.path.dirname(os.path.commonprefix([self.currentDirectory, newDirectory]))
            if commonDirPrefix:
                directoriesToLeave = self.currentDirectory[len(commonDirPrefix)+1:].split(os.sep)
                directoriesToEnter = newDirectory[len(commonDirPrefix)+1:].split(os.sep)
            else:
                if self.currentDirectory:
                    directoriesToLeave = self.currentDirectory.split(os.sep)
                else:
                    directoriesToLeave = []
                directoriesToEnter = newDirectory.split(os.sep)
            if directoriesToLeave:
                numberDirectoriesToLeave = len(directoriesToLeave)
                del self.currentNodePath[-numberDirectoriesToLeave:]
            for dir in directoriesToEnter:
                self.currentNodePath.append(et.SubElement(self.currentNodePath[-1], 'directory', {'name':dir}))
            self.currentDirectory = newDirectory

    def _addTestData(self, testScriptFileName, testScript):
        testData = et.SubElement(testScript, 'testdata')
        dataFileName = os.path.dirname(testScriptFileName) + os.sep + 'TestData.csv'
        try:
            with open(dataFileName, 'rb') as dataFile:
                dataNames = dataFile.readline().rstrip('\r\n').split(';')
                testDataNames = et.SubElement(testData, 'names')
                for dataName in dataNames:
                    if dataName:
                        et.SubElement(testDataNames, 'name').text = unicode(dataName, 'utf-8')
                row = 1
                for dataLine in dataFile:
                    if not dataLine.startswith('#'):
                        dataValues = dataLine.rstrip('\r\n').split(';')
                        notEmpty = 0
                        for dataValue in dataValues:
                            notEmpty += len(dataValue)
                        if notEmpty:
                            testDataValues = et.SubElement(testData, 'row', {'id':str(row)})
                            for dataName, dataValue in zip(dataNames, dataValues):
                                if dataName:
                                    et.SubElement(testDataValues, 'value').text = unicode(dataValue, 'utf-8')
                    row = row + 1
        except IOError:
            pass


    def _addTestRequirement(self, testScriptFileName, testScript):
        testRequirement = et.SubElement(testScript, 'testRequirement')
        requirementFileName = os.path.dirname(testScriptFileName) + os.sep + 'Req.xml'
        try:
            requirementFile = et.parse(requirementFileName, TreeBuilder())
            root = requirementFile.getroot()
            for requirement in root.getiterator('REQ'):
                xmlReq = et.SubElement(testRequirement, 'REQ')
                et.SubElement(xmlReq, 'ID').text = requirement.get('id')
                for desc in requirement.getiterator('REQ_DESCRIPTION'):
                    et.SubElement(xmlReq, 'DESCRIPTION').text = desc.text
        except IOError:
            pass

    def _getVersion(self, testScriptFileName):
        from org.apache.log4j import Logger as _Logger, Level as _Level
        # set log4j logger level to ERROR
        rootLogger = _Logger.getRootLogger()
        rootLevel = rootLogger.getLevel()
        rootLogger.setLevel(_Level.ERROR)
        from com.qspin.qtaste.util.versioncontrol import VersionControl
        version = VersionControl.getInstance().getTestApiVersion(os.path.dirname(testScriptFileName))
        rootLogger.setLevel(rootLevel)
        return version
