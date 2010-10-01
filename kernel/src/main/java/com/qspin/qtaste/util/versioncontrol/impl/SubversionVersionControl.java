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

package com.qspin.qtaste.util.versioncontrol.impl;

import com.qspin.qtaste.util.versioncontrol.*;
import com.qspin.qtaste.util.Exec;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import java.io.ByteArrayOutputStream;
import org.apache.log4j.Logger;

/**
 * SubversionVersionControl is the implementation for Subversion version contol tool
 * @author lvboque
 */
public class SubversionVersionControl implements VersionControlInterface {

    private static Logger logger = Log4jLoggerFactory.getLogger(SubversionVersionControl.class);

    public String getVersion(String path) {
        Exec executor = new Exec();
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            executor.exec("svn info " + path, null, null, null, output);
            String outputStr = output.toString();

            String svnTagString = "/tags/";
            int index = outputStr.indexOf(svnTagString);
            if (index == -1) {
                return "undefined";
            }
            int startIndex = index + svnTagString.length();
            // Extract tag name from url
            String[] tokens = outputStr.substring(startIndex).split("/");
            String versionString = tokens[0];
            return versionString;
        } catch (Exception e) {
            logger.fatal("Error extracting testscript version from svn", e);
        }
        return "undefined";
    }
}
