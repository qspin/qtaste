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

import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.log.Log4jServer;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.versioncontrol.VersionControl;

/**
 * CampaignLauncher is the main command-line program for the execution of a campaign
 *
 * @author lvboque
 */
public class CampaignLauncher {

    private static Logger logger = Log4jLoggerFactory.getLogger(CampaignLauncher.class);

    private static void showUsage() {
	System.err.println("Usage: <command> <campaignFileName.xml> [-engine <engineFileName.xml>] [-sutversion <sut_version_identifier>]");
        System.exit(1);
    }

    private static void shutdown() {
        Log4jServer.getInstance().shutdown();
        LogManager.shutdown();
    }

    public static void main(String[] args) throws Exception {
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

        // log version information
        logger.info("QTaste kernel version: " + com.qspin.qtaste.kernel.Version.getInstance().getFullVersion());
        logger.info("QTaste testAPI version: " + VersionControl.getInstance().getTestApiVersion(""));

        // handle config file name and optional -sutversion
        if (args.length < 1 || args.length > 5) {
            showUsage();
        }

        int i = 0;
        String campaignFileName = args[i++];
        while (i < args.length) {
            if (args[i].equals("-engine") && (i + 1 < args.length)) {
                logger.info("Using " + args[i + 1] + " as engine configuration file");
                TestEngineConfiguration.setConfigFile(args[i + 1]);
                    i += 2;
            } else if (args[i].equals("-sutversion") && (i + 1 < args.length)) {
                logger.info("Using " + args[i + 1] + " as SUT version");
                    TestBedConfiguration.setSUTVersion(args[i + 1]);
                    i += 2;
            } else {
                    showUsage();
            }
        }

        // start the log4j server
        Log4jServer.getInstance().start();

        // initialize Python interpreter
        Properties properties = new Properties();
        properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
        properties.setProperty("python.path", StaticConfiguration.JYTHON_LIB);
        PythonInterpreter.initialize(System.getProperties(), properties, new String[] {""});

        CampaignManager campaignManager = CampaignManager.getInstance();
        boolean executionResult;

        try {
            Campaign campaign = campaignManager.readFile(campaignFileName);
            executionResult = campaignManager.execute(campaign);
        } finally {
            shutdown();
        }
        System.exit(executionResult ? 0 : 1);
    }
}
