##
# Control script jython module.
#
# This module contains the following classes:
# - ControlScript: this is the main class to be used at the main control script file. This class is initialized with an array controlActions. 
#   Those control actions are classes derived from ControlAction class  having 2 methods implemented: start and stop
#
# - ControlAction: this is the class from which specific actions can be derived from. Also following generic methods are implemented:
# 		- executeCommand(command) where command is the command to be executed (os)
#		- executeShellScript(name, arguments) where
#			name is the name of the shell script without extension (for windows "cmd", for Unix "sh"
#			arguments (optional) the string to be passed as arguments to the shell script
#		- start: abstract method
#		- stop: abstract method
#
# - NativeProcess: derived from ControlAction where start and stop methods are implemented
#	This class is initialized with following parameters:
#		- description: name of the native process (title of the window) 
#		-executable: native process to execute
#		-args (optional): arguments to pass to the application or None if no argument
#		-workingDir: working directory to start process in, defaults to QTaste root directory
#		-checkAfter (optional): number of seconds after which to check if process still exist or None to not check
#		- start: method called by ControlScript when the ControlScript needs to start NativeProcess
#		- stop: method called by ControlScript when the ControlScript needs to stop NativeProcess
#
# - JavaProcess: derived from ControlAction where start and stop methods are implemented
#	This class is initialized with following parameters:
#		- description: name of the java process (title of the window) 
#		- mainClassOrJar: name of the class or Jar file as passed to the java VM
#		- args (optional): arguments to be passed to the java application
#		- workingDir (optional): the working directory where the java process must be launched, defaults to QTaste root directory
#		- classPath (optional): specify additional classpath to the java process (equivalent to -cp switch)
#		- vmArgs (optional): arguments to be passed to the java VM
#		- jmxPort (optional): specify the jmx port if the java process must be started with JMX interface
#		- checkAfter (optional): specifies if the control script needs to check if the process is still present after the period of time specified
#
#	- start: method called by ControlScript when the ControlScript needs to start JavaProcess
#	- stop: method called by ControlScript when the ControlScript needs to stop JavaProcess
#
# - PortmapCleanup: derived from ControlAction
#	this ControlAction is used to cleanup the portmap when applicable (when using RPC servers for instance).
#
# - ReplaceInFiles: derived from ControlAction
#	This control action has the goal to use the sed function (as in Unix)
#	This class is initialized  with following parameters:
#		- findString: the string to find
#		- replaceStrin: the replace string
# 		- files name of the file(s) where the replace must be done
#
# - Rsh: derived from ControlAction, for executing a command on a remote host using rsh
# 	this class is initialized with following parameters
#		- startCommand: command to be executed when the control script is called to start the SUT
#		- stopCommand: command to be executed when the control script is called to stop the SUT
#		- host: name of the host where the command must be executed
#		- login: login name 
#
# - RExec: derived from ControlAction, for executing a command on a remote host using rexec
# 	this class is initialized with following parameters
#		- startCommand: command to be executed when the control script is called to start the SUT
#		- stopCommand: command to be executed when the control script is called to stop the SUT
#		- host: name of the host where the command must be executed
#		- login: login name 
#		- password: password associated to the login name
#
# - RLogin: derived from JavaProcess, for executing a command on a remote host using rlogin
# 	This class is used to launch specific process running on VME. To launch it, rlogin is used with a command (for example cu startUp) and the log4j configuration to be associated to this to receive logs from the remote process.
#	This class is in fact launched using a java process that performs a rlogin.
# 	This class is initialized with following parameters
#		- command: command to be executed using rlogin
#		- host: name of the host where the command must be executed
#		- login: login name 
#		- log4jconf: configuration of the log4j in order to receive the logs to the QTaste log4j server
#
# - RebootRlogin: derived from ControlAction
#	This class has the goal to reboot remote VME process using rlogin
# 	This class is initialized with following parameters
#		- host: name of the host where the command must be executed
#		- login: login name 
#		- waitingTime: default is 60 seconds. Sleeping time before continuing to the control script.
#
##


import os as _os, sys as _sys, re as _re, time as _time
import datetime as _datetime
from org.apache.log4j import Logger as _Logger, Level as _Level
from com.qspin.qtaste.util import OS as _OS, Exec as _Exec
from com.qspin.qtaste.config import TestBedConfiguration as _TestBedConfiguration
from com.qspin.qtaste.tcom.rlogin import RLogin as _RLogin

