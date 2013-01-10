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

package com.qspin.qtaste.reporter.testresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.qspin.qtaste.reporter.testresults.TestResultImpl.StepResult;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.TestRequirement;

/**
 * Description of all the fields present in the testresult reports
 * @author lvboque
 */
public interface TestResult {

    public enum Status {
        NOT_EXECUTED,
        RUNNING,
        NOT_AVAILABLE,
        SUCCESS,
        FAIL
    }
    
    public TestData getTestData();
    public List<TestRequirement> getTestRequirements();
    public String getExtraResultDetails();
    public void setExtraResultDetails(String extraResultDetails);        
    public String getReturnValue();
    public void setReturnValue(String returnValue);        
    public String getId();
    public String getComment();
    public void setName(String name);
    public String getName();
    public void setTestScriptVersion(String version);
    public String getTestScriptVersion();
    public void setComment(String comment);
    public Status getStatus();
    public void start();
    public void stop();
    public void setStatus(Status status);
    public Date getStartDate();
    public Date getEndDate();
    public long getElapsedTimeMs();
    public String getFormattedElapsedTime(boolean showMilliseconds);
    public void setStackTrace(String stackTrace);
    public String getStackTrace();
    
    public void addStackTraceElement(StackTraceElement stackElement);
    public void setStack(ArrayList<StackTraceElement> stack);
    public ArrayList<StackTraceElement> getStack();
    
    //public TestResultsReportManager getReportManager();
    //public void setReportManager(TestResultsReportManager reportFormaterManager);
    
    /**
     * 
     * @return diretory containing the testcase
     */
    public String getTestCaseDirectory();
    
    /**
     * Set the testcase directory to the directory value 
     */
    public void setTestCaseDirectory(String directory);
    
    public int getFailedLineNumber() ;

    public void setFailedLineNumber(int failedLineNumber);
    
    public String getFailedFunctionId();
    public void setFailedFunctionId(String failedFunctionId);
    
    public int getRetryCount();
    public void setRetryCount(int retryCount);
    public void addStepResult(String stepId, String functionName, String stepDescription, String expectedResult, Status stepStatus, double elapsedTime);
    public Collection<StepResult> getStepResults();
    public int getCurrentRowIndex();
    public int getNumberRows();

}
