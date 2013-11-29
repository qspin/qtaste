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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketNode;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * Log4j TCP server.
 * 
 * @author David Ergo
 */
public class Log4jServer extends Thread {

    private static final Logger LOGGER = Log4jLoggerFactory.getLogger(Log4jServer.class);
    private static Log4jServer mInstance;
    private ServerSocket mServerSocket;
    private int port;
    private boolean mIsTerminating = false;

    private Log4jServer() {
        // get port
        TestEngineConfiguration config = TestEngineConfiguration.getInstance();
        port = 4446;
        try {
            port = config.getInt("log4j_server.port");
        } catch (Exception e) {
            LOGGER.info("No or invalid log4j_server.port engine property, using default port " + port);
        }
    }

    public static Log4jServer getInstance() {
        if (mInstance == null) {
            mInstance = new Log4jServer();
        }
        return mInstance;
    }

    @Override
    public void run() {
        mIsTerminating = false;
        
        try {
            LOGGER.info("Starting log4j server on port " + port);
            mServerSocket = new ServerSocket(port);
            while (true) {
                Socket socket = mServerSocket.accept();
                LOGGER.info("Log4j server accepting connection from client at " + socket.getInetAddress());
                new Thread(new SocketNode(socket, LogManager.getLoggerRepository())).start();
            }
        } catch (Exception e) {
            if (!mIsTerminating) {
                LOGGER.error(e);
            }
        }
    }

    public void shutdown() {
        mIsTerminating = true;
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
            }
        }
        mServerSocket = null;
    }
}
