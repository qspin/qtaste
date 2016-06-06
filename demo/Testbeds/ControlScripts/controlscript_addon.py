##
# Control script Addon jython module.
#
# This module contains extention of the ControlScript class:
# - VirtualBox: this extention class is to be used to control Sun VirtualBox images.
##

from controlscript import *
import time

class ControlScriptAddon(ControlScript):
    """ Control script Addon"""
    def __init__(self, controlActions):
        """
        Initialize ControlScript object.
        Store controlActions in self.controlActions,
        store additional command-line arguments (arguments except first one) in self.arguments,
        store TESTBED environment variable in self.testbed,
        and execute start() or stop() following the value of the first command-line argument (must be 'start' or 'stop')
        @param controlActions sequence of ControlAction (list or tuple)
        """
        ControlScript.__init__(self, controlActions)

class VirtualBox(ControlAction):
    """ Control script action for starting/stopping a Virtual Box image """
    def __init__(self, description, nameOfVBoxImage, active=True):
        """
        Initialize VirtualBox object
        @param description control script action description, also used as window title
        @param nameOfVBoxImage the sun virtual box image id to be started
        @param args arguments to pass to the application or None if no argument
        @param workingDir working directory to start process in, defaults to QTaste root directory
        """
        ControlAction.__init__(self, description, active)
        self.callerScript = traceback.format_stack()[0].split("\"")[1]
        self.nameOfVBoxImage = nameOfVBoxImage

    def dumpDataType(self, prefix, writer):
        """ Method called on start. It dumps the data type. to be overridden by subclasses """
        super(VirtualBox, self).dumpDataType(prefix, writer)
        writer.write(prefix + ".nameOfVBoxImage=string\n")

    def dump(self, writer):
        """ Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
        super(VirtualBox, self).dump(writer)
        writer.write(str(self.caID) + ".nameOfVBoxImage=\"" + str(self.nameOfVBoxImage) + "\"\n")

    def start(self):
        # the VBoxManage command has to be in the PATH ...
        commandArguments = ['VBoxManage','startvm',self.nameOfVBoxImage]
        print "Starting " + self.description + "..."
        print commandArguments;
        self.executeCommand(commandArguments);
        time.sleep(30)
        print
        
    def stop(self):
        commandArguments = ['VBoxManage', 'controlvm', self.nameOfVBoxImage, 'poweroff']
        print "Stopping " + self.description + "..."
        print commandArguments;
        self.executeCommand(commandArguments);
        commandArguments = ['VBoxManage', 'snapshot', self.nameOfVBoxImage, 'restorecurrent']
        self.executeCommand(commandArguments);
        print
