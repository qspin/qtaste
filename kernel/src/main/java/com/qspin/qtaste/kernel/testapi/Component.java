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

package com.qspin.qtaste.kernel.testapi;

import com.qspin.qtaste.testsuite.QTasteException;

/**
 * Interface for all Test API components
 * 
 * @author David Ergo
 */
public interface Component {

    /**
     * Initializes the component.
     * This method is called before executing a test.
     * 
     * @throws QTasteException if component cannot be initialized
     */
    public void initialize() throws QTasteException;
    
    /**
     * Terminates the component.
     * This method is called after executing a test.
     * 
     * @throws QTasteException if component cannot be terminated
     */
    public void terminate() throws QTasteException;
    
    /**
     * All Test API verbs have the following signature:
     *   ReturnType verbName(TestData data) throws QTasteException;
     * 
     * @param data the test data
     * @return optional return value, can be any type, including void
     * @throws com.qspin.qtaste.testsuite.QTasteTestFailException
     *             to set test status to fail and interrupt its execution
     * @throws com.qspin.qtaste.testsuite.QTasteDataException
     *             if a test data is missing or invalid, to set test status to 
     *             not available and interrupt its execution
     * @throws QTasteException (base QTasteException) if verb cannot be executed
     *             for some other reason, to set test status to 
     *             not available and interrupt its execution
     */
}
