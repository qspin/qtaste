from controlscript_addon import *

ControlScript([





VirtualBox(nameOfVBoxImage="ate-bugzilla",
	description="Bugzilla debian server",
	active=True,),





JavaProcess(mainClassOrJar="demo/selenium-server-standalone-2.25.0.jar",
	active=True,
	workingDir="/home/sjansse/workspaces/qtaste_branches/addon/",
	useJavaGUI=False,
	checkAfter=5,
	description="Selenium Server",
	useJacoco=False,),
])
