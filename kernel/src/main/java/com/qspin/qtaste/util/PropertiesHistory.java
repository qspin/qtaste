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

package com.qspin.qtaste.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * @author lvboque
 */
public class PropertiesHistory {

    private static Logger logger = Log4jLoggerFactory.getLogger(PropertiesHistory.class);
    private String component;
    private boolean possibleNotificationLoss = false;

    public class TimestampedValue {

        TimestampedValue(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        String value;
        long timestamp;
    }

    private HashMap<String, LinkedList<TimestampedValue>> hash;

    public PropertiesHistory(String component) {
        this.component = component;
        hash = new HashMap<>();
    }

    public synchronized void reset() {
        hash.clear();
        possibleNotificationLoss = false;
    }

    public synchronized void clear() {
        hash.clear();
    }

    public synchronized void clearHistory() {
        for (String property : hash.keySet()) {
            LinkedList<TimestampedValue> list = hash.get(property);
            while (list.size() > 1) {
                list.remove();
            }
        }
    }

    public void signalPossibleNotificationLoss() {
        possibleNotificationLoss = true;
    }

    public synchronized void addChange(String property, String oldValue, String newValue, long timestamp, boolean checkOldValue) {
        oldValue = oldValue.toLowerCase();
        newValue = newValue.toLowerCase();

        LinkedList<TimestampedValue> list = hash.computeIfAbsent(property, k -> new LinkedList<>());

        if (list.isEmpty()) {
            list.add(new TimestampedValue(oldValue, 0));
        } else {
            if (checkOldValue && !list.getLast().value.equals(oldValue)) {
                logger.warn(component + " " + property + " property change old value (" + oldValue
                      + ") doesn't match last received value (" + list.getLast().value + ")");
                list.add(new TimestampedValue(oldValue, 0));
            }
            if (list.getLast().value.equals(newValue)) {
                logger.debug("Ignoring not-changed new value of " + component + " " + property + " property (" + newValue + ")");
                return;
            }
        }
        list.add(new TimestampedValue(newValue, timestamp));

        String logMessage =
              "Change event on " + component + ": " + property + ": " + oldValue + " -> " + newValue + " (" + timestamp + ")";
        logger.trace(logMessage);
    }

    /**
     * Checks that property reaches given value or makes given transition within given time.
     * If found, remove old values from history.
     *
     * @param property the property name
     * @param values the expected property value or values (transition)
     * @param mustBeAtBegin true if value or transition must occur at begin of history
     * @param mustBeAtEnd true if value or transition must occur at end of history
     * @param maxTime_ms the maximum time to wait for the property value, in milliseconds
     * @param expectedValueOrTransition expected value or transition (only used for error messages)
     * @throws QTasteTestFailException if the property doesn't reach specified value or make specified transition or
     * if a possible notification loss occurred
     */
    public void checkPropertyValueOrTransition(String property, String[] values, boolean mustBeAtBegin, boolean mustBeAtEnd,
          long maxTime_ms, String expectedValueOrTransition)
          throws QTasteDataException, QTasteTestFailException {
        long beginTime_ms = System.currentTimeMillis(); // begin time
        long elapsedTime_ms; // total elapsed time
        long checkTimeInterval_ms = 100; // check every 100 milliseconds
        LinkedList<TimestampedValue> list = null;

        long lastCheckedTimestamp = 0;
        boolean foundMatching = false;
        boolean foundNotMatching = false;
        String currentPropertyHistory = null;

        loop:
        do {
            synchronized (this) {
                // get list of values if none yet
                if (list == null) {
                    list = hash.get(property);
                }
                if (list != null && list.size() > 0) {
                    if (mustBeAtBegin) {
                        if (!list.get(0).value.equals(values[0])) {
                            foundNotMatching = true;
                        } else if (list.size() >= values.length) {
                            if ((values.length == 1 || list.get(1).value.equals(values[1])) && (!mustBeAtEnd
                                  || list.size() == values.length)) {
                                foundMatching = true;
                                lastCheckedTimestamp = list.get(values.length - 1).timestamp;
                                break loop;
                            } else {
                                foundNotMatching = true;
                            }
                        }
                    } else {
                        ListIterator<TimestampedValue> iTSValue = list.listIterator();
                        int index = -1;
                        while (iTSValue.hasNext()) {
                            index++;
                            TimestampedValue tsValue = iTSValue.next();
                            TimestampedValue nextTsValue = null;
                            if (iTSValue.hasNext()) {
                                nextTsValue = iTSValue.next();
                                iTSValue.previous();
                            }
                            if (tsValue.value.equals(values[0]) && (values.length == 1 || (iTSValue.hasNext() && nextTsValue.value
                                  .equals(values[1])))) {
                                if (!mustBeAtEnd || list.size() == (index + values.length)) {
                                    foundMatching = true;
                                    lastCheckedTimestamp = (values.length == 1 ? tsValue.timestamp : nextTsValue.timestamp);
                                    break loop;
                                } else {
                                    foundNotMatching = true;
                                }
                            }
                        }
                    }
                }

                // value is not in list yet
                elapsedTime_ms = System.currentTimeMillis() - beginTime_ms;
                if (foundNotMatching || (elapsedTime_ms >= maxTime_ms)) {
                    currentPropertyHistory = getHistoryString(property, false);
                    break;
                }
            }

            try {
                Thread.sleep(checkTimeInterval_ms);
            } catch (InterruptedException e) {
                throw new QTasteDataException("Sleep has been interrupted while checking " + component + " property");
            }
        }
        while (true);

        if (possibleNotificationLoss) {
            throw new QTasteTestFailException(
                  component + " property value cannot be checked because of a possible notification loss!");
        }

        if (foundMatching) {
            removePrecedingValues(property, lastCheckedTimestamp);
        } else {
            throw new QTasteTestFailException(
                  component + " " + property + " property didn't behave as expected ('" + currentPropertyHistory
                        + "' doesn't match expected '" + expectedValueOrTransition + "')");
        }
    }

