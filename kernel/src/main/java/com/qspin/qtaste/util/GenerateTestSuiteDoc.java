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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * This class is responsible for generating the documentation of a testsuite
 * @author lvboque
 */
public class GenerateTestSuiteDoc {
    public static void displayUsage() {
        System.out.println("Usage: generate-TestSuite-doc <TestSuiteDirectory>");
        System.exit(1);
    }

    public static void generate(String testSuiteDir) {
        System.out.println("Test suite directory:" + testSuiteDir);

        File testSuiteDirFile = new File(testSuiteDir);
        if (!(testSuiteDirFile).exists()) {
            System.out.println("Directory " + testSuiteDir + " doesn't exist");
            System.exit(2);
        }
        File [] testScripts = searchTestScripts(testSuiteDirFile);
        if (testScripts.length == 0) {
            System.out.println("No testscript found!");
            System.exit(3);
        }

        StringBuffer testScriptsList = new StringBuffer();
        boolean firstTime = true;
        System.out.println("list of testscripts: " );
        for (File f : testScripts) {
            System.out.println("\t" + f.toString() );
            if (firstTime)
                testScriptsList.append("'" + f.getAbsolutePath() + "'");
            else
                testScriptsList.append(", '" + f.getAbsolutePath() + "'");                
            firstTime = false;
        }
        System.out.println("testscripts:" + testScriptsList.toString());
        System.out.println("Generating Test Scripts and Test suite XML doc...");
  

        StringWriter outputs = new StringWriter();
        try {
            Properties properties = new Properties();
            properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
            properties.setProperty("python.path", StaticConfiguration.FORMATTER_DIR);
            PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
            PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
            interp.setOut(outputs);
            interp.setErr(outputs);
            interp.cleanup();
            //java -cp %JYTHON_HOME%\jython.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %JYTHON_HOME%\Lib\pythondoc.py -f -s -Otestscriptdoc_xmlformatter -Dtestsuite_dir=%TEST_SUITE_DIR% !TEST_SCRIPTS!
            String args = "import sys;sys.argv[1:]= ['-f', '-s', '-Otestscriptdoc_xmlformatter', '-Dtestsuite_dir=" + testSuiteDir + "'," + testScriptsList.toString() + "]";
            System.out.println(args);
            System.out.println(args.toString());
            interp.exec(args.toString());
            interp.exec("__name__ = '__main__'");
            interp.exec("execfile(r'" + StaticConfiguration.JYTHON_HOME + "/Lib/pythondoc.py')");
            interp.cleanup();
            interp = null;
        }
        catch (Exception e) {
            System.err.println("Exception occurs executing PythonInterpreter:" + e.getMessage());
        }
        finally{
        	System.out.println(outputs.getBuffer().toString());
        }

        for (File testscript : testScripts) {
            File testScriptDir = new File (testscript.getParent() + "/TestScript-doc.xml");
            if (testScriptDir.exists()) {
                System.out.println("Converting Test Script XML doc to HTML for " + testscript.getParent());
                File xmlDocFile = new File(testscript.getParent() + "/TestScript-doc.xml");

                String []  a = {"-XSLTC", "-XT", "-IN", xmlDocFile.toString(), "-XSL",
                         StaticConfiguration.FORMATTER_DIR + "/testscriptdoc_xml2html.xsl",
                         "-OUT", testscript.getParent() + "/TestScript-doc.html"};
                org.apache.xalan.xslt.Process.main(a);
                xmlDocFile.delete();
            } else {
                System.out.println("XML test script doc has not been generated for " + testscript.getParent());
                new File(testscript.getParent() + "/TestScript-doc.html").delete();
            }
        }
        System.out.println("Converting Test Suite frameset ...");

        String [] b = {"-XSLTC", "-XT", "-IN", testSuiteDir + "/TestSuite-doc.xml", "-XSL", StaticConfiguration.FORMATTER_DIR + "/testsuitedoc_list_xml2html.xsl", "-OUT", testSuiteDir + "/TestSuite-doc-list.html"};
        org.apache.xalan.xslt.Process.main(b);

        String [] c = {"-XSLTC", "-XT", "-IN", testSuiteDir + "/TestSuite-doc.xml", "-XSL", StaticConfiguration.FORMATTER_DIR + "/testsuitedoc_summary_xml2html.xsl", "-OUT", testSuiteDir + "/TestSuite-doc-summary.html"};
        org.apache.xalan.xslt.Process.main(c);

        File testSuiteDocXML = new File(testSuiteDir + "/TestSuite-doc.xml");

        if (testSuiteDocXML.exists()) {
            System.out.println("Creating Test suite frameset in " + testSuiteDir + "/TestSuite-doc.html");
            try {
                FileWriter output = new FileWriter(testSuiteDir + "/TestSuite-doc.html");
                BufferedWriter out = new BufferedWriter(output);
                out.write("<HTML>\n" +
                      "<HEAD>" +
		      "<FRAMESET cols=\"15%%,85%%\">" +
		      "     <FRAME src=\"TestSuite-doc-list.html\" name=\"listFrame\" title=\"List of all Test suite scripts\"/>" +
		      "     <FRAME src=\"TestSuite-doc-summary.html\" name=\"testScriptFrame\" title=\"Test script documentation\" scrolling=\"yes\"/>" +
		      "</FRAMESET>" +
		      "</HEAD>" +
		      "</HTML>");
                out.close();
            }
            catch (Exception e) {
                System.out.println("Error writting file " + e);
            }
            testSuiteDocXML.delete();
        } else {
            System.out.println("XML test suite doc has not been generated for " + testSuiteDir);
            new File(testSuiteDir + "/TestSuite-doc-list.html").delete();
            new File(testSuiteDir + "/TestSuite-doc-summary.html").delete();
            new File(testSuiteDir + "/TestSuite-doc.html").delete();
        }

    }
    
    public static void main(String [] args) {
        if (args.length != 1)
            displayUsage();
        else {
            generate(args[0]);
        }
    }
    
    /**
     * Searches for a TestScript.py in the Test Script Directory. If none found try to search in the contained directories.
     * @param aTestScriptDirectory the directory to scan.
     * @return the found TestScript.py files.
     */
    private static File[] searchTestScripts(File aTestScriptDirectory) {
    	List<File> files = new ArrayList<File>();
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().equals("TestScript.py");
            }
        };
        File [] testScripts = aTestScriptDirectory.listFiles(fileFilter);
        if ( testScripts.length != 0 ) {
        	return testScripts;
        } else {
            FileFilter directoryFilter = new FileFilter() {
                public boolean accept(File file) {
                    return !file.isFile();
                }
            };
        	for (File dir : aTestScriptDirectory.listFiles(directoryFilter) ) {
        		for ( File f : searchTestScripts(dir) ) {
        			files.add(f);
        		}
        	}
        	return files.toArray(new File[0]);
        }
    }
}
