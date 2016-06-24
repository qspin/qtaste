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

package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * MultipleInstancesTest is the interface of the QTaste Test API component providing verbs
 * for the testing of the QTaste MultipleInstancesComponent class.
 *
 * @author Laurent Vanboquestal
 */
public interface MultipleInstancesTest extends MultipleInstancesComponent {

    /**
     * Checks that multipleInstanceId is equals to the given one.
     *
     * @param expectedInstance the expected instances id
     * @throws QTasteTestFailException if the returned instance id is not equals to expected instance
     */
    void checkInstanceId(String expectedInstance) throws QTasteTestFailException;
}
