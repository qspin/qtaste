from controlscript import *

ControlScript([
    JavaProcess("Selenium Server",				 
                mainClassOrJar="demo/selenium-server-standalone-2.25.0.jar", 
                checkAfter=5)
])
