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

import java.io.StringWriter;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 *
 * @author lvboque
 */
public class GenerateTestStepsModulesDoc {

    private static Logger logger = Log4jLoggerFactory.getLogger(GenerateTestStepsModulesDoc.class);

    public static void generate(String directory) {
        logger.debug("Generating Test steps module XML doc...");
      
        try {
            
            Properties properties = new Properties();
            properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
            properties.setProperty("python.path", StaticConfiguration.FORMATTER_DIR);
            PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
   
            StringWriter output = new StringWriter();
            PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());

            interp.setOut(output);
            interp.setErr(output);
            interp.cleanup();

            //java -cp %JYTHON_HOME%\jython.jar -Dpython.home=%JYTHON_HOME% -Dpython.path=%FORMATTER_DIR% org.python.util.jython %JYTHON_HOME%\Lib\pythondoc.py -f -s -Otestscriptdoc_xmlformatter -Dtestsuite_dir=%TEST_SUITE_DIR% !TEST_SCRIPTS!
            String args = "import sys;sys.argv[1:]= ['" + directory +  "']";

            interp.exec(args);

            interp.exec("__name__ = '__main__'");

            interp.exec("execfile(r'" + StaticConfiguration.FORMATTER_DIR + "/stepsmoduledoc_xmlformatter.py')");

            interp.cleanup();
            interp = null;
        }
        catch (Exception e) {
            System.err.println("Exception occurs executing PythonInterpreter:" + e);
        }
    }

    public static void displayUsage() {
        System.out.println("Usage: generate-TestStepsModuleDoc [TestStepsModuleFile dir| BaseDirectory]");
        System.out.println("Default base directory is TestSuites");
        System.exit(1);
    }

    public static void main(String [] args) {
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

        switch (args.length) {
            case 0:
                generate("TestSuites");
                break;
            case 1:
                generate(args[0]);
                break;
            default:
                displayUsage();
        }
    }
}