# set log4j logger level to WARN
_Logger.getRootLogger().setLevel(_Level.WARN)

# conditional expression
_IF = lambda a,b,c:(a and [b] or [c])[0]


def _exitWithError(message):
	""" Exits program with error code 1 after printing given message to standard error output """
	print >> _sys.stderr, message
	_sys.exit(1);


def _parseCommandLineArguments():
	"""
	Parse command-line arguments
	@return tuple (start, arguments) where start is true if and only if first argument is 'start'
			and arguments is the additional arguments
	"""
	firstArgument = None
	if len(_sys.argv) > 1:
		firstArgument = _sys.argv[1].lower()
	if firstArgument == "start":
		start = True
	elif firstArgument == "stop":
		start = False
	else:
		_exitWithError("Invalid syntax: first argument of control script should be 'start' or 'stop'")
	arguments = _sys.argv[2:]
	return (start, arguments)

def _getTestbedConfig():
	""" Get TestBedConfiguration instance testbedConfig from the TESTBED environment variable """
	testbed = _os.getenv("TESTBED");
	if testbed is None:
		_exitWithError("TESTBED environment variable is not defined")
	_TestBedConfiguration.setConfigFile(_os.path.abspath(testbed))
	return _TestBedConfiguration.getInstance()


# parsing command-line arguments
# set start to true if and only if first argument is 'start'
# set arguments to the additional arguments
start, arguments = _parseCommandLineArguments()

# QTaste root directory
qtasteRootDirectory = _os.getenv("QTASTE_ROOT") + _os.sep

# QTaste kernel class path
qtasteKernelClassPath = qtasteRootDirectory + "kernel/target/qtaste-kernel-deploy.jar"
qtasteKernelClassPath = qtasteKernelClassPath.replace("/", _os.sep)

# get TestBedConfiguration instance testbedConfig from the TESTBED environment variable
testbedConfig = _getTestbedConfig()


class ControlScript(object):
	""" Control script """
	def __init__(self, controlActions):
		"""
		Initialize ControlScript object.
		Store controlActions in self.controlActions,
		store additional command-line arguments (arguments except first one) in self.arguments,
		store TESTBED environment variable in self.testbed,
		and execute start() or stop() following the value of the first command-line argument (must be 'start' or 'stop')
		@param controlActions sequence of ControlAction (list or tuple) 
		"""
		self.controlActions = controlActions
		if start:
			self.start()
		else:
			self.stop()
	
	def start(self):
		""" Method called on start, starts control actions in defined order"""
		for controlAction in self.controlActions:
			controlAction.start()
	
	def stop(self):
		""" Method called on stop, stops control actions in reverse order """
		for controlAction in self.controlActions[::-1]:
			controlAction.stop()

class ControlAction(object):
	""" Control script action """
	def __init__(self, description):
		"""
		Initialize ControlAction object.
		@param description string describing the control action
		"""
		self.description = description

	def start(self):
		""" Method called on start, to be overridden by subclasses """
		pass

	def stop(self):
		""" Method called on stop, to be overridden by subclasses """
		pass

	def executeCommand(command):
		""" 
		Execute a command and exit with error code if command returned an error
		@param command command (string or strings list)
		"""
		# don't use os.exec() because it don't return until all launched process are terminated
		error = _Exec().exec(command)
		if error:
			_sys.exit(error)
	executeCommand = staticmethod(executeCommand)

	def executeShellScript(name, arguments=None):
		""" 
		Execute a shell script with arguments and exit with error code if shell script returned an error
		@param name shell script name without extension
		@param args arguments string passed to the shell script
		"""
		shellScriptFileName = ControlAction.shellScriptsDirectory + name + ControlAction.shellScriptExtension
		if type(arguments) is list:
			shellCommand =[shellScriptFileName]
			for argument in arguments:
				shellCommand.append(argument)
			ControlAction.executeCommand(shellCommand)
		else:
			shellCommand = shellScriptFileName
			if arguments:
				shellCommand += " " + arguments;
			ControlAction.executeCommand(shellCommand)
	executeShellScript = staticmethod(executeShellScript)
	
	def escapeArgument(argument):
		"""
		Escape special characters in argument
		@param argument argument to escape
		@return argument with special characters escaped
		"""
		if (_OS.getType() == _OS.Type.WINDOWS):
			# under Windows, escape '"' characters in command
			return argument.replace('"', r'\"')
		else:
			return argument
	escapeArgument = staticmethod(escapeArgument)

	# shell scripts directory
	shellScriptsDirectory = qtasteRootDirectory + "tools/"
	
	# shell script extension
	shellScriptExtension = _IF(_OS.getType() == _OS.Type.WINDOWS, ".cmd", ".sh")


