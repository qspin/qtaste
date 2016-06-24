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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * Python helper class.
 *
 * @author David Ergo
 */
public class PythonHelper {

    /**
     * Executes a python script.
     *
     * @param fileName the filename of the python script to execute
     * @param arguments the arguments to pass to the python script
     * @return the output of the python script execution (combined standard and error outputs)
     * @throws PyException in case of exception during Python script execution
     */
    public static String execute(String fileName, String... arguments) throws PyException {
        Properties properties = new Properties();
        properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
        properties.setProperty("python.path", StaticConfiguration.FORMATTER_DIR);
        PythonInterpreter.initialize(System.getProperties(), properties, new String[] {""});

        PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
        StringWriter output = new StringWriter();
        interp.setOut(output);
        interp.setErr(output);
        interp.cleanup();
        interp.exec("import sys;sys.argv[1:]= [r'" + StringUtils.join(arguments, "','") + "']");
        interp.exec("__name__ = '__main__'");
        interp.exec("execfile(r'" + fileName + "')");
        interp.cleanup();
        return output.toString();
    }

    /**
     * Gets message of a PyException.
     * If internal message is null, uses printStackTrace() method to build message.
     *
     * @param e PyException
     * @return message string
     */
    public static String getMessage(PyException e) {
        if (e.getMessage() != null) {
            return e.getMessage();
        } else {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        }
    }
}
