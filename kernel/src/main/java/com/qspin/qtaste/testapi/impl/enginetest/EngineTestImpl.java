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
 *
 * @author lvb
 */
public class EngineTestImpl implements EngineTest {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestAPIImpl.class);

    @Override
    public void initialize() throws QTasteException {
        // nothing to do
    }

    @Override
    public void terminate() throws QTasteException {
        // nothing to do
    }

    @Override
    public void throwNoException() {
    }

    @Override
    public void throwQTasteTestFailException(boolean withCause) throws QTasteTestFailException {
        if (withCause) {
            throw new QTasteTestFailException("This verb always fails!", new RuntimeException("Root cause of the failure"));
        } else {
            throw new QTasteTestFailException("This verb always fails!");
        }
    }

    @Override
    public void throwQTasteDataException() throws QTasteDataException {
        throw new QTasteDataException("Invalid data");
    }

    @Override
    public void throwQTasteException() throws QTasteException {
        throw new QTasteException("Generic QTasteException");
    }

    @Override
    public void throwRuntimeException() throws RuntimeException {
        throw new RuntimeException("Runtime exception");
    }

    @Override
    public void sleep(double duration) {
        try {
            Thread.sleep(Math.round(duration * 1000));
        } catch (InterruptedException e) {
            logger.debug("sleep has been interrupted!!!");
        }
    }

    @Override
    public void neverReturn() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug("neverReturn has been interrupted!!!");
            }
        }
    }

    @Override
    public void checkDataIsInteger(int data) {
    }

    @Override
    public void checkDataIsDouble(double data) {
    }

    @Override
    public void checkDataIsBoolean(boolean data) {
    }

    @Override
    public void checkData1To5(int data1, int data2, int data3, int data4, int data5) throws QTasteTestFailException {
        if (data1 != 1 || data2 != 2 || data3 != 3 || data4 != 4 || data5 != 5) {
            throw new QTasteTestFailException("DATA1 to DATA5 are not equals to 1 to 5 respectively!");
        }
    }

    @Override
    public String returnDataAsString(String data) {
        return data;
    }

    @Override
    public int returnDataAsInteger(int data) {
        return data;
    }

    @Override
    public double returnDataAsDouble(double data) {
        return data;
    }

    @Override
    public boolean returnDataAsBoolean(boolean data) {
        return data;
    }
}
