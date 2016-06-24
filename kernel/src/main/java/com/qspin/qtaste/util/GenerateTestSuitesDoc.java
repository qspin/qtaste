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
import java.io.FileFilter;

import org.apache.log4j.PropertyConfigurator;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * This class is responsible for generating the documentation of all the testsuites
 *
 * @author lvboque
 */
public class GenerateTestSuitesDoc {

    public static void generate(String directory) {
        GenerateTestStepsModulesDoc.generate(directory);
        File dir = new File(directory);
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                // escape hidden directory like .svn, etc.
                return file.isDirectory() && !file.isHidden();
            }
        };

        File[] files = dir.listFiles(fileFilter);

        for (File file : files) {
            System.out.println("Directory:" + file.getPath());
            GenerateTestSuiteDoc.generate(file.getPath());
        }
    }

    public static void displayUsage() {
        System.out.println("Usage: generate-TestSuites-doc");
        System.exit(1);
    }

    public static void main(String[] args) {
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

        if (args.length != 0) {
            displayUsage();
        } else {
            generate("TestSuites");
        }
    }
}
