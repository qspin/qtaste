#
# Control script jython module.
#
# See classes documentation for more details
#

import os as _os
import signal as _signal
import sys as _sys
import re as _re
import fileinput as _fileinput
import time as _time
import datetime as _datetime
import subprocess as _subprocess
import tempfile as _tempfile
import traceback

from org.apache.log4j import Logger as _Logger, Level as _Level
from com.qspin.qtaste.util import OS as _OS
from com.qspin.qtaste.tcom.rlogin import RLogin as _RLogin

#**************************************************************
# Global variables & Initialization
#**************************************************************

# set log4j logger level to WARN
_Logger.getRootLogger().setLevel(_Level.WARN)

# conditional expression
_IF = lambda a,b,c:(a and [b] or [c])[0]

# check script arguments
if len(_sys.argv) <= 1 or _sys.argv[1].lower() not in ['start', 'stop']:
    print >> _sys.stderr, "Invalid syntax: the first argument of a control script must be 'start' or 'stop'"
    _sys.exit(-1)

# the control script action 'start' or 'stop' is provided as argument of the script
start = (_sys.argv[1].lower() == 'start')

# others scripts arguments
arguments = _sys.argv[2:]

# QTaste root directory
qtasteRootDirectory = _os.path.abspath(_os.getenv("QTASTE_ROOT") + "/")

# QTaste control script debug flag
verbose = _os.environ.get('QTASTE_CONTROL_SCRIPT_VERBOSE')

def print_verbose(message):
    """
    Print a message if the verbose mode is activated
    """
    if verbose:
        print message

#**************************************************************
# Generic Classes
#**************************************************************

#--------------------------------------------------------------
class ControlScript(object):
    """ 
    This is the main class to be used at the main control script file.
    A control script instance manages a list of control actions. 
    When the control script starts, it starts all the control actions from its list.
    When the control script stops, it stops all the control actions from its list.
    """

    def __init__(self, controlActions):
        """
        Initialize a ControlScript object and execute start() or stop() following the value 
        of the first command-line argument (must be 'start' or 'stop').
        @param controlActions sequence of ControlAction (list or tuple) 
        """
        caller = traceback.format_stack()[0].split("\"")[1]
        
        self.controlActions  = controlActions
        self.callerScript    = caller.split("/")[len(caller.split("/")) - 1]
        self.callerDirectory = caller.replace(self.callerScript, "")

        # execute the control script action    
        if start:
            self.start()
        else:
            self.stop()
        
    def start(self):
        """ 
        Start all the control actions in the defined order.
        If an action failed, the control script stops starting the control actions.
        """

        # dump data types and data values of every control actions in a file <CallerScript>.param
        try:
            writer = open(self.callerDirectory + _os.sep + self.callerScript.replace(".py", ".param"), "w")
            try:
                processId = ""
                for controlAction in self.controlActions:
                    controlAction.dump(writer)
                    if len(processId) != 0:
                        processId += "|"
                        processId += str(controlAction.caID)
                        controlAction.dumpDataType(controlAction.__class__.__name__, writer)
                        writer.write("processes=" + processId + "\n")
            finally:
                writer.close
        except:
            print "error during the param file generation"
            raise
            
        # start all control actions
        for controlAction in self.controlActions:
            if controlAction.active:
                controlAction.start()
    
    def stop(self):
        """ 
        Stop all the control actions in the reverse order 
        """
        for controlAction in self.controlActions[::-1]:
            controlAction.stop()

