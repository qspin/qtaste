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
 *
 * @author lvboque
 */
public interface TestResult {

    enum Status {
        NOT_EXECUTED,
        RUNNING,
        NOT_AVAILABLE,
        SUCCESS,
        FAIL
    }

    TestData getTestData();

    List<TestRequirement> getTestRequirements();

    String getExtraResultDetails();

    void setExtraResultDetails(String extraResultDetails);

    String getReturnValue();

    void setReturnValue(String returnValue);

    String getId();

    String getComment();

    void setName(String name);

    String getName();

    void setTestScriptVersion(String version);

    String getTestScriptVersion();

    void setComment(String comment);

    Status getStatus();

    void start();

    void stop();

    void setStatus(Status status);

    Date getStartDate();

    Date getEndDate();

    long getElapsedTimeMs();

    String getFormattedElapsedTime(boolean showMilliseconds);

    void setStackTrace(String stackTrace);

    String getStackTrace();

    void addStackTraceElement(StackTraceElement stackElement);

    void setStack(ArrayList<StackTraceElement> stack);

    ArrayList<StackTraceElement> getStack();

    //public TestResultsReportManager getReportManager();
    //public void setReportManager(TestResultsReportManager reportFormaterManager);

    /**
     * @return diretory containing the testcase
     */
    String getTestCaseDirectory();

    /**
     * Set the testcase directory to the directory value
     */
    void setTestCaseDirectory(String directory);

    int getFailedLineNumber();

    void setFailedLineNumber(int failedLineNumber);

    String getFailedFunctionId();

    void setFailedFunctionId(String failedFunctionId);

    int getRetryCount();

    void setRetryCount(int retryCount);

    void addStepResult(String stepId, String functionName, String stepDescription, String expectedResult, Status stepStatus,
          double elapsedTime);

    Collection<StepResult> getStepResults();

    int getCurrentRowIndex();

    int getNumberRows();

}
