##
# QTaste Kernel installation packager.
# Usage: packager_QTaste_Kernel.py

import os, sys
from packaging_tools import IF, updateSvnRepository, checkSvnUncommittedChanges, getJarVersion, createArchive, commandSeparator

print 'Rebuilding QTaste kernel project'
error = os.system('cd kernel & build')
if error:
	print "Warning: Error while rebuilding QTaste kernel project"
	sys.exit(1)
	

# Files or directories to be included in package
# The '*' is used to include only the content of the specified directory
# The '**' is used to include recursively the content of the specified directory and all the sub-directories
inclusions = [  'bin/*',
				'kernel/target/qtaste-kernel-deploy.jar',
				'lib/**',
				'COPYING',
				'COPYING.LESSER',
				'conf/**',
				'doc/**',
				'tools/**',
				'Testbeds/*',
				'Testbeds/ControlScripts/*',
				'TestSuites/**',
				'plugins/',
				'TestCampaigns/*']

# Files or directories to be excluded from package
# '*' matches any character but the directory separator (name or part of name of directory or file) 
# '**' matches a path of zero, one or several directories
exclusions = [  'conf/gui.xml',
				'tools/jython/lib/cachedir',
				'tools/jython/lib/**/*.class',
				'tools/TestScriptDoc/*.class',
				'**/Thumbs.db']

packageName = getJarVersion('kernel/target/qtaste-kernel-deploy.jar') + '.zip'
print 'Creating QTaste installation package ' + packageName + ' \n'
createArchive(packageName, inclusions, exclusions)