#--------------------------------------------------------------
class ControlAction(object):
    """ 
    This is the base class of all control action classes.
    All children classes shall :
    - override start() and stop() methods and implement actions to do on start/stop of the control script.
    - override dumpDataType() and dump() methods to dump data types and data values in a file on control script start.
    """    

    # Id of the next control action
    NextControlActionId = 1    

    def __init__(self, description, active=True):
        """
        Initialize a ControlAction object.
            @param description string describing the control action
        @param active indicates if this action is active or not
        """
        self.description = description
        self.active = active
        self.caID = ControlAction.NextControlActionId
        
        ControlAction.NextControlActionId += 1

    def start(self):
        """ 
        Method called during control script start.
        This method must be overridden. 
        """
        pass

    def stop(self):
        """ 
        Method called during control script stop.
        This method must be overridden. 
        """
        pass
    
    def dumpDataType(self, prefix, writer):
        """ 
        Dump the data types of the control action during control script start. 
        To be overridden by subclasses. 
        @param prefix prefix to add at the beginning of each line
        @param writer a writer to write in
        """
        self.dumpTypeItem(prefix, "description", "string")
        self.dumpTypeItem(prefix, "type", "string")
        self.dumpTypeItem(prefix, "controlActionID", "integer")
        self.dumpTypeItem(prefix, "active", "boolean")
    
    def dump(self, writer):
        """ 
        Dump the data values of the control action during control script start. 
        To be overridden by subclasses.
        @param writer a writer to write in
        """
        self.dumpItem(writer, "description", "\"" + self.description + "\"")
        self.dumpItem(writer, "type", self.__class__.__name__)
        self.dumpItem(writer, "controlActionID", str(self.caID))
        self.dumpItem(writer,"active", self.active)

    def dumpTypeItem(self, prefix, writer, item, type):
        """
        Dump a data type.
        This utility method shall be used in the dumpDataType() method.
        @param prefix prefix to add at the beginning of the line
        @param writer a writer to write in
        @param item item name
        @param type type of the item
        """
        writer.write(prefix + "." + item + "=" + type + "\n")

    def dumpItem(self, writer, item, value):
        """
        Dump a data value.
        This utility method shall be used in the dump() method.
        @param writer a writer to write in
        @param item item to dump
        @param value value of the item to dump
        """
        writer.write(str(self.caID) + "." + item + "=" + str(value) + "\n")

    def listifyArguments(self, arguments):
        """
        Convert arguments into a list of strings
        @param arguments could be :
            - a simple string where every arguments are separated by a space
            - a list of arguments
        @return a list or None
        """
        normalizedArguments = None
        
        if arguments:
            if isinstance(arguments, basestring):
                normalizedArguments = arguments.split(" ")
            else:
                normalizedArguments = arguments
        
        return normalizedArguments

    def stringifyArguments(self, arguments):
        """ 
        Convert arguments into a string.
        @param arguments could be:
            - a simple string where every arguments are separated by a space
            - a list of strings
        @return a string or None
        """

        if arguments is None:
            return "None"
        if isinstance(arguments, basestring):
            return arguments
        else:
            return ' '.join(arguments)

#--------------------------------------------------------------
class Command(ControlAction):
    """ 
    Control script action to execute a specific command on control script start/stop.
    This control action always waits for the end of the command before returning.
    """

    def __init__(self, description, startCommand=None, stopCommand=None, active=True):
        """
        Initialize a Command object.
        @param startCommand command to execute on start (string or strings list)
        @param stopCommand command to execute on stop (string or strings list)
        @param active indicates if this action is active or not
        @remark if the command to execute is quite complex (with spaces, ...), it's recommended
            to use a strings list instead of a simple string.
        """
        ControlAction.__init__(self, description, active)
        
        self.startCommand = startCommand
        self.stopCommand  = stopCommand

    def execute(self, command):
        """
        Execute a command.
        @param command command to execute (a string or a strings list)
        @return the code returned by the command
        """
        print "Executing '{}' ...".format(self.description)
        print_verbose('Command: "%s"' % self.stringifyArguments(command))
        return _subprocess.call(command)

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(Command, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "startCommand", "string or stringList")
        self.dumpTypeItem(writer, prefix, "stopCommand",  "string or stringList")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(Command, self).dump(writer)
        self.dumpItem(writer, "startCommand", self.listifyArguments(self.startCommand))
        self.dumpItem(writer, "stopCommand",  self.listifyArguments(self.stopCommand))

    def start(self):
        """ 
        If a start command has been defined, execute it
        """
        if self.startCommand:
            returnCode = self.execute(self.startCommand) 
            if returnCode != 0:
                _sys.exit(returnCode)

    def stop(self):
        """ 
        If a stop command has been defined, execute it
        """
        if self.stopCommand:
            self.execute(self.stopCommand)

