"""Application wrapper for qtapp"""
 
from strongwind import *
import os

# class to represent the application
class MainWindow(accessibles.Application):
    def __init__(self, accessible, subproc=None):
        'Get a reference to the main window'
        super(MainWindow, self).__init__(accessible, subproc)
        self.findFrame("SUT GUI Demonstration controlled by QTaste", logName="MainWindow")


def launchQtApp(exe=None):
    """Launch qtapp with accessibility enabled """
 
    # specify the path to the testable application manually if it isn't provide
    # as a function argument
    if exe is None:
        exe = '/home/remy/QSpin/workspace/qtaste_svn/branches/qt-demo/qt-demo/qtapp/bin/qtapp'
 
    # raise an exception if the path provided does not exist
    if not os.path.exists(exe):
      raise IOError, "%s does not exist" % exe
 
    # see launchApplication in Strongwind's cache.py file to see what is going
    # on here if you need to pass more information (e.g., command-line
    # arguments) to your testable application to start it.
    args = [exe]
    (app, subproc) = cache.launchApplication(args=args, name='qtapp')
 
    # get an object of the accessible frame and return it    
    mainWindow = MainWindow(app, subproc)
    cache.addApplication(mainWindow)
    mainWindow.mainWindowFrame.app = mainWindow
    return mainWindow


