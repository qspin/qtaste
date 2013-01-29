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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.reporter.HTMLFormatter;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NamesValuesList;

/**
 * CampaignHTMLFormatter is a HTML formatter able to generate "Campaign" reports
 * @author lvboque
 */
public class CampaignHTMLFormatter extends HTMLFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(CampaignHTMLFormatter.class);
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
    private static final String FILE_NAME_FORMAT = "campaign-%TY.%<tm.%<td-%<THh%<TMm%<TSs.html";
    private static final String INDEX_FILE_NAME = "campaign.html";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date generationDate;
    private static String outputDir = TestEngineConfiguration.getInstance().getString("reporting.generated_report_path");

    public CampaignHTMLFormatter(HashMap<String, String> templates, String pOutputDir) throws IOException {
        super(templates, new File(outputDir), String.format(FILE_NAME_FORMAT, new Date()));
    }

    public void generateHeader() {
        NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
        namesValues.add("###QTaste_KERNEL_VERSION###", kernelVersion);
        namesValues.add("###QTaste_TESTAPI_VERSION###", testapiVersion);
        namesValues.add("###SUT_VERSION###", TestBedConfiguration.getSUTVersion());
        namesValues.add("###DATE_OF_REPORT###", DATE_FORMAT.format(generationDate));
        namesValues.add("###DATE_START###", startDate != null ? DATE_FORMAT.format(startDate) : "&nbsp;");
        namesValues.add("###DATE_END###", endDate != null ? DATE_FORMAT.format(endDate) : "&nbsp;");
        namesValues.add("###CAMPAIGN_NAME###", CampaignReportManager.getInstance().getReportName());
        substituteAndWriteFile(this.templateContents.get("start"), namesValues);
    }

    public void makeBody() {
        for (CampaignResult cr : CampaignReportManager.getInstance().getResults()) {
            NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
            boolean notRunYet = cr.getStatus() == CampaignResult.Status.NOT_EXECUTED;
            if (notRunYet) {
                namesValues.add("###TESTBED###", cr.getTestBed());
                namesValues.add("###TESTS_EXECUTED###", "&nbsp;");
                namesValues.add("###TESTS_PASSED###", "&nbsp;");
                namesValues.add("###TESTS_FAILED###", "&nbsp;");
                namesValues.add("###TESTS_NOT_AVAILABLE###", "&nbsp;");
                namesValues.add("###TESTS_RETRIES###", "&nbsp;");
                namesValues.add("###ELAPSED_TIME###", "&nbsp;");
            } else {
                String absoluteDetailedURL = cr.getDetailedURL();
                String relativeDetailedURL = reportFile.toURI().relativize(new File(absoluteDetailedURL).toURI()).getPath();
                String nbTestsToExecuteStr = cr.getNbTestsToExecute() != -1 ? "" + cr.getNbTestsToExecute() : "-";
                namesValues.add("###TESTBED###", "<a href=" + relativeDetailedURL + ">" + cr.getTestBed() + "</a>");        
                namesValues.add("###TESTS_EXECUTED###", cr.getNbTestsExecuted() + "/" + nbTestsToExecuteStr);
                namesValues.add("###TESTS_PASSED###", cr.getNbTestsPassed() + "/" + nbTestsToExecuteStr);
                namesValues.add("###TESTS_FAILED###", cr.getNbTestsFailed() + "/" + nbTestsToExecuteStr);
                namesValues.add("###TESTS_NOT_AVAILABLE###", cr.getNbTestsNotAvailable() + "/" + nbTestsToExecuteStr);
                namesValues.add("###TESTS_RETRIES###", cr.getNbTestsRetries() + "/" + nbTestsToExecuteStr);
                namesValues.add("###ELAPSED_TIME###", "" + cr.getFormattedElapsedTime(false));
            }
            switch (cr.getStatus()) {
                case NOT_EXECUTED:
                    namesValues.add("###STATUS###", NE_IMAGE);
                    namesValues.add("###STATUS_TEXT###", NE_TEXT);
                    break;
                case RUNNING:
                    namesValues.add("###STATUS###", RUN_IMAGE);
                    namesValues.add("###STATUS_TEXT###", RUN_TEXT);
                    break;
                case NOT_AVAILABLE:
                    namesValues.add("###STATUS###", NA_IMAGE);
                    namesValues.add("###STATUS_TEXT###", NA_TEXT);
                    break;
                case SUCCESS:
                    namesValues.add("###STATUS###", OK_IMAGE); // default
                    namesValues.add("###STATUS_TEXT###", OK_TEXT);
                    break;
                case FAIL:
                    namesValues.add("###STATUS###", KO_IMAGE);
                    namesValues.add("###STATUS_TEXT###", KO_TEXT);
                    break;
            }
            try {
                substituteAndWriteFile(this.templateContents.get("rowData"), namesValues);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void refresh() {
        boolean firstTime = (generationDate == null);
        generationDate = new Date();
        super.refresh();
        try {
            if (firstTime) {
                // generate index file                
                String indexFileName = outputDir + File.separator + INDEX_FILE_NAME;
                PrintWriter index = new PrintWriter(new BufferedWriter(new FileWriter(indexFileName)));
                index.println("<html><head><meta http-equiv=\"refresh\" content=\"0; url=" + this.reportFile.getCanonicalPath() + "\"/></head><body><a href=\"" + this.reportFile.getName() + "\">Redirection</a></body></html>");
                index.close();
            }
        } catch (IOException e) {
            logger.error("Cannot refresh the HTML report", e);
        }

    }

    public void generateFooter() {
        NamesValuesList<String, String> namesValues = new NamesValuesList<String, String>();
        substituteAndWriteFile(this.templateContents.get("end"), namesValues);
    }
}
