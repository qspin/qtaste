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

import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.testapi.api.Windows;
import com.qspin.qtaste.testsuite.QTasteException;
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
    private String name;

    public WindowsImpl() {
    }

    public void startApplication(String name) throws Exception {
        this.name = name;
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{name};
        this.sessionID = (Integer) client.execute("startApplication", params);
        logger.info("sessionID:" + sessionID);
    }

    public void stopApplication() throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
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
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{sessionID, windowName, name};
        return (String) client.execute("getText", params).toString();
    }

    public void setText(String windowName, String name, String value) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://127.0.0.1:8080"));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{sessionID, windowName, name, value};
            client.execute("setText", params);
        } catch (Exception e) {
            logger.fatal("Cannot setText of window called " + windowName + ":" + name + " with value " + value, e);
        }
    }

    public void listElements() throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{sessionID};
        int result = (Integer) client.execute("listElements", params);
    }

    private int send(String function, String[] data) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[1 + data.length];
        params[0] = sessionID;
        for (int i = 0; i < data.length; i++) {
            params[1 + i] = data[i];
        }
        return (Integer) client.execute(function, params);
    }

    public void execute(String command) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{sessionID, command};
        client.execute("execute", params);
    }

    public void executeWithoutEval(String command) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{sessionID, command};
        client.execute("executeWithoutEval", params);
    }

    @Override
    public void initialize() throws QTasteException {
    }

    @Override
    public void terminate() throws QTasteException {
    }
}
