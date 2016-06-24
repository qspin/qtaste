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

package com.qspin.qtaste.ui.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * @author vdubois
 */
public class TestScriptCreation {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestScriptCreation.class);
    private String TEMPLATE_DIR = StaticConfiguration.CONFIG_DIRECTORY + "/templates/TestScript";
    private String mTestName, mTestSuiteDir;

    public TestScriptCreation(String testName, String testSuiteDir) {
        mTestName = testName;
        mTestSuiteDir = testSuiteDir;
    }

    public void copyTestSuite(String sourceTestDir) {
        String sourceFileName = sourceTestDir + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME;
        String destFileName =
              mTestSuiteDir + File.separator + mTestName + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME;
        // check if file already exists
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                  "Test " + mTestName + " already exists. Do you want to overwrite the already existing test?", "Confirmation",
                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        FileUtilities.copy(sourceFileName, destFileName);
        // copy TestData files
        copyTestData(sourceTestDir, mTestSuiteDir + File.separator + mTestName);
    }

    public void createTestSuite() {
        BufferedWriter output = null;
        String testSuiteDirectoryName = mTestSuiteDir + File.separator + mTestName;
        String outputFileName = testSuiteDirectoryName + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME;
        File destFile = new File(outputFileName);
        if (destFile.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                  "Test " + mTestName + " already exists. Do you want to overwrite the already existing test?", "Confirmation",
                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        try {
            String templateFile = TEMPLATE_DIR + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME;
            String strContents = getTemplateContent(templateFile);
            strContents = strContents.replace("[$TEST_NAME]", mTestName);

            File outputFile = new File(outputFileName);
            outputFile.getParentFile().mkdirs();
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
            output.append(strContents);
            output.close();

            // copy the TestData files
            copyTestData(TEMPLATE_DIR, testSuiteDirectoryName);
            // create empty requirement xml file
            copyTestRequirement(TEMPLATE_DIR, testSuiteDirectoryName);
        } catch (IOException ex) {
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    logger.error("Error during creation of TestScript: " + ex.getMessage());
                }
            } else {
                logger.error("Error during creation of TestScript");
            }
        }
    }

    private void copyTestData(String sourceDir, String DestDir) {
        String testDataFileNameWithoutExtension = StaticConfiguration.TEST_DATA_FILENAME.substring(0,
              StaticConfiguration.TEST_DATA_FILENAME.lastIndexOf('.'));

        String extension = ".csv";
        String testDataSourceFileName = sourceDir + File.separator + testDataFileNameWithoutExtension + extension;
        String testDataDestFileName = DestDir + File.separator + testDataFileNameWithoutExtension + extension;
        FileUtilities.copy(testDataSourceFileName, testDataDestFileName);

        extension = ".xls";
        testDataSourceFileName = sourceDir + File.separator + testDataFileNameWithoutExtension + extension;
        if (new File(testDataSourceFileName).exists()) {
            testDataDestFileName = DestDir + File.separator + testDataFileNameWithoutExtension + extension;
            FileUtilities.copy(testDataSourceFileName, testDataDestFileName);
        }
    }

    private void copyTestRequirement(String sourceDir, String DestDir) {
        logger.debug("Create test requirement XML file");
        String testRequirementSourceFileName = sourceDir + File.separator + StaticConfiguration.TEST_REQUIREMENTS_FILENAME;
        logger.debug("test Requirement Source Filename : " + testRequirementSourceFileName);
        if (new File(testRequirementSourceFileName).exists()) {
            String testRequirementDestFileName = DestDir + File.separator + StaticConfiguration.TEST_REQUIREMENTS_FILENAME;
            FileUtilities.copy(testRequirementSourceFileName, testRequirementDestFileName);
        }
    }

    private String getTemplateContent(String templateName) throws FileNotFoundException, IOException {
        BufferedReader input;
        StringBuilder contents = new StringBuilder();
        input = new BufferedReader(new InputStreamReader(new FileInputStream(templateName), "UTF-8"));
        final String eol = System.getProperty("line.separator");
        String line; //not declared within while loop
        while ((line = input.readLine()) != null) {
            contents.append(line);
            contents.append(eol);
        }
        input.close();
        // replace the template by the specific items
        return contents.toString();
    }
}
