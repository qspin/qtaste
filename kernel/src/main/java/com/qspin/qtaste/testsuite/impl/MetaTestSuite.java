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
 * MetaTestSuite.java
 *
 * Created on 31 octobre 2008
 */
package com.qspin.qtaste.testsuite.impl;

import java.util.ArrayList;
import java.util.List;

import com.qspin.qtaste.kernel.campaign.TestSuiteParams;
import com.qspin.qtaste.testsuite.TestScript;
import com.qspin.qtaste.testsuite.TestSuite;

/**
 * A MetaTestSuite is a TestSuite containing several test suites.
 */
public class MetaTestSuite extends TestSuite {

    //private static Logger logger = Log4jLoggerFactory.getLogger(MetaTestSuite.class);
    private ArrayList<TestSuite> testSuites = new ArrayList<TestSuite>();

    /**
     * Create a Meta TestSuite.
     * @param name the test suite name
     * @param testSuitesParams the list of test suites parameters
     */
    public MetaTestSuite(String name, List<TestSuiteParams> testSuitesParams) {
        super(name);
        for (TestSuiteParams testSuiteParams: testSuitesParams) {
            DirectoryTestSuite testSuite = new DirectoryTestSuite(testSuiteParams.getDirectory());
            testSuite.selectRows(testSuiteParams.getSelectedDataRows());
            testSuite.setExecutionLoops(testSuiteParams.getCount(), testSuiteParams.loopInHours());
            testSuite.addTestReportListener(this);
            testSuites.add(testSuite);
        }
    }
    
    @Override
    protected void finalize() {
        for (TestSuite testSuite : testSuites) {
            testSuite.removeTestReportListener(this);
        }
    }

    @Override
    public int computeNumberTestsToExecute() {
        if (numberLoops == -1 || loopsInHours) {
            return -1;
        } else {
            int numberTestsToExecute = 0;
            for (TestSuite testSuite : testSuites) {
                int n = testSuite.computeNumberTestsToExecute();
                if (n == -1) {
                    return -1;
                } else {
                    numberTestsToExecute += n;
                }
            }
            numberTestsToExecute *= numberLoops;
            return numberTestsToExecute;
        }
    }
    
    @Override
    public boolean executeOnce(boolean debug) {
    	boolean result = true;
        for (TestSuite testSuite : testSuites) {
            if (!testSuite.execute(debug, false)) {
                if (testSuite.isAbortedByUser()) {
                    setAbortedByUser(true);
                    return false;
                }
                result = false;
            }
        }
        return result;
    }

    public List<TestScript> getTestScripts() {
        List<TestScript> testScripts = new ArrayList<TestScript>();
        for (TestSuite testSuite : testSuites) {
            testScripts.addAll(testSuite.getTestScripts());
        }
        return testScripts;
    }
}
