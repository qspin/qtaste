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

package com.qspin.qtaste.tcom.rlogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.net.bsd.RLoginClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.OS;

/**
 * Rlogin/TCOM enables the creation of rlogin connection (like the one used by the VME as example).
 * A main method is provided to enable the "reboot" of the VME from a QTaste control script.
 * It also enable the formatting of output using log4j package.
 *
 * @author lvboque
 */
public class RLogin {

    private static Logger logger = Log4jLoggerFactory.getLogger(RLogin.class);
    private RLoginClient client;
    private String remoteHost;
    private String localUser;
    private String remoteUser;
    private String terminalType;
    private boolean logOutput;
    private boolean interactive;
    private Logger outputLogger;
    private OutputStreamWriter writer;
    private Thread standardInputReaderThread;
    private Thread outputReaderThread;

    /**
     * Create a new instance of RLogin client
     *
     * @param remoteHost the remote host
     * @param localUser the user account on the local machine that is trying to login to the remote host, or empty if not used
     * @param remoteUser the remoteuser or empty if not used
     * @param terminalType the name of the user's terminal (e.g., "vt100", "network", etc.) or empty if not used
     * @param logOutput enable or disable sending the output to log4j
     * @param interactive enable or disable interactive mode, i.e. sending commands from standard input
     */
    public RLogin(String remoteHost, String localUser, String remoteUser, String terminalType, boolean logOutput, boolean
          interactive) {
        this.remoteHost = remoteHost;
        this.localUser = localUser;
        this.remoteUser = remoteUser;
        this.terminalType = terminalType;
        this.client = new RLoginClient();
        this.logOutput = logOutput;
        this.interactive = interactive;
        this.outputLogger = Log4jLoggerFactory.getLogger(remoteHost + "(rlogin)");

        // initialize the shutdown hook to terminate application properly
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
    }

    /**
     * Create a rlogin connection to the specified remote host.
     *
     * @return true if connected, false otherwise
     */
    public boolean connect() {
        if (client.isConnected()) {
            logger.warn("Already connected");
            return true;
        }
        try {
            logger.info("Connecting to remote host " + remoteHost);
            client.connect(remoteHost);
            client.rlogin(localUser, remoteUser, terminalType);
            writer = new OutputStreamWriter(client.getOutputStream());
            outputReaderThread = new Thread(new OutputReader());
            outputReaderThread.start();
            if (interactive) {
                standardInputReaderThread = new Thread(new StandardInputReader());
                standardInputReaderThread.start();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
            }
            if (client.isConnected()) {
                return true;
            } else {
                logger.fatal("Client has been immediately disconnected from remote host:" + remoteHost);
                return false;
            }
            //outputReaderThread.
        } catch (IOException e) {
            logger.fatal("Could not connect to remote host:" + remoteHost, e);
            return false;
        }
    }

    /**
     * Reboot the remote host by sending the reboot command and check that
     * the remote host is not accessible anymore.
     *
     * @return true if success, false otherwise
     */
    public boolean reboot() {
        if (!sendCommand("reboot")) {
            return false;
        }

        // wait 1 second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

        disconnect();

        // check that remote host is not accessible anymore
        // open a socket without any parameters. It hasn't been binded or connected
        try (Socket socket = new Socket()) {
            // bind to a local ephemeral port
            socket.bind(null);
            socket.connect(new InetSocketAddress(remoteHost, RLoginClient.DEFAULT_PORT), 1);
        } catch (SocketTimeoutException e) {
            logger.info("Rebooted host " + remoteHost + " successfully");
            return true;
        } catch (IOException e) {
            logger.error("Something went wrong while rebooting host:" + remoteHost);
            return false;
        }
        // Expected to get an exception as the remote host should not be reachable anymore
        logger.error(
              "Host " + remoteHost + " did not reboot as expected! Please check that no other rlogin client is connected!");
        return false;
    }

