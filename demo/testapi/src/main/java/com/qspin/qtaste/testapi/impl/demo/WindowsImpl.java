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
package com.qspin.qtaste.testapi.impl.demo;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.testapi.api.Windows;
import com.qspin.qtaste.testsuite.QTasteException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Implementation of the Windows Test API
 * @author Laurent Vanbboquestal
 */
public class WindowsImpl implements Windows {
    static Logger logger = Logger.getLogger(TestAPIImpl.class);
    private int sessionID;
    private String host;
    private int port;
    private XmlRpcClient client;

    public WindowsImpl() throws QTasteException {
        initialize();
    }

    public void initialize() throws QTasteException {
        TestBedConfiguration tb = TestBedConfiguration.getInstance();
        this.host = tb.getString("singleton_components.Windows.host");
        this.port = tb.getInt("singleton_components.Windows.port");
        this.client = new XmlRpcClient();
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            
            config.setServerURL(new URL("http://" + host + ":" + port));
            client.setConfig(config);
        } catch (MalformedURLException ex) {
            logger.fatal("Cannot connect to http://" + host + ":" + port, ex);
            throw new QTasteException(ex.getMessage(), ex);
        }
    }

    public void startApplication(String name) throws Exception {
        Object[] params = new Object[]{name};
        this.sessionID = (Integer) client.execute("startApplication", params);
        logger.info("sessionID:" + sessionID);
    }

    public void stopApplication() throws Exception {
        Object[] params = new Object[]{sessionID};
        int result = (Integer) client.execute("stopApplication", params);
        logger.info("sessionID:" + sessionID);

    }

    public void selectTreeViewItem(String windowName, String treeviewName, String item) throws Exception {
        logger.info("Selecting the item  " + item + " of the treeview " + treeviewName);
        send("selectTreeViewItem", new String[]{windowName, treeviewName, item});
    }

    public void pressButton(String windowName, String name) throws Exception {
        logger.info("Selecting the button " + name);
        send("pressButton", new String[]{windowName, name});
    }

    public void selectMenu(String windowName, String menu) throws Exception {
        logger.info("Pressing the menu " + menu);
        send("selectMenu", new String[]{windowName, menu});
    }

    public String getText(String windowName, String name) throws Exception {
        Object[] params = new Object[]{sessionID, windowName, name};
        return (String) client.execute("getText", params).toString();
    }

    public void setText(String windowName, String name, String value) {
        try {
            Object[] params = new Object[]{sessionID, windowName, name, value};
            client.execute("setText", params);
        } catch (Exception e) {
            logger.fatal("Cannot setText of window called " + windowName + ":" + name + " with value " + value, e);
        }
    }

    public void listElements() throws Exception {
        Object[] params = new Object[]{sessionID};
        int result = (Integer) client.execute("listElements", params);
    }

    private int send(String function, String[] data) throws Exception {
        Object[] params = new Object[1 + data.length];
        params[0] = sessionID;
        for (int i = 0; i < data.length; i++) {
            params[1 + i] = data[i];
        }
        return (Integer) client.execute(function, params);
    }

    public void execute(String command) throws Exception {
        Object[] params = new Object[]{sessionID, command};
        client.execute("execute", params);
    }

    public void executeWithoutEval(String command) throws Exception {
        Object[] params = new Object[]{sessionID, command};
        client.execute("executeWithoutEval", params);
    }


    @Override
    public void terminate() throws QTasteException {
        if (client != null) {
            client = null;
        }
    }
}