    /**
     * Checks that a property reaches a given value or do a given values
     * transition within given time.
     * If found, remove old values from history.
     *
     * @param propertyValueOrTransition the property value or transition to check (case insensitive)
     * <dl>
     * <dd>Format: "<code><i>property</i>:</code>[<code>[</code>]<code><i>expected_value</i></code>[<code>]</code>]" or
     * "<code><i>property</i>:</code>[<code>[</code>]<code><i>initial_value</i>-&gt;<i>final_value</i></code>[<code>]</code>]"
     * <dd>beginning <code>[</code> means that the expected or initial value must be the first one in the current values history
     * <dd>ending <code>]</code> means that the expected or final value must be the last one in the current values history
     * </dl>
     * @param maxTime the maximum time to wait for the property value or transition, in seconds
     * @throws QTasteDataException in case of invalid syntax in propertyValueOrTransition
     * @throws QTasteTestFailException if the property doesn't reach specified value or do specified values transition or
     * if a possible notification loss occurred
     * within specified time
     */
    public void checkPropertyValueOrTransition(String propertyValueOrTransition, double maxTime)
          throws QTasteDataException, QTasteTestFailException {
        long beginTime_ms = System.currentTimeMillis();
        long maxTime_ms = Math.round(maxTime * 1000);
        propertyValueOrTransition = propertyValueOrTransition.toLowerCase();
        String[] splitted = propertyValueOrTransition.split(" *: *", 2);
        if (splitted.length != 2) {
            throw new QTasteDataException("Invalid syntax");
        }
        String property = splitted[0];
        if (property.length() == 0) {
            throw new QTasteDataException("Invalid syntax");
        }
        String transition = splitted[1];
        boolean mustBeAtBegin = transition.matches("^\\[.*");
        if (mustBeAtBegin) {
            transition = transition.replaceFirst("^\\[ *", "");
        }
        boolean mustBeAtEnd = transition.matches(".*\\]$");
        if (mustBeAtEnd) {
            transition = transition.replaceFirst(" *\\]$", "");
        }
        String[] values = transition.split(" *-> *");
        if ((values.length != 1) && (values.length != 2)) {
            throw new QTasteDataException("Invalid syntax");
        }
        String expectedValueOrTransition = propertyValueOrTransition.replaceFirst(".*: *", "");

        long remainingTime_ms = maxTime_ms - (System.currentTimeMillis() - beginTime_ms);
        checkPropertyValueOrTransition(property, values, mustBeAtBegin, mustBeAtEnd, remainingTime_ms, expectedValueOrTransition);
    }

    public synchronized void removePrecedingValues(String checkedProperty, long timestamp) {
        for (String property : hash.keySet()) {
            LinkedList<TimestampedValue> list = hash.get(property);
            TimestampedValue head = null;
            while (!list.isEmpty() && (list.peekFirst().timestamp < timestamp)) {
                head = list.remove();
            }
            if ((head != null) && (!property.equals(checkedProperty))) {
                list.offerFirst(head);
            }
        }
    }

    public synchronized void dump(boolean withTimestamps) {
        System.out.println(component + " properties history:");
        TreeSet<String> sortedKeys = new TreeSet<>(hash.keySet());
        for (String property : sortedKeys) {
            System.out.println(getHistoryString(property, withTimestamps));
        }
        System.out.println();
    }

    public String getHistoryString(String property, boolean withPropertyPrefix) {
        return getHistoryString(property, withPropertyPrefix, false);
    }

    public synchronized String getHistoryString(String property, boolean withPropertyPrefix, boolean withTimestamps) {
        String result = (withPropertyPrefix ? (property + ": ") : "");
        LinkedList<TimestampedValue> list = hash.get(property);
        if (list != null) {
            Iterator<TimestampedValue> iValue = list.iterator();
            while (iValue.hasNext()) {
                TimestampedValue timestampedValue = iValue.next();
                result += timestampedValue.value;
                if (withTimestamps) {
                    result += " (" + timestampedValue.timestamp + ")";
                }
                if (iValue.hasNext()) {
                    result += " -> ";
                }
            }
        }
        return result;
    }
}
