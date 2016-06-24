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
package com.qspin.qtaste.reporter.testresults.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.reporter.XMLFormatter;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultImpl.StepResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NamesValuesList;

/**
 * XMLReportFormatter is a XML formatter able to generate "test results" reports
 *
 * @author lvboque
 */
public class XMLReportFormatter extends XMLFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(XMLFormatter.class);
    private static final String FILE_NAME_DATE_FORMAT = "%TY.%<tm.%<td-%<THh%<TMm%<TSs";
    private static final String FILE_NAME_FORMAT = "log-" + FILE_NAME_DATE_FORMAT + ".xml";
    private static final String RESULTS_FILE_NAME_FORMAT = "log-results-" + FILE_NAME_DATE_FORMAT + ".xml";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String testSuiteDir;
    private static String outputDir;
    private String fileName;
    private Date date;
    private TestSuite currentTestSuite;
    private static String template;
    private static String rowTemplate;
    private static String rowStepsTemplate;

    static {
        TestEngineConfiguration config = TestEngineConfiguration.getInstance();
        String template_root = config.getString("reporting.xml_template");
        if (!new File(template_root).isAbsolute()) {
            template_root = StaticConfiguration.QTASTE_ROOT + File.separator + template_root;
        }
        template = template_root + File.separator + "report_template.xml";
        rowTemplate = template_root + File.separator + "report_test_data.xml";
        rowStepsTemplate = template_root + File.separator + "report_test_steps.xml";
        outputDir = config.getString("reporting.generated_report_path");
    }

    public XMLReportFormatter(String reportName) throws FileNotFoundException, IOException {
        super(template, rowTemplate, rowStepsTemplate, new File(outputDir), String.format(RESULTS_FILE_NAME_FORMAT, new Date()));
        this.testSuiteDir = reportName;
    }

    public void generateReport() {
        for (TestResult result : TestResultsReportManager.getInstance().getResults()) {
            if (result.getStatus() != TestResult.Status.RUNNING) {
                writeTestResult(result);
            }
        }
    }

    @Override
    public void startReport(Date timeStamp, String name) {
        super.startReport(timeStamp, name);

        if (date == null) {
            date = new Date();
        }
        fileName = outputDir + File.separator;
        fileName += new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(timeStamp) + File.separator;
        fileName += String.format(FILE_NAME_FORMAT, date);
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }

        try {
            generateMainFile(false);
        } catch (IOException e) {
            logger.info(e);
        }

        currentTestSuite = TestEngine.getCurrentTestSuite();
    }

    @Override
    public void stopReport() {
        super.stopReport();

        try {
            generateMainFile(true);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void generateMainFile(boolean testSuiteEnded) throws FileNotFoundException, IOException {
        NamesValuesList<String, String> namesValues = new NamesValuesList<>();
        namesValues.add("###QTaste_KERNEL_VERSION###", kernelVersion);
        namesValues.add("###QTaste_TESTAPI_VERSION###", getTestAPIVersion());
        namesValues.add("###RESULTS_FILE###", reportFileName);
        namesValues.add("###LOG_DATE###", DATE_FORMAT.format(date));
        namesValues.add("###TESTBED###", getTestbedConfigurationName());
        namesValues.add("###TESTBED_CONFIGURATION_FILE_NAME###", getTestbedConfigurationFileName());
        namesValues.add("###TESTBED_CONFIGURATION_FILE_CONTENT###",
              StringEscapeUtils.escapeXml(getTestbedConfigurationFileContent()));
        String testbedControlScriptFileName = getTestbedControlScriptFileName();
        if (testbedControlScriptFileName != null) {
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_NAME###", getTestbedControlScriptFileName());
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_CONTENT###",
                  StringEscapeUtils.escapeXml(getTestbedControlScriptFileContent()));
        } else {
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_NAME###", "none");
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_CONTENT###", "");
        }
        namesValues.add("###TEST_SUITE###", testSuiteDir);
        if (currentTestSuite != null && currentTestSuite.getStartExecutionDate() != null) {
            namesValues.add("###DATE_START###", DATE_FORMAT.format(currentTestSuite.getStartExecutionDate()));
        } else {
            namesValues.add("###DATE_START###", "");
        }
        if (currentTestSuite != null && currentTestSuite.getStopExecutionDate() != null) {
            namesValues.add("###DATE_END###", DATE_FORMAT.format(currentTestSuite.getStopExecutionDate()));
        } else {
            namesValues.add("###DATE_END###", "");
        }
        if (currentTestSuite != null) {
            namesValues.add("###TESTS_TO_EXECUTE###", String.valueOf(currentTestSuite.getNbTestsToExecute()));
        } else {
            namesValues.add("###TESTS_TO_EXECUTE###", "");
        }
        if (currentTestSuite != null && testSuiteEnded) {
            namesValues.add("###TESTS_EXECUTED###", String.valueOf(currentTestSuite.getNbTestsExecuted()));
            namesValues.add("###TESTS_PASSED###", String.valueOf(currentTestSuite.getNbTestsPassed()));
            namesValues.add("###TESTS_FAILED###", String.valueOf(currentTestSuite.getNbTestsFailed()));
            namesValues.add("###TESTS_NOT_AVAILABLE###", String.valueOf(currentTestSuite.getNbTestsNotAvailable()));
            namesValues.add("###TESTS_RETRIES###", String.valueOf(currentTestSuite.getNbTestsRetries()));
        } else {
            namesValues.add("###TESTS_EXECUTED###", "");
            namesValues.add("###TESTS_PASSED###", "");
            namesValues.add("###TESTS_FAILED###", "");
            namesValues.add("###TESTS_NOT_AVAILABLE###", "");
            namesValues.add("###TESTS_RETRIES###", "");
        }

        output = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        substituteAndWriteFile(templateContent, namesValues);
        output.close();
    }

    private void writeTestResult(TestResult tr) {
        try {
            NamesValuesList<String, String> namesValues = new NamesValuesList<>();
            namesValues.add("###TEST_CASE###", tr.getId());
            String testScriptVersion = tr.getTestScriptVersion();
            if (testScriptVersion != null) {
                namesValues.add("###TEST_SCRIPT_VERSION###", testScriptVersion);
            } else {
                namesValues.add("###TEST_SCRIPT_VERSION###", "undefined");
            }
            namesValues.add("###TEST_DATA_ROW###", tr.getTestData() != null ? "" + tr.getTestData().getRowId() : "");
            namesValues.add("###TEST_COMMENT###", StringEscapeUtils.escapeXml(tr.getComment()));

            String failedReason = tr.getExtraResultDetails();
            if (tr.getStackTrace() != null && tr.getStackTrace().length() > 0) {
                failedReason += "\n\nStack trace:" + tr.getStackTrace();
            }
            namesValues.add("###RESULT_DESCRIPTION###", StringEscapeUtils.escapeXml(failedReason));
            namesValues.add("###START_DATE###", "" + DATE_FORMAT.format(tr.getStartDate()));
            namesValues.add("###END_DATE###", "" + DATE_FORMAT.format(tr.getEndDate()));
            namesValues.add("###ELAPSED_TIME###", "" + tr.getElapsedTimeMs());

            switch (tr.getStatus()) {
                case NOT_EXECUTED:
                    namesValues.add("###RESULT###", "not executed");
                    break;
                case RUNNING:
                    namesValues.add("###RESULT###", "running");
                    break;
                case NOT_AVAILABLE:
                    namesValues.add("###RESULT###", "not available");
                    break;
                case SUCCESS:
                    namesValues.add("###RESULT###", "pass");
                    break;
                case FAIL:
                    namesValues.add("###RESULT###", "fail");
                    break;
            }

            // add steps details
            String stepsContent = "";
            // Display steps.
            for (StepResult stepResult : tr.getStepResults()) {
                NamesValuesList<String, String> stepsNamesValues = new NamesValuesList<>();
                stepsNamesValues.add("###STEP_ID###", stepResult.getStepId());
                stepsNamesValues.add("###STEP_NAME###", stepResult.getFunctionName());
                stepsNamesValues.add("###STEP_DESCRIPTION###", stepResult.getStepDescription());
                stepsNamesValues.add("###STEP_EXPECTED_RESULT###", stepResult.getExpectedResult());
                stepsNamesValues.add("###STEP_STATUS###", stepResult.getStatus().toString());
                stepsNamesValues.add("###STEP_TIME###", String.valueOf(Math.round(stepResult.getElpasedTime())));
                stepsContent += getSubstitutedTemplateContent(rowStepsTemplateContent, stepsNamesValues);
            }
            namesValues.add("###STEPS###", stepsContent); // default 
            substituteAndWriteFile(rowTemplateContent, namesValues);
            output.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
