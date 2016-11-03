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

package com.qspin.qtaste.tcom.db.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tcom.db.JDBCClient;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * JDBCClientImpl is a JDBC connection to a database
 *
 * @author lvboque
 */
public class JDBCClientImpl implements JDBCClient {

    private static Logger logger = Log4jLoggerFactory.getLogger(JDBCClientImpl.class);
    private Connection con;
    private boolean connected;
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;

    public final static char QUERY_ENDS = ';';

    /**
     * Create a JDBCClientImpl instance
     *
     * @param jdbcDriver the jdbcDriver to use for the connection
     * @param jdbcURL the jdbcURL to use to locate the database
     * @param user the username to use for the connection
     * @param password the password to use for the connection
     */
    public JDBCClientImpl(String jdbcDriver, String jdbcURL, String user, String password) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcURL = jdbcURL;
        this.connected = false;
        this.con = null;
        this.user = user;
        this.password = password;
    }

    /**
     * Open a JDBC connection to the database
     *
     * @throws java.sql.SQLException If a SQL error occurs
     * @throws java.lang.ClassNotFoundException If the driver class does not exists
     */
    public void open() throws SQLException, ClassNotFoundException {
        logger.info("Using database driver: " + jdbcDriver);
        Class.forName(jdbcDriver);
        logger.info("Using database.url: " + jdbcURL);
        // connect login/pass
        con = DriverManager.getConnection(jdbcURL, user, password);
        // Exception will be thrown if something went wrong
        connected = true;
    }

    private boolean isComment(String line) {
        return (line != null) && (line.length() > 0) && (line.charAt(0) == '#');
    }

    private boolean checkStatementEnds(String s) {
        return (s.indexOf(QUERY_ENDS) != -1);
    }

    public void executeSQLScript(String scriptFile)
          throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            if (!connected) {
                open();
            }
            Statement stmt = con.createStatement();

            String line;
            StringBuilder query = new StringBuilder();
            boolean queryEnds;

            while ((line = reader.readLine()) != null) {
                if (isComment(line)) {
                    continue;
                }
                queryEnds = checkStatementEnds(line);
                query.append(line);
                if (queryEnds) {
                    stmt.addBatch(query.toString());
                    query.setLength(0);
                }
            }
            stmt.executeBatch();
        }
    }

    /**
     * Execute the specified query
     * If the SQL connection if not open, it will be opened automatically
     *
     * @param query The specified query
     * @return the ResultSet object. The returned ResultSet has to be closed manually.
     * @throws java.sql.SQLException If a SQL error occurs
     * @throws java.lang.ClassNotFoundException If the driver class does not exists
     */
    public ResultSet executeQuery(String query) throws SQLException, ClassNotFoundException {
        if (!connected) {
            open();
        }
        Statement stmt = con.createStatement();

        return stmt.executeQuery(query);
    }

    /**
     * Execute the specified SQL command
     * If the SQL connection if not open, it will be opened automatically
     *
     * @param query The specified query
     * @return the ResultSet object. The returned ResultSet has to be closed manually.
     * @throws java.sql.SQLException If a SQL error occurs
     * @throws java.lang.ClassNotFoundException If the driver class does not exists
     */
    public boolean executeCommand(String query) throws SQLException, ClassNotFoundException {
        if (!connected) {
            open();
        }
        Statement stmt = con.createStatement();

        return stmt.execute(query);
    }

    /**
     * Close the JDBC conncetion to the database
     *
     * @throws java.sql.SQLException If a SQL error occurs
     */
    public void close() throws SQLException {
        if (connected) {
            con.close();
            connected = false;
        }
    }

    public Connection getConnection() {
        return this.con;
    }
}
