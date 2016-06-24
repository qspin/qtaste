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
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * HTMLFormatter is a formatter able to generate HTML reports using templates files
 *
 * @author lvboque
 */
public abstract class HTMLFormatter extends ReportFormatter {

    private static Logger logger = Log4jLoggerFactory.getLogger(HTMLFormatter.class);
    protected HashMap<String, String> templateContents;

    public HTMLFormatter(HashMap<String, String> templates, File reportDirectory, String reportFileName) throws IOException {
        super(reportDirectory, reportFileName);
        templateContents = new HashMap<String, String>();
        Iterator<String> templateNames = templates.keySet().iterator();
        while (templateNames.hasNext()) {
            String templateName = templateNames.next();
            String templateFileName = templates.get(templateName);
            templateContents.put(templateName, FileUtilities.readFileContent(templateFileName));
        }
    }

    public void refresh() {
        try {
            File outputFile = new File(reportFile.getAbsoluteFile() + ".tmp");
            File tempOutputFile = new File(outputFile.getPath());
            output = new PrintWriter(new BufferedWriter(new FileWriter(tempOutputFile)));
            generateHeader();
            makeBody();
            generateFooter();
            output.close();

            if (reportFile.exists()) {
                reportFile.delete();
            }

            if (!tempOutputFile.renameTo(reportFile)) {
                logger.error("Couldn't rename HTML report file " + tempOutputFile + " into " + outputFile);
            }
        } catch (IOException e) {
            logger.error("Cannot refresh the HTML report", e);
        }
    }

    public abstract void generateHeader();

    public abstract void makeBody();

    public abstract void generateFooter();
}
