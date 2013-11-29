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

package com.qspin.qtaste.kernel.campaign;

import java.util.Date;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.reporter.campaign.CampaignReportManager;
import com.qspin.qtaste.reporter.campaign.CampaignResult;
import com.qspin.qtaste.reporter.campaign.CampaignResult.Status;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestReportListener;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.testsuite.impl.MetaTestSuite;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * The CampaignManager is responsible for the execution of a campaign and the generation of a campaign report
 * @author lvboque
 */
public class CampaignManager implements TestReportListener {

    private static CampaignManager instance = null;
    private static Logger logger = Log4jLoggerFactory.getLogger(CampaignManager.class);
    private Campaign currentCampaign;
    private Date campaignStartTimeStamp;
    private String currentTestBed;
    private TestSuite currentTestSuite;
    private boolean campaignResult;

    private CampaignManager() {
    }

    /**
     * Get an instance of the CampaignManager.
     * @return The CampaignManager.
     */
    synchronized public static CampaignManager getInstance() {
        if (instance == null) {
            instance = new CampaignManager();
        }
        return instance;
    }

    /**
     * Read the xml campaign file 
     * @param fileName the xml campaign file
     * @return an object describing the campaign
     * @throws java.lang.Exception
     */
    public Campaign readFile(String fileName) throws Exception {
        Campaign result = new Campaign();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(fileName);
        doc.getDocumentElement().normalize();

        Element el = doc.getDocumentElement();
        if (!el.getNodeName().equals("campaign")) {
            throw new Exception(fileName + " is not a valid xml campain file");
        }

        String campaignName = el.getAttributeNode("name").getValue();
        result.name = campaignName;

        NodeList nodeLst = doc.getElementsByTagName("run");
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node node = nodeLst.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                CampaignRun run = new CampaignRun();
                run.testbed = element.getAttribute("testbed");
                result.runs.add(run);
                NodeList nodeList = element.getElementsByTagName("testsuite");

                for (int t = 0; t < nodeList.getLength(); t++) {
                    TestSuiteParams params = new TestSuiteParams();

                    run.testsuites.add(params);
                    params.setDirectory(nodeList.item(t).getAttributes().getNamedItem("directory").getNodeValue());

                    NodeList childList = nodeList.item(t).getChildNodes();
                    for (int c = 0; c < childList.getLength(); c++) {
                        Node childNode = childList.item(c);
                        if (childNode.getNodeName().equals("testdata")) {
                            String selectorStr = childNode.getAttributes().getNamedItem("selector").getNodeValue();
                            String[] selectedRowsStr = selectorStr.split(",");
                            TreeSet<Integer> selectedRows = new TreeSet<Integer>();
                            for (int i = 0; i < selectedRowsStr.length; i++) {
                                selectedRows.add(Integer.parseInt(selectedRowsStr[i]));
                            }
                            params.setDataRows(selectedRows);
                        }
                        if (childList.item(c).getNodeName().equals("loopInHours")) {
                            params.setLoopInHours(true);
                        }

                        if (childList.item(c).getNodeName().equals("count")) {
                            try {
                                params.setCount(Integer.parseInt(childList.item(c).getTextContent()));
                            } catch (NumberFormatException e) {
                                logger.error("count field in " + fileName + " file should be numeric");
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return the currently selected campaign
     * @return the currently selected campaign
     */
    public Campaign getCurrentCampaign() {
        return currentCampaign;
    }

    /**
     * Return the current campaign start timestamp
     * @return the current campaign start timestamp
     */
    public Date getTimeStampCampaign() {
        return campaignStartTimeStamp;
    }

    /**
     * Executes a campaign
     * @param campaign the campaign to execute.
     */
    public boolean execute(Campaign campaign) {
    	campaignResult = true;
        currentCampaign = campaign;
        campaignStartTimeStamp = new Date();
        try
        {
	        createReport();
	
	        for (CampaignRun run : currentCampaign.getRuns()) {
	            currentTestBed = run.getTestbed();
	            String testSuiteName = currentCampaign.getName() + " - " + currentTestBed.substring(0, currentTestBed.lastIndexOf('.'));
	            TestBedConfiguration.setConfigFile(StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/" + currentTestBed);
	            currentTestSuite = new MetaTestSuite(testSuiteName, run.getTestsuites());
	            currentTestSuite.addTestReportListener(this);
	            campaignResult &= TestEngine.execute(currentTestSuite); // NOSONAR - Potentially dangerous use of non-short-circuit logic
	            boolean abortedByUser = currentTestSuite.isAbortedByUser();
	            currentTestSuite.removeTestReportListener(this);
	            currentTestSuite = null;
	            if (abortedByUser) {
	                break;
	            }
	        }
	        CampaignReportManager.getInstance().stopReport();
        }
        finally
        {
        	campaignStartTimeStamp = null;
        	currentCampaign = null;
        }
        return campaignResult;
    }

    private void updateReport() {
        for (CampaignResult result : CampaignReportManager.getInstance().getResults()) {
            if (!result.getTestBed().equals(currentTestBed)) {
                continue;
            }
            result.setDetailedURL(TestResultsReportManager.getInstance().getReportFileName("HTML"));
            result.setTestSuiteResult(currentTestSuite);
        }
        CampaignReportManager.getInstance().refresh();
    }

    /**
     * Create a empty report. All campaign run will be "Not Executed"
     */
    private void createReport() {
        CampaignReportManager.getInstance().startReport(campaignStartTimeStamp, currentCampaign.getName());
        for (CampaignRun run : currentCampaign.getRuns()) {
            CampaignResult result = new CampaignResult(run.getTestbed());
            result.setStatus(Status.NOT_EXECUTED);
            CampaignReportManager.getInstance().putEntry(result);
        }

        CampaignReportManager.getInstance().refresh();
    }

    public void reportTestSuiteStarted() {
        updateReport();
    }

    public void reportTestSuiteStopped() {
        updateReport();        
    }

    public void reportTestResult(TestResult.Status status) {
        updateReport();
    }

    public void reportTestRetry() {
        updateReport();
    }
}
