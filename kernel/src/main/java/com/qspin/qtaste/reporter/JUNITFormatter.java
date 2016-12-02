package com.qspin.qtaste.reporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

public abstract class JUNITFormatter extends ReportFormatter {
    private static Logger logger = Log4jLoggerFactory.getLogger(JUNITFormatter.class);
    protected String templateStartContent;
    protected String templateRefreshContent;
    protected String templateEndContent;
    private int numberOfAlreadyPrintedResult = 0;

    public JUNITFormatter(String templateStart, String templateRefresh, String templateEnd, File reportDirectory, String
          reportName)
          throws IOException {
        super(reportDirectory, reportName);
        this.templateStartContent = FileUtilities.readFileContent(templateStart);
        this.templateRefreshContent = FileUtilities.readFileContent(templateRefresh);
        this.templateEndContent = FileUtilities.readFileContent(templateEnd);
    }

    public void refresh() {
        try {
            ArrayList<TestResult> results = TestResultsReportManager.getInstance().getResults();
            int numberOfResults = results.size();
            for (int i = numberOfAlreadyPrintedResult; i < numberOfResults; i++) {
                TestResult result = results.get(i);
                if (result.getStatus() != TestResult.Status.RUNNING) {
                    writeTestResult(result);
                    numberOfAlreadyPrintedResult++;
                }
            }
        } catch (IOException e) {
            logger.error("Cannot refresh the XML report", e);
        }
    }

    public abstract void writeTestResult(TestResult result) throws IOException;
}
