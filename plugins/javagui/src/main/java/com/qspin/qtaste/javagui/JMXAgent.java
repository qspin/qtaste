/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui;

import java.beans.PropertyChangeEvent;
import java.lang.management.ManagementFactory;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

/**
 * An abstract Agent is a JMX Agent
 * @author lvboque
 */
public abstract class JMXAgent extends NotificationBroadcasterSupport {
    private ObjectName mbeanName = null;
    private long notifSequenceNumber = 0;

   
    public void init() {
        try {
            register();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mbeanName != null) {
                unregister();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        System.out.println("Registering JMX agent " + mbeanName);
        ManagementFactory.getPlatformMBeanServer().registerMBean(this, mbeanName);
        System.out.println("JMX agent " + mbeanName + " registered");
    }

    /**
     * Unregister the JMX agent
     */
    public synchronized void unregister() throws Exception {
        if (mbeanName == null) {
            throw new Exception("Agent not registered");
        }
        System.out.println("Unregistering JMX agent " + mbeanName);
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(mbeanName);
        System.out.println("JMX agent " + mbeanName + " unregistered");
    }

    /**
     * Send a JMX notification of a property change event
     * 
     * @param pEvt
     */
    public synchronized void sendNotification(PropertyChangeEvent pEvt) {
        String oldValue = pEvt.getOldValue() == null ? "null" : pEvt.getOldValue().toString();
        String newValue = pEvt.getNewValue() == null ? "null" : pEvt.getNewValue().toString();
        String sourceName = pEvt.getSource().getClass().getCanonicalName();
        String message = sourceName + ":" + pEvt.getPropertyName() + " changed from " + oldValue + " to " + newValue;
        Notification n = new AttributeChangeNotification(sourceName, notifSequenceNumber++, System.currentTimeMillis(),
                message, pEvt.getPropertyName(), "java.lang.String", oldValue, newValue);
        sendNotification(n);
        System.out.println("Sent notification: " + message);
    }
}
