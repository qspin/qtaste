/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
 */

package com.qspin.qtaste.kernel.engine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.datacollection.collection.ProbeManager;
import com.qspin.qtaste.log.Log4jServer;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResult.Status;
import com.qspin.qtaste.reporter.testresults.TestResultImpl;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.testsuite.impl.DirectoryTestSuite;
import com.qspin.qtaste.util.Exec;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * This is the main entry point for the TestEngine application
 * 
 * @author lvboque
 */
public class TestEngine {

	protected static Logger logger = Log4jLoggerFactory.getLogger(TestEngine.class);
	private static TestSuite currentTestSuite;
	private static boolean needToRestartSUT;
	private static Exec sutStartStopExec = new Exec();
	private static volatile boolean isStartStopSUTCancellable = false;
	public static volatile boolean isStartStopSUTCancelled = false;
	private static volatile boolean ignoreControlScript = false;

	/**
	 * Execute a test suite.
	 * 
	 * @param testSuite
	 *            the test suite to execute
	 * @return true if execution successful, false otherwise (aborted)
	 */
	public static boolean execute(TestSuite testSuite) {
		return execute(testSuite, false);
	}

	/**
	 * Execute a test suite.
	 * 
	 * @param testSuite
	 *            the test suite to execute
	 * @param debug
	 *            true to execute in debug mode, false otherwise
	 * @return true if execution successful, false otherwise (aborted)
	 */
	public static boolean execute(TestSuite testSuite, boolean debug) {
		TestBedConfiguration.reloadConfigFileIfModified();
		currentTestSuite = testSuite;
		TestResultsReportManager reportManager = TestResultsReportManager.getInstance();
		reportManager.startReport(testSuite.getName());
		boolean executionSuccess = testSuite.execute(debug, true);
		reportManager.stopReport();
		currentTestSuite = null;
		return executionSuccess;
	}

	public static TestSuite getCurrentTestSuite() {
		return currentTestSuite;
	}

	public static boolean ignoreControlScript() {
		return ignoreControlScript;
	}

	public static void setIgnoreControlScript(boolean pIgnoreControlScript) {
		ignoreControlScript = pIgnoreControlScript;
	}

	public static boolean startSUT(TestResult tr) {
		return startOrStopSUT(true, tr);
	}

	public static boolean stopSUT(TestResult tr) {
		return startOrStopSUT(false, tr);
	}

	/**
	 * Cancels start/stop of SUT. SUT stop in terminate() is not cancellable.
	 */
	public static void cancelStartStopSUT() {
		logger.info("Cancel start/stop SUT");
		if (isStartStopSUTCancellable) {
			isStartStopSUTCancelled = true;
			sutStartStopExec.kill();
		}
	}

