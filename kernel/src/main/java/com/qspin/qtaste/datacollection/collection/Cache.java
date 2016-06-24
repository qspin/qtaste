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

package com.qspin.qtaste.datacollection.collection;

import java.util.HashMap;
import java.util.Iterator;

import com.qspin.qtaste.datacollection.Data;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.util.NameValue;

/**
 * Interface of the Cache
 *
 * @author lvboque
 */
public interface Cache extends DataReceivedListener {

    /**
     * Initialize the cache
     */
    void init();

    void dump();

    void save(String fout) throws Exception;

    void load(String fin) throws Exception;

    void clear();

    void clearHistory();

    long getClearHistoryTimestamp();

    Iterator<NameValue<String, Data>> getContent();

    /**
     * Invalidate the data present in the cache for the reason specified as parameter
     *
     * @param reason the reason
     */
    void invalidate(String reason);

    /**
     * Return the last Data structure for the specified variable
     *
     * @param name the variable of which to get the value
     * @return the data object
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if data in the cache have been invalidated
     * @throws com.qspin.qtaste.testsuite.QTasteTestFailException if there is no data in the cache for the specified variable
     * @throws QTasteException
     */
    Data getLast(String name) throws QTasteTestFailException, QTasteException;

    /**
     * Comparators for the {@link #waitForValue} method.
     */
    enum Comparator {

        COMPARATOR_EQ, // equal ("==")
        COMPARATOR_NEQ, // not equal ("!=")
        COMPARATOR_LT, // lower than ("<")
        COMPARATOR_GT, // greater than (">")
        COMPARATOR_LEQ, // lower or equal ("<=")
        COMPARATOR_GEQ  // greater or equal (">=")
    }

    /**
     * Get comparator from its string representation
     *
     * @param comparatorString string representation of the comparator: "==", "!=", "&lt;", "&gt;", "&lt;=" or "&gt;="
     * @return comparator corresponding to comparatorString
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if comparatorString is invalid
     */
    Comparator getComparatorFromString(String comparatorString) throws QTasteDataException;

    /**
     * Wait that specified variable compares to given value as specified by the given comparator,
     * and return corresponding Data structure.
     * <p>
     * Note that the variable value class must be implement the Comparable interface.
     *
     * @param name the variable for which to wait the value for
     * @param comparator the comparator to use to compare the variable value
     * @param value the value to compare the variable value with, of the same class as the variable values
     * @param timeout the maximum time to wait for, in milliseconds
     * @return the data object
     * @throws com.qspin.qtaste.testsuite.QTasteException if data in the cache have been invalidated or if value is not an
     * instance of Comparable
     * @throws com.qspin.qtaste.testsuite.QTasteTestFailException if there is no data in the cache for the specified variable,
     * or its value didn't compares to given value as specified by the given comparator within given time
     */
    Data waitForValue(String name, Comparator comparator, Object value, long timeout)
          throws QTasteTestFailException, QTasteException;

    HashMap<String, Data> getCopyContent();
}
