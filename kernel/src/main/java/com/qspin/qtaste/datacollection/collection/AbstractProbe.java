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

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * An AbstractProbe is the class used to feed the Cache
 * The received variables are stored in the {@link Cache}
 * @author lvboque
 */
public abstract class AbstractProbe implements Probe, Runnable {

    private Thread t;
    protected boolean interrupted;
    private boolean running;
    protected DataReceivedListener listener;
    private static Logger logger = Log4jLoggerFactory.getLogger(AbstractProbe.class);

    /** Creates a new instance of AbstractProbe */
    public AbstractProbe() {
        this.interrupted = false;
        this.running = false;
        this.t = new Thread(this);
        register();
    }

    public abstract void serverLoop() throws Exception;

    public void run() {
        running = true;
        try {
            serverLoop();
        } catch (Exception e) {
            logger.fatal("AbstractProbe got an exception", e);
            CacheImpl.getInstance().invalidate(e.getMessage());
        } finally {
            running = false;
        }
    }

    public void register() {
        listener = (DataReceivedListener) CacheImpl.getInstance();
    }
        
    public void unregister() {
        listener = null;
    }

    public void start() {
        assert !running;
        interrupted = false;
        logger.info("Starting probe");
        CacheImpl.getInstance().init();
        t.start();
        logger.info("Probe started");
    }

    public void stop() {
        assert running;
        logger.info("Stopping probe");
        interrupted = true;
        try {
            while (running) {
                Thread.sleep(10);
            }
            logger.info("Probe stopped");
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
