from controlscript_addon import *

ControlScript([
    JavaProcess( "Sikuli",
                 mainClassOrJar="qtaste-sikuli-deploy.jar",
                 workingDir="../plugins/SUT/",
                 jmxPort=10101,
                 vmArgs="-Duser.language=en -Duser.region=EN",
                 checkAfter=5),
])
