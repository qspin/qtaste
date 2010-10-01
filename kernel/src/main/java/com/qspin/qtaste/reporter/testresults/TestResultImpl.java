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
 * TestResultImpl.java
 *
 * Created on 11 octobre 2007, 16:26
 */
package com.qspin.qtaste.reporter.testresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;

import com.qspin.qtaste.reporter.Result;
import com.qspin.qtaste.testsuite.TestData;

/**
 *
 * @author lvboque
 */
public class TestResultImpl extends Result implements TestResult  {

    private TestData data;
    private long start;
    private long end;
    private String name;
    private String testScriptVersion;
    private String id;
    private String comment;
    private String extraResultDetails;
    private String returnValue;
    private Status status;
    private String testCaseDirectory;
    private int failedLineNumber;
    private String failedFunctionId;
    private String stackTrace;
    private ArrayList<StackTraceElement> stack=null;
    private int retryCount=0;
    private int mCurrentRowIndex;
    private int mNumberRows;

    
    public int getCurrentRowIndex() {
    	return mCurrentRowIndex;
    }
    public int getNumberRows() {
    	return mNumberRows;
    }
    
    // stores the step execution result
    private LinkedHashMap<String, StepResult> stepResults;
    //private TestResultsReportManager reportFormaterManager;

    /** Creates a new instance of TestResultImpl */
    public TestResultImpl(String name, TestData data, int currentRowIndex, int numberRows) {
        this.data = data;
        mCurrentRowIndex = currentRowIndex;
        mNumberRows = numberRows;
        setName(name);
        testCaseDirectory = "Not defined";
        comment = "";
        extraResultDetails = "";
        failedLineNumber = 0;
        failedFunctionId = "";
        start = 0;
        end = 0;
        status = Status.NOT_EXECUTED;
        extraResultDetails = "N/A";
        stepResults = new LinkedHashMap<String, StepResult>();
        this.stack  = new ArrayList<StackTraceElement>();
    }

    public TestData getTestData() {
        return data;
    }
    
    /**
     * Getter for property extraResultDetails.
     * @return Value of property extraResultDetails.
     */
    public String getExtraResultDetails() {
        return extraResultDetails;
    }

    /**
     * Setter for property extraResultDetails.
     * @param extraResultDetails New value of property extraResultDetails.
     */
    public void setExtraResultDetails(String extraResultDetails) {
        this.extraResultDetails = extraResultDetails;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
        id = name;
        if (data != null && this.getNumberRows() > 1) {
          id += " - " + data.getRowId();
        }
    }

    public String getName() {
        return name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public Status getStatus() {
        return status;
    }

    public void start() {
        status = Status.RUNNING;
        extraResultDetails = "Running...";
        if (start == 0) {
            start = System.currentTimeMillis();
        }
    }

    public void stop() {
        if (end == 0) {
            this.end = System.currentTimeMillis();
        }
        if (status == Status.RUNNING) {
            status = Status.SUCCESS;
            extraResultDetails = "Passed";
        }
    }

    /**
     * Setter for property status.
     * @param status New value of property status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartDate() {
        return new Date(start);
    }

    public Date getEndDate() {
        return new Date(end);
    }
    
    public long getElapsedTimeMs() {
        if (start == 0) {
            return 0;
        }
        if (end != 0) {
            return end - start;
        } else {
            return System.currentTimeMillis() - start;
        }
    }
    
    public String getFormattedElapsedTime(boolean showMilliseconds) {
        return getFormattedElapsedTime(getElapsedTimeMs(), showMilliseconds);
    }

    public void setTestCaseDirectory(String directory) {
        testCaseDirectory = directory;
    }

    public String getTestCaseDirectory() {
        return testCaseDirectory;
    }

    public int getFailedLineNumber() {
        return failedLineNumber;
    }

    public void setFailedLineNumber(int failedLineNumber) {
        this.failedLineNumber = failedLineNumber;
    }

    public String getFailedFunctionId() {
        return failedFunctionId;
    }

    public void setFailedFunctionId(String failedFunctionId) {
        this.failedFunctionId = failedFunctionId;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public void addStepResult(String stepId, String functionName, String stepDescription, String expectedResult, Status stepStatus, double elapsedTime) {
        StepResult stepResult;
        if (stepResults.containsKey(stepId)) {
            stepResult = stepResults.get(stepId);
        } else {
            stepResult =  new StepResult();
        	stepResult.setStepDescription(stepDescription);
        	stepResult.setExpectedResult(expectedResult);
            stepResults.put(stepId, stepResult);
        }
        stepResult.setStepId(stepId);
        stepResult.setFunctionName(functionName);
        stepResult.setStatus(stepStatus);
        stepResult.setElpasedTime(elapsedTime);
    }

    public Collection<StepResult> getStepResults() {
        return stepResults.values();
    }
    
    public class StepResult {
        private Status status;
        private double elpasedTime;
        private String functionName;
        private String stepId;
        private String stepDescription;
        private String expectedResult;

        public StepResult()
        {}
        
        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public double getElpasedTime() {
            return elpasedTime;
        }

        public void setElpasedTime(double elpasedTime) {
            this.elpasedTime = elpasedTime;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getStepId() {
            return stepId;
        }

        public void setStepId(String stepId) {
            this.stepId = stepId;
        }

		public void setStepDescription(String stepDescription) {
			this.stepDescription = stepDescription;
		}

		public String getStepDescription() {
			return stepDescription;
		}

		public void setExpectedResult(String expectedResult) {
			this.expectedResult = expectedResult;
		}

		public String getExpectedResult() {
			return expectedResult;
		}
    }

//    public TestResultsReportManager getReportManager() {
//        return this.reportFormaterManager;
//    }

//    public void setReportManager(TestResultsReportManager reportFormaterManager) {
//        this.reportFormaterManager = reportFormaterManager;
//    }

    public void setStack(ArrayList<StackTraceElement> stack) {
        this.stack = stack;
    }

    public ArrayList<StackTraceElement> getStack() {
        return stack;
    }

    public void addStackTraceElement(StackTraceElement stackElement)
    {
        stack.add(stackElement);
    }

	public String getTestScriptVersion() {
		return testScriptVersion;
	}

	public void setTestScriptVersion(String version) {
		testScriptVersion = version;
	}
}
