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

package com.qspin.qtaste.datacollection.pusher.impl;

import java.util.ListIterator;

import org.apache.log4j.Logger;

import com.qspin.qtaste.datacollection.Data;
import com.qspin.qtaste.datacollection.collection.Cache;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.util.HashtableLinkedList;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.NameValue;

/**
 *
 * @author lvboque
 */
public abstract class AbstractPusher implements Runnable {
    protected static Logger logger = Log4jLoggerFactory.getLogger(AbstractPusher.class);
    private HashtableLinkedList<String,Data> hash;
    private Thread thread;
    private boolean interrupted;
    private boolean running;
    private ListIterator<NameValue<String,Data>> iCacheValues;
    private long startTimestamp;

    /**
     * Creates a new instance of AbstractPusher
     */
    public AbstractPusher(HashtableLinkedList<String,Data> data) {
        this.hash = data;
        this.thread = new Thread(this);
        this.interrupted = false;
        this.running = false;
        this.iCacheValues = null;
    }

    public void start() {
        //logger.info("AbstractPusher has been started");
        assert !running;
        interrupted = false;

        Cache cache = CacheImpl.getInstance();

        iCacheValues = hash.getByInsertionTime();
        cache.clear();
        // TODO: only clear data from simulated CU

        startTimestamp = cache.getClearHistoryTimestamp();

        broadcastTill(hash.getClearHistoryTimestamp(), false);       
        thread.start();
    }

    public void join() {
        try {
            thread.join();
        } catch (InterruptedException ex) {
            
        }
    }
    public void stop() {
        //logger.info("AbstractPusher has been stopped");
         assert running;
        interrupted = true;
        try {
            while (running) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.fatal("Exception in stop", e);            
        }
    }

    public abstract void prepare(String name, Data value);

    public abstract void publish();

    public void run() {
        running = true;
        broadcastTill(Long.MAX_VALUE, true);
        running = false;
    }

    /**
     * 
     * @param tillTimestamp timestamp till which values must be broadcasted
     * @param respectInsertionTime true to insert values at the rate respecting
     *                                  timestamps
     *                             false to insert all values immediately
     */
    private void broadcastTill(long tillTimestamp, boolean respectInsertionTime) {
        long previousCacheValueTimestamp = 0;
        boolean hasPreparedData = false;

        while (!interrupted && iCacheValues.hasNext()) {
            NameValue<String,Data> nameValue = iCacheValues.next();
            String name = nameValue.name;
            Data value = nameValue.value;

            // get timestamp
            long cacheValueTimestamp = value.getTimestamp();
            
            // break out of loop if "till" timestamp is reached
            if (cacheValueTimestamp >= tillTimestamp) {
                iCacheValues.previous();
                break;
            }

            // correct timestamp reference            
            cacheValueTimestamp += (startTimestamp - hash.getClearHistoryTimestamp());
            value.setTimestamp(cacheValueTimestamp);

            if (previousCacheValueTimestamp == 0) {
                previousCacheValueTimestamp = cacheValueTimestamp;
            }
            if (cacheValueTimestamp == previousCacheValueTimestamp) {
                prepare(name, value);
                hasPreparedData = true;
            } else {
                // publish prepared values for which timestamp was previousCacheValueTimestamp
                if (respectInsertionTime) {
                    //long now = System.currentTimeMillis();
                    if (cacheValueTimestamp > previousCacheValueTimestamp) {
                        try {
                            long waitTime = cacheValueTimestamp - previousCacheValueTimestamp;
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                        }
                    }
                }

                assert hasPreparedData;
                publish();
                prepare(name, value);

                previousCacheValueTimestamp = cacheValueTimestamp;
            }
        }
        if (hasPreparedData) {
            publish();
            
        }
    }
}
