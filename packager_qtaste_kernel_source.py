##
# QTaste Kernel installation packager for the kernel source.
# Usage: packager_QTaste_Kernel_Source.py

import os, sys
from packaging_tools import IF, updateSvnRepository, checkSvnUncommittedChanges, getJarVersion, createArchive, commandSeparator

# Files or directories to be included in package
# The '*' is used to include only the content of the specified directory
# The '**' is used to include recursively the content of the specified directory and all the sub-directories
inclusions = [  'bin/*',
				'pom.xml',
				'clean.*',
				'packag*.*',
				'COPYING',
				'COPYING.LESSER',
				'kernel/*',
				'kernel/src/**',
				'testapi-parent/*',
				'testapi-parent/testapi-deploy/*',
				'testapi-parent/testapi-deploy/src/**',
				'toolbox/*',
				'toolbox/testapi/*',
				'toolbox/testapi/src/**',
				'lib/**',
				'conf/**',
				'doc/**',
				'simulators-doc/**',
				'tools/**',
				'Testbeds/**',
				'TestSuites/**',
				'plugins/',
				'TestCampaigns/**']

# Files or directories to be excluded from package
# '*' matches any character but the directory separator (name or part of name of directory or file) 
# '**' matches a path of zero, one or several directories
exclusions = [  'conf/gui.xml',
				'tools/jython/lib/cachedir',
				'tools/jython/lib/**/*.class',
				'tools/TestScriptDoc/*.class',
				'**/Thumbs.db']

# The kernel has to be compiled in order to get the version from the jar file
packageName = getJarVersion('kernel/target/qtaste-kernel-deploy.jar') + '_src.zip'
print 'Creating QTaste installation package ' + packageName + ' \n'
createArchive(packageName, inclusions, exclusions)
