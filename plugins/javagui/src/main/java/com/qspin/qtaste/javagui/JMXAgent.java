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

    /** Initialization method for spring.
     * 
     */
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
