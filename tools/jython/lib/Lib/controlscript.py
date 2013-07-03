##.
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
# - ServiceProcess: derived from ControlAction where start and stop methods are implemented
#	This class is initialized with following parameters:
#		- description: name of the native process (title of the window) 
#		-serviceName: name of the service to control
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
#       -priority (optional): specifies to run the process with the given priority: "low", "belownormal", "normal", "abovenormal", "high" or "realtime" or none for default priority
#		-useJacoco (optional): enable the coverage analysis using jacoco tool
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
import traceback
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

controlScriptID = 1

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
		caller = traceback.format_stack()[0].split("\"")[1]
		self.callerScript = caller.split("/")[len(caller.split("/"))-1]
		self.callerDirectory = caller.replace(self.callerScript, "")
		
		if start:
			self.start()
		else:
			self.stop()
	
	def start(self):
		""" Method called on start, starts control actions in defined order"""
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

		for controlAction in self.controlActions:
			if controlAction.active:
				controlAction.start()
	
	def stop(self):
		""" Method called on stop, stops control actions in reverse order """
		for controlAction in self.controlActions[::-1]:
			controlAction.stop()

class ControlAction(object):
	""" Control script action """
	def __init__(self, description, active=True):
		"""
		Initialize ControlAction object.
		@param description string describing the control action
		"""
		global controlScriptID
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.description = description
		self.caID = controlScriptID
		self.active = active
		controlScriptID += 1
		
	def start(self):
		""" Method called on start, to be overridden by subclasses """
		pass

	def stop(self):
		""" Method called on stop, to be overridden by subclasses """
		pass

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		writer.write(prefix + ".description=string\n")
		writer.write(prefix + ".type=string\n")
		writer.write(prefix + ".controlActionID=integer\n")
		writer.write(prefix + ".callerScript=string\n")
		writer.write(prefix + ".active=boolean\n")

	def dump(self, writer):
		""" Method called on start. It dumps the control action parameter in the writer, to be overridden by subclasses """
		writer.write(str(self.caID) + ".description=\"" + self.description + "\"\n")
		writer.write(str(self.caID) + ".type=" + self.__class__.__name__+ "\n")
		writer.write(str(self.caID) + ".controlActionID=" + str(self.caID) + "\n")
		writer.write(str(self.caID) + ".callerScript=" + self.callerScript + "\n")
		if self.active:
			writer.write(str(self.caID) + ".active=true\n")
		else:
			writer.write(str(self.caID) + ".active=false\n")

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
	def __init__(self, description, mainClassOrJar, args=None, workingDir=qtasteRootDirectory, classPath=None, vmArgs="", jmxPort=None, checkAfter=None, priority=None, useJacoco=False, useJavaGUI=False, active=True):
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
		"""
		ControlAction.__init__(self, description, active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.mainClassOrJar = mainClassOrJar
		self.args = args
		if args is None:
			self.mainWithArgs = mainClassOrJar
		else:
			self.mainWithArgs = mainClassOrJar + ' ' + args
		if _OS.getType() != _OS.Type.WINDOWS:
			self.workingDir = workingDir
		else:
			self.workingDir = workingDir.replace("/", _os.sep)
		if classPath:
			if _OS.getType() != _OS.Type.WINDOWS:
				self.classPath = classPath.replace(";",":")
			else:
				self.classPath = classPath.replace(":",";")
				self.classPath = self.classPath.replace("/", _os.sep)
		else:
			self.classPath = None
		self.vmArgs = vmArgs
# 		if useJacoco:
# 			jacocoHome = _os.getenv("JACOCO_HOME")
# 			if not jacocoHome:
# 				print "WARNING: JACOCO_HOME variable not defined - Jacoco coverage disabled!\n"
# 			else:
# 				self.vmArgs += " -javaagent:" + jacocoHome + _os.sep + "lib" + _os.sep + "jacocoagent.jar=append=true,destfile=" + "reports" + _os.sep + description + ".jacoco"
		self.useJacoco = useJacoco
# 		if useJavaGUI:
# 			self.vmArgs += " -javaagent:" + qtasteRootDirectory + "plugins" + _os.sep + "SUT" + _os.sep + "qtaste-javagui-deploy.jar"
		self.useJavaGUI = useJavaGUI
		if jmxPort:
			self.jmxPort = "%d" % jmxPort
		else:
			self.jmxPort = None
		if checkAfter:
			self.checkAfter = "%d" % checkAfter
		else:
			self.checkAfter = None
		self.priority = priority

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(JavaProcess, self).dump(writer)
		if self.args is not None:
			writer.write(str(self.caID) + ".args=\"" + str(self.args) + "\"\n")
		if self.workingDir is not None:
			writer.write(str(self.caID) + ".workingDir=\"" + str(self.workingDir) + "\"\n")
		if self.mainClassOrJar is not None:
			writer.write(str(self.caID) + ".mainClassOrJar=\"" + str(self.mainClassOrJar) + "\"\n")
		if self.classPath is not None:
			writer.write(str(self.caID) + ".classPath=\"" + str(self.classPath) + "\"\n")
		if self.vmArgs is not None:
			writer.write(str(self.caID) + ".vmArgs=\"" + str(self.vmArgs) + "\"\n")
		if self.useJacoco:
			writer.write(str(self.caID) + ".useJacoco=True\n")
		else:
			writer.write(str(self.caID) + ".useJacoco=False\n")
		if self.useJavaGUI:
			writer.write(str(self.caID) + ".useJavaGUI=True\n")
		else:
			writer.write(str(self.caID) + ".useJavaGUI=False\n")
		if self.jmxPort is not None:
			writer.write(str(self.caID) + ".jmxPort=" + str(self.jmxPort) + "\n")
		if self.checkAfter is not None:
			writer.write(str(self.caID) + ".checkAfter=" + str(self.checkAfter) + "\n")
		if self.priority is not None:
			writer.write(str(self.caID) + ".priority=\"" + str(self.priority) + "\"\n")
		pass

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(JavaProcess, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".args=string\n")
		writer.write(prefix + ".workingDir=string\n")
		writer.write(prefix + ".mainClassOrJar=string\n")
		writer.write(prefix + ".classPath=string\n")
		writer.write(prefix + ".vmArgs=string\n")
		writer.write(prefix + ".useJacoco=boolean\n")
		writer.write(prefix + ".useJavaGUI=boolean\n")
		writer.write(prefix + ".jmxPort=integer\n")
		writer.write(prefix + ".checkAfter=integer\n")
		writer.write(prefix + ".priority=string\n")
	
	def getJacocoVar(self):
		if self.useJacoco:
			jacocoHome = _os.getenv("JACOCO_HOME")
			if not jacocoHome:
				print "WARNING: JACOCO_HOME variable not defined - Jacoco coverage disabled!\n"
				return ""
			else:
				return " -javaagent:" + jacocoHome + _os.sep + "lib" + _os.sep + "jacocoagent.jar=append=true,destfile=" + "reports" + _os.sep + self.description + ".jacoco"

	def getJavaGUIVar(self):
		if self.useJavaGUI:
			return " -javaagent:" + qtasteRootDirectory + "plugins" + _os.sep + "SUT" + _os.sep + "qtaste-javagui-deploy.jar"
		return ""

	def start(self):
		print "Starting " + self.description + "...";
		isJar = self.mainClassOrJar.endswith(".jar")
		
		vmArgs = self.vmArgs
		if self.useJacoco:
			vmArgs += " " + self.getJacocoVar()
		if self.useJavaGUI:
			vmArgs += " " + self.getJavaGUIVar()
			
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
			if len(vmArgs) > 0:
				shellScriptArguments.append("-vmArgs")
				shellScriptArguments.append(vmArgs)
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
			if len(self.vmArgs) > 0:
				shellScriptArguments += ' -vmArgs "' + vmArgs + '"';
			if self.jmxPort:
				shellScriptArguments += ' -jmxPort ' + str(self.jmxPort);
			if self.checkAfter:
				shellScriptArguments += ' -checkAfter ' + str(self.checkAfter);
			if self.priority:
				shellScriptArguments += ' -priority ' + self.priority;
		
		print(str(shellScriptArguments))
		ControlAction.executeShellScript("start_java_process", shellScriptArguments);
		print 

	def stop(self):
		print "Stopping " + self.description + "...";
		ControlAction.executeShellScript("stop_java_process", [self.mainWithArgs])
		print

class NativeProcess(ControlAction):
	""" Control script action for starting/stopping a native process """
	def __init__(self, description, executable, args=None, workingDir=qtasteRootDirectory, checkAfter=None, active=True):
		"""
		Initialize NativeProcess object
		@param description control script action description, also used as window title
		@param executable native process to execute
		@param args arguments to pass to the application or None if no argument
		@param workingDir working directory to start process in, defaults to QTaste root directory
		@param checkAfter number of seconds after which to check if process still exist or None to not check
		"""
		ControlAction.__init__(self, description, active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.executable = executable
		self.args = args
		if args is None:
			self.args = ''
		self.workingDir = workingDir
		if checkAfter:
			self.checkAfter = "%d" % checkAfter
		else:
			self.checkAfter = None

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(NativeProcess, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".executable=string\n")
		writer.write(prefix + ".args=string\n")
		writer.write(prefix + ".workingDir=string\n")
		writer.write(prefix + ".checkAfter=integer\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(NativeProcess, self).dump(writer)
		writer.write(str(self.caID) + ".executable=\"" + str(self.executable) + "\"\n")
		writer.write(str(self.caID) + ".workingDir=\"" + str(self.workingDir) + "\"\n")
		writer.write(str(self.caID) + ".args=\"" + str(self.args) + "\"\n")
		writer.write(str(self.caID) + ".checkAfter=" + str(self.checkAfter) + "\n")

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
		shellScriptArguments = self.executable
		shellScriptArguments = shellScriptArguments + " " + self.args
		if _OS.getType() != _OS.Type.WINDOWS:
			ControlAction.executeShellScript("stop_process", '"' + shellScriptArguments + '"')
		else:
			ControlAction.executeShellScript("stop_process", shellScriptArguments)

class ServiceProcess(ControlAction):
	""" Control script action for starting/stopping a service process """
	def __init__(self, description, serviceName, active=True):
		"""
		Initialize ServiceProcess object
		@param description control script action description, also used as window title
		@param serviceName name of the service to control
		"""
		ControlAction.__init__(self, description, active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.serviceName = serviceName

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(ServiceProcess, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".serviceName=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(ServiceProcess, self).dump(writer)
		writer.write(str(self.caID) + ".serviceName=\"" + str(self.serviceName) + "\"\n")

	def start(self):
		print "Starting " + self.description + "...";
		if _OS.getType() != _OS.Type.WINDOWS:
			print "Not yet implemented!"
			#shellScriptArguments = []
			#shellScriptArguments.append(self.serviceName)
			#shellScriptArguments.append("-title")
			#shellScriptArguments.append(self.description)
		else:
			shellScriptArguments = '"' + self.serviceName + '" -title "' + self.description + '"';
		
		ControlAction.executeShellScript("start_service_process", shellScriptArguments);
		print

	def stop(self):
		print "Stopping " + self.description + "...";
		shellScriptArguments = self.serviceName
		if _OS.getType() != _OS.Type.WINDOWS:
			print "Not yet implemented!"
			#ControlAction.executeShellScript("stop_service_process", '"' + shellScriptArguments + '"')
		else:
			print "command : stop_service_process " + shellScriptArguments
			ControlAction.executeShellScript("stop_service_process", shellScriptArguments)

class ReplaceInFiles(ControlAction):
	""" Control script action for doing a replace in file(s), only on start """
	def __init__(self, findString, replaceString, files, active=True):
		"""
		Initialize ReplaceInFiles object.
		@param findString regular expression string to find
		@param replaceString string by which to replace findString, may contain matches references in the form \1
		@param files file name or list of files names
		"""
		ControlAction.__init__(self, "Replace in file(s)", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.findString = findString
		self.replaceString = replaceString
		self.files = _IF(type(files) == str, files, " ".join(files)) 
		sed = _IF(_OS.getType() == _OS.Type.WINDOWS, qtasteRootDirectory + r"tools\GnuWin32\bin\sed", "sed")
		self.sedCommand = sed + " -r -i s/" + findString.replace("/", r"\/") + "/" + replaceString.replace("/", r"\/") + "/g " + self.files

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(ReplaceInFiles, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".findString=string\n")
		writer.write(prefix + ".replaceString=string\n")
		writer.write(prefix + ".files=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(ReplaceInFiles, self).dump(writer)
		writer.write(str(self.caID) + ".findString=\"" + str(self.findString) + "\"\n")
		writer.write(str(self.caID) + ".replaceString=\"" + str(self.replaceString) + "\"\n")
		writer.write(str(self.caID) + ".files=\"" + str(self.files) + "\"\n")

	def start(self):
		print "Replacing", repr(self.findString), "by", repr(self.replaceString), "in", self.files
		ControlAction.executeCommand(self.sedCommand)
		print

	def stop(self):
		pass


class Rsh(ControlAction):
	""" Control script action for executing a command on a remote host using rsh """
	def __init__(self, startCommand, stopCommand, host, login, active=True):
		"""
		Initialize Rsh object.
		@param startCommand command to execute on start
		@param stopCommand command to execute on stop
		@param host remote host
		@param login remote user login
		"""
		ControlAction.__init__(self, "Remote command execution using rsh", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.startCommand = startCommand
		self.stopCommand = stopCommand
		self.host = host
		self.login = login

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(Rsh, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".startCommand=string\n")
		writer.write(prefix + ".stopCommand=string\n")
		writer.write(prefix + ".host=string\n")
		writer.write(prefix + ".login=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(Rsh, self).dump(writer)
		writer.write(str(self.caID) + ".startCommand=\"" + str(self.startCommand) + "\"\n")
		writer.write(str(self.caID) + ".stopCommand=\"" + str(self.stopCommand) + "\"\n")
		writer.write(str(self.caID) + ".host=\"" + str(self.host) + "\"\n")
		writer.write(str(self.caID) + ".login=\"" + str(self.login) + "\"\n")
		
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
	def __init__(self, startCommand, stopCommand, host, login, password, active=True):
		"""
		Initialize RExec object.
		@param startCommand command to execute on start
		@param stopCommand command to execute on stop
		@param host remote host
		@param login remote user login
		@param password remote user password
		"""
		ControlAction.__init__(self, "Remote command execution using rexec", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.startCommand = startCommand
		self.stopCommand = stopCommand
		self.host = host
		self.login = login
		self.password = password

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(RExec, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".findString=string\n")
		writer.write(prefix + ".replaceString=string\n")
		writer.write(prefix + ".files=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(RExec, self).dump(writer)
		writer.write(str(self.caID) + ".startCommand=\"" + str(self.startCommand) + "\"\n")
		writer.write(str(self.caID) + ".stopCommand=\"" + str(self.stopCommand) + "\"\n")
		writer.write(str(self.caID) + ".host=\"" + str(self.host) + "\"\n")
		writer.write(str(self.caID) + ".login=\"" + str(self.login) + "\"\n")
		writer.write(str(self.caID) + ".password=\"" + str(self.password) + "\"\n")
		
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
	def __init__(self, host, login, log4jconf, command=None, active=True):
		"""
		Initialize RLogin object.
		@param command command to execute using rlogin
		@param host remote host
		@param login remote user login
		"""
		ControlAction.__init__(self, "Remote command execution and/or logging using rlogin", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.command = ControlAction.escapeArgument(command)
		self.host = host
		self.login = login
		self.logconf = log4jconf
		if command:
			JavaProcess.__init__(self, "RLogin", "com.qspin.qtaste.tcom.rlogin.RLogin", '%s -command "%s" -logOutput -interactive -log4jconf %s' %(self.host, command, self.logconf), "%s" % qtasteRootDirectory, "kernel/target/qtaste-kernel-deploy.jar" )
		else:		
			JavaProcess.__init__(self, "RLogin", "com.qspin.qtaste.tcom.rlogin.RLogin", '%s -logOutput -interactive -log4jconf %s' %(self.host, self.logconf), "%s" % qtasteRootDirectory, "kernel/target/qtaste-kernel-deploy.jar" )

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(RLogin, self).dump(writer, prefix)
		writer.write(prefix + ".command=string\n")
		writer.write(prefix + ".host=string\n")
		writer.write(prefix + ".login=string\n")
		writer.write(prefix + ".logconf=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(RLogin, self).dump(writer)
		writer.write(str(self.caID) + ".command=\"" + str(self.command) + "\"\n")
		writer.write(str(self.caID) + ".log4jconf=\"" + str(self.logConf) + "\"\n")
		writer.write(str(self.caID) + ".host=\"" + str(self.host) + "\"\n")
		writer.write(str(self.caID) + ".login=\"" + str(self.login) + "\"\n")

	def start(self):
		print "Starting execution of remote command '%s' and logging output using log4j on %s" % (self.command, self.host)
		super(RLogin, self).start()

	def stop(self):
		print "Stopping execution of remote command '%s' and logging output using log4j on %s" % (self.command, self.host)
		super(RLogin, self).stop()


class RebootRlogin(ControlAction):
	def __init__(self, host, login, waitingTime=60, active=True):
		"""
		Initialize RebootRLogin object.
		@param host remote host
		@param login remote user login
		@param waitingTime time to wait, after reboot
		"""
		ControlAction.__init__(self, "Remote reboot using rlogin", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.host = host
		self.login = login
		self.waitingTime = waitingTime
		self.localuser = _IF(_OS.getType() == _OS.Type.WINDOWS, _os.getenv("username"), _os.getenv("user"))
		self.rlogin = _RLogin(host, self.localuser, login, "", False, False)

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(RebootRlogin, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".waitingTime=integer\n")
		writer.write(prefix + ".host=string\n")
		writer.write(prefix + ".login=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(RebootRlogin, self).dump(writer)
		writer.write(str(self.caID) + ".waitingTime=" + str(self.waitingTime) + "\n")
		writer.write(str(self.caID) + ".host=\"" + str(self.host) + "\"\n")
		writer.write(str(self.caID) + ".login=\"" + str(self.login) + "\"\n")

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
	def __init__(self, time, message = None, active=True):
		"""
		Initialize Sleep object.
		@param time time to sleep, in seconds, may be a floating point value
		@param message message to print or None to print a standard message 
		"""
		ControlAction.__init__(self, "Sleep", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.time = time
		self.message = message

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(Sleep, self).dumpDataType(prefix, writer)
		writer.write(prefix + ".time=integer\n")
		writer.write(prefix + ".message=string\n")

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(Sleep, self).dump(writer)
		writer.write(str(self.caID) + ".time=" + str(self.time) + "\n")
		writer.write(str(self.caID) + ".message=\"" + str(self.message) + "\"\n")

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
	def __init__(self, controlAction, active=True):
		"""
		Initialize OnStart object.
		@param controlAction control action to execute only on start
		"""
		ControlAction.__init__(self, controlAction.description + " on start", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.controlAction = controlAction

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(OnStart, self).dumpDataType(prefix, writer)
		controlAction.dumpDataType(prefix, writer)

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(OnStart, self).dump(writer)
		self.controlAction.dump(writer)

	def start(self):
		self.controlAction.start()

	def stop(self):
		pass


class OnStop(ControlAction):
	""" Control script action to execute an action only on stop """
	def __init__(self, controlAction, active=True):
		"""
		Initialize OnStop object.
		@param controlAction control action to execute only on stop
		"""
		ControlAction.__init__(self, controlAction.description + " on stop", active)
		self.callerScript = traceback.format_stack()[0].split("\"")[1]
		self.controlAction = controlAction

	def dumpDataType(self, prefix, writer):
		""" Method called on start. It dumps the data type. to be overridden by subclasses """
		super(OnStop, self).dumpDataType(prefix, writer)
		controlAction.dumpDataType(prefix, writer)

	def dump(self, writer):
		""" Method called on start. It dump the control action parameter in the writer, to be overridden by subclasses """
		super(OnStop, self).dump(writer)
		self.controlAction.dump(writer)

	def start(self):
		pass

	def stop(self):
		self.controlAction.stop()
