from controlscript import *

ControlScript([
    JavaProcess("Selenium Server",				 
                mainClassOrJar="demo/selenium-server.jar", 
                checkAfter=5)
])