#--------------------------------------------------------------
class NativeProcess(ControlAction):
    """ 
    Control script action to start/stop a detached process.
    
    Architecture limitations:
    + the start/stop methods are not called on the same Process instance because the start/stop methods
      of ControlScript class are not called on the same ControlScript instance (QTaste creates a new ControlScript 
      instance each time it wants to do a start or a stop of a control script). That means, it's hard to keep 
      keep information between a start call and a stop call (for example, an instance of subprocess in Python).
          
    Here, the solution consists in using a temporary PID file to store the process PID between start and stop actions.

    Note: On POSIX systems, to be able to set a process priority higher than the default one, you must edit the file
          /etc/security/limits.conf    
    """
    
    def __init__(self, description, executable, args=None, workingDir=qtasteRootDirectory, checkAfter=None, 
         active=True, priority=None, outFilename=None):
    
        """  
        Initialize a native process
        @param description control script action description, also used as window title
        @param executable native process to execute
        @param args arguments to pass to the application or None if no argument
        @param workingDir working directory to start process in, defaults to QTaste root directory
        @param checkAfter number of seconds after which to check if process still exist or None to not check
        @param active indicates if the action if active or not
        @param priority priority of the process. Could be 'low', 'belownormal', 'normal', 'abovenormal', 'high', 'realtime'
        @param outFilename a filename to write the process output in it (stderr and stdout)
        """
        ControlAction.__init__(self, description, active)
        
        self.executable  = executable
        self.args        = self.listifyArguments(args)
        self.workingDir  = _os.path.abspath(workingDir)
        self.checkAfter  = checkAfter
        self.priority    = priority
        self.outFilename = outFilename

    def getProcessPidFilename(self):
        """
        Get the name of the file to store the current process PID.
        @return a normalized path.
        """
        return _os.path.abspath(_tempfile.gettempdir() + "/qtaste_ca_" + str(self.caID) + ".pid")

    def getNiceValue(self):
        """
        Convert the priority into a value for the POSIX nice command.
        @return the nice value.
        """
        
        if self.priority == 'low':
            return 19
        
        if self.priority == 'belownormal':
            return 10
            
        if self.priority == 'abovenormal':
            return -5

        if self.priority == 'high':
            return -10

        if self.priority == 'realtime':
            return -20

        return 0
        
    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(NativeProcess, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "executable",  "string")       
        self.dumpTypeItem(writer, prefix, "args",        "stringList")       
        self.dumpTypeItem(writer, prefix, "workingDir",  "string")       
        self.dumpTypeItem(writer, prefix, "checkAfter",  "integer")       
        self.dumpTypeItem(writer, prefix, "priority",    "string")
        self.dumpTypeItem(writer, prefix, "outFilename", "string")
    
    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(NativeProcess, self).dump(writer)
        self.dumpItem(writer, "executable",  self.executable)
        self.dumpItem(writer, "args",        self.stringifyArguments(self.args))
        self.dumpItem(writer, "workingDir",  self.workingDir)
        self.dumpItem(writer, "checkAfter",  self.checkAfter)
        self.dumpItem(writer, "priority",    self.priority)
        self.dumpItem(writer, "outFilename", self.outFilename)

    def start(self):
        """ 
        Start the native process.
        """

        print "Starting '%s' ..." % self.description
        
        # build the complete command
        command = list()

        # add process priority
        if (_OS.getType() == _OS.Type.WINDOWS):
            command.extend(["%COMSPEC%", "/B"])
            
            if self.priority is not None:
                command.append("/{}".format(self.priority))
                
            command.append("/C")
        else:
            command.extend(["nice", "-n", str(self.getNiceValue())])

        # add the process executable
        command.append(self.executable)
        
        # add arguments
        if self.args:
            command.extend(self.listifyArguments(self.args))

        # add output redirection
        if self.outFilename is not None:
            command.append("> {}".format(self.outFilename))

        # move into the new working directory
        if self.workingDir is not None:
            _os.chdir(self.workingDir)

        print_verbose("full native process command: {}".format(' '.join(command)))
            
        # launch the process
        # Note: 
        #    Here, command must be a string because, according to the documentation :
        #    On Unix, with shell=True: If args is a sequence, the first item specifies the command string, 
        #    and any additional items will be treated as additional arguments to the shell itself.
        process = _subprocess.Popen(' '.join(command), shell=True, stderr=_subprocess.STDOUT) 
  
        # check if the process is alive
        if process.poll() is not None:
            print "Error: The process has exited with error code {}".format(process.returncode)
            _sys.exit(-1) 
            
        # wait for some seconds...
        if self.checkAfter:
            _time.sleep(self.checkAfter)
        
        # then, check agains if the process is alive
        if process.poll() is not None:
            print "Error: The process has exited with error code {}".format(process.returncode)
            _sys.exit(-1) 
            
        # get the child process PID
        if (_OS.getType() == _OS.Type.WINDOWS):
            pid = str(process.pid)
        else:
            pid = _subprocess.check_output("ps --no-headers -o %p --ppid {}".format(process.pid), shell=True)

        # save the PID in the file
        try:
            pidFile = open(self.getProcessPidFilename(), "w")
            pidFile.write(pid)
            pidFile.close()
        except:
            print "Unable to save the PID in the file {}".format(self.getProcessPidFilename())
            _sys.exit(-1) 
            
    def _posix_stop(self, pid):

        """
        Stop a POSIX process.
        First, try to stop it properly, using the SIGTERM signal. If it doesn't work, kill it using the SIGKILL signal.
        @param pid the PID of the process
        """
        # kill the process
        if pid is not None:

            # first, try to stop the process properly
            killed = False
            count=0

            while not killed and count < 5:
                
                # stop the process ...
                try:
                    _os.kill(pid, _signal.SIGTERM)
                except:
                    pass
                
                # ... and check if the process has been killed
                try:
                    _os.kill(pid, 0)
                    _time.sleep(0.3)
                    count += 1
                except:
                    killed = True

            # if it's not possible, kill it
            if not killed:
                try:
                    _os.kill(pid, _signal.SIGKILL)
                except:
                    pass
                    
    def _win32_stop(self, pid):
        """ 
        Stop a WIN32 process 
        @param pid the PID of the process
        """
        if pid is not None:
            FNULL = open(_os.devnull, 'w')

            # first, try to stop the process properly
            killed = False
            count=0

            while not killed and count < 5:
                # stop the process ...
                if _subprocess.call("taskkill /T /PID {}".format(pid), stdout=FNULL, stderr=FNULL, shell=True) == 0:
                    killed = True
                else:
                    count += 1
                    _time.sleep(0.3)

            # if it's not possible, kill it
            if not killed:
                _subprocess.call("taskkill /F /T /PID {}".format(pid), stdout=FNULL, stderr=FNULL, shell=True)

            FNULL.close()
                 
    def stop(self):
        """ 
        Stop the process 
        """
        pid = None

        print "Stopping '%s' ..." % self.description

        # get the PID from the PID file
        try:
            pidFile = open(self.getProcessPidFilename(), "r")
            pid = pidFile.read()
            pid = int(pid)
            pidFile.close()

            # remove the file
            _os.remove(self.getProcessPidFilename())
        except:
            return

        # then, stop the process
        if pid is not None:
            if (_OS.getType() == _OS.Type.WINDOWS):
                self._win32_stop(pid)
            else:
                self._posix_stop(pid)
        
