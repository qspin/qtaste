##
# QTaste Test API installation packager.
# Usage: packager_QTaste_TestAPI.py

import os, sys
from packaging_tools import IF, updateSvnRepository, checkSvnUncommittedChanges, getJarVersion, createArchive, commandSeparator

print 'Rebuilding QTaste demo project'
error = os.system('cd demo & build')
if error:
	print "Warning: Error while rebuilding QTaste testapi project"
	sys.exit(1)

print 'Generating test suites documentation\n'
error = os.system(os.getenv('QTASTE_ROOT') + r'\bin\generate-TestSuites-doc.bat')
if error:
	print 'Cannot generate test suites documentation'
	sys.exit(1)


# Files or directories to be included in package
# The '*' is used to include only the content of the specified directory
# The '**' is used to include recursively the content of the specified directory and all the sub-directories
inclusions = [  'demo/testapi/target/qtaste-testapi-deploy.jar',
				'demo/testapi/target/TestAPI-doc/**',
				'demo/pywinauto-0.3.8/**',
				'demo/COPYING',
				'demo/COPYING.LESSER',
				'demo/Testbeds/**',
				'demo/TestSuites/**',
				'demo/selenium-server.jar',
				'demo/startUI.cmd',
				'demo/startUI.sh',
				'demo/TestCampaigns/**',
				'doc/third_products/**']

# Files or directories to be excluded from package
# '*' matches any character but the directory separator (name or part of name of directory or file) 
# '**' matches a path of zero, one or several directories
exclusions = [  '**/Thumbs.db']

packageName = getJarVersion('demo/testapi/target/qtaste-testapi-deploy.jar') + '.zip'
print 'Creating QTaste Test API installation package ' + packageName + ' \n'
createArchive(packageName, inclusions, exclusions)
