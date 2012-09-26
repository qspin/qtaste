from controlscript_addon import *

ControlScript([
	JavaProcess( "PlayBack-Interface",
	mainClassOrJar="com.qspin.qtaste.sutuidemo.Interface",
	workingDir="demo",
	classPath="testapi/target/qtaste-testapi-deploy.jar",
	vmArgs="-javaagent:../plugins/javagui/target/qtaste-javagui-deploy.jar",
	jmxPort=10101,
	checkAfter=5)
])
