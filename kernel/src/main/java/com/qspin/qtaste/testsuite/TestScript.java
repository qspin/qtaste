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

/*
 * TestScript.java
 *
 * Created on 11 octobre 2007, 15:21
 */
package com.qspin.qtaste.testsuite;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.kernel.testapi.TestAPI;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultImpl;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author lvboque
 */
public abstract class TestScript implements Executable {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestScript.class);
    private String name;
    private String version;
    private File fileName;
    protected TestSuite testSuite;
    protected File testSuiteDirectory;
    private TestDataSet ds;
    private List<TestRequirement> requirements;
    private List<TestResult> testResults;
    protected TestAPI testAPI;
    private boolean abortedByUser = false;
    private static final int DEFAULT_TIMEOUT = 60 * 1000;
    private int RETRY_COUNTER;

    public TestScript(File fileName, File testSuiteDirectory, String name, TestDataSet data, List<TestRequirement> requirements, TestSuite testSuite) {
        this.name = name;
        this.ds = data;
        this.requirements = requirements;
        this.testAPI = TestAPIImpl.getInstance();
        this.fileName = fileName;
        this.testSuite = testSuite;
        this.testSuiteDirectory = testSuiteDirectory;
        // initialize the retry count
        this.RETRY_COUNTER = TestEngineConfiguration.getInstance().getBoolean("retry_test_on_fail", true) ? 1 : 0;
    }

    public TestDataSet getTestDataSet() {
        return ds;
    }

    /**
     * This method will run the TestCase with all the TestData defined
     * @param debug true if in debug mode, false otherwise
     * @return true if success, false otherwise
     */
    public boolean execute(boolean debug) {
        final String INTERACTIVE_REPORT_NAME = "Interactive";
        boolean returnStatus = true;
        TestResultsReportManager reportManager = TestResultsReportManager.getInstance();
        if (testSuite == null && 
        		(reportManager.getReportName() == null ||  reportManager.getReportName().equals(INTERACTIVE_REPORT_NAME)) ) {
            reportManager.startReport(new Date(), INTERACTIVE_REPORT_NAME);
        }
        testResults = new LinkedList<TestResult>();
        
        for (TestData data : ds.getData()) {
            if (data.isSelected()) {
                data.setTestCaseDirectory(fileName.toString());
                data.loadFileIfAny();

                TestResult.Status status = TestResult.Status.NOT_EXECUTED;
                int trial = 0;
                boolean needToRetry = false;
                // Retry the script "RETRY_COUNTER" times in case of failure
                do {
                    if (TestEngine.needToRestartSUT()) {
                        logger.info("SUT has to be restarted");
                        if (!TestEngine.restartSUT()) {
                            logger.fatal("Failed to restart SUT - exiting");
                            return false;
                        }
                    }

                    if (trial == 0) {
                        logger.info("Executing test script: " + getName() + " (row " + data.getRowId() + ")");
                    } else {
                        logger.info("Retrying test script: " + getName() + " (row " + data.getRowId() + ") after SUT restart");
                        if (testSuite != null) {
                            testSuite.reportTestRetry();
                        }
                    }

                    TestResult testResult = initTestResult(data, requirements, trial, reportManager, ds.getData().indexOf(data), ds.getData().size());
                    testResults.add(testResult);

                    int timeout = DEFAULT_TIMEOUT;
                    if (debug) {
                        logger.info("Not using test timeout because running in debug mode");                    	
                    } else {
	                    try {
	                        timeout = data.getIntValue("TIMEOUT");
	                        logger.info("Using test timeout of " + timeout + " seconds");
	                        timeout = timeout * 1000;
	                    } catch (QTasteDataException e) {
	                        if (e.getMessage().contains("doesn't contain")) {
	                            logger.info("No TIMEOUT test data, using default test timeout (" + DEFAULT_TIMEOUT / 1000 + " seconds)");
	                        } else {
	                            logger.error(e.getMessage() + ". Using default test timeout (" + DEFAULT_TIMEOUT / 1000 + " seconds)");
	                        }
	                    }
                    }

                    TaskThread taskThread = new TaskThread(debug, data, testResult, timeout);

                    // clear cache history
                    CacheImpl.getInstance().clearHistory();

                    // initialize instantiated components
                    testAPI.initializeComponents();

                    testResult.start();

                    // wait till the end of the Task or Timeout
                    reportManager.putEntry(testResult);

                    taskThread.start();
                    boolean taskThreadTerminated = taskThread.waitForEnd();

                    reportManager.refresh();

                    // terminate instantiated components
                    testAPI.terminateComponents();

                    // exit QTaste if test thread couldn't be stopped, because we are in an unstable state
                    if (!taskThreadTerminated) {
                        JOptionPane.showMessageDialog(null, "Couldn't stop test thread!\nQTaste will now exit because system state is unstable.", "Fatal error", JOptionPane.ERROR_MESSAGE);
                        TestEngine.shutdown();
                        System.exit(1);
                    }

                    status = testResult.getStatus();
                    if (status != TestResult.Status.SUCCESS) 
                    {
                        if (status == TestResult.Status.FAIL) {
                           needToRetry = TestEngine.setNeedToRestartSUT();
                        }
                        returnStatus = false;
                    }
                    trial++;
                } while (needToRetry && trial <= RETRY_COUNTER);

                if (testSuite != null) {
                    testSuite.reportTestResult(status);
                }
            }

            if (isAbortedByUser()) {
                return false;
            }
        }
        return returnStatus;
    }

    public boolean isAbortedByUser() {
        return abortedByUser;
    }

    public void setAbortedByUser(boolean value) {
        abortedByUser = value;
    }

    public String getTestCaseDirectory() {
        return fileName.toString();
    }

    private TestResult initTestResult(TestData data, List<TestRequirement> requirements, int retryCount, TestResultsReportManager reporter, int currentRowIndex, int numberRows) {
        String testCaseDirectory = getTestCaseDirectory();
        String testSuiteDirectory = this.testSuiteDirectory.toString();
        String testCaseName;
        if (testCaseDirectory.equals(testSuiteDirectory)) {
            testCaseName = testCaseDirectory.substring(testCaseDirectory.lastIndexOf(File.separator) + 1);
        } else {
            testCaseName = testCaseDirectory.substring(testSuiteDirectory.length() + 1);
        }
        if (testCaseName.equals("QTaste_interactive")) {
        	testCaseName = name;
        }

        TestResult result = new TestResultImpl(testCaseName, data, requirements, currentRowIndex, numberRows);
        result.setTestCaseDirectory(testCaseDirectory);
        result.setTestScriptVersion(version);

        // TODO: What's this???
        //if (reporter != null) {
        //    result.setReportManager(reporter);
        //}
        try {
            result.setComment(data.getValue("COMMENT"));
        } catch (QTasteException e) {
        }

        result.setRetryCount(retryCount);

        return result;
    }

    public void handleQTasteException(QTasteException e, TestResult result) {
    	String message = null;
    	
    	if (e instanceof QTasteTestFailException) {
            result.setStatus(TestResult.Status.FAIL);
            message = e.getMessage();    		
    	} else if (e instanceof QTasteDataException) {
            result.setStatus(TestResult.Status.NOT_AVAILABLE);
            message = e.getMessage();    		
    	} else {
            result.setStatus(TestResult.Status.NOT_AVAILABLE);
            message = e.getMessage();
            StackTraceElement elements[] = (e.getCause() != null ? e.getCause().getStackTrace() : e.getStackTrace());
            for (int i = 0,  n = elements.length; i < n; i++) {
                if (elements[i].getMethodName().startsWith("invoke")) {
                    break;
                }
                message += "\nat " + elements[i].getClassName() + "." + elements[i].getMethodName() + "(" + elements[i].getFileName() + ":" + elements[i].getLineNumber() + ")";
            }    		
    	}
    	
    	result.stop();
        result.setExtraResultDetails(message);
    }

    public void setName(String value) {
        name = value;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String value) {
    	version = value;
    }

    public String getVersion() {
        return version;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public class TaskThread extends Thread {

        private boolean debug;
        private TestData data;
        private TestResult result;
        private long timeout;

        private TaskThread(boolean debug, TestData data, TestResult result, long timeout) {
            this.debug = debug;
            this.data = data;
            this.result = result;
            this.timeout = timeout;
            setName("taskThread");
        }

        public boolean waitForEnd() {
            try {
            	if (debug) {
            		join();
            	} else {
            		join(timeout);
            	}

                if (getState() == Thread.State.TERMINATED) {
                    return true;
                } else {
                    logger.info("Task thread timed out!");
                    return abort("Test execution timeout", TestResult.Status.FAIL, false);
                }
            } catch (InterruptedException e) {
                logger.error("waitForEnd() has been interrupted!");
                return false;
            }
        }

        @SuppressWarnings("deprecation")
        public boolean abort(String message, TestResult.Status status, boolean abortedByUser) {
            try {
                logger.info("Aborting test thread!");
                stop(); // force the task thread to stop (WARNING: this is unsafe)
                join(30000); // wait for the task thread end for max 30 seconds
            } catch (InterruptedException e) {
                logger.error("abort has been interrupted!");
            }

            setAbortedByUser(abortedByUser);
            result.setStatus(status);
            result.setExtraResultDetails(message);
            result.stop();

            if (getState() == State.TERMINATED) {
                return true;
            } else {
                logger.fatal("Couldn't stop test thread");
                return false;
            }
        }

        @Override
        public void run() {
            execute(data, result, debug);
            result.stop();
        }
    }
}
