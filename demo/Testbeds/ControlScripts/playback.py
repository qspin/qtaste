from controlscript_addon import *

ControlScript([
	JavaProcess( "PlayBack-Interface",
	mainClassOrJar="com.qspin.qtaste.sutuidemo.Interface",
	workingDir="demo",
	classPath="testapi/target/qtaste-testapi-deploy.jar",
	vmArgs="-javaagent:../plugins/javagui/target/qtaste-javagui-deploy.jar",
	#vmArgs="-javaagent:../plugins/recorder/target/qtaste-recorder-deploy.jar=/home/sjansse/filter.xml",
	jmxPort=10101,
	checkAfter=5)
])