#**************************************************************
# Command classes
#**************************************************************

#--------------------------------------------------------------
class RExec(Command):
    """ 
    Control script action to execute a command on a remote host using rexec. 
    """
    
    def __init__(self, startCommand, stopCommand, host, login, password, active=True):
        """
        Initialize RExec object.
        @param startCommand command to execute on start
        @param stopCommand command to execute on stop
        @param host remote host
        @param login remote user login
        @param password remote user password
        @param active indicates if this action is active or not
        """
        Command.__init__(self, "Remote command execution using rexec", startCommand, stopCommand, active)
        self.host = host
        self.login = login
        self.password = password

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(RExec, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "host",     "string")       
        self.dumpTypeItem(writer, prefix, "login",    "string")       
        self.dumpTypeItem(writer, prefix, "password", "string")       

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(RExec, self).dump(writer)
        self.dumpItem(writer, "host",     self.host)
        self.dumpItem(writer, "login",    self.login)
        self.dumpItem(writer, "password", self.password)
    
    def execute(self, command):
        """
        Execute a command on a remote host
        @param command command to execute
        """
        if command:
            print_verbose("Remotely executing '%s' on %s using rexec" % (command, self.host))

            # add rexec parameters to the command        
            fullCommand = ["rexec", "-l", self.login, "-p", self.password, self.host]
            fullCommand.extend(self.listifyArguments(command))

            # and execute the full command
            return super(RExec, self).execute(fullCommand)
            
        return -1

