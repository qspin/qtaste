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

package com.qspin.qtaste.tcom.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lvboque
 */
public interface JDBCClient {

    /**
     * Open a JDBC connection to the database
     *
     * @throws java.sql.SQLException If a SQL error occurs
     * @throws java.lang.ClassNotFoundException If the driver class does not exists
     */
    void open() throws SQLException, ClassNotFoundException;

    /**
     * Execute the specified query
     * If the SQL connection if not open, it will be opened automatically
     *
     * @param query The specified query
     * @return the ResultSet object. The returned ResultSet has to be closed manually.
     * @throws java.sql.SQLException If a SQL error occurs
     * @throws java.lang.ClassNotFoundException If the driver class does not exists
     */
    ResultSet executeQuery(String query) throws SQLException, ClassNotFoundException;

    boolean executeCommand(String query) throws SQLException, ClassNotFoundException;

    void executeSQLScript(String scriptFile) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException;

    /**
     * Close the JDBC conncetion to the database
     *
     * @throws java.sql.SQLException If a SQL error occurs
     */
    void close() throws SQLException;

    Connection getConnection();
}
