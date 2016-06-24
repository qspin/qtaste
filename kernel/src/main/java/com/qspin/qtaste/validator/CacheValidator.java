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
 * CacheValidator.java
 *
 * Created on 11 mars 2008, 10:19
 */
package com.qspin.qtaste.validator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.datacollection.collection.Cache;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * CacheValidator is responsible for comparaison between expected values and values present in the Cache.
 *
 * @author lvboque
 */
public class CacheValidator extends Validator {

    private LinkedHashMap<String, Object> expectedValues;
    private long timeout;
    private String extraDetails;

    public static void check(String name, Object expectedValue, long timeout) throws QTasteTestFailException {
        LinkedHashMap<String, Object> nameExpectedValues = new LinkedHashMap<>();
        nameExpectedValues.put(name, expectedValue);
        check(nameExpectedValues, timeout);
    }

    /**
     * Note that since nameExpectedValues is a map, it can only contain one expected value per name.
     */
    public static void check(LinkedHashMap<String, Object> nameExpectedValues, long timeout) throws QTasteTestFailException {
        check(nameExpectedValues, timeout, null);
    }

    /**
     * Note that since nameExpectedValues is a map, it can only contain one expected value per name.
     */
    public static void check(LinkedHashMap<String, Object> nameExpectedValues, long timeout, String failMessagePrefix)
          throws QTasteTestFailException {
        CacheValidator validator = new CacheValidator(nameExpectedValues, timeout);
        validator.validate(failMessagePrefix);
    }

    /**
     * Creates a new instance of CacheValidator .
     * Note that since nameExpectedValues is a map, it can only contain one expected value per name.
     */
    private CacheValidator(LinkedHashMap<String, Object> nameExpectedValues, long timeout) {
        this.expectedValues = nameExpectedValues;
        this.timeout = timeout;
        this.extraDetails = "";

        //TODO: change this temporary hack by making a SmartSocketsCache ?
        TestBedConfiguration config = TestBedConfiguration.getInstance();
        if (config == null || !config.containsKey("probe_manager.probe")) {
            throw new RuntimeException("Error: Probe is not configured in testbed!");
        }
    }

    protected boolean validate() {
        try {
            final long beginTime_ms = System.currentTimeMillis(); // begin time
            long elapsedTime_ms; // total elapsed time
            final long checkTimeInterval_ms = 100; // check every 100 ms

            Cache cache = CacheImpl.getInstance();

            LinkedList<Object> currentValues = new LinkedList<>();
            String mismatchVariableName = null;
            Object mismatchVariableValue = null;
            Object mismatchVariableExpectedValue = null;

            do {
                // Get current values and check if they match the expected values
                currentValues.clear();
                Set<Map.Entry<String, Object>> list = expectedValues.entrySet();
                Iterator<Map.Entry<String, Object>> i = list.iterator();
                boolean allVariablesMatch = true;
                while (i.hasNext()) {
                    Map.Entry<String, Object> entry = i.next();
                    String key = entry.getKey();
                    Object expected = entry.getValue();

                    // get current value and store it
                    Object currentValue;
                    try {
                        currentValue = cache.getLast(key).getValue();
                    } catch (QTasteTestFailException e) {
                        currentValue = null;
                    }
                    currentValues.add(currentValue);

                    // only check value if there is no mismatch yet
                    if (allVariablesMatch && ((currentValue == null) || !expected.equals(currentValue))) {
                        allVariablesMatch = false;

                        mismatchVariableName = key;
                        mismatchVariableExpectedValue = expected;
                        mismatchVariableValue = currentValue;
                    }
                }

                if (allVariablesMatch) {
                    return true;
                }

                try {
                    Thread.sleep(checkTimeInterval_ms);
                    elapsedTime_ms = System.currentTimeMillis() - beginTime_ms;
                } catch (InterruptedException e) {
                    return false;
                }
            }
            while (elapsedTime_ms < timeout); // Wait checkTimeInterval_ms

            if (mismatchVariableName != null) {
                if (mismatchVariableValue == null) {
                    extraDetails = "Expected to get a value for variable " + mismatchVariableName + " but didn't.";
                } else {
                    extraDetails = "Expected to get " + mismatchVariableExpectedValue + " for variable " + mismatchVariableName
                          + " but got " + mismatchVariableValue + ".";
                }

                assert expectedValues.size() == currentValues.size() : "currentValues has not same size as expectedValues";
                extraDetails += "\nMismatching expected/current values are: ";
                Set<Map.Entry<String, Object>> list = expectedValues.entrySet();
                Iterator<Map.Entry<String, Object>> iExpectedValue = list.iterator();
                Iterator<Object> iCurrentValue = currentValues.iterator();
                while (iExpectedValue.hasNext()) {
                    Map.Entry<String, Object> entry = iExpectedValue.next();
                    String key = entry.getKey();
                    Object expected = entry.getValue();
                    Object current = iCurrentValue.next();
                    if ((current == null) || !current.equals(expected)) {
                        extraDetails += (key + "=" + expected + "/" + (current != null ? current : "null") + ", ");
                    }
                }
                if (extraDetails.endsWith(", ")) {
                    extraDetails = extraDetails.substring(0, extraDetails.length() - 2);
                }
            }
            return false;
        } catch (QTasteException e) {
            this.extraDetails = e.getMessage();
            return false;
        }
    }

    protected String getExtraDetails() {
        return extraDetails;
    }
}
