##
# QTaste Python steps module Pythondoc XML formatter.
# Usage: pythondoc.py -f -Ostepsmoduledoc-xmlformatter <python_modules_files>
#    or  stepsmoduledoc-xmlformatter.py <python_modules_files | python_modules_root_dir>
# In the latter form, documentation is generated only if needed (non-existent or modified)
##

import string, os, re, sys, glob

try:
	import xml.etree.ElementTree as et
except ImportError:
	import elementtree.ElementTree as et

JYTHON_HOME = os.getenv('QTASTE_ROOT') + '/tools/jython/lib'
	
class PythonDocGenerator:

	tags = ('name', 'preparation', 'data', 'step', 'expected')

	def __init__(self, options):
		self.encoding = options.get('encoding')
		self.stepsTableDefPattern = re.compile('(\w+)\s*=\s*[\(\[]\s*(\(\s*[\'"]?\w+[\'"]?\s*,\s*\w+\s*\)(?:\s*,\s*\(\s*[\'"]?\w+[\'"]?\s*,\s*\w+\s*\))*)\s*[\)\]]')

	def save(self, module, filename):
		moduleFileName = module.get('filename')
		moduleFileNameWithoutExtension = os.path.splitext(moduleFileName)[0]
		filename = moduleFileNameWithoutExtension + '-steps-doc.xml'
		moduleName = os.path.splitext(os.path.basename(moduleFileName))[0]
		self._parseStepsModuleFile(moduleFileName)
		stepsModuleElement = et.Element('stepsModule', {'name':moduleName, 'filename':moduleFileName})
		# add infos to stepsModule element
		info = module.find('info')
		if not info is None:
			stepsModuleElement.append(info)
		stepsElement = None
		# add steps to stepsModule element
		for elem in module.getiterator('function'):
			if not elem.find('info/step') is None:
				name = elem.findtext('info/name')
				info = elem.find('info')
				info.remove(info.find('description'))
				info.find('step').tag = 'description'
				description = elem.find('info/description')
				expected = elem.find('info/expected')
				if not stepsElement:
					stepsElement = et.SubElement(stepsModuleElement, 'steps')
				stepElement = et.SubElement(stepsElement, 'step', {'name':name})
				stepElement.append(description)
				if not expected is None:
					stepElement.append(expected)
		if self.stepsTables:
			stepsTablesElement = et.SubElement(stepsModuleElement, 'stepsTables')
			for stepsTableName in self.stepsTablesNames:
				stepsTable = self.stepsTables[stepsTableName]
				stepsTableElement = et.SubElement(stepsTablesElement, 'stepsTable', {'name':stepsTableName})
				for stepId, stepName in stepsTable:
					et.SubElement(stepsTableElement, 'step', {'id':stepId, 'name':stepName})
		tree = et.ElementTree(stepsModuleElement)
		file = open(filename, 'wb')
		tree.write(file, self.encoding)
		file.close()
		return filename

	def _parseStepsModuleFile(self, filename):
		file = open(filename, 'rb')
		content = ''
		for line in file:
			if line:
				line = line.split('#', 1)[0]  # remove comment
				if line:
					content += line
		file.close()
		self.stepsTables = {}
		self.stepsTablesNames = []
		for match in self.stepsTableDefPattern.finditer(content):
			stepsTableName = match.group(1)
			stepsIdAndNames = re.split('\W+', match.group(2))[1:-1]
			stepsId = stepsIdAndNames[::2]
			stepsNames = stepsIdAndNames[1::2]
			self.stepsTables[stepsTableName] = zip(stepsId, stepsNames)
			self.stepsTablesNames.append(stepsTableName)

	def done(self):
		pass


def checkForModifiedFiles(fileOrDir, modifiedFiles):
	if os.path.isdir(fileOrDir):
		checkForModifiedFilesInDir(fileOrDir, modifiedFiles)
	elif os.path.isfile(fileOrDir):
		checkIfFileModified(fileOrDir, modifiedFiles)

def checkIfFileModified(file, modifiedFiles):
	docFile = os.path.splitext(file)[0] + '-steps-doc.xml'
	if not os.path.exists(docFile) or os.path.getmtime(docFile) < os.path.getmtime(file):
		modifiedFiles.append(file)

def checkForModifiedFilesInDir(dirname, modifiedFiles):
	for subdir in os.listdir(dirname):
		full_subdir = dirname + os.sep + subdir
		if os.path.isdir(full_subdir):
			if subdir == 'pythonlib':
				for file in glob.glob(full_subdir + os.sep + '*.py'):
					checkIfFileModified(file, modifiedFiles)                    
			elif subdir != '.svn':
				checkForModifiedFilesInDir(full_subdir, modifiedFiles)

# standalone execution
if __name__ == '__main__':
	moduleName = os.path.splitext(os.path.basename(sys.argv[0]))[0]
	filesOrDirs = sys.argv[1:]
	modifiedFiles = []
	for fileOrDir in filesOrDirs:
		checkForModifiedFiles(fileOrDir, modifiedFiles)
	if modifiedFiles:
		print 'Generating test steps doc for files:', ', '.join(modifiedFiles)
		sys.argv[1:]= ['-f', '-s', '-O' + moduleName]
		sys.argv.extend(modifiedFiles)
		execfile(JYTHON_HOME + '/Lib/pythondoc.py')
	else:
		print 'No test steps doc to generate'
