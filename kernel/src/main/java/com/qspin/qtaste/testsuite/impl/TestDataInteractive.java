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
 * TestDataImpl.java
 */
package com.qspin.qtaste.testsuite.impl;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author lvboque
 */
@SuppressWarnings("serial")
public class TestDataInteractive extends TestDataImpl {

    static private Logger logger = Log4jLoggerFactory.getLogger(TestDataInteractive.class);
    static private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private boolean isGUIMonitored = false;
    private Component mParent = null;

    public void setGUIMonitoring(boolean value, Component parent) {
        isGUIMonitored = value;
        mParent = parent;
    }

    /**
     * Creates a new instance of TestDataInteractive
     */
    // TODO: Check if pairs is not more appropriate?
    public TestDataInteractive(String name, int rowId, String[] names, String[] values) {
        super(rowId, new LinkedHashMap<String, String>());
        ///setId(name, rowId);
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                hash.put(names[i], values[i]);
            }
        }
    }

    @Override
    public String getValue(String key) throws QTasteDataException {
        try {
            String input = null;
            if (!hash.containsKey(key))
            {
                if (isGUIMonitored) {
                    input = JOptionPane.showInputDialog(mParent,
                            "Give the value of " + key + "?",
                            "TestData value",
                            JOptionPane.QUESTION_MESSAGE);

                } else {
                    System.out.println("QTaste>Give the value of " + key + ":");
                    // Read a line of text from the user.
                    input = stdin.readLine();
                }
                hash.put(key, input);
                if (input == null) {
                    throw new QTasteDataException(key + " input value entry cancelled by user");
                // fill hashFiles if necessary
                }
                loadFileIfAny();
            }
            else
                input = hash.get(key);
            
            return input;
        } catch (Exception ex) {
            logger.error(ex);
            throw new QTasteDataException(key + " input value invalid");
        }
    }
}
