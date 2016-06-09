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
 * EngineTest.java
 */
package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.SingletonComponent;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * EngineTest is the interface of the QTaste Test API component providing verbs
 * for testing the QTaste engine.
 * 
 * @author David Ergo
 */
public interface EngineTest extends SingletonComponent {

    /**
     * Does nothing, in particular doesn't throw any exception.
     */
    void throwNoException();

    /**
     * Throws a QTasteTestFailException, to interrupt test execution and
     * set test status to fail.
     *
     * @param withCause true to set a cause exception to the QTasteTestFailException, false otherwise
     * @throws QTasteTestFailException always
     */

    void throwQTasteTestFailException(boolean withCause) throws QTasteTestFailException;

    /**
     * Throws a QTasteDataException, to interrupt test execution and
     * set test status to not available.
     * 
     * @throws QTasteDataException always
     */
    
    void throwQTasteDataException() throws QTasteDataException;

    /**
     * Throws a QTasteException, to interrupt test execution and
     * set test status to not available.
     * 
     * @throws QTasteException always
     */
    
    void throwQTasteException() throws QTasteException;

    /**
     * Throws a RuntimeException, to interrupt test execution and 
     * set test status to not available.
     * 
     * @throws RuntimeException always
     */
    
    void throwRuntimeException() throws RuntimeException;

    /**
     * Sleeps for given duration.
     * Returns if sleep is interrupted.
     * 
     * @param duration the sleep duration, in seconds
     */
    
    void sleep(double duration);

    /**
     * Never returns.
     */
    
    void neverReturn();

    /**
     * Checks that data argument is an integer, which is always the case.
     * @param data integer value
     */
    
    void checkDataIsInteger(int data);

    /**
     * Checks that data argument is a double, which is always the case
     * @param data double value
     */
    
    void checkDataIsDouble(double data);

    /**
     * Checks that data argument is a boolean, which is always the case.
     * @param data boolean value
     */
    
    void checkDataIsBoolean(boolean data);

    /**
     * Checks that data1 to data5 arguments have respectively the values 1 to 5.
     * 
     * @param data1 value that is expected to be 1
     * @param data2 value that is expected to be 2
     * @param data3 value that is expected to be 3
     * @param data4 value that is expected to be 4
     * @param data5 value that is expected to be 5
     * @throws QTasteTestFailException if data1 is not 1, data2 is not 2, data3 is not 3, data4 is not 4 or data5 is not 5
     */
    
    void checkData1To5(int data1, int data2, int data3, int data4, int data5) throws QTasteTestFailException;

    /**
     * Returns the given string value.
     * 
     * @param data string value
     * @return data
     */

    
    String returnDataAsString(String data);
    
    /**
     * Returns the given integer value.
     * 
     * @param data integer value
     * @return data
     */

    
    int returnDataAsInteger(int data);
    
    /**
     * Returns the given double value.
     * 
     * @param data double value
     * @return data
     */
    
    double returnDataAsDouble(double data);

    /**
     * Returns the given boolean value.
     * 
     * @param data boolean value
     * @return data
     */
    
    boolean returnDataAsBoolean(boolean data);
}
