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
 * Bugzilla is the interface of the QTaste Test API component providing verbs
 * for testing the Bugzilla application.
 *
 * @author lvboque
 */
public interface Bugzilla extends MultipleInstancesComponent {

    /**
     * Compare the content of the specified defectID record with the expected value provided as parameter
     *
     * @param defectId the identified of the record
     * @param shortDescription the title of the defect
     * @param longDescription the long description of the defect
     * @param assignee the identifier of the person assigned to this defect
     * @throws QTasteTestFailException If the content of the DB doesn't correspond to the specified values
     * @throws Exception Throw an exception in case of database connection errors
     */
    void checkDatabase(int defectId, String shortDescription, String longDescription, String assignee)
          throws QTasteTestFailException, Exception;
}
