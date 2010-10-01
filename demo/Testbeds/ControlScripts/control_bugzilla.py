from controlscript_addon import *

ControlScript([
    VirtualBox("Bugzilla debian server", 
                nameOfVBoxImage="ate-bugzilla",
                ),
    JavaProcess("Selenium Server",
                mainClassOrJar="demo/selenium-server.jar", 
                checkAfter=5)
])
