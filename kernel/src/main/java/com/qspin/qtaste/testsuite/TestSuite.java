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
 * TestSuite.java
 *
 * Created on 11 octobre 2007, 15:43
 */
package com.qspin.qtaste.testsuite;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author lvboque
 */
public abstract class TestSuite implements TestReportListener {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestSuite.class);
    protected String name;
    protected int numberLoops = 1;
    protected boolean loopsInHours = false;
    private Date startExecutionDate;
    private Date stopExecutionDate;
    private int nbTestsToExecute = 0;
    private int nbTestsExecuted = 0;
    private int nbTestsPassed = 0;
    private int nbTestsFailed = 0;
    private int nbTestsNotAvailable = 0;
    private int nbTestsRetries = 0;
    private boolean abortedByUser = false;
    private List<TestReportListener> testReportListeners = new LinkedList<TestReportListener>();

    /** Creates a new instance of TestSuite */
    public TestSuite(String name) {
        this.name = name;
    }

    /**
     * Computes and returns the number of tests to execute.
     * @return number of tests to execute or -1 if unknown
     */
    public abstract int computeNumberTestsToExecute();

    /**
     * Executes the test suite once in specified debug mode.
     * 
     * @param debug true if in debug mode, false otherwise
     * @return true if execution successful, false otherwise (aborted)
     */
    public abstract boolean executeOnce(boolean debug);

    /** Executes a test suite, the number of times specified by numberLoops/loopsInHours.
     * 
     * @param debug true to execute in debug mode, false otherwise
     * @param initializeTestEngine true to initialize/terminate the test engine, false otherwise
     * @return true if execution successful, false otherwise (aborted)
     */
    public boolean execute(boolean debug, boolean initializeTestEngine) {
        boolean executionSuccess = true;
        nbTestsToExecute = computeNumberTestsToExecute();
        startExecutionDate = new Date();
        reportTestSuiteStarted();
        if (nbTestsToExecute != 0) {
            if (!initializeTestEngine || TestEngine.initialize()) {
                if (numberLoops != 1 || loopsInHours) {
                    boolean continueExecution = true;
                    int currentExecution = 1;
                    long startTime_ms = System.currentTimeMillis();
                    do {
                        logger.info("Execution " + currentExecution + (numberLoops != -1 && !loopsInHours ? " of " + numberLoops : "") + " of test suite " + getName());
                        if (!executeOnce(debug)) {
                            executionSuccess = false;
                            break;
                        }
                        if (loopsInHours) {
                            long elapsedTime_ms = System.currentTimeMillis() - startTime_ms;
                            long elapsedTime_h = elapsedTime_ms / 1000 / 3600;
                            continueExecution = elapsedTime_h < numberLoops;
                        } else {
                            continueExecution = numberLoops == -1 || currentExecution < numberLoops;
                        }
                        currentExecution++;
                    } while (continueExecution);
                } else {
                    executionSuccess = executeOnce(debug);
                }
            }
            this.abortedByUser = TestEngine.isStartStopSUTCancelled;
            if (initializeTestEngine) {
                TestEngine.terminate();
            }
        } else {
            logger.warn("Test suite " + getName() + " doesn't contain any test to execute");
        }
        stopExecutionDate = new Date();
        reportTestSuiteStopped();
        return executionSuccess;
    }

    public abstract List<TestScript> getTestScripts();

    public void reportTestSuiteStarted() {
        TestResultsReportManager.getInstance().refresh();
        
        for (TestReportListener testReportListener: testReportListeners) {
            testReportListener.reportTestSuiteStarted();
        }        
    }

    public void reportTestSuiteStopped() {
        TestResultsReportManager.getInstance().refresh();
        
        for (TestReportListener testReportListener: testReportListeners) {
            testReportListener.reportTestSuiteStopped();
        }        
    }

    public boolean isAbortedByUser() {
        return abortedByUser;
    }

    public void setAbortedByUser(boolean value) {
        abortedByUser = value;
    }

    /**
     * @param numberLoops number of executions, number of hours of executions,
     *                    or -1 for infinite number of executions
     * @param loopsInHours if true, numberLoops is the number of hours of executions
     *                     otherwise numberLoops is the number of executions
     */
    public void setExecutionLoops(int numberLoops, boolean loopsInHours) {
        this.numberLoops = numberLoops;
        this.loopsInHours = loopsInHours;
    }
    
    public String getName() {
        return name;
    }

    public Date getStartExecutionDate() {
        return startExecutionDate;
    }

    public Date getStopExecutionDate() {
        return stopExecutionDate;
    }

    public int getNbTestsToExecute() {
        return nbTestsToExecute;
    }

    public void setNbTestsToExecute(int nbTestsToExecute) {
        this.nbTestsToExecute = nbTestsToExecute;
    }

    public int getNbTestsExecuted() {
        return nbTestsExecuted;
    }

     public void reportTestResult(TestResult.Status status) {
        nbTestsExecuted++;
        switch (status) {
            case SUCCESS:
                nbTestsPassed++;
                break;
            case FAIL:
                nbTestsFailed++;
                break;
            case NOT_AVAILABLE:
                nbTestsNotAvailable++;
                break;
            default:
                logger.error("Invalid status: " + status);
        }
        
        for (TestReportListener testReportListener: testReportListeners) {
            testReportListener.reportTestResult(status);
        }
    }

    public void reportTestRetry() {
        nbTestsRetries++;
        
        for (TestReportListener testResultListener: testReportListeners) {
            testResultListener.reportTestRetry();
        }
    }
    
    public int getNbTestsPassed() {
        return nbTestsPassed;
    }

    public int getNbTestsFailed() {
        return nbTestsFailed;
    }

    public int getNbTestsNotAvailable() {
        return nbTestsNotAvailable;
    }

    public int getNbTestsRetries() {
        return nbTestsRetries;
    }

    public void addTestReportListener(TestReportListener listener) {
        testReportListeners.add(listener);
    }
    
    public void removeTestReportListener(TestReportListener listener) {
        testReportListeners.remove(listener);
    }
}
