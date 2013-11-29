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

import org.python.core.PyException;

/**
 * Python helper class.
 * 
 * @author David Ergo
 */
public class PythonHelper {

    /** 
     * Gets message of a PyException.
     * If internal message is null, uses printStackTrace() method to build message.
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
