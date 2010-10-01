##
# QTaste Test API installation packager.
# Usage: packager_QTaste_TestAPI.py

import os, sys
from packaging_tools import IF, updateSvnRepository, checkSvnUncommittedChanges, getJarVersion, createArchive, commandSeparator

# Files or directories to be included in package
# The '*' is used to include only the content of the specified directory
# The '**' is used to include recursively the content of the specified directory and all the sub-directories
inclusions = [  'demo/testapi/src/**',
				'demo/testapi/pom.xml',
				'demo/*',
				'demo/pywinauto-0.3.8/**',
				'demo/Testbeds/**',
				'demo/TestSuites/**',
				'demo/TestCampaigns/**',
				'doc/third_products/**']

# Files or directories to be excluded from package
# '*' matches any character but the directory separator (name or part of name of directory or file) 
# '**' matches a path of zero, one or several directories
exclusions = [  '**/Thumbs.db']

# The testapi has to be compiled in order to get the version from the jar file
packageName = getJarVersion('demo/testapi/target/qtaste-testapi-deploy.jar') + '_src.zip'
print 'Creating QTaste Test API installation package ' + packageName + ' \n'
createArchive(packageName, inclusions, exclusions)
