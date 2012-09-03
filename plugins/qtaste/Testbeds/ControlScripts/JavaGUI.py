from controlscript import *
import os.sep, os.getcwd, os.mkdir, re, shutil, tempfile

ControlScript([
	JavaProcess( "JavaGUI-TestInterface",
				mainClassOrJar="com.qspin.qtaste.testapi.ui.TestInterface",
				workingDir="./testapi/target",
				classPath="qtaste-testapi-deploy.jar",
				vmArgs="-javaagent:/home/sjansse/workspaces/qtaste/plugins/javagui/target/qtaste-javagui-deploy.jar",
				jmxPort=10101,
				checkAfter=2)
])