    /**
     * Send the specified command to the remote host
     *
     * @param command command line, without terminating character
     * @return true if success, false otherwise
     */
    public boolean sendCommand(String command) {
        if (writer != null) {
            try {
                logger.info("Sending command " + command + " to remote host " + remoteHost);
                writer.write(command);
                writer.write('\r');
                writer.flush();
            } catch (IOException e) {
                logger.fatal("Error while sending command " + command + " to remote host " + remoteHost, e);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return true if the rlogin client is connected to the specified remote host
     *
     * @return true is connected, false if not connected.
     */
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * Disconnect the rlogin client from the remote host.
     */
    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
            if (standardInputReaderThread != null) {
                standardInputReaderThread = null;
            }
            if (outputReaderThread != null) {
                outputReaderThread.join();
                outputReaderThread = null;
            }
            writer = null;
        } catch (InterruptedException ex) {
        } catch (IOException e) {
            logger.fatal("Error while disconnecting from rlogin session. Host: " + remoteHost, e);
        }
    }

    private class StandardInputReader implements Runnable {

        private BufferedReader reader;

        StandardInputReader() {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!sendCommand(line)) {
                        break;
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    private class OutputReader implements Runnable {

        private BufferedReader reader;

        OutputReader() {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }

        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (logOutput) {
                        outputLogger.info(line);
                    }
                }
            } catch (IOException ex) {
            } finally {
                try {
                    client.disconnect();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * This method is called by the ShutdownHookThread to terminate the application properly.
     * <p>
     * Shuts down the connection and threads.
     */
    protected void shutdown() {
        logger.info("Shutting down RLogin");
        disconnect();
    }

    private static void showUsage() {
        System.err.println(
              "Usage: <command> <remote_host> [-user <user>] [-reboot | -command <command>] [-logOutput] [-wait [seconds]] "
                    + "[-log4jconf <properties_file>]");
        System.exit(1);
    }

    /**
     * Main program.
     * <p>
     * {@code Usage: <command> <remote_host> [-user <user>] [-reboot | -command <command>] [-interactive] [-logOutput] [-wait
     * [seconds]] [-log4jconf <properties_file>]
     * remote_host: remote host
     * -user <user>: remote user
     * -reboot: reboot remote host
     * -command <command>: send command to remote host
     * -interactive: enable interactive mode, i.e. sending commands from standard input, this also enables the wait parameter
     * -logOutput: enable logging of remote host output
     * -wait [seconds]: wait until connection is closed or given seconds
     * -log4jconf <properties_file>: use given file as log4j properties file
     * }
     */
    public static void main(String[] args) {
        // parameters
        String remoteHost;
        String remoteUser = "";
        boolean reboot = false;
        String command = null;
        boolean interactive = false;
        boolean logOutput = false;
        String log4jconf = null;
        boolean wait = false;
        Integer waitTime = null;

        // parse command-line arguments
        if (args.length == 0) {
            showUsage();
        }
        remoteHost = args[0];
        int i = 1;
        while (i < args.length) {
            if (args[i].equals("-reboot")) {
                reboot = true;
                i++;
            } else if (args[i].equals("-user") && (i + 1 < args.length)) {
                remoteUser = args[i + 1];
                i += 2;
            } else if (args[i].equals("-command") && (i + 1 < args.length)) {
                command = args[i + 1];
                i += 2;
            } else if (args[i].equals("-interactive")) {
                interactive = true;
                wait = true;
                i++;
            } else if (args[i].equals("-logOutput")) {
                logOutput = true;
                i++;
            } else if (args[i].equals("-wait")) {
                wait = true;
                if ((i + 1 < args.length)) {
                    // more arguments, check if next argument is a wait argument
                    if (args[i + 1].startsWith("-")) {
                        i++;
                    } else {
                        waitTime = Integer.valueOf(args[i + 1]);
                        i += 2;
                    }
                } else {
                    // no more arguments
                    i++;
                }
            } else if (args[i].equals("-log4jconf") && (i + 1 < args.length)) {
                log4jconf = args[i + 1];
                i += 2;
            } else {
                showUsage();
            }
        }
        if (reboot && (command != null)) {
            showUsage();
        }

        try {
            // Log4j Configuration
            String log4jPropertiesFileName =
                  log4jconf != null ? log4jconf : StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties";
            PropertyConfigurator.configure(log4jPropertiesFileName);

            // get local user name
            String localUser;
            if (OS.getType() == OS.Type.WINDOWS) {
                localUser = System.getenv("USERNAME");
            } else {
                localUser = System.getenv("USER");
            }

            // rlogin connection
            RLogin rlogin = new RLogin(remoteHost, localUser, remoteUser, "", logOutput, interactive);
            if (!rlogin.connect()) {
                System.exit(1);
            }

            // reboot if asked
            if (reboot) {
                boolean success = rlogin.reboot();
                System.exit(success ? 0 : 1);
            }

            // send command if asked
            if (command != null) {
                if (!rlogin.sendCommand(command)) {
                    rlogin.disconnect();
                    System.exit(1);
                }
            }

            // wait if asked
            if (wait) {
                try {
                    if (waitTime != null) {
                        rlogin.outputReaderThread.join(SEC_TO_MS_FACTOR * waitTime);
                    } else {
                        rlogin.outputReaderThread.join();
                    }
                } catch (InterruptedException ex) {
                    logger.error("Wait interrupted");
                }
            }

            rlogin.disconnect();
            System.exit(0);
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }
    }

    /**
     * Shutdown hook thread.
     * Shuts down calls the shutdown method.
     */
    private class ShutdownHookThread extends Thread {

        @Override
        public void run() {
            shutdown();
        }
    }

    /**
     * Factor used to convert second into millisecond.
     */
    private static final long SEC_TO_MS_FACTOR = 1000;
}
