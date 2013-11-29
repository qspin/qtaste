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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.reporter.ReportFormatter;
import com.qspin.qtaste.reporter.ReportManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * A TestResultsReportManager manager is responsible to maintain the results of a test results report
 * @author lvboque
 */
public class TestResultsReportManager extends ReportManager {
   

    private static Logger logger = Log4jLoggerFactory.getLogger(TestResultsReportManager.class);
    private static TestResultsReportManager instance = null;
    private ArrayList<TestResult> results;

    private TestResultsReportManager() {
        super();
        results = new ArrayList<TestResult>();

        TestEngineConfiguration config = TestEngineConfiguration.getInstance();
        String output = config.getString("reporting.generated_report_path");

        // create the directory if not exists
        File outputDir = new File(output);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    /**
     * Get an instance of the TestResultsReportManager. 
     * @return The TestResultsReportManager.
     */
    synchronized public static TestResultsReportManager getInstance() {
        if (instance == null) {
            instance = new TestResultsReportManager();
        }
        return instance;
    }

    @Override
    public void startReport(Date timeStamp, String name) {
        results.clear();
        initFormatters(name);
        super.startReport(timeStamp, name);
    }

    private void initFormatters(String reportName) {
        formatters.clear();
        TestEngineConfiguration config = TestEngineConfiguration.getInstance();

        int reportersCount = config.getMaxIndex("reporting.reporters.format") + 1;
        for (int reporterIndex = 0; reporterIndex < reportersCount; reporterIndex++) {
            String reportFormat = config.getString("reporting.reporters.format(" + reporterIndex + ")");
            try {

                Class<?> formatterClass = Class.forName("com.qspin.qtaste.reporter.testresults." + reportFormat.toLowerCase() + "." + reportFormat.toUpperCase() + "ReportFormatter");

                Constructor<?> formatterConstructor = formatterClass.getConstructor(reportName.getClass());                
                formatters.add((ReportFormatter) formatterConstructor.newInstance(reportName));
            } catch (Exception e) {
                logger.fatal("Exception initializing the report format: " + reportFormat, e);
            }
        }

    }

    public void putEntry(TestResult tr) {
        results.add(tr);
        for (ReportFormatter formatter : formatters) {
            formatter.refresh();
        }
    }

    public String getReportFileName(String format) {
        try {
            for (ReportFormatter formatter : formatters) {

                if (formatter.getClass().getName().endsWith("." + format.toUpperCase() + "ReportFormatter")) {
                    return formatter.getReportFile().getAbsoluteFile().getCanonicalPath();
                }

            }
        } catch (IOException e) {
            logger.error("Error in getReportFileFormat", e);
        }
        return null;
    }

    public ArrayList<TestResult> getResults() {
        return results;
    }
}