class JavaProcess(ControlAction):
	""" Control script action for starting/stopping a Java process """
	def __init__(self, description, mainClassOrJar, args=None, workingDir=qtasteRootDirectory, classPath=None, vmArgs=None, jmxPort=None, checkAfter=None, priority=None):
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
		"""
		ControlAction.__init__(self, description)
		self.mainClassOrJar = mainClassOrJar
		self.args = args
		if args is None:
			self.mainWithArgs = mainClassOrJar
		else:
			self.mainWithArgs = mainClassOrJar + ' ' + args
		if _OS.getType() != _OS.Type.WINDOWS:
			self.workingDir = workingDir
		else:
			self.workingDir = workingDir.replace("/", "\\")
		if classPath:
			if _OS.getType() != _OS.Type.WINDOWS:
				self.classPath = classPath.replace(";",":")
			else:
				self.classPath = classPath.replace(":",";")
		else:
			self.classPath = None
		self.vmArgs = vmArgs
		if jmxPort:
			self.jmxPort = "%d" % jmxPort
		else:
			self.jmxPort = None
		if checkAfter:
			self.checkAfter = "%d" % checkAfter
		else:
			self.checkAfter = None
		self.priority = priority

	def start(self):
		print "Starting " + self.description + "...";
		isJar = self.mainClassOrJar.endswith(".jar")
		if _OS.getType() != _OS.Type.WINDOWS:
			shellScriptArguments = []
			if isJar:
				shellScriptArguments.append("-jar")
			shellScriptArguments.append(self.mainWithArgs)
			shellScriptArguments.append("-dir")
			shellScriptArguments.append(self.workingDir)
			if self.classPath:
				shellScriptArguments.append("-cp")
				shellScriptArguments.append(self.classPath)

			shellScriptArguments.append("-title")
			shellScriptArguments.append(self.description)
			if self.vmArgs:
				shellScriptArguments.append("-vmArgs")
				shellScriptArguments.append(self.vmArgs)
			if self.jmxPort:
				shellScriptArguments.append("-jmxPort")
				shellScriptArguments.append(self.jmxPort)
			if self.checkAfter:
				shellScriptArguments.append("-checkAfter")
				shellScriptArguments.append(self.checkAfter)
			if self.priority:
				shellScriptArguments.append("-priority")
				shellScriptArguments.append(self.priority)
		else:
			shellScriptArguments = _IF(isJar, '-jar ', '') + '"' + self.mainWithArgs + '" -dir ' + self.workingDir + ' -title "' + self.description + '"';
			if self.classPath:
				updateQTasteRoot = qtasteRootDirectory.replace(":",";")
				self.classPath = self.classPath.replace(updateQTasteRoot, qtasteRootDirectory)
				shellScriptArguments += ' -cp "' + self.classPath + '"';
			if self.vmArgs:
				shellScriptArguments += ' -vmArgs "' + self.vmArgs + '"';
			if self.jmxPort:
				shellScriptArguments += ' -jmxPort ' + str(self.jmxPort);
			if self.checkAfter:
				shellScriptArguments += ' -checkAfter ' + str(self.checkAfter);
			if self.priority:
				shellScriptArguments += ' -priority ' + self.priority;
		
		ControlAction.executeShellScript("start_java_process", shellScriptArguments);
		print

	def stop(self):
		print "Stopping " + self.description + "...";
		ControlAction.executeShellScript("stop_java_process", [self.mainWithArgs])
		print

class NativeProcess(ControlAction):
	""" Control script action for starting/stopping a native process """
	def __init__(self, description, executable, args=None, workingDir=qtasteRootDirectory, checkAfter=None):
		"""
		Initialize NativeProcess object
		@param description control script action description, also used as window title
		@param executable native process to execute
		@param args arguments to pass to the application or None if no argument
		@param workingDir working directory to start process in, defaults to QTaste root directory
		@param checkAfter number of seconds after which to check if process still exist or None to not check
		"""
		ControlAction.__init__(self, description)
		self.executable = executable
		self.args = args
		if args is None:
			self.args = ''
		self.workingDir = workingDir
		if checkAfter:
			self.checkAfter = "%d" % checkAfter
		else:
			self.checkAfter = None

	def start(self):
		print "Starting " + self.description + "...";
		if _OS.getType() != _OS.Type.WINDOWS:
			shellScriptArguments = []
			shellScriptArguments.append(self.executable)
			shellScriptArguments.append(self.args)
			shellScriptArguments.append("-dir")
			shellScriptArguments.append(self.workingDir)
			shellScriptArguments.append("-title")
			shellScriptArguments.append(self.description)
			if self.checkAfter:
				shellScriptArguments.append("-checkAfter")
				shellScriptArguments.append(self.checkAfter)
		else:
			shellScriptArguments = '"' + self.executable + '" "' + self.args + '" -dir ' + self.workingDir + ' -title "' + self.description + '"';
			if self.checkAfter:
				shellScriptArguments += ' -checkAfter ' + str(self.checkAfter);
		
		ControlAction.executeShellScript("start_process", shellScriptArguments);
		print

	def stop(self):
		print "Stopping " + self.description + "...";
		shellScriptArguments = []
		shellScriptArguments.append(self.executable)
		shellScriptArguments.append(self.args)
		if _OS.getType() != _OS.Type.WINDOWS:
			ControlAction.executeShellScript("stop_process", '"' + shellScriptArguments + '"')
		else:
			ControlAction.executeShellScript("stop_process", shellScriptArguments)

class ReplaceInFiles(ControlAction):
	""" Control script action for doing a replace in file(s), only on start """
	def __init__(self, findString, replaceString, files):
		"""
		Initialize ReplaceInFiles object.
		@param findString regular expression string to find
		@param replaceString string by which to replace findString, may contain matches references in the form \1
		@param files file name or list of files names
		"""
		ControlAction.__init__(self, "Replace in file(s)")
		self.findString = findString
		self.replaceString = replaceString
		self.files = _IF(type(files) == str, files, " ".join(files)) 
		sed = _IF(_OS.getType() == _OS.Type.WINDOWS, qtasteRootDirectory + r"tools\GnuWin32\bin\sed", "sed")
		self.sedCommand = sed + " -r -i s/" + findString.replace("/", r"\/") + "/" + replaceString.replace("/", r"\/") + "/g " + self.files

	def start(self):
		print "Replacing", repr(self.findString), "by", repr(self.replaceString), "in", self.files
		ControlAction.executeCommand(self.sedCommand)
		print

	def stop(self):
		pass


class Rsh(ControlAction):
	""" Control script action for executing a command on a remote host using rsh """
	def __init__(self, startCommand, stopCommand, host, login):
		"""
		Initialize Rsh object.
		@param startCommand command to execute on start
		@param stopCommand command to execute on stop
		@param host remote host
		@param login remote user login
		"""
		ControlAction.__init__(self, "Remote command execution using rsh")
		self.startCommand = startCommand
		self.stopCommand = stopCommand
		self.host = host
		self.login = login
		
	def remoteExecute(self, command):
		print 'Remotely executing "%s" on %s using rsh' % (command, self.host) 
		ControlAction.executeShellScript("rsh_with_result", [self.host, "-l", self.login, ControlAction.escapeArgument(command)])
		print

	def start(self):
		self.remoteExecute(self.startCommand)

	def stop(self):
		self.remoteExecute(self.stopCommand)


class RExec(ControlAction):
	""" Control script action for executing a command on a remote host using rexec """
	def __init__(self, startCommand, stopCommand, host, login, password):
		"""
		Initialize RExec object.
		@param startCommand command to execute on start
		@param stopCommand command to execute on stop
		@param host remote host
		@param login remote user login
		@param password remote user password
		"""
		ControlAction.__init__(self, "Remote command execution using rexec")
		self.startCommand = startCommand
		self.stopCommand = stopCommand
		self.host = host
		self.login = login
		self.password = password
		
	def remoteExecute(self, command):
		print 'Remotely executing "%s" on %s using rexec' % (command, self.host) 
		ControlAction.executeShellScript("rexec_with_result", ["-l", '"'+self.login+'"', "-p", '"'+self.password+'"', self.host, ControlAction.escapeArgument(command)])
		print

	def start(self):
		self.remoteExecute(self.startCommand)

	def stop(self):
		self.remoteExecute(self.stopCommand)

class RLogin(JavaProcess):
	""" Control script action for doing a rlogin connection using the RLogin QTaste TCOM """
	def __init__(self, host, login, log4jconf, command=None):
		"""
		Initialize RLogin object.
		@param command command to execute using rlogin
		@param host remote host
		@param login remote user login
		"""
		ControlAction.__init__(self, "Remote command execution and/or logging using rlogin")
		self.command = ControlAction.escapeArgument(command)
		self.host = host
		self.login = login
		self.logconf = log4jconf
		if command:
			JavaProcess.__init__(self, "RLogin", "com.qspin.qtaste.tcom.rlogin.RLogin", '%s -command "%s" -logOutput -interactive -log4jconf %s' %(self.host, command, self.logconf), "%s" % qtasteRootDirectory, "kernel/target/qtaste-kernel-deploy.jar" )
		else:		
			JavaProcess.__init__(self, "RLogin", "com.qspin.qtaste.tcom.rlogin.RLogin", '%s -logOutput -interactive -log4jconf %s' %(self.host, self.logconf), "%s" % qtasteRootDirectory, "kernel/target/qtaste-kernel-deploy.jar" )

	def start(self):
		print "Starting execution of remote command '%s' and logging output using log4j on %s" % (self.command, self.host)
		super(RLogin, self).start()

	def stop(self):
		print "Stopping execution of remote command '%s' and logging output using log4j on %s" % (self.command, self.host)
		super(RLogin, self).stop()


class RebootRlogin(ControlAction):
	def __init__(self, host, login, waitingTime=60):
		"""
		Initialize RebootRLogin object.
		@param host remote host
		@param login remote user login
		@param waitingTime time to wait, after reboot
		"""
		ControlAction.__init__(self, "Remote reboot using rlogin")
		self.host = host
		self.login = login
		self.waitingTime = waitingTime
		self.localuser = _IF(_OS.getType() == _OS.Type.WINDOWS, _os.getenv("username"), _os.getenv("user"))
		self.rlogin = _RLogin(host, self.localuser, login, "", False, False)

	def start(self):
		print "Rebooting %s..." % self.host
		if self.rlogin.connect() and self.rlogin.reboot():
			print
			print "Waiting %g seconds while %s is rebooting..." % (self.waitingTime, self.host)
			_time.sleep(self.waitingTime)
			print
		else:
			_sys.exit(1)
		
	def stop(self):
		pass


class Sleep(ControlAction):
	""" Control script action to sleep some time """
	def __init__(self, time, message = None):
		"""
		Initialize Sleep object.
		@param time time to sleep, in seconds, may be a floating point value
		@param message message to print or None to print a standard message 
		"""
		ControlAction.__init__(self, "Sleep")
		self.time = time
		self.message = message

	def execute(self):
		if self.message is None:
			print "Sleeping", str(self.time), "seconds..." 
		else:
			print self.message
		_time.sleep(self.time)
		print

	def start(self):
		self.execute()

	def stop(self):
		self.execute()


class OnStart(ControlAction):
	""" Control script action to execute an action only on start """
	def __init__(self, controlAction):
		"""
		Initialize OnStart object.
		@param controlAction control action to execute only on start
		"""
		ControlAction.__init__(self, controlAction.description + " on start")
		self.controlAction = controlAction

	def start(self):
		self.controlAction.start()

	def stop(self):
		pass


class OnStop(ControlAction):
	""" Control script action to execute an action only on stop """
	def __init__(self, controlAction):
		"""
		Initialize OnStop object.
		@param controlAction control action to execute only on stop
		"""
		ControlAction.__init__(self, controlAction.description + " on stop")
		self.controlAction = controlAction

	def start(self):
		pass

	def stop(self):
		self.controlAction.stop()
