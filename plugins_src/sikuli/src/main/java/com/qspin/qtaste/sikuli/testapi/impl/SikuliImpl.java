/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.sikuli.testapi.impl;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.testsuite.QTasteException;

public class SikuliImpl extends com.qspin.qtaste.sikuli.client.Sikuli implements com.qspin.qtaste.sikuli.testapi.api.Sikuli {

    public SikuliImpl(String instanceId) throws Exception {
        super(TestBedConfiguration.getInstance().getMIString(instanceId, "Sikuli", "jmx_url"));
        mInstanceId = instanceId;
    }

    @Override
    public String getInstanceId() {
        return mInstanceId;
    }

    @Override
    public void terminate() throws QTasteException {
        super.terminate();
    }

    @Override
    public void initialize() throws QTasteException {
        super.initialize();
    }

    protected String mInstanceId;

}
