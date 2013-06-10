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

package com.qspin.qtaste.reporter.campaign;

import java.util.Date;

import com.qspin.qtaste.reporter.Result;
import com.qspin.qtaste.testsuite.TestSuite;

/**
 * Description of all the fields present in the campaign reports
 * @author lvboque
 */
public class CampaignResult extends Result {

    private String testbed;
    private Status status;
    private Date startExecutionDate;
    private Date stopExecutionDate;
    private int nbTestsToExecute = 0;
    private int nbTestsExecuted = 0;
    private int nbTestsPassed = 0;
    private int nbTestsFailed = 0;
    private int nbTestsNotAvailable = 0;
    private int nbTestsRetries = 0;
    private String detailedURL;

    public enum Status {

        NOT_EXECUTED,
        RUNNING,
        NOT_AVAILABLE,
        SUCCESS,
        FAIL
    }

    public CampaignResult(String testbed) {
        this.testbed = testbed;
    }

    public String getDetailedURL() {
        return detailedURL;
    }

    public void setDetailedURL(String detailedURL) {
        this.detailedURL = detailedURL;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTestbed(String testbed) {
        this.testbed = testbed;
    }

    public String getTestBed() {
        return this.testbed;
    }

    public void setTestSuiteResult(TestSuite testSuite) {
        nbTestsToExecute = testSuite.getNbTestsToExecute();
        nbTestsExecuted = testSuite.getNbTestsExecuted();
        nbTestsPassed = testSuite.getNbTestsPassed();
        nbTestsFailed = testSuite.getNbTestsFailed();
        nbTestsNotAvailable = testSuite.getNbTestsNotAvailable();
        nbTestsRetries = testSuite.getNbTestsRetries();
        nbTestsFailed = testSuite.getNbTestsFailed();
        startExecutionDate = testSuite.getStartExecutionDate();
        stopExecutionDate = testSuite.getStopExecutionDate();

        if (stopExecutionDate == null) {
            setStatus(Status.RUNNING);
        } else if (nbTestsFailed > 0) {
            setStatus(Status.FAIL);
        } else if (nbTestsNotAvailable > 0) {
            setStatus(Status.NOT_AVAILABLE);            
        } else if (nbTestsExecuted == 0) {
            setStatus(Status.NOT_AVAILABLE);
        } else {
            if ( (nbTestsExecuted == nbTestsToExecute) ) {
                setStatus(Status.SUCCESS);
            } else {
                // Something went wrong ... Probably SUT was not started properly
                setStatus(Status.NOT_AVAILABLE);
            }
        }
    }

    public int getNbTestsExecuted() {
        return nbTestsExecuted;
    }

    public int getNbTestsFailed() {
        return nbTestsFailed;
    }

    public int getNbTestsNotAvailable() {
        return nbTestsNotAvailable;
    }

    public int getNbTestsPassed() {
        return nbTestsPassed;
    }

    public int getNbTestsRetries() {
        return nbTestsRetries;
    }

    public int getNbTestsToExecute() {
        return nbTestsToExecute;
    }

    public Date getStartExecutionDate() {
        return startExecutionDate;
    }

    public Date getStopExecutionDate() {
        return stopExecutionDate;
    }

    public long getElapsedTimeMs() {
        if (startExecutionDate == null) {
            return 0;
        }
        if (stopExecutionDate != null) {
            return stopExecutionDate.getTime() - startExecutionDate.getTime();
        } else {
            return System.currentTimeMillis() - startExecutionDate.getTime();
        }
    }
    
     public String getFormattedElapsedTime(boolean showMilliseconds) {
        return getFormattedElapsedTime(getElapsedTimeMs(), showMilliseconds);
    }
}
