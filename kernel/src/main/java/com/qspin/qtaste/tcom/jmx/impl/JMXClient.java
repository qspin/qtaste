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

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * JMXClient is a connection to a JMX server
 * @author lvboque
 */
public class JMXClient {

    private static Logger logger = Log4jLoggerFactory.getLogger(JMXClient.class);
    private JMXServiceURL jmxServiceURL;
    private JMXConnector jmxc;
    private MBeanServerConnection mbsc;

    public JMXClient(String url) throws Exception {
        jmxServiceURL = new JMXServiceURL(url);
    }

    public void connect() throws Exception {
        if (!isConnected()) {
            logger.debug("Connecting to JMX server at " + jmxServiceURL);
            jmxc = JMXConnectorFactory.connect(jmxServiceURL);
            mbsc = jmxc.getMBeanServerConnection();
        }
    }

    public boolean isConnected() {
        if (jmxc != null) {
            try {
                jmxc.getConnectionId();
            } catch (IOException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return proxy to MBean object or null if not connected
     * @throws java.lang.Exception
     */
    public <T> T getProxy(String mbeanName, Class<T> mbeanInterface) throws Exception {
        if (isConnected()) {
            ObjectName objectName = new ObjectName(mbeanName);
            if (!mbsc.isRegistered(objectName)) {
                throw new InstanceNotFoundException("JMX MBean " + mbeanName + " is not registered at " + jmxServiceURL);
            }

            return (T) JMX.newMBeanProxy(mbsc, objectName, mbeanInterface, true);
        } else {
            return null;
        }
    }

    /*
     * Adds listener as notification and connection notification listener.
     * @return true if successful, false otherwise
     * @throws java.lang.Exception
     */
    public boolean addNotificationListener(String mbeanName, NotificationListener listener) throws Exception {
        if (isConnected()) {
            ObjectName objectName = new ObjectName(mbeanName);
            jmxc.addConnectionNotificationListener(listener, null, null);
            mbsc.addNotificationListener(objectName, listener, null, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes listener as notification and connection notification listener.
     * @return true if successful, false otherwise
     * @throws java.lang.Exception
     */
    public boolean removeNotificationListener(String mbeanName, NotificationListener listener) throws Exception {
        if (isConnected()) {
            ObjectName objectName = new ObjectName(mbeanName);
            mbsc.removeNotificationListener(objectName, listener, null, null);
            jmxc.removeConnectionNotificationListener(listener);
            return true;
        } else {
            return false;
        }
    }

    public void disconnect() throws Exception {
        mbsc = null;
        if (jmxc != null) {
            jmxc.close();
            jmxc = null;
        }
    }
}
