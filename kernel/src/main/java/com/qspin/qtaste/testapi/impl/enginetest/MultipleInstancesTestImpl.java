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

import com.qspin.qtaste.testapi.api.MultipleInstancesTest;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * MultipleInstancesTest component is providing verbs for testing the QTaste engine.
 * @author lvboque
 */

public class MultipleInstancesTestImpl implements MultipleInstancesTest  {
    
    private String instanceId;
    
    public MultipleInstancesTestImpl(String id) {
        this.instanceId = id;
    }

    public String getInstanceId() {
        return this.instanceId;
    }
    
    public void initialize() throws QTasteException {
        // nothing to do
    }
    
    public void terminate() throws QTasteException {
        // nothing to do
    }
    
    public void checkInstanceId(String expectedInstance) throws QTasteTestFailException {
        if (!instanceId.equals(expectedInstance)) {
            throw new QTasteTestFailException("instanceId should be equals to " + expectedInstance);
        }
    }
}
