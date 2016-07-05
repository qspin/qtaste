from controlscript_addon import *

ControlScript([
    JavaProcess( "PlayBack-Interface-FX",
                 mainClassOrJar="SUT-FX/target/qtaste-demo-sut-fx-jar-with-dependencies.jar",
                 workingDir="../demo",
                 jmxPort=10101,
                 vmArgs="-Duser.language=en -Duser.region=EN",
                 checkAfter=5,
                 useJavaGUIFX=True,
                 useJavaGUI=False,
                 useJacoco=True),
    JavaProcess( "PlayBack-Interface-FX-Bis",
                 mainClassOrJar="SUT-FX/target/qtaste-demo-sut-fx-jar-with-dependencies.jar",
		 workingDir="../demo",
                 jmxPort=10102,
                 vmArgs="-Duser.language=en -Duser.region=EN",
                 checkAfter=5,
                 useJavaGUIFX=True,
                 useJavaGUI=False,
                 useJacoco=True),
])
