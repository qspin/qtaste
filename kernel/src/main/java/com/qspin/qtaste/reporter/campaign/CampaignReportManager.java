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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.reporter.ReportManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * A CampaignReportManager is responsible to maintain the results of a campaign report
 *
 * @author lvboque
 */
public class CampaignReportManager extends ReportManager {

    private static Logger logger = Log4jLoggerFactory.getLogger(CampaignReportManager.class);
    private static CampaignReportManager instance = null;
    private ArrayList<CampaignResult> results;

    private CampaignReportManager() {
        super();
        results = new ArrayList<CampaignResult>();
        initFormatters();
    }

    private void initFormatters() {
        try {
            formatters.clear();
            TestEngineConfiguration config = TestEngineConfiguration.getInstance();
            String output = config.getString("reporting.generated_report_path");

            // create the directory if not exists
            File outputDir = new File(output);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            HashMap<String, String> templates = new HashMap<String, String>();
            templates.put("start", StaticConfiguration.CONFIG_DIRECTORY + "/reporting/campaign/campaign_start_template.html");
            templates.put("rowData", StaticConfiguration.CONFIG_DIRECTORY + "/reporting/campaign/campaign_row_data.html");
            templates.put("end", StaticConfiguration.CONFIG_DIRECTORY + "/reporting/campaign/campaign_end_template.html");
            formatters.add(new CampaignHTMLFormatter(templates, output));
            formatters.add(new CampaignGUIFormatter());
        } catch (IOException e) {
            logger.error("Cannot add HTMLCampaignReport", e);
        }
    }

    /**
     * Get an instance of the CampaignReportManager.
     *
     * @return The TestResultsReportManager.
     */
    synchronized public static CampaignReportManager getInstance() {
        if (instance == null) {
            instance = new CampaignReportManager();
        }
        return instance;
    }

    @Override
    public void startReport(Date timeStamp, String name) {
        results.clear();
        initFormatters();
        super.startReport(timeStamp, name);
    }

    @Override
    public void stopReport() {
        super.stopReport();
    }

    public void putEntry(CampaignResult result) {
        results.add(result);
    }

    public ArrayList<CampaignResult> getResults() {
        return results;
    }
}