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
 * TestDataSet.java
 *
 * Created on 11 octobre 2007, 15:26
 */
package com.qspin.qtaste.testsuite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.impl.TestDataImpl;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author lvboque
 */
public class TestDataSet {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestDataSet.class);
    protected ArrayList<TestData> testDataList;
    protected SortedSet<Integer> selectedRows;

    /**
     * Creates a new instance of TestDataSet
     */
    public TestDataSet() {
        testDataList = new ArrayList<TestData>();
    }

    public TestDataSet(List<LinkedHashMap<String, String>> data) {
        testDataList = new ArrayList<TestData>();
        int i = 1;
        for (LinkedHashMap<String, String> testData: data) {
            TestData td = new TestDataImpl(i++, testData);
            testDataList.add(td);
        }
    }

    public void selectRows(SortedSet<Integer> selectedRows) {
        this.selectedRows = selectedRows;
        boolean selectAllRows = (selectedRows == null);
        for (TestData testData: testDataList) {
            testData.setSelected(selectAllRows);
        }
        if (selectedRows != null) {
            for (Integer selectedRow: selectedRows) {
                if (selectedRow >= 1 && selectedRow <= testDataList.size()) {
                    testDataList.get(selectedRow-1).setSelected(true);
                } else {
                    logger.warn("Selected data row (" + selectedRow + ") doesn't exist");
                }
            }
        }
    }
    
    public List<TestData> getData() {
        return testDataList;
    }
    
    public int size() {
        return testDataList.size();
    }
    
    public int getNumberSelectedRows() {
        int numberSelectedRows = 0;
        for (TestData testData: testDataList) {
            if (testData.isSelected()) {
                numberSelectedRows++;
            }
        }
        return numberSelectedRows;
    }
    
    public SortedSet<Integer> getSelectedRows() {
    	return selectedRows;
    }
}
