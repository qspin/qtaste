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

import com.qspin.qtaste.config.StaticConfiguration;
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.python.util.PythonInterpreter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for generating a HTML document containing all the documentation of the tests included in a specified test campaign file
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
            for (int i = 0; i < nodelist.getLength(); i++) {
                String directory = nodelist.item(i).getAttributes().getNamedItem("directory").getNodeValue();
                // TODO: Keep only the first level
                System.out.println("Directory is " + directory);
                GenerateTestSuiteDoc.generate(directory);
            }

            try {
                StringWriter output = new StringWriter();
                Properties properties = new Properties();
                properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
                PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
                PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
                interp.setOut(output);
                interp.setErr(output);
                interp.cleanup();
                //java -cp %JYTHON_HOME%\jython.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %JYTHON_HOME%\Lib\pythondoc.py -f -s -Otestscriptdoc_xmlformatter -Dtestsuite_dir=%TEST_SUITE_DIR% !TEST_SCRIPTS!
                String args = "import sys;sys.argv[1:]= ['" + campaignFile +  "']";
                interp.exec(args);
                interp.exec("__name__ = '__main__'");
                interp.exec("execfile(r'" + StaticConfiguration.QTASTE_ROOT + "/tools/TestProcedureDoc/generateTestCampaignDoc.py')");
                interp.cleanup();
                interp = null;

            } catch (Exception e) {
                System.err.println("Exception occurs executing PythonInterpreter:" + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error occurs parsing file " + campaignFile + " " + e.getMessage());
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            displayUsage();
        } else {
            generate(args[0]);
        }
    }
}
