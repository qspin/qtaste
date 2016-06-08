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

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * This class is responsible for generating the documentation of a TestScript
 * @author lvboque
 */
public class GenerateTestScriptDoc {
    public static final String FS = File.separator;

    public static void displayUsage() {
        System.out.println("Usage: generate-TestScript-doc <TestScriptFile>");
        System.exit(1);
    }

    public static void generate(String testScript) {

        File testScriptFile = new File(testScript);
        File testDir = testScriptFile.getParentFile();

        if (!testScriptFile.exists()) {
            System.out.println("File " + testScriptFile + " doesn't exist");
            System.exit(2);
        }

        GenerateTestStepsModulesDoc.generate("TestSuites");
        
        try {
            System.out.println("Generate test script documentation.");
            StringWriter output = new StringWriter();
            Properties properties = new Properties();
            properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
            properties.setProperty("python.path", StaticConfiguration.JYTHON_LIB);
            PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
            PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
            interp.setOut(output);
            interp.setErr(output);
            interp.cleanup();
            String args = "import sys;sys.argv[1:]= ['-V', '-f', '-s', '-Otestscriptdoc_xmlformatter', '" + testScriptFile.getAbsolutePath() + "']";
            interp.exec(args);
            interp.exec("__name__ = '__main__'");
            interp.exec("execfile(r'" + StaticConfiguration.JYTHON_HOME + "/Lib/pythondoc.py')");
            interp.cleanup();
        } catch (Exception e) {
            System.err.println("Error executing PythonInterpreter: " + e);
        }

        File testScriptDocXML = new File(testDir + "/TestScript-doc.xml");

        if (testScriptDocXML.exists()) {
            System.out.println("Converting Test script XML doc to HTML...");
            String[] a = {"-XSLTC", "-XT", "-IN", testDir + FS + "TestScript-doc.xml", "-XSL", StaticConfiguration.FORMATTER_DIR + FS + "testscriptdoc_xml2html.xsl", "-OUT", testDir + FS + "TestScript-doc.html"};
            org.apache.xalan.xslt.Process.main(a);
            testScriptDocXML.delete();
        } else {
            System.out.println("XML test script doc has not been generated for " + testScript);
            new File(testDir + "/TestScript-doc.html").delete();
        }

    }

    public static void main(String[] args) {
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

        if (args.length != 1) {
            displayUsage();
        } else {
            File file = new File(args[0]);
            if (file.exists() && file.isFile()) {
                generate(args[0]);
            } else {
                displayUsage();
            }
        }
    }
}
