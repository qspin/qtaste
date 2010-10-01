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

package com.qspin.qtaste.testapi.impl.enginetest;

import org.apache.log4j.Logger;

import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.testapi.api.EngineTest;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * EngineTest component is providing verbs for testing the QTaste engine.
 * @author lvb
 */
public class EngineTestImpl implements EngineTest {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestAPIImpl.class);
      
    public void initialize() throws QTasteException {
        // nothing to do
    }
    
    public void terminate() throws QTasteException {
        // nothing to do
    }
    
    public void throwNoException() {
    }

    public void throwQTasteTestFailException() throws QTasteTestFailException {
        throw new QTasteTestFailException("This verb always fails!");
    }

    public void throwQTasteDataException() throws QTasteDataException {
        throw new QTasteDataException("Invalid data");
    }

    public void throwQTasteException() throws QTasteException {
        throw new QTasteException("Generic QTasteException");
    }

    public void throwRuntimeException() throws RuntimeException {
        throw new RuntimeException("Runtime exception");
    }

    public void sleep(double duration) {
        try {
            Thread.sleep(Math.round(duration * 1000));
        } catch (InterruptedException e) {
            logger.debug("sleep has been interrupted!!!");
        }
    }

    public void neverReturn() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug("neverReturn has been interrupted!!!");
            }
        }
    }

    public void checkDataIsInteger(int data) {
    }

    public void checkDataIsDouble(double data) {
    }

    public void checkDataIsBoolean(boolean data) {
    }

    public void checkData1To5(int data1, int data2, int data3, int data4, int data5) throws QTasteTestFailException {
        if (data1 != 1 || data2 != 2 || data3 != 3 || data4 != 4 || data5 != 5) {
            throw new QTasteTestFailException("DATA1 to DATA5 are not equals to 1 to 5 respectively!");
        }
    }

    public String returnDataAsString(String data) {
        return data;
    }

    public int returnDataAsInteger(int data) {
        return data;
    }

    public double returnDataAsDouble(double data) {
        return data;
    }

    public boolean returnDataAsBoolean(boolean data) {
        return data;
    }
}
