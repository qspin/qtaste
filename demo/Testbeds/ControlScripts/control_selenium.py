from controlscript import *

ControlScript([
    JavaProcess("Selenium Server",				 
                mainClassOrJar="demo/selenium-server-standalone-2.35.0.jar", 
                checkAfter=5)
])
