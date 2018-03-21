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

package com.qspin.qtaste.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.ServerSocketReceiver;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;


public class LogbackReceiver extends ServerSocketReceiver {

    private static final Logger LOGGER = Log4jLoggerFactory.getLogger(LogbackReceiver.class);
    private static LogbackReceiver mInstance;
    private int port;

    private LogbackReceiver() {
        super();

        // get port
        TestEngineConfiguration config = TestEngineConfiguration.getInstance();
        port = 4447;
        try {
            port = config.getInt("logback_server.port");
        } catch (Exception e) {
            LOGGER.info("No or invalid logback_server.port engine property, using default port " + port);
        }
        setPort(port);
        setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    }

    public static LogbackReceiver getInstance() {
        if (mInstance == null) {
            mInstance = new LogbackReceiver();
        }
        return mInstance;
    }
}
