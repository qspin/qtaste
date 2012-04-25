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

package com.qspin.qtaste.reporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NameValue;
import com.qspin.qtaste.util.NamesValuesList;

/** 
 * A report formatter provides methods to ease the generation of test reports
 * @author lvboque 
 */
public abstract class ReportFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(ReportFormatter.class);
    protected File reportFile;
    protected PrintWriter output;
    protected Date startDate, endDate;
    protected static final String kernelVersion, testapiVersion;

    static {
        kernelVersion = com.qspin.qtaste.kernel.Version.getInstance().getFullVersion();
        testapiVersion = StaticConfiguration.VERSION_CONTROL.getTestApiVersion("");
    }

    public ReportFormatter(File reportFile) {
        this.reportFile = reportFile;
    }

    public ReportFormatter() {
    }

    public abstract void refresh();

    public File getReportFile() {
        return reportFile;
    }

    /**
     * Substitutes names by values in template and return result.
     * @param templateContent content of the template to substitute
     * @param namesValues list of names/values to substitute
     * @return result of substitution
     */
    protected static String getSubstitutedTemplateContent(String templateContent, NamesValuesList<String, String> namesValues) {
        String templateContentSubst = templateContent;
        // substitute the name/values

        for (NameValue<String, String> nameValue : namesValues) {
            templateContentSubst = templateContentSubst.replace(nameValue.name, nameValue.value);
        }
        return templateContentSubst;
    }

    protected void substituteAndWriteFile(String templateContent, NamesValuesList<String, String> namesValues) {
        try {
            output.print(getSubstitutedTemplateContent(templateContent, namesValues));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the name of the testbed configuration.
     * @return the name of the testbed configuration
     */
    protected static String getTestbedConfigurationName() {
        File testbedConfigurationFile = new File(getTestbedConfigurationFileName());
        String fileName = testbedConfigurationFile.getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Gets the name of the testbed configuration file.
     * @return the name of the testbed configuration file
     */
    protected static String getTestbedConfigurationFileName() {
        return TestBedConfiguration.getInstance().getFileName();
    }

    /**
     * Gets the sut version string corresponding to the sut selected in the the testbed configuration file.
     * @return the name of the testbed configuration file
     */
    protected static String getSUTVersion() {
        return TestBedConfiguration.getSUTVersion();
    }

    /**
     * Gets the content of the testbed configuration file.
     * @return the content of the testbed configuration file (with tabs converted to 4 spaces),
     *         or an error message if an error occurred reading file
     */
    protected static String getTestbedConfigurationFileContent() {
        try {
            return FileUtilities.readFileContent(getTestbedConfigurationFileName()).replace("\t", "    ");
        } catch (FileNotFoundException e) {
            logger.error("Testbed configuration file not found (" + getTestbedConfigurationFileName() + ")");
            return "File not found!";
        } catch (IOException e) {
            logger.error("Error while reading testbed configuration file (" + getTestbedConfigurationFileName() + "): " + e.getMessage());
            return "Error reading file!";
        }
    }

    /**
     * Gets the name of the testbed control script file.
     * @return the name of the testbed control script file,
     *         or null if testbed has no control script
     */
    protected static String getTestbedControlScriptFileName() {
        return TestBedConfiguration.getInstance().getControlScriptFileName();
    }

    /**
     * Gets the content of the testbed control script file.
     * @return the content of the testbed control script file (with tabs converted to 4 spaces),
     *         an error message if an error occurred reading file,
     *         or null if testbed has no control script
     */
    protected static String getTestbedControlScriptFileContent() {
        final String controlScriptFileName = getTestbedControlScriptFileName();
        if (controlScriptFileName != null) {
            try {
                return FileUtilities.readFileContent(controlScriptFileName).replace("\t", "    ");
            } catch (FileNotFoundException e) {
                logger.error("Testbed configuration file not found (" + controlScriptFileName + ")");
                return "File not found!";
            } catch (IOException e) {
                logger.error("Error while reading testbed configuration file (" + controlScriptFileName + "): " + e.getMessage());
                return "Error reading file!";
            }
        } else {
            return null;
        }
    }

    public void startReport(String name) {
        startDate = new Date();
        refresh();
    }

    public void stopReport() {
        endDate = new Date();
        refresh();
    }
}
