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

package com.qspin.qtaste.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.PropertyConfigurator;
import org.python.core.PyException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * This class is responsible for generating a HTML document containing all the documentation of the tests included in a specified
 * test campaign file
 *
 * @author lvboque
 */
public class GenerateTestCampaignDoc {

    public static void displayUsage() {
        System.out.println("Usage: generate-TestCampaign-doc <TestCampaignFile>");
        System.exit(1);
    }

    public static void generate(String campaignFile) {
        System.out.println("Generating documentation of test suites included in test campaign " + campaignFile);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(campaignFile);
            NodeList nodelist = doc.getElementsByTagName("testsuite");
            Set<String> testSuites = new HashSet<>();
            Pattern testSuitePattern = Pattern.compile("^(TestSuites/.*?)/.*$");
            for (int i = 0; i < nodelist.getLength(); i++) {
                String directory = nodelist.item(i).getAttributes().getNamedItem("directory").getNodeValue();
                Matcher matcher = testSuitePattern.matcher(directory);
                String testSuite = matcher.matches() ? matcher.group(1) : directory;
                testSuites.add(testSuite);
            }
            for (String testSuite : testSuites) {
                GenerateTestSuiteDoc.generate(testSuite);
            }

            try {
                PythonHelper.execute(StaticConfiguration.QTASTE_ROOT + "/tools/TestProcedureDoc/generateTestCampaignDoc.py",
                      campaignFile);
            } catch (PyException e) {
                System.err.println("Exception occurs executing PythonInterpreter: " + e.value);
            }
        } catch (Exception e) {
            System.out.println("Error occurs generating documentation for test campaign " + campaignFile + ": " + e.getMessage());
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

        if (args.length != 1) {
            displayUsage();
        } else {
            generate(args[0]);
        }
    }
}
