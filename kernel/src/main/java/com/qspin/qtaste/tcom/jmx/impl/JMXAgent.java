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

package com.qspin.qtaste.tcom.jmx.impl;

import java.beans.PropertyChangeEvent;
import java.lang.management.ManagementFactory;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * An abstract Agent is a JMX Agent
 * @author lvboque
 */
public abstract class JMXAgent extends NotificationBroadcasterSupport {

    private static Logger logger = Log4jLoggerFactory.getLogger(JMXAgent.class);
    private ObjectName mbeanName = null;
    private long notifSequenceNumber = 0;

    /** Initialization method for spring.
     * 
     */
    public void init() {
        try {
            register();
        } catch (Exception e) {
            logger.fatal(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mbeanName != null) {
                unregister();
            }
        } catch (Exception e) {
            logger.error(e);
        }

        super.finalize();
    }

    /**
     * Register the JMX agent
     * @throws java.lang.Exception
     */
    public synchronized void register() throws Exception {
        if (mbeanName != null) {
            throw new Exception("Agent already registered");
        }
        mbeanName = new ObjectName(getClass().getPackage().getName() + ":type=" + getClass().getSimpleName());
        logger.info("Registering JMX agent " + mbeanName);
        ManagementFactory.getPlatformMBeanServer().registerMBean(this, mbeanName);
        logger.info("JMX agent " + mbeanName + " registered");
    }

    /**
     * Unregister the JMX agent
     */
    public synchronized void unregister() throws Exception {
        if (mbeanName == null) {
            throw new Exception("Agent not registered");
        }
        logger.info("Unregistering JMX agent " + mbeanName);
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(mbeanName);
        logger.info("JMX agent " + mbeanName + " unregistered");
    }

    /**
     * Send a JMX notification of a property change event
     * 
     * @param pEvt a property change event
     */
    public synchronized void sendNotification(PropertyChangeEvent pEvt) {
        String oldValue = pEvt.getOldValue() == null ? "null" : pEvt.getOldValue().toString();
        String newValue = pEvt.getNewValue() == null ? "null" : pEvt.getNewValue().toString();
        String sourceName = pEvt.getSource().getClass().getCanonicalName();
        String message = sourceName + ":" + pEvt.getPropertyName() + " changed from " + oldValue + " to " + newValue;
        Notification n = new AttributeChangeNotification(sourceName, notifSequenceNumber++, System.currentTimeMillis(),
                message, pEvt.getPropertyName(), "java.lang.String", oldValue, newValue);
        sendNotification(n);
        logger.trace("Sent notification: " + message);
    }
}
