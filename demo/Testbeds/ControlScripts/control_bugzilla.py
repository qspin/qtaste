from controlscript_addon import *

ControlScript([
    VirtualBox("Bugzilla debian server", 
                nameOfVBoxImage="ate-bugzilla",
                ),
    JavaProcess("Selenium Server",
                mainClassOrJar="demo/selenium-server-standalone-2.25.0.jar", 
                checkAfter=5)
])