#--------------------------------------------------------------
class ReplaceInFiles(Command):
    """ 
    Control script action to replace string(s) in file(s), only on start 
    """

    def __init__(self, findString, replaceString, files, active=True):
        """
        Initialize ReplaceInFiles object.
        @param findString regular expression string to find
        @param replaceString string by which to replace findString, may contain matches references in the form \1
        @param files file name or list of files names
        @param active indicates if this action is active or not
        """
        Command.__init__(self, "Replace in file(s)", "dummy_start", None, active)

        self.files = files 
        self.findString    = findString.replace("\\", "\\\\")
        self.replaceString = replaceString.replace("\\", "\\\\")

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(ReplaceInFiles, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(prefix, "findString",    "string")
        self.dumpTypeItem(prefix, "replaceString", "string")
        self.dumpTypeItem(prefix, "files",        "string|sequence")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(ReplaceInFiles, self).dump(writer)
        self.dumpItem(writer, "findString",    self.findString)
        self.dumpItem(writer, "replaceString", self.replaceString)
        self.dumpItem(writer, "files",        self.stringifyArguments(self.files))

    def execute(self, command):
        """ 
        Replace the 'findString' by the 'replaceString' in all file(s)
        Command only executed on start, because a "dummy_start" start command has been defined.
        @return -1 when the regex are not a valid, 0 otherwise
        """
        print_verbose("Replacing {} by {} in {}".format(repr(self.findString), repr(self.replaceString), self.stringifyArguments(self.files)))
        
        try:
            for aFile in self.listifyArguments(self.files):
                for line in _fileinput.input(aFile, inplace=True):
                    print _re.sub(self.findString, self.replaceString, line),
        except _re.error:
            return -1
                
        return 0
    
#--------------------------------------------------------------
class Rsh(Command):
    """ 
    Control script action for executing a command on a remote host using rsh 
    """

    def __init__(self, startCommand, stopCommand, host, login, active=True):
        """
        Initialize Rsh object.
        @param startCommand command to execute on start
        @param stopCommand command to execute on stop
        @param host remote host
        @param login remote user login
        @param active indicates if this action is active or not
        """
        Command.__init__(self, "Remote command execution using rsh", startCommand, stopCommand, active)
        self.host  = host
        self.login = login

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(Rsh, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "host",  "string")
        self.dumpTypeItem(writer, prefix, "login", "string")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(Rsh, self).dump(writer)
        self.dumpItem(writer, "host",  self.host)
        self.dumpItem(writer, "login", self.login)

    def execute(self, command):
        """
        Execute the command on the remote host using rsh
        """
        if command:
            print_verbose('Remotely executing "%s" on %s using rsh' % (command, self.host))

            # add rsh parameters to the command        
            fullCommand = ["rsh", "-l", self.login, self.host]
            fullCommand.extend(self.listifyArguments(command))

            # and execute the full command
            return super(Rsh, self).execute(fullCommand)    

            
        return -1
        
#--------------------------------------------------------------
class Ssh(Command):
    """ 
    Control script action for executing a command on a remote host using SSH
    """
    def __init__(self, host, login, startCommand=None, stopCommand=None, active=True):
        """
        Initialize Ssh object.
        @param host remote host
        @param login remote user login
        @param startCommand command to execute on start, or None
        @param stopCommand command to execute on stop, or None
        """
        Command.__init__(self, "Remote command execution using ssh", startCommand, stopCommand, active)
        self.host = host
        self.login = login

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(Ssh, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "host",  "string")
        self.dumpTypeItem(writer, prefix, "login", "string")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(Ssh, self).dump(writer)
        self.dumpItem(writer, "host",  self.host)
        self.dumpItem(writer, "login", self.login)

    def execute(self, command):
        """
        Execute the command on the remote host using ssh
        """
        if command:
            print_verbose('Remotely executing "%s" on %s using ssh' % (command, self.host))
            ssh = _IF(_OS.getType() == _OS.Type.WINDOWS, qtasteRootDirectory + "tools/tools4ever/T4eSsh", "ssh")
            return super(Rsh, self).execute([ssh, self.host, "-l", self.login, command])    

        return -1

#--------------------------------------------------------------
class ShellCommand(Command):
    """ 
    Control script action for executing a shell command. 
    """
    def __init__(self, startCommand=None, stopCommand=None, shell=_IF(_OS.getType() == _OS.Type.WINDOWS, "cmd", "bash"), active=True):
        """
        Initializes ShellCommand object.
        @param startCommand shell command to execute on start, or None
        @param stopCommand shell command to execute on stop, or None
        @param shell the shell to use (default is "bash" on Linux, "cmd" on Windows)
        """
        Command.__init__(self, "Command execution using " + shell, startCommand, stopCommand, active)
        self.shell = shell

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(ShellCommand, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "shell", "string")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(ShellCommand, self).dump(writer)
        self.dumpItem(writer, "shell",  self.shell)

    def execute(self, command):
        """
        Execute the shell command
        """
        if command:
            print_verbose('Executing "%s" using %s' % (command, self.shell))
            
            # build shell option according to the shell used
            if _OS.getType() == _OS.Type.WINDOWS:
                if "powershell" in self.shell:
                    shellOption = "-command"
                else:
                    shellOption = "/c"
            else:
                shellOption = "-c"
            
            return super(ShellCommand, self).execute([self.shell, shellOption, command])

        return -1
     
#**************************************************************
# Process classes
#**************************************************************

#--------------------------------------------------------------
class JavaProcess(NativeProcess):
    """ Control script action for starting/stopping a Java process """

    def __init__(self, description, mainClassOrJar, args=None, workingDir=qtasteRootDirectory, classPath=None, 
         vmArgs=None, jmxPort=None, checkAfter=None, priority=None, useJacoco=False, useJavaGUI=False, 
         active=True, jacocoIncludes=None, jacocoExcludes=None):
        """
        Initialize JavaProcess object
        @param description control script action description, also used as window title
        @param mainClassOrJar java main class or jar file to execute
        @param args arguments to pass to the application or None if no argument
        @param workingDir working directory to start process in, defaults to QTaste root directory
        @param classPath class path or None to use current class path
        @param vmArgs arguments to be passed to the java VM or None to use no additional java VM arguments
        @param jmxPort JMX port or None to disable JMX
        @param checkAfter number of seconds after which to check if process still exist or None to not check
        @param priority specifies to run the process with the given priority: "low", "belownormal", "normal", "abovenormal", "high" or "realtime" or none for default priority
        @param useJacoco enable the coverage analysis using jacoco tool
        @param useJavaGUI enable the javagui service to enable remote javagui accessibility 
        @param active indicates if this action is active or not
        @param jacocoIncludes the Jacoco 'includes' parameter (without the 'includes=' part)
        @param jacocoExcludes the Jacoco 'excludes' parameter (without the 'excludes=' part)
        """
        self.mainClassOrJar  = mainClassOrJar
        self.mainArgs        = self.listifyArguments(args)
        self.classPath       = self._normalizeClassPath(classPath)
        self.vmArgs      = self.listifyArguments(vmArgs)
        self.jmxPort      = jmxPort
        self.jacocoArgument  = None
        self.javaGUIArgument = None

        # Build the Jacoco argument
        if useJacoco:
            jacocoHome = _os.getenv("JACOCO_HOME")
            if jacocoHome:
                self.jacocoArgument = "-javaagent:" + jacocoHome + _os.sep + "lib" + _os.sep + "jacocoagent.jar=append=true,destfile=" + "reports" + _os.sep + self.description + ".jacoco"
                           
            if jacocoIncludes:
                self.jacocoArgument += ",includes=" + jacocoIncludes

            if jacocoExcludes:
                self.jacocoArgument += ",excludes=" + jacocoExcludes            
            else:
                print "WARNING: JACOCO_HOME variable not defined - Jacoco coverage disabled!\n"

        # Build the Java GUI argument
        if useJavaGUI:
            self.javaGUIArgument = "-javaagent:" + qtasteRootDirectory + _os.sep + "plugins" + _os.sep + "SUT" + _os.sep + "qtaste-javagui-deploy.jar"

        # build the process argument list
        arguments = self._buildProcessArguments()

        # finalize the process initialization
        NativeProcess.__init__(self, description, "java", arguments, workingDir, checkAfter, active, priority, description + ".out")

    def _normalizeClassPath(self, classpath):
        """ 
        Normalize a classpath according to the OS type 
        @param classpath classpath to normalize
        @return the normalized classpath
        """
        normalizedClassPath = None
        
        if classpath:
            if _OS.getType() == _OS.Type.WINDOWS:
                normalizedClassPath = classpath.replace(":", ";")
                normalizedClassPath = normalizedClassPath.replace("/", _os.sep)
            else:
                normalizedClassPath = classpath.replace(";", ":")
        
        return normalizedClassPath

    def _buildProcessArguments(self):
        """ 
        Build the list of process arguments
        @return the list of process arguments
        """
        command = []

        # add classpath
        if self.classPath:
            command.append("-cp")
            command.append(self.classPath)

        # add JVM arguments
        if self.vmArgs:
            command.extend(self.vmArgs)

        # add JACOCO argument(s)
        if self.jacocoArgument:
            command.append(self.jacocoArgument)

        # add Java GUI argument(s)
        if self.javaGUIArgument:
            command.append(self.javaGUIArgument)

        # add JMX arguments
        if self.jmxPort:
            command.extend(["-Dcom.sun.management.jmxremote.port=%d" % self.jmxPort,
                    "-Dcom.sun.management.jmxremote.authenticate=false",
                    "-Dcom.sun.management.jmxremote.ssl=false"])

        # add main class or jar file
        if self.mainClassOrJar.endswith(".jar"):
            command.append("-jar")
        command.append(self.mainClassOrJar)
        
        # add main argument(s)
        if self.mainArgs:
            command.extend(command, self.mainArgs)

        return command

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(JavaProcess, self).dump(writer)
        self.dumpItem(writer, "mainClassOrJar",   self.mainClassOrJar)
        self.dumpItem(writer, "args",             self.mainArgs)
        self.dumpItem(writer, "classPath",        self.classPath)
        self.dumpItem(writer, "vmArgs",           self.stringifyArguments(self.vmArgs))
        self.dumpItem(writer, "jmxPort",          self.jmxPort)
        self.dumpItem(writer, "jacocoArguments",  self.jacocoArgument)
        self.dumpItem(writer, "javaGUIArguments", self.javaGUIArgument)

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(JavaProcess, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "mainClassOrJar",   "string")
        self.dumpTypeItem(writer, prefix, "args",             "string|list")
        self.dumpTypeItem(writer, prefix, "classPath",        "string")
        self.dumpTypeItem(writer, prefix, "vmArgs",           "string|list")
        self.dumpTypeItem(writer, prefix, "jmxPort",          "integer")
        self.dumpTypeItem(writer, prefix, "jacocoArguments",  "string")
        self.dumpTypeItem(writer, prefix, "javaGUIArguments", "string")


#--------------------------------------------------------------
class RLogin(JavaProcess):
    """ 
    Control script action for doing a rlogin connection using the RLogin QTaste TCOM 
    """

    def __init__(self, host, login, log4jconf, command=None, active=True):
        """
        Initialize RLogin object.
        @param command command to execute using rlogin
        @param host remote host
        @param login remote user login
        @param active indicates if this action is active or not
        """
        self.command = command
        self.host    = host
        self.login   = login
        self.logconf = log4jconf

        # build java command arguments
        commandArguments = [self.host]

        if self.command:
            commandArguments.append("-command")
            commandArguments.append("\"" + self.stringifyArguments(command) + "\"")

        commandArguments.append("-logOutput")
        commandArguments.append("-interactive")
        commandArguments.append("-log4jconf")
        commandArguments.append(self.logconf)

        # finish initializing the process
        JavaProcess.__init__(self, 
                    "RLogin", 
                    "com.qspin.qtaste.tcom.rlogin.RLogin", 
                    commandArguments,
                    qtasteRootDirectory, 
                    "kernel/target/qtaste-kernel-deploy.jar")

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(RLogin, self).dump(writer, prefix)
        self.dumpTypeItem(prefix, "command", "string")
        self.dumpTypeItem(prefix, "host",    "string")
        self.dumpTypeItem(prefix, "login",   "string")
        self.dumpTypeItem(prefix, "logconf", "string")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(RLogin, self).dump(writer)
        self.dumpItem(writer, "command", self.command)
        self.dumpItem(writer, "host",      self.host)
        self.dumpItem(writer, "login",   self.login)
        self.dumpItem(writer, "logconf", self.logconf)

#--------------------------------------------------------------
class ServiceProcess(Command):
    """ 
    Control script action for starting/stopping a service process 
    """

    def __init__(self, description, serviceName, active=True):
        """
        Initialize ServiceProcess object
        @param description control script action description, also used as window title
        @param serviceName name of the service to control
        @param active indicates if this action is active or not
        """
        self.serviceName = serviceName

        startCommand = self._buildCommand("start")
        stopCommand  = self._buildCommand("stop")

        Command.__init__(self, description, startCommand, stopCommand, active)

    def _buildCommand(self, action):
        """
        Build the command to execute according to the current OS
        @return the command to execute in a list
        """
        if _OS.getType() == _OS.Type.WINDOWS:
            return ["net", action, self.serviceName]
        else:
            return ["service", self.serviceName, action]

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(ServiceProcess, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(prefix, "serviceName", "string")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(ServiceProcess, self).dump(writer)
        self.dumpItem(writer, "serviceName", self.serviceName)

#**************************************************************
# Others classes
#**************************************************************

#--------------------------------------------------------------
class RebootRlogin(ControlAction):
    """
    Control action to reboot a remote host.
    """
    
    def __init__(self, host, login, waitingTime=60, active=True):
        """
        Initialize RebootRLogin object.
        @param host remote host
        @param login remote user login
        @param waitingTime time to wait, after reboot
        @param active indicates if this action is active or not
        """
        ControlAction.__init__(self, "Remote reboot using rlogin", active)
        self.host = host
        self.login = login
        self.waitingTime = waitingTime

        if _OS.getType() == _OS.Type.WINDOWS:
            self.localuser = _os.getenv("username")
        else:
            self.localuser = _os.getenv("user")
            
        self.rlogin = _RLogin(host, self.localuser, login, "", False, False)

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(RebootRlogin, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "host",      "string")
        self.dumpTypeItem(writer, prefix, "login",      "string")
        self.dumpTypeItem(writer, prefix, "waitingTime", "integer")

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(RebootRlogin, self).dump(writer)
        self.dumpItem(writer, "host",      self.host)
        self.dumpItem(writer, "login",      self.login)
        self.dumpItem(writer, "waitingTime", self.waitingTime)
    
    def start(self):
        """
        Do the reboot on start
        @return True if the reboot succeed, false otherwise.
        """
        
        print_verbose("Rebooting %s..." % self.host)

        if self.rlogin.connect() and self.rlogin.reboot():
            print_verbose("Waiting for %g seconds while %s is rebooting..." % (self.waitingTime, self.host))
            _time.sleep(self.waitingTime)
        else:
            _sys.exit(-1)
    
#--------------------------------------------------------------
class OnStart(ControlAction):
    """ 
    Control script action to execute an action only on start 
    """

    def __init__(self, controlAction, active=True):
        """
        Initialize OnStart object.
        @param controlAction control action to execute only on start
        @param active indicates if this action is active or not
        """
        ControlAction.__init__(self, controlAction.description + " on start", active)
        self.controlAction = controlAction

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(OnStart, self).dumpDataType(prefix, writer)
        controlAction.dumpDataType(prefix, writer)

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(OnStart, self).dump(writer)
        self.controlAction.dump(writer)

    def start(self):
        """
        Do the start action on start.
        """
        self.controlAction.start()

#--------------------------------------------------------------
class OnStop(ControlAction):
    """ 
    Control script action to execute an action only on stop 
    """

    def __init__(self, controlAction, active=True):
        """
        Initialize OnStop object.
        @param controlAction control action to execute only on stop
        @param active indicates if this action is active or not
        """
        ControlAction.__init__(self, controlAction.description + " on stop", active)
        self.controlAction = controlAction

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(OnStop, self).dumpDataType(prefix, writer)
        controlAction.dumpDataType(prefix, writer)

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(OnStop, self).dump(writer)
        self.controlAction.dump(writer)

    def stop(self):
        """
        Do the stop action on stop
        """
        self.controlAction.stop()

#--------------------------------------------------------------
class Sleep(ControlAction):
    """ 
    Control script action to sleep some time 
    """

    def __init__(self, time, message = None, active=True):
        """
        Initialize Sleep object.
        @param time time to sleep, in seconds, may be a floating point value
        @param message message to print or None to print a standard message 
        @param active indicates if this action is active or not
        """
        ControlAction.__init__(self, "Sleep", active)
        self.time = time
        self.message = message

    def dumpDataType(self, prefix, writer):
        """ 
        @see ControlAction.dumpDataType()
        """
        super(Sleep, self).dumpDataType(prefix, writer)
        self.dumpTypeItem(writer, prefix, "time",    "integer")       
        self.dumpTypeItem(writer, prefix, "message", "string")       

    def dump(self, writer):
        """ 
        @see ControlAction.dump()
        """
        super(Sleep, self).dump(writer)
        self.dumpItem(writer, "time",    self.time)
        self.dumpItem(writer, "message", self.message)

    def execute(self):
        """
        Execute the sleep command
        """

        # print the message
        if self.message is None:
            print_verbose("Sleeping {} seconds...".format(str(self.time))) 
        else:
            print_verbose(self.message)

        #sleep
        _time.sleep(self.time)

    def start(self):
        self.execute()

    def stop(self):
        self.execute()

