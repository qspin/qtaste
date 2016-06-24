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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.datacollection.Data;
import com.qspin.qtaste.io.ObjectFile;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.util.HashtableLinkedList;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NameValue;

/**
 * @author lvboque
 */
public class CacheImpl implements Cache, DataReceivedListener {

    private static Logger logger = Log4jLoggerFactory.getLogger(CacheImpl.class);
    private static Cache instance = null;
    private HashtableLinkedList<String, Data> hash;
    private final Map<String, Data> hash2 = Collections.synchronizedMap(new HashMap<String, Data>());
    private boolean isValid; // data in the cache are still valid
    private String reason; // reason why data are not valid

    synchronized public static Cache getInstance() {
        if (instance == null) {
            instance = new CacheImpl();
        }
        return instance;
    }

    /**
     * Creates a new instance of CacheImpl
     */
    private CacheImpl() {
        hash = new HashtableLinkedList<String, Data>();
        init();
    }

    public void init() {
        isValid = true;
        reason = null;
    }

    public void dataReceived(long timestamp, String sender, String dest, String name, Object value, Data.DataSource source,
          Object type) {
        // logger.debug("CacheImpl: dataReceived got " + name + " value:" + value);
        hash.put(name, new Data(timestamp, sender, dest, name, value, source, type));
    }

    public Iterator<NameValue<String, Data>> getContent() {
        return hash.getByInsertionTime();
    }

    public HashMap<String, Data> getCopyContent() {
        HashMap<String, Data> returnHash = new HashMap<String, Data>();
        synchronized (hash2) {
            Iterator<NameValue<String, Data>> it = hash.getByInsertionTime();
            NameValue<String, Data> value;
            while (it.hasNext()) {
                try {
                    value = (NameValue<String, Data>) it.next();
                    returnHash.put(value.name, value.value);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return null;
                }
            }
        }
        return returnHash;
    }

    public void dump() {
        Iterator<NameValue<String, Data>> i = hash.getByInsertionTime();
        long t0 = hash.getClearHistoryTimestamp();
        System.out.println("Dumping the Cache (Cache contains " + hash.size() + " entries)");
        System.out.println("Timestamp of the clear history: " + t0 + " (" + new Date(t0) + ")");
        while (i.hasNext()) {
            NameValue<String, Data> nameValue = i.next();
            //if (!inBlackList(nameValue.name)) {
            Data entry = nameValue.value;
            System.out.println(
                  "name:" + nameValue.name + " value:" + entry.getValue() + " timestamp:" + (entry.getTimestamp() - t0));
            //}
        }
        System.out.println("End of dump (Cache contains " + hash.size() + " entries)");
    }

    public void save(String fout) throws Exception {
        logger.info("Saving cache to file " + fout);
        new ObjectFile(fout).save(hash);
        logger.info("" + hash.size() + " entries in the cache");
    }

    @SuppressWarnings("unchecked")
    public void load(String fin) throws Exception {
        logger.info("Loading cache from file " + fin);
        hash = (HashtableLinkedList<String, Data>) new ObjectFile(fin).load();
        logger.info("" + hash.size() + " entries in the cache");
    }

    public void clear() {
        hash.clear();
    }

    public void clearHistory() {
        hash.clearHistory();
    }

    public long getClearHistoryTimestamp() {
        return hash.getClearHistoryTimestamp();
    }

    public void invalidate(String reason) {
        this.isValid = false;
        this.reason = reason;
    }

    public Data getLast(String name) throws QTasteException, QTasteTestFailException {
        if (!isValid) {
            throw new QTasteException("Cache is not valid. Reason:" + reason);
        }
        Data data = hash.getLast(name);
        if (data == null) {
            throw new QTasteTestFailException("Variable " + name + " is not present in cache");
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public Data waitForValue(String name, Comparator comparator, Object value, long timeout)
          throws QTasteException, QTasteTestFailException {
        final long beginTime_ms = System.currentTimeMillis(); // begin time
        long elapsedTime_ms = 0; // total elapsed time
        final long checkTimeInterval_ms = 100; // check every 100 ms

        if (!(value instanceof Comparable)) {
            throw new QTasteDataException("Value is not an instance of Comparable");
        }

        do {
            Object lastValue;
            try {
                Data lastData = getLast(name);
                lastValue = lastData.getValue();
                int comparisonResult = ((Comparable) value).compareTo(lastValue);
                boolean comparisonSuccessful = false;
                switch (comparator) {
                    case COMPARATOR_EQ:
                        comparisonSuccessful = comparisonResult == 0;
                        break;
                    case COMPARATOR_NEQ:
                        comparisonSuccessful = comparisonResult != 0;
                        break;
                    case COMPARATOR_LT:
                        comparisonSuccessful = comparisonResult > 0;
                        break;
                    case COMPARATOR_GT:
                        comparisonSuccessful = comparisonResult < 0;
                        break;
                    case COMPARATOR_LEQ:
                        comparisonSuccessful = comparisonResult >= 0;
                        break;
                    case COMPARATOR_GEQ:
                        comparisonSuccessful = comparisonResult <= 0;
                        break;
                }
                if (comparisonSuccessful) {
                    return lastData;
                }
            } catch (QTasteTestFailException e) {
                // variable not in cache
                lastValue = null;
            }
            if (elapsedTime_ms >= timeout) {
                String lastValueStr = (lastValue == null ? "null" : lastValue.toString());
                String comparatorStr = "";
                switch (comparator) {
                    case COMPARATOR_EQ:
                        comparatorStr = "==";
                        break;
                    case COMPARATOR_NEQ:
                        comparatorStr = "!=";
                        break;
                    case COMPARATOR_LT:
                        comparatorStr = "<";
                        break;
                    case COMPARATOR_GT:
                        comparatorStr = ">";
                        break;
                    case COMPARATOR_LEQ:
                        comparatorStr = "<=";
                        break;
                    case COMPARATOR_GEQ:
                        comparatorStr = ">=";
                        break;
                }
                throw new QTasteTestFailException(
                      "Variable " + name + " value (" + lastValueStr + ") didn't reach expected value (" + comparatorStr + " "
                            + value + ")");
            }
            // wait
            try {
                Thread.sleep(checkTimeInterval_ms);
                elapsedTime_ms = System.currentTimeMillis() - beginTime_ms;
            } catch (InterruptedException e) {
                throw new QTasteDataException("Sleep interrupted");
            }
        }
        while (true);
    }

    public Comparator getComparatorFromString(String comparatorString) throws QTasteDataException {
        if (comparatorString.equals("==")) {
            return Comparator.COMPARATOR_EQ;
        } else if (comparatorString.equals("!=")) {
            return Comparator.COMPARATOR_NEQ;
        } else if (comparatorString.equals("<")) {
            return Comparator.COMPARATOR_LT;
        } else if (comparatorString.equals(">")) {
            return Comparator.COMPARATOR_GT;
        } else if (comparatorString.equals("<=")) {
            return Comparator.COMPARATOR_LEQ;
        } else if (comparatorString.equals(">=")) {
            return Comparator.COMPARATOR_GEQ;
        } else {
            throw new QTasteDataException("Invalid comparator (" + comparatorString + ")");
        }
    }
}
