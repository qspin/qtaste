from controlscript_addon import *

ControlScript([
    JavaProcess( "PlayBack-Interface-FX",
                 mainClassOrJar="qtaste-demo-sut-fx-jar-with-dependencies.jar",
                 workingDir="../demo/SUT-FX/target",
                 jmxPort=10101,
                 vmArgs="-Duser.language=en -Duser.region=EN",
                 checkAfter=5,
                 useJavaGUIFX=True,
                 useJavaGUI=False,
                 useJacoco=True),
    JavaProcess( "PlayBack-Interface-FX-Bis",
                 mainClassOrJar="qtaste-demo-sut-fx-jar-with-dependencies.jar",
                 workingDir="../demo/SUT-FX/target",
                 jmxPort=10102,
                 vmArgs="-Duser.language=en -Duser.region=EN",
                 checkAfter=5,
                 useJavaGUIFX=True,
                 useJavaGUI=False,
                 useJacoco=True),
])
