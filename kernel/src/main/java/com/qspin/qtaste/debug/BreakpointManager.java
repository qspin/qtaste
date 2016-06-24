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

package com.qspin.qtaste.debug;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * @author vdubois
 */
public class BreakpointManager {

    private Lock lock = new ReentrantLock();
    private Condition startCondition = lock.newCondition();
    private static BreakpointManager instance = null;
    private boolean canStart = false;
    private static Logger logger = Log4jLoggerFactory.getLogger(BreakpointManager.class);

    public static BreakpointManager getInstance() {
        if (instance == null) {
            instance = new BreakpointManager();
        }
        return instance;
    }

    public void stop() throws InterruptedException {
        lock.lock();
        canStart = false;
        try {
            while (!canStart) {
                // Waiting for the condition to be satisfied
                // Note: At this time, the thread will give up the lock
                // until the condition is satisfied. (Signaled by other threads)
                startCondition.await();
            }
        } finally {
            try {
                logger.debug("unlocking the BreakpointManager");
                lock.unlock();
                logger.debug("unlocked the BreakpointManager successfully");
            } catch (IllegalMonitorStateException e) {
                logger.debug("unlocked the BreakpointManager unsuccessfully");
                lock = new ReentrantLock();
                startCondition = lock.newCondition();
                logger.debug("recreated the lock & cond");
            }
        }
    }

    public void start() throws InterruptedException {
        lock.lock();
        try {
            // Signaling other threads that the condition is satisfied
            // Wakes up any one of the waiting threads
            canStart = true;
            startCondition.signal();          // Wakes up all threads waiting for this condition

            // method body
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
            }
        }
    }
}
