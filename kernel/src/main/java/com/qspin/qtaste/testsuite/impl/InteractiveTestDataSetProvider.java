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

package com.qspin.qtaste.testsuite.impl;

import java.util.ArrayList;
import java.util.List;

import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.TestDataSet;

/**
 * @author lvboque
 */
public class InteractiveTestDataSetProvider extends TestDataSet {

    public InteractiveTestDataSetProvider(boolean GUIMonitored, TestData data) {
        testDataList = new ArrayList<>();
        testDataList.add(data);
    }

    @Override
    public List<TestData> getData() {
        return testDataList;
    }

    public TestDataSet getTestDataSet() {
        return this;
    }
}
