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
package com.qspin.qtaste.reporter.testresults.html;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.reporter.HTMLFormatter;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultImpl.StepResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.TestRequirement;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NamesValuesList;

/**
 * HTMLReportFormatter is a HTML formatter able to generate "test results" reports
 *
 * @author lvboque
 */
public class HTMLReportFormatter extends HTMLFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(HTMLFormatter.class);
    private static final String NE_IMAGE = "ne.png";
    private static final String NA_IMAGE = "na.png";
    private static final String RUN_IMAGE = "run.png";
    private static final String OK_IMAGE = "ok.png";
    private static final String KO_IMAGE = "ko.png";
    private static final String NE_TEXT = "Not executed";
    private static final String NA_TEXT = "Test in error";
    private static final String RUN_TEXT = "Running";
    private static final String OK_TEXT = "Passed";
    private static final String KO_TEXT = "Failed";
    private String testSummaryFileName;
    private Date generationDate;
    private static final String FILE_NAME_FORMAT = "index-%TY.%<tm.%<td-%<THh%<TMm%<TSs.html";
    private static final String TEST_SUMMARY_FILE_NAME_FORMAT = "summary-%TY.%<tm.%<td-%<THh%<TMm%<TSs.png";
    private static final String INDEX_FILE_NAME = "index.html";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String testSuiteName;
    private static String outputDir;
    private TestSuite currentTestSuite;
    private boolean generateDataColumn, generateStepsRows;
    private boolean reportStopStartSUT, reportReStartSUT;

    private static TestEngineConfiguration config = TestEngineConfiguration.getInstance();
    private static HashMap<String, String> templates = new HashMap<String, String>();

    static {
        try {
            outputDir = config.getString("reporting.generated_report_path");
            String template_root = config.getString("reporting.html_template");
            if (!new File(template_root).isAbsolute()) {
                template_root = StaticConfiguration.QTASTE_ROOT + File.separator + template_root;
            }

            templates.put("start", template_root + File.separator + "report_start_template.html");
            templates.put("end", template_root + File.separator + "report_end_template.html");
            templates.put("testScript", template_root + File.separator + "report_testscript_template.html");
            templates.put("testStartStopScript", template_root + File.separator + "report_testscript_startstop_template.html");
            templates.put("testScriptRowResult", template_root + File.separator + "report_testscript_row_result.html");
            templates.put("dataColumn", template_root + File.separator + "report_test_data_column.html");
            templates.put("requirementColumn", template_root + File.separator + "report_test_requirement_column.html");
            templates.put("rowSteps", template_root + File.separator + "report_test_steps.html");
            templates.put("stepsHeader", template_root + File.separator + "report_test_step_header.html");
            templates.put("testData", template_root + File.separator + "report_test_data_header.html");
            templates.put("testRequirement", template_root + File.separator + "report_test_requirement_header.html");
            templates.put("executiveSummary", template_root + File.separator + "executive_summary_line.html");

        } catch (Exception e) {
            logger.fatal("Exception initialising the HTML report:" + e);
        }
    }

    private static void copyImages(File targetDirectory) {
        try {
            String template_root = config.getString("reporting.html_template");
            if (!new File(template_root).isAbsolute()) {
                template_root = StaticConfiguration.QTASTE_ROOT + File.separator + template_root;
            }

            // copy images to report directory
            String[] imagesExtensions = {"gif", "jpg", "png"};
            FileUtilities.copyFiles(template_root, targetDirectory.getAbsolutePath(), new FileMask(imagesExtensions));
        } catch (IOException e) {
            logger.fatal("Exception initialising the HTML report:" + e);
        }
    }

    public HTMLReportFormatter(String reportName) throws FileNotFoundException, IOException {
        super(HTMLReportFormatter.templates, new File(outputDir), String.format(FILE_NAME_FORMAT, new Date()));

        this.testSuiteName = reportName;

        this.testSummaryFileName = String.format(TEST_SUMMARY_FILE_NAME_FORMAT, new Date());

        this.generateDataColumn = config.getBoolean("reporting.html_settings.generate_test_data");
        this.generateStepsRows = config.getBoolean("reporting.html_settings.generate_steps_rows");
        if (!this.generateStepsRows) {
            // remove the hyperlink
            String content = this.templateContents.get("testResult");
            content = this.templateContents.get("testScriptRowResult").replace(
                  "<a href=\"javascript:showHide('###ROW_ID###')\">###TEST_ID###</a>", "###TEST_ID###");
            this.templateContents.remove("testScriptRowResult");
            this.templateContents.put("testScriptRowResult", content);
        }
        this.reportStopStartSUT = config.getBoolean("reporting.html_settings.report_stop_start_sut");
        this.reportReStartSUT = config.getBoolean("reporting.html_settings.report_restart_sut");
    }

    public void generateHeader() {
        NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();

        namesValues.add("###QTaste_KERNEL_VERSION###", kernelVersion);
        namesValues.add("###QTaste_TESTAPI_VERSION###", getTestAPIVersion());

        namesValues.add("###DATE_OF_REPORT###", DATE_FORMAT.format(generationDate));
        if (currentTestSuite != null && currentTestSuite.getStartExecutionDate() != null) {
            namesValues.add("###DATE_START###", DATE_FORMAT.format(currentTestSuite.getStartExecutionDate()));
        } else {
            namesValues.add("###DATE_START###", "&nbsp;");
        }

        if (currentTestSuite != null && currentTestSuite.getStopExecutionDate() != null) {
            namesValues.add("###DATE_END###", DATE_FORMAT.format(currentTestSuite.getStopExecutionDate()));
        } else {
            namesValues.add("###DATE_END###", "&nbsp;");
        }

        if (currentTestSuite != null) {
            String nbTestsToExecuteStr =
                  currentTestSuite.getNbTestsToExecute() != -1 ? "" + currentTestSuite.getNbTestsToExecute() : "-";
            namesValues.add("###TESTS_EXECUTED###", currentTestSuite.getNbTestsExecuted() + "/" + nbTestsToExecuteStr);
            namesValues.add("###TESTS_PASSED###", currentTestSuite.getNbTestsPassed() + "/" + nbTestsToExecuteStr);
            namesValues.add("###TESTS_FAILED###", currentTestSuite.getNbTestsFailed() + "/" + nbTestsToExecuteStr);
            namesValues.add("###TESTS_NOT_AVAILABLE###", currentTestSuite.getNbTestsNotAvailable() + "/" + nbTestsToExecuteStr);
            namesValues.add("###TESTS_RETRIES###", currentTestSuite.getNbTestsRetries() + "/" + nbTestsToExecuteStr);
        } else {
            namesValues.add("###TESTS_EXECUTED###", "&nbsp;");
            namesValues.add("###TESTS_PASSED###", "&nbsp;");
            namesValues.add("###TESTS_FAILED###", "&nbsp;");
            namesValues.add("###TESTS_NOT_AVAILABLE###", "&nbsp;");
            namesValues.add("###TESTS_RETRIES###", "&nbsp;");
        }

        namesValues.add("###TESTBED###", getTestbedConfigurationName());
        namesValues.add("###TEST_SUITE###", testSuiteName);
        namesValues.add("###SUT_VERSION###", TestBedConfiguration.getSUTVersion());
        namesValues.add("###EXECUTIVE_SUMMARY###", generateSummaryReport());
        substituteAndWriteFile(templateContents.get("start"), namesValues);
    }

    private String generateSummaryReport() {
        StringBuffer content = new StringBuffer();
        String templateContent = this.templateContents.get("executiveSummary");

        for (TestResult tr : TestResultsReportManager.getInstance().getResults()) {
            try {
                String testcaseName = tr.getName();
                if (testcaseName.equals("Start SUT")) {
                    continue;
                }

                if (testcaseName.equals("Restart SUT")) {
                    continue;
                }

                if (testcaseName.equals("Stop SUT")) {
                    continue;
                }

                // If no Test Data (Start/Restart/...), no summary line
                if (tr.getTestData() == null) {
                    continue;
                }

                NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
                namesValues.add("###TEST_SCRIPT###", tr.getName());
                namesValues.add("###TEST_SCRIPT_SECTION_ID###", "#" + tr.getName() + "-" + (tr.getCurrentRowIndex() + 1));

                namesValues.add("###TESTDATA_ROW_ID###", "" + tr.getTestData().getRowId());

                switch (tr.getStatus()) {
                    case NOT_EXECUTED:
                        namesValues.add("###RESULT_PICTURE###", NE_IMAGE);
                        namesValues.add("###RESULT_TEXT###", NE_TEXT);
                        break;

                    case RUNNING:
                        namesValues.add("###RESULT_PICTURE###", RUN_IMAGE);
                        namesValues.add("###RESULT_TEXT###", RUN_TEXT);
                        break;

                    case NOT_AVAILABLE:
                        namesValues.add("###RESULT_PICTURE###", NA_IMAGE);
                        namesValues.add("###RESULT_TEXT###", NA_TEXT);
                        break;

                    case SUCCESS:
                        namesValues.add("###RESULT_PICTURE###", OK_IMAGE);
                        namesValues.add("###RESULT_TEXT###", OK_TEXT);
                        break;

                    case FAIL:
                        namesValues.add("###RESULT_PICTURE###", KO_IMAGE);
                        namesValues.add("###RESULT_TEXT###", KO_TEXT);
                        break;

                }

                content.append(getSubstitutedTemplateContent(templateContent, namesValues));
            } catch (Exception e) {
                logger.fatal("Error occurs while generating summary report", e);
            }

        }
        return content.toString();
    }

    private String generateDataColumn(TestResult tr) throws FileNotFoundException {
        String dataColumnContent = "";
        try {
            TestData data = tr.getTestData();
            Set<Entry<String, String>> entrySet = data.getDataHash().entrySet();
            if (!entrySet.isEmpty()) {
                for (Entry<String, String> entry : entrySet) {
                    NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
                    namesValues.add("###DATA_NAME###", entry.getKey());
                    namesValues.add("###DATA_VALUE###", StringEscapeUtils.escapeHtml(entry.getValue()));
                    dataColumnContent += getSubstitutedTemplateContent(templateContents.get("dataColumn"), namesValues);
                }
            } else {
                dataColumnContent = "None";
            }

        } catch (Exception e) {
            return "None";
        }

        return dataColumnContent;
    }

    private String generateRequirementColumn(TestResult tr) throws FileNotFoundException {
        String requirementColumnContent = "";
        try {
            if (tr.getTestRequirements() == null || tr.getTestRequirements().isEmpty()) {
                return "Not specified";
            }
            for (TestRequirement req : tr.getTestRequirements()) {
                NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
                namesValues.add("###REQ_ID###", req.getId());
                namesValues.add("###REQ_DESC###",
                      req.getDescription() != null ? StringEscapeUtils.escapeHtml(req.getDescription()) : "");
                requirementColumnContent += getSubstitutedTemplateContent(templateContents.get("requirementColumn"), namesValues);
            }

        } catch (Exception e) {
            return "Not specified";
        }

        return requirementColumnContent;
    }

    public void makeBody() {
        int rowId = 0;
        String previousTestSuiteName = "";

        for (TestResult tr : TestResultsReportManager.getInstance().getResults()) {
            try {
                String testcaseName = tr.getName();
                if (testcaseName.equals("Start SUT") && !reportStopStartSUT) {
                    continue;
                }

                if (testcaseName.equals("(Re)start SUT") && !reportReStartSUT) {
                    continue;
                }

                if (testcaseName.equals("Stop SUT") && !reportStopStartSUT) {
                    continue;
                }

                if (!testcaseName.equals(previousTestSuiteName) && !testcaseName.equals("Restart SUT")) {
                    NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
                    namesValues.add("###TEST_SCRIPT###", testcaseName);
                    String testScriptVersion = tr.getTestScriptVersion();
                    if (testScriptVersion == null || testScriptVersion.equalsIgnoreCase("undefined")) {
                        namesValues.add("###TEST_SCRIPT_VERSION###", "");
                    } else {
                        namesValues.add("###TEST_SCRIPT_VERSION###", "<b>Version:</b> " + testScriptVersion);
                    }

                    output.print("</table>");

                    if (generateDataColumn) {
                        namesValues.add("###DATA_COLUMN###", "<td width=\"5%\" align=\"center\">Data</td>");
                    } else {
                        namesValues.add("###DATA_COLUMN###", "");
                    }

                    if (testcaseName.equals("Start SUT") ||
                          testcaseName.equals("(Re)start SUT") ||
                          testcaseName.equals("Stop SUT")) {
                        substituteAndWriteFile(templateContents.get("testStartStopScript"), namesValues);
                    } else {
                        String requirementColumn = generateRequirementColumn(tr);
                        if (!requirementColumn.equalsIgnoreCase("Not specified")) {
                            NamesValuesList<String, String> requirementValue = new NamesValuesList<String, String>();
                            requirementValue.add("###REQUIREMENT_TEMPLATE###", requirementColumn);
                            requirementColumn = "<p><h4>Verified requirement(s)</h4>" + getSubstitutedTemplateContent(
                                  templateContents.get("testRequirement"), requirementValue);
                        } else {
                            requirementColumn = "";
                        }
                        namesValues.add("###REQUIREMENT_TEMPLATE###", requirementColumn);
                        substituteAndWriteFile(templateContents.get("testScript"), namesValues);
                    }
                    previousTestSuiteName = testcaseName;
                }
                // TODO: Convert String into HTML String

                NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
                String testId = tr.getId();
                String testComment = tr.getComment().replace("\n", "<BR>");
                if (testComment.length() > 0) {
                    testId += "<BR> (" + testComment + ")";
                }

                testId = testId.replace('\\', '/');
                namesValues.add("###ROW_ID###", "row_" + rowId);
                namesValues.add("###TEST_SCRIPT_SECTION_ID###", tr.getName() + "-" + (tr.getCurrentRowIndex() + 1));
                if (tr.getTestData() != null) {
                    namesValues.add("###TESTDATA_ROW_ID###", "" + tr.getTestData().getRowId());
                    namesValues.add("###TEST_ID###",
                          tr.getTestData().getRowId() + " - " + StringEscapeUtils.escapeHtml(testComment));

                } else {
                    namesValues.add("###TESTDATA_ROW_ID###", "" + "");
                    namesValues.add("###TEST_ID###", tr.getName());
                }

                String failedReason = tr.getExtraResultDetails();
                if (tr.getStackTrace() != null && tr.getStackTrace().length() > 0) {
                    failedReason += "\n\nScript stack trace:\n" + tr.getStackTrace();
                }

                if (tr.getStatus() == TestResult.Status.FAIL || tr.getStatus() == TestResult.Status.NOT_AVAILABLE) {
                    failedReason += "\n\nDate: " + DATE_FORMAT.format(tr.getEndDate());
                }

                namesValues.add("###SHORT_DESCRIPTION###", StringEscapeUtils.escapeHtml(failedReason).replace("\n", "<BR>"));
                namesValues.add("###ELAPSED_TIME###", "" + tr.getFormattedElapsedTime(true));

                switch (tr.getStatus()) {
                    case NOT_EXECUTED:
                        namesValues.add("###RESULT_PICTURE###", NE_IMAGE);
                        namesValues.add("###RESULT_TEXT###", NE_TEXT);
                        namesValues.add("###TC-STATUS###", "tc-ok"); // default
                        break;

                    case RUNNING:
                        namesValues.add("###RESULT_PICTURE###", RUN_IMAGE);
                        namesValues.add("###RESULT_TEXT###", RUN_TEXT);
                        namesValues.add("###TC-STATUS###", "tc-ok"); // default
                        break;

                    case NOT_AVAILABLE:
                        namesValues.add("###RESULT_PICTURE###", NA_IMAGE);
                        namesValues.add("###RESULT_TEXT###", NA_TEXT);
                        namesValues.add("###TC-STATUS###", "tc-ok"); // default
                        break;

                    case SUCCESS:
                        if (tr.getTestData() != null) // if null -> stop/start/restart SUT
                        {
                            namesValues.add("###TC-STATUS###", "tc-ok"); // default
                        } else {
                            namesValues.add("###TC-STATUS###", "tc-SUT"); // default
                        }

                        namesValues.add("###RESULT_PICTURE###", OK_IMAGE);
                        namesValues.add("###RESULT_TEXT###", OK_TEXT);
                        break;

                    case FAIL:
                        namesValues.add("###RESULT_PICTURE###", KO_IMAGE);
                        namesValues.add("###RESULT_TEXT###", KO_TEXT);
                        namesValues.add("###TC-STATUS###", "tc-nok");
                        break;
                }

                String dataColumn = generateDataColumn(tr);
                NamesValuesList<String, String> dataValues = new NamesValuesList<String, String>();
                dataValues.add("###DATA_TEMPLATE###", dataColumn);
                dataColumn = getSubstitutedTemplateContent(templateContents.get("testData"), dataValues);

                namesValues.add("###DATA_CONTENT###", dataColumn);

                // add step entries if any
                String stepsContent = "";

                Collection<StepResult> steps = tr.getStepResults();
                if (generateStepsRows && !steps.isEmpty()) {
                    // Display (sorted) steps.
                    for (StepResult step : steps) {
                        NamesValuesList<String, String> stepsNamesValues = new NamesValuesList<String, String>();
                        stepsNamesValues.add("###STEP_ID###", step.getStepId());
                        stepsNamesValues.add("###STEP_NAME###", step.getFunctionName());
                        final String stepDescription = step.getStepDescription();
                        final String expectedResult = step.getExpectedResult();
                        stepsNamesValues.add("###STEP_DESCRIPTION###", stepDescription.isEmpty() ? "&nbsp;" : stepDescription);
                        stepsNamesValues.add("###STEP_EXPECTED_RESULT###", expectedResult.isEmpty() ? "&nbsp;" : expectedResult);
                        switch (step.getStatus()) {
                            case NOT_EXECUTED:
                                stepsNamesValues.add("###STEP_RESULT_PICTURE###", NA_IMAGE);
                                stepsNamesValues.add("###STEP_RESULT_TEXT###", NA_TEXT);
                                stepsNamesValues.add("###STEP_STATUS###", "tc-ok"); // default
                                break;

                            case RUNNING:
                                if (tr.getStatus() == TestResult.Status.FAIL) {
                                    stepsNamesValues.add("###STEP_RESULT_PICTURE###", KO_IMAGE);
                                    stepsNamesValues.add("###STEP_RESULT_TEXT###", KO_TEXT);
                                    stepsNamesValues.add("###STEP_STATUS###", "tc-nok"); // default
                                } else if (tr.getStatus() == TestResult.Status.NOT_AVAILABLE) {
                                    stepsNamesValues.add("###STEP_RESULT_PICTURE###", KO_IMAGE);
                                    stepsNamesValues.add("###STEP_RESULT_TEXT###", KO_TEXT);
                                    stepsNamesValues.add("###STEP_STATUS###", "tc-nok"); // default
                                } else {
                                    stepsNamesValues.add("###STEP_RESULT_PICTURE###", RUN_IMAGE);
                                    stepsNamesValues.add("###STEP_RESULT_TEXT###", RUN_TEXT);
                                    stepsNamesValues.add("###STEP_STATUS###", "tc-ok"); // default
                                }

                                break;
                            case NOT_AVAILABLE:
                                stepsNamesValues.add("###STEP_RESULT_PICTURE###", NA_IMAGE);
                                stepsNamesValues.add("###STEP_RESULT_TEXT###", NA_TEXT);
                                stepsNamesValues.add("###STEP_STATUS###", "tc-ok"); // default
                                break;

                            case SUCCESS:
                                stepsNamesValues.add("###STEP_RESULT_PICTURE###", OK_IMAGE);
                                stepsNamesValues.add("###STEP_RESULT_TEXT###", OK_TEXT);
                                stepsNamesValues.add("###STEP_STATUS###", "tc-ok"); // default
                                break;

                            case FAIL:
                                stepsNamesValues.add("###STEP_RESULT_PICTURE###", KO_IMAGE);
                                stepsNamesValues.add("###STEP_RESULT_TEXT###", KO_TEXT);
                                stepsNamesValues.add("###STEP_STATUS###", "tc-nok"); // default
                                break;

                        }

                        stepsNamesValues.add("###STEP_TIME###", String.valueOf(Math.round(step.getElpasedTime())));
                        stepsContent += getSubstitutedTemplateContent(templateContents.get("rowSteps"), stepsNamesValues);
                    }

                    namesValues.add("###STEPS###", stepsContent); // default
                    namesValues.add("###STEP_CONTENT###",
                          getSubstitutedTemplateContent(templateContents.get("stepsHeader"), namesValues)); // default
                } else {
                    namesValues.add("###STEP_CONTENT###", ""); // do not generate the steps content
                }

                substituteAndWriteFile(templateContents.get("testScriptRowResult"), namesValues);
            } catch (Exception e) {
                logger.error("Exception generating body of document:" + e.getMessage(), e);
            }

            rowId++;
        }

    }

    @Override
    public void refresh() {
        boolean firstTime = (generationDate == null);
        generationDate = new Date();
        super.refresh();

        try {
            if (firstTime) {
                currentTestSuite = TestEngine.getCurrentTestSuite();
                // generate index file
                String indexFileName = outputDir + File.separator + INDEX_FILE_NAME;
                String pathToReport = new File(outputDir).toURI().relativize(reportFile.toURI()).getPath();
                PrintWriter index = new PrintWriter(new BufferedWriter(new FileWriter(indexFileName)));
                index.println(
                      "<html>" + "<head>" + "<meta http-equiv=\"refresh\" content=\"0; url=" + pathToReport + "\"/>" + "</head>"
                            + "<body>" + "<a href=\"" + pathToReport + "\">" + "Redirection" + "</a>" + "</body>" + "</html>");
                index.close();
                copyImages(reportFile.getParentFile());
            }

        } catch (IOException e) {
            logger.error("Cannot refresh the HTML report", e);
        }

    }

    public void generateFooter() {
        NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
        namesValues.add("###SUMMARY_PICTURE###", testSummaryFileName);
        namesValues.add("###TESTBED_CONFIGURATION_FILE_NAME###", StringEscapeUtils.escapeHtml(getTestbedConfigurationFileName()));
        namesValues.add("###TESTBED_CONFIGURATION_FILE_CONTENT###",
              StringEscapeUtils.escapeHtml(getTestbedConfigurationFileContent()));
        String testbedControlScriptFileName = getTestbedControlScriptFileName();
        if (testbedControlScriptFileName != null) {
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_NAME###",
                  StringEscapeUtils.escapeHtml(getTestbedControlScriptFileName()));
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_CONTENT###",
                  StringEscapeUtils.escapeHtml(getTestbedControlScriptFileContent()));
        } else {
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_NAME###", "none");
            namesValues.add("###TESTBED_CONTROL_SCRIPT_FILE_CONTENT###", "");
        }

        substituteAndWriteFile(templateContents.get("end"), namesValues);
    }

    @Override
    public void stopReport() {
        super.stopReport();
        generatePieChart();

    }

    private void generatePieChart() {
        if (currentTestSuite == null) {
            return;
        }

        File testSummaryFile = new File(reportFile.getParentFile(), testSummaryFileName);
        File tempTestSummaryFile = new File(testSummaryFile.getPath() + ".tmp");

        final DefaultPieDataset pieDataSet = new DefaultPieDataset();

        pieDataSet.setValue("Passed", new Integer(currentTestSuite.getNbTestsPassed()));
        pieDataSet.setValue("Failed", new Integer(currentTestSuite.getNbTestsFailed()));
        pieDataSet.setValue("Tests in error", new Integer(currentTestSuite.getNbTestsNotAvailable()));
        pieDataSet.setValue("Not executed",
              new Integer(currentTestSuite.getNbTestsToExecute() - currentTestSuite.getNbTestsExecuted()));
        JFreeChart chart = null;
        final boolean drilldown = true;

        // create the chart...
        if (drilldown) {
            final PiePlot plot = new PiePlot(pieDataSet);

            Color[] colors = {new Color(100, 230, 40), new Color(210, 35, 35), new Color(230, 210, 40), new Color(100, 90, 40)};
            PieRenderer renderer = new PieRenderer(colors);
            renderer.setColor(plot, (DefaultPieDataset) pieDataSet);

            plot.setURLGenerator(new StandardPieURLGenerator("pie_chart_detail.jsp"));
            plot.setLabelGenerator(new TestSectiontLabelPieGenerator());
            chart = new JFreeChart("Test summary", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        } else {
            chart = ChartFactory.createPieChart("Test summary", // chart title
                  pieDataSet, // data
                  true, // include legend
                  true, false);
        }

        chart.setBackgroundPaint(java.awt.Color.white);

        try {
            final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            ChartUtilities.saveChartAsPNG(tempTestSummaryFile, chart, 600, 400, info);
        } catch (IOException e) {
            logger.error("Problem saving png chart", e);
        }

        testSummaryFile.delete();
        if (!tempTestSummaryFile.renameTo(testSummaryFile)) {
            logger.error("Couldn't rename test summary file " + tempTestSummaryFile + " into " + testSummaryFile);
        }
    }

    /**
     * A simple renderer for setting custom colors
     * for a pie chart.
     */
    public static class PieRenderer {

        private Color[] color;

        public PieRenderer(Color[] color) {
            this.color = color;
        }

        @SuppressWarnings("unchecked")
        public void setColor(PiePlot plot, DefaultPieDataset dataset) {
            List<Comparable> keys = dataset.getKeys();
            int aInt;

            for (int i = 0; i < keys.size(); i++) {
                aInt = i % color.length;
                plot.setSectionPaint(keys.get(i), color[aInt]);
            }
        }
    }

    public class TestSectiontLabelPieGenerator implements PieSectionLabelGenerator {

        /**
         * Generates a customized label for a pie section of pie chart depicting
         * test results.
         *
         * @param aDataset Dataset destined for pie chart.
         * @param aKey The identifying key for each section of the pie chart.
         * @return Customized label for section of pie chart identified by aKey.
         */
        public String generateSectionLabel(final PieDataset aDataset, @SuppressWarnings("rawtypes") final Comparable aKey) {
            String labelResult = null;
            if (aDataset != null) {
                TestSuite currentTestSuite = TestEngine.getCurrentTestSuite();
                if (currentTestSuite != null) {
                    if (aDataset.getValue(aKey).intValue() == 0) {
                        return null;
                    }
                    double percentage = aDataset.getValue(aKey).intValue() * 100 / currentTestSuite.getNbTestsToExecute();
                    labelResult = percentage + "%";
                } else {
                    return null;
                }
            }

            return labelResult;
        }

        @SuppressWarnings("unchecked")
        public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
            return null;
        }
    }
}