	private static boolean startOrStopSUT(boolean start, TestResult tr) {
		needToRestartSUT = !start;
		String startOrStop = start ? "start" : "stop";
		TestBedConfiguration config = TestBedConfiguration.getInstance();
		if (hasControlScript()) {
			if (isStartStopSUTCancelled) {
				if (tr != null) {
					tr.setStatus(Status.FAIL);
					tr.setExtraResultDetails("SUT " + startOrStop + " command cancelled");
				}
				return false;
			}

			String scriptFilename = config.getControlScriptFileName();
			String scriptEngine = null;
			if (scriptFilename.endsWith(".py")) {
				final String jythonHome = StaticConfiguration.JYTHON_HOME;
				final String jythonJar = jythonHome + "/jython.jar";
				final String jythonLib = formatPathString(StaticConfiguration.JYTHON_LIB.trim());
				final String classPath = formatPathString(System.getProperties().getProperty("java.class.path", "").trim());
				// final String javaHome = System.getenv("JAVA_HOME");
				// if (javaHome!=null)
				// 		scriptEngine = javaHome + "/bin/java -Dpython.path=" + jythonJar + File.pathSeparator + jythonLib + " -cp \"" +
				// 					   jythonHome + "/../build/jython-engine.jar"+ // File.pathSeparator + jythonJar + File.pathSeparator +
				// 					   classPath + "\" org.python.util.jython";
				// else
				scriptEngine = "java -Dpython.path=\"" + jythonJar + "\"" + File.pathSeparator + jythonLib 
									+ " -cp \"" + jythonHome + "/../build/jython-engine.jar" + "\"" + File.pathSeparator 
									+ "\"" + jythonJar + "\"" + File.pathSeparator + classPath + " org.python.util.jython";
			}
			String scriptArguments = config.getControlScriptArguments();
			String startOrStopCommand = scriptFilename + " " + startOrStop + " " + (scriptArguments != null ? scriptArguments : "");
			logger.info((start ? "Starting" : "Stopping") + " SUT using command '" + startOrStopCommand + "'");
			// report the control script
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				Map<String, String> env = new HashMap<String, String>(
						System.getenv());
				env.put("TESTBED", config.getFileName());
				String startOrStopFullCommand = (scriptEngine != null ? scriptEngine + " " + startOrStopCommand : startOrStopCommand);
				int exitCode = sutStartStopExec.exec(startOrStopFullCommand, env, output);
				if (exitCode == 0) {
					logger.info("SUT " + (start ? "started" : "stopped"));
					return true;
				} else if (isStartStopSUTCancelled) {
					String errMsg = "SUT " + startOrStop + " command cancelled";
					logger.info(errMsg);
					if (tr != null) {
						tr.setStatus(Status.FAIL);
						tr.setExtraResultDetails(errMsg);
					}
					return false;
				} else {
					String errMsg = "SUT " + startOrStop + " command '" + startOrStopCommand + "' exited with error code " + exitCode + ". Output:\n" + output.toString();
					logger.error(errMsg);
					if (tr != null) {
						tr.setExtraResultDetails(errMsg);
					}
				}
			} catch (IOException e) {
				String errMsg = "Couldn't execute SUT " + startOrStop + " command '" + startOrStopCommand + "': " + e.getMessage();
				logger.error(errMsg);
				if (tr != null) {
					tr.setExtraResultDetails(errMsg);
				}
			} catch (InterruptedException e) {
				String errMsg = "Interrupted while executing SUT " + startOrStop + " command '" + startOrStopCommand + "': " + e.getMessage();
				logger.error(errMsg);
				if (tr != null) {
					tr.setExtraResultDetails(errMsg);
				}
			}
			logger.error("Couldn't " + startOrStop + " SUT");
			if (tr != null) {
				tr.setStatus(Status.FAIL);
			}
		} else {
			logger.info("No SUT control script available for this testbed!");
		}
		return false;
	}

	/**
	 * Detects {@value File#pathSeparator}, and surround them with ".
	 * If the path contains at least one {@value File#pathSeparator}, adds a " at the beginning and at the end of the path.
	 * @param pathToFormat
	 * @return the formatted path.
	 */
	private static String formatPathString(String pathToFormat) {
		//remove first and/or last character is their are ;
		if(  pathToFormat.startsWith(File.pathSeparator) ) {
			pathToFormat = pathToFormat.substring(1);
		}
		if(  pathToFormat.endsWith(File.pathSeparator) ) {
			pathToFormat = pathToFormat.substring(0,pathToFormat.length() -1);
		}
		pathToFormat = pathToFormat.replaceAll(File.pathSeparator, "\"" + File.pathSeparator + "\"");
		if (pathToFormat.indexOf(File.pathSeparator) > 0) {
			if ( !pathToFormat.startsWith("\"")) {
				pathToFormat = "\"" + pathToFormat;
			}
			if ( !pathToFormat.endsWith("\"")) {
				pathToFormat = pathToFormat + "\"";
			}
		}
		return pathToFormat;
	}

	public static boolean restartSUT() {
		if (useControlScript()) {
			logger.info("Restarting SUT");
			TestResult tr = new TestResultImpl("Restart SUT", null, 1, 1);
			tr.setTestScriptVersion("-");
			tr.start();
			TestResultsReportManager reportManager = TestResultsReportManager.getInstance();
			reportManager.putEntry(tr);
			boolean returnValue = stopSUT(tr) && startSUT(tr);
			tr.stop();
			reportManager.refresh();
			// reportManager.stopReport();
			return returnValue;
		} else {
			return false;
		}
	}

	public static boolean needToRestartSUT() {
		return useControlScript() && needToRestartSUT;
	}

	/**
	 * Set need to restart SUT.
	 * 
	 * @return value of needToRestartSUT, which may be set to false if no
	 *         control_script declared
	 */
	public static boolean setNeedToRestartSUT() {
		if (useControlScript()) {
			needToRestartSUT = true;
		} else {
			needToRestartSUT = false;
		}
		return needToRestartSUT;
	}

	public static boolean initialize() {
		if (useControlScript()) {
			isStartStopSUTCancellable = true;
			isStartStopSUTCancelled = false;

			TestResult tr = new TestResultImpl("Start SUT", null, 1, 1);
			tr.setTestScriptVersion("-");
			tr.start();
			TestResultsReportManager reportManager = TestResultsReportManager.getInstance();
			reportManager.putEntry(tr);
			stopSUT(null);
			boolean success = startSUT(tr);
			tr.stop();
			reportManager.refresh();
			if (!success) {
				return false;
			}
		}

		logger.info("Starting TestEngine");
		ProbeManager.getInstance().start();
		return true;
	}

	public static void terminate() {
		// Stop all the probes
		ProbeManager.getInstance().stop();
		logger.info("TestEngine terminated");

		isStartStopSUTCancellable = false;
		isStartStopSUTCancelled = false;

		if (useControlScript()) {
			TestResultsReportManager reportManager = TestResultsReportManager.getInstance();
			TestResult tr = new TestResultImpl("Stop SUT", null, 1, 1);
			tr.setTestScriptVersion("-");
			tr.start();
			reportManager.putEntry(tr);
			stopSUT(tr);
			tr.stop();
			reportManager.refresh();
		}
	}

	public static void shutdown() {
		Log4jServer.getInstance().shutdown();
		LogManager.shutdown();
	}

	private static boolean hasControlScript() {
		return TestBedConfiguration.getInstance().hasControlScript();
	}

	private static boolean useControlScript() {
		return hasControlScript() && !ignoreControlScript();
	}

	private static void showUsage() {
		System.err.println("Usage: <command> -testsuite <testsuiteDirectory> -testbed <configFileName.xml> [-engine <engineFileName.xml>] [-loop [<count> | <hours>h]] [-sutversion <sut_version_identifier>]");
		shutdown();
		System.exit(1);
	}

	public static void main(String[] args) {
		try {
			// Log4j Configuration
			PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

			// log version information
			logger.info("QTaste kernel version: " + com.qspin.qtaste.kernel.Version.getInstance().getFullVersion());
			logger.info("QTaste testAPI version: " + StaticConfiguration.VERSION_CONTROL.getTestApiVersion(""));

			// handle optional config file name
			if ((args.length < 4) || (args.length > 10)) {
				showUsage();
			}
			String testSuiteDir = null;
			String testbed = null;
			int numberLoops = 1;
			boolean loopsInHours = false;
			int i = 0;
			while (i < args.length) {
				if (args[i].equals("-testsuite") && (i + 1 < args.length)) {
					logger.info("Using " + args[i + 1] + " as test suite directory");
					testSuiteDir = args[i + 1];
					i += 2;
				} else if (args[i].equals("-testbed") && (i + 1 < args.length)) {
					logger.info("Using " + args[i + 1] + " as testbed configuration file");
					testbed = args[i + 1];
					i += 2;
				} else if (args[i].equals("-engine") && (i + 1 < args.length)) {
					logger.info("Using " + args[i + 1] + " as engine configuration file");
					TestEngineConfiguration.setConfigFile(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-loop")) {
					String message = "Running test suite in loop";
					numberLoops = -1;
					if ((i + 1 < args.length)) {
						// more arguments, check if next argument is a loop argument
						if (args[i + 1].startsWith("-")) {
							i++;
						} else {
							String countOrHoursStr;
							if (args[i + 1].endsWith("h")) {
								loopsInHours = true;
								countOrHoursStr = args[i + 1].substring(0, args[i + 1].length() - 1);
							} else {
								loopsInHours = false;
								countOrHoursStr = args[i + 1];
							}
							try {
								numberLoops = Integer.parseInt(countOrHoursStr);
								if (numberLoops <= 0) {
									throw new NumberFormatException();
								}
								message += (loopsInHours ? " during " : " ")
										+ numberLoops + " "
										+ (loopsInHours ? "hour" : "time")
										+ (numberLoops > 1 ? "s" : "");
								i += 2;
							} catch (NumberFormatException e) {
								showUsage();
							}
						}
					} else {
						// no more arguments
						i++;
					}
					logger.info(message);
				} else if (args[i].equals("-sutversion") && (i + 1 < args.length)) {
					logger.info("Using " + args[i + 1] + " as sutversion");
					TestBedConfiguration.setSUTVersion(args[i + 1]);
					i += 2;
				} else {
					showUsage();
				}
			}

			if (testSuiteDir == null || testbed == null) {
				showUsage();
			}

			TestBedConfiguration.setConfigFile(testbed);

			// start the log4j server
			Log4jServer.getInstance().start();

			// initialize Python interpreter
			Properties properties = new Properties();
			properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
			properties.setProperty("python.path", StaticConfiguration.JYTHON_LIB);
			PythonInterpreter.initialize(System.getProperties(), properties, new String[] { "" });

			TestSuite testSuite = new DirectoryTestSuite(testSuiteDir);
			testSuite.setExecutionLoops(numberLoops, loopsInHours);
			execute(testSuite);
		} finally {
			shutdown();
		}
	}
}
