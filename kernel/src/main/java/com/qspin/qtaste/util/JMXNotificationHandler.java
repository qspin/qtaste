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

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectionNotification;

import org.apache.log4j.Logger;

/**
 * JMXNotificationHandler is responsible to store AttributeChangeNotification in a PropertyHistory container and
 * keep track of the JMX connnection status.
 *
 * @author lvboque
 */
public class JMXNotificationHandler implements NotificationListener {

    private static Logger logger = Log4jLoggerFactory.getLogger(JMXNotificationHandler.class);
    private long lastNotificationSequenceNumber = -1;
    private HashMap<String, String> subComponentsMap;
    private String componentName;
    private PropertiesHistory propertiesHistory;

    // Possible enhancement: Define a Container interface (or Collection)
    public JMXNotificationHandler(String componentName, PropertiesHistory propertiesHistory) {
        subComponentsMap = new HashMap<String, String>();
        this.componentName = componentName;
        this.propertiesHistory = propertiesHistory;
    }

    public void reset() {
        propertiesHistory.reset();
        lastNotificationSequenceNumber = -1;
    }

    public void setSubComponentName(String subComponentClassName, String subComponentName) {
        subComponentsMap.put(subComponentClassName, subComponentName);
    }

    // This handler relies on the "message" field of the notification to extract the information
    public void handleNotification(Notification notification, Object obj) {
        // handle JMX connection status notification
        if (notification instanceof JMXConnectionNotification) {
            JMXConnectionNotification jmxcNotification = (JMXConnectionNotification) notification;
            if (jmxcNotification.getType().equals(JMXConnectionNotification.FAILED)) {
                logger.error(
                      "JMX connection to " + componentName + " (" + jmxcNotification.getConnectionId() + ") has been lost !\n"
                            + jmxcNotification.getMessage());
                propertiesHistory.signalPossibleNotificationLoss();
            } else if (jmxcNotification.getType().equals(JMXConnectionNotification.NOTIFS_LOST)) {
                logger.error("JMX connection to " + componentName + " (" + jmxcNotification.getConnectionId()
                      + ") could have lost some notifications:\n" + jmxcNotification.getMessage());
                /*
                 if (jmxcNotification.getUserData() instanceof Long) {
                    lastNotificationSequenceNumber += (Long)jmxcNotification.getUserData();
                }*/
                propertiesHistory.signalPossibleNotificationLoss();
            }
            return;
        }

        // handle JMX attribute change notification
        if (!(notification instanceof AttributeChangeNotification)) {
            logger.error(
                  "Received a JMX notification from " + componentName + " which is not of type AttributeChangeNotification ("
                        + notification.getClass().getName() + ")");
            return;
        }

        AttributeChangeNotification attributeChangeNotification = (AttributeChangeNotification) notification;
        Object source = attributeChangeNotification.getSource();
        if (!(source instanceof String)) {
            // ignore AttributeChangeDetected sent by RequiredModelMBean#sendAttributeChangeNotification because sequence
            // number is always set to 1
            if (source instanceof ObjectName && "AttributeChangeDetected".equals(attributeChangeNotification.getMessage())) {
                logger.warn("Ignoring JMX AttributeChangeDetected notification from " + componentName + " for attribute "
                      + attributeChangeNotification.getAttributeName());
            } else {
                logger.error(
                      "JMX notification source from " + componentName + " is not of type String (" + source.getClass().getName()
                            + ")");
            }
            return;
        }

        // check that we didn't miss a notification
        long notificationSequenceNumber = notification.getSequenceNumber();
        if (lastNotificationSequenceNumber != -1) {
            if (notificationSequenceNumber != (lastNotificationSequenceNumber + 1)) {
                logger.error("Missed a JMX notification from " + componentName + "! (Received sequence number "
                      + notificationSequenceNumber + " while last was " + lastNotificationSequenceNumber + ")");
                propertiesHistory.signalPossibleNotificationLoss();
            }
        }
        lastNotificationSequenceNumber = notificationSequenceNumber;

        String message = (String) attributeChangeNotification.getMessage();
        int separatorIndex = message.indexOf(':');
        String subComponent = ((separatorIndex != -1) ? message.substring(0, separatorIndex) : "");
        String subComponentName = subComponentsMap.get(subComponent);
        if (subComponentName == null) {
            logger.error("Received JMX notification from " + componentName + " from unknown sub-component " + subComponent
                  + " - message: " + attributeChangeNotification.getMessage());
            return;
        }

        String attributeName = attributeChangeNotification.getAttributeName();
        // remove "prefix." or "prefix:" if any
        int dotIndex = attributeName.lastIndexOf('.');
        if (dotIndex < 0) {
            dotIndex = attributeName.lastIndexOf(':');
        }
        if (dotIndex > 0) {
            if (dotIndex == attributeName.length() - 1) {
                logger.error("Received JMX notification from " + componentName + " for empty attribute name: " + attributeName);
                return;
            }
            attributeName = attributeName.substring(dotIndex + 1);
        }
        // rename "StateProperty" into "state"
        if (attributeName.equals("StateProperty")) {
            attributeName = "state";
        }

        String propertyName =
              (subComponentName.isEmpty() ? "" : subComponentName.toLowerCase() + ".") + attributeName.toLowerCase();
        String oldValue = String.valueOf(attributeChangeNotification.getOldValue()).toLowerCase();
        String newValue = String.valueOf(attributeChangeNotification.getNewValue()).toLowerCase();

        // hack to get "null" string if the String is empty ("")
        if (oldValue.isEmpty()) {
            oldValue = "null";
        }
        if (newValue.isEmpty()) {
            newValue = "null";
        }

        boolean checkOldValue = (attributeChangeNotification.getOldValue() != null);

        propertiesHistory.addChange(propertyName, oldValue, newValue, notificationSequenceNumber, checkOldValue);
    }
}
