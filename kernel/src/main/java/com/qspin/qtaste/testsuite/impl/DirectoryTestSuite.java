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

/*
 * DirectoryTestSuite.java
 *
 * Created on 11 octobre 2007, 14:39
 */
package com.qspin.qtaste.testsuite.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.io.CSVFile;
import com.qspin.qtaste.io.XMLFile;
import com.qspin.qtaste.testsuite.TestRequirement;
import com.qspin.qtaste.testsuite.TestScript;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * A DirectoryTestSuite is a TestSuite containing all the test scripts located in a directory (or sub-directories).
 * @author lvboque
 */
public class DirectoryTestSuite extends TestSuite {

    private static Logger logger = Log4jLoggerFactory.getLogger(DirectoryTestSuite.class);
    private List<TestScript> testScripts = new ArrayList<TestScript>();
    private File directory;

    /**
     * Create a Directory TestSuite with the specified root directory.
     * @param strDirectory the directory root
     */
    public DirectoryTestSuite(String strDirectory) {
        super(strDirectory);
        directory = new File(strDirectory);
        addTestScripts(directory);
    }

    /**
     * @param dataRows set of data rows for which to execute test scripts
     *                 or null to execute test scripts for all data rows
     */
    public void selectRows(SortedSet<Integer> dataRows) {
        for (TestScript testScript : testScripts) {
            testScript.getTestDataSet().selectRows(dataRows);
        }
    }

    public int computeNumberTestsToExecute() {
        if (numberLoops == -1 || loopsInTime) {
            return -1;
        } else {
            int numberTestsToExecute = 0;
            for (TestScript testScript : testScripts) {
                numberTestsToExecute += testScript.getTestDataSet().getNumberSelectedRows();
            }
            numberTestsToExecute *= numberLoops;
            return numberTestsToExecute;
        }
    }

    public boolean executeOnce(boolean debug) {
    	boolean result = true; 
        for (TestScript testScript : testScripts) {
            if (!testScript.execute(debug)) {
                if (testScript.isAbortedByUser()) {
                    setAbortedByUser(true);
                    return false;
                }
                result = false;
            }
        }
        return result;
    }

    public List<TestScript> getTestScripts() {
        return testScripts;
    }

    /**
     * Add test scripts from given directory and its sub-directories.
     * @param directory directory from which to start adding test scripts
     */
    private void addTestScripts(File directory) {
        File scriptFile = new File(directory + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME);
        File csvFile = new File(directory + File.separator + StaticConfiguration.TEST_DATA_FILENAME);
        File xmlFile = new File(directory + File.separator + StaticConfiguration.TEST_REQUIREMENTS_FILENAME);
        if (scriptFile.exists() && csvFile.exists()) {
            // test case directory: add test script
            try {
                List<LinkedHashMap<String, String>> csvDataSet = new CSVFile(csvFile).getCSVDataSet();
                List<TestRequirement> xmlRequirements;
                if ( xmlFile.exists() ) {
                	xmlRequirements = new XMLFile(xmlFile).getXMLDataSet();
                } else {
                	xmlRequirements = new ArrayList<TestRequirement>();
                }
                if (csvDataSet.isEmpty()) {
                    logger.warn("Ignoring test case " + scriptFile + " " + csvFile.getName() + " because it contains no data row");
                } else {
                    logger.info("Adding test case " + scriptFile + " " + csvFile.getName() + " " + xmlFile);
                    TestScript ts = new JythonTestScript(csvDataSet, xmlRequirements, scriptFile, directory, DirectoryTestSuite.this);
                    testScripts.add(ts);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
        } else {
            // intermediate directory: add test scripts from sub-directories
            File[] subdirectories = FileUtilities.listSortedFiles(directory, new FileFilter() {

                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (subdirectories != null && subdirectories.length > 0) {
                // sort subdirectories by alphabetic order
                Arrays.sort(subdirectories, new Comparator<File>() {

                    public int compare(File file1, File file2) {
                        return file1.getName().compareToIgnoreCase(file2.getName());
                    }
                });
                for (File subdir : subdirectories) {
                    addTestScripts(subdir);
                }
            }
        }
    }
}
