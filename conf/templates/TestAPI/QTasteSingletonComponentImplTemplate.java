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

package com.iba.ate.testapi.impl.vrenov;

import com.iba.ate.config.TestBedConfiguration;
import com.iba.ate.testsuite.QTasteException;
import com.iba.ate.testsuite.QTasteTestFailException;
import com.iba.ate.testsuite.TestData;
import org.apache.log4j.Logger;
import com.iba.ate.testapi.api.[$COMPONENT_NAME];

/**
 *
 * @author Vincent Dubois
 */
public class [$COMPONENT_NAME]Impl implements [$COMPONENT_NAME] {

    private static Logger logger = Logger.getLogger([$COMPONENT_NAME]Impl.class);

    public [$COMPONENT_NAME]Impl() throws Exception {
        TestBedConfiguration config = TestBedConfiguration.getInstance();
        initialize();
    }

    public void initialize() throws QTasteException {
    }

    public void terminate() throws QTasteException {
    }

	public void myMethod(TestData data) throws QTasteException {
	}
}
