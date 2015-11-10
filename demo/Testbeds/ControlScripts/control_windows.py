from controlscript import *

ControlScript([
    NativeProcess("Windows control native Agent",
                executable="python.exe",
                args="demo/pywinauto-0.3.8/XMLRPCServer.py",
                checkAfter=5)
])
