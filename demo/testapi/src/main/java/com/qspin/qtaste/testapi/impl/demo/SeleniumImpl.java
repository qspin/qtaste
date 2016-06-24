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

import com.thoughtworks.selenium.DefaultSelenium;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.tcom.WebBrowser;
import com.qspin.qtaste.testapi.api.Selenium;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 * Implementation of the Selenium Test API
 *
 * @author Laurent Vanbboquestal
 */
public class SeleniumImpl extends WebBrowser implements Selenium {
    private String instanceId;
    private String host;
    private int port;
    private String URL;
    static Logger logger = Logger.getLogger(TestAPIImpl.class);

    public SeleniumImpl(String instanceId) {
        super();
        TestBedConfiguration config = TestBedConfiguration.getInstance();
        this.instanceId = instanceId;
        this.host = config.getMIString(instanceId, "Selenium", "host");
        this.port = config.getMIInt(instanceId, "Selenium", "port");
        this.URL = config.getMIString(instanceId, "Selenium", "url");
        logger.debug("Target url : " + this.URL);
    }

    @Override
    public void openBrowser(String browserName) {
        setSelenium(new DefaultSelenium(host, port, browserName, URL));
        this.start();
    }

    @Override
    public void closeBrowser() {
        if (selenium != null) {
            stop();
            setSelenium(null);
        }
    }

    @Override
    public void initialize() throws QTasteException {
    }

    @Override
    public void terminate() throws QTasteException {
    }

    @Override
    public String getInstanceId() {
        return this.instanceId;
    }

}

