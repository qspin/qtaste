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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * XMLFormatter is a formatter able to generate XML reports using templates files
 * @author lvboque
 */
public abstract class XMLFormatter extends ReportFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(XMLFormatter.class);
    protected String templateContent,  rowTemplateContent,  rowStepsTemplateContent;
    
    public XMLFormatter(String template, String rowTemplate, String rowStepsTemplate, File reportFile) throws IOException {
        super(reportFile);
        this.templateContent = FileUtilities.readFileContent(template);
        this.rowTemplateContent = FileUtilities.readFileContent(rowTemplate);
        this.rowStepsTemplateContent = FileUtilities.readFileContent(rowStepsTemplate);    
    }
                   
    public void refresh() {        
        try {
            File outputFile = new File(reportFile.getAbsoluteFile() + ".tmp");
            File tempOutputFile = new File(outputFile.getPath());
            output = new PrintWriter(new BufferedWriter(new FileWriter(tempOutputFile)));
            generateReport();
            output.close();

            if (reportFile.exists()) {
                reportFile.delete();
            }
            
            if (!tempOutputFile.renameTo(reportFile)) {
                logger.error("Couldn't rename XML report file " + tempOutputFile + " into " + outputFile);
            }
        } catch (IOException e) {
            logger.error("Cannot refresh the XML report", e);
        }        
    }
    
    public abstract void generateReport();
}
