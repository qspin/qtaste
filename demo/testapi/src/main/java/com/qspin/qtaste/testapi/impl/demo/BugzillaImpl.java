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

import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.tcom.db.JDBCClient;
import com.qspin.qtaste.tcom.db.impl.JDBCClientImpl;
import com.qspin.qtaste.testapi.api.Bugzilla;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Implementation of the Bugzilla Test API
 *
 * @author Laurent Vanbboquestal
 */
public class BugzillaImpl implements Bugzilla {

    private String instanceId;
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;
    static Logger logger = Logger.getLogger(TestAPIImpl.class);

    public BugzillaImpl(String instanceId) {
        TestBedConfiguration config = TestBedConfiguration.getInstance();
        this.instanceId = instanceId;
        this.jdbcDriver = config.getMIString(instanceId, "Bugzilla", "jdbcDriver");
        this.jdbcURL = config.getMIString(instanceId, "Bugzilla", "jdbcURL");
        this.user = config.getMIString(instanceId, "Bugzilla", "dbuser");
        this.password = config.getMIString(instanceId, "Bugzilla", "dbpassword");
    }

    @Override
    public void checkDatabase(int defectId, String shortDescription, String longDescription, String assignee) throws Exception {
        JDBCClient jdbcClient = new JDBCClientImpl(jdbcDriver, jdbcURL, user, password);

        jdbcClient.open();
        ResultSet result = jdbcClient.executeQuery("select profiles.login_name, bugs.short_desc, longdescs.thetext " +
              "from profiles, bugs, longdescs " +
              "where bugs.bug_id = " + defectId + " and profiles.userid = bugs.assigned_to and longdescs.bug_id = " + defectId);

        while (result.next()) {
            String shortDescDB = result.getString("bugs.short_desc");
            logger.info("shortDesc:" + shortDescDB);
            String theTextDB = result.getString("longdescs.thetext");
            logger.info("thetext:" + theTextDB);
            String loginNameDB = result.getString("profiles.login_name");
            logger.info("login_name:" + loginNameDB);
            if (!shortDescDB.equals(shortDescription)) {
                throw new QTasteTestFailException(
                      "Expected to get '" + shortDescription + "' as short description but got " + shortDescDB);
            }
            if (!theTextDB.equals(longDescription)) {
                throw new QTasteTestFailException(
                      "Expected to get '" + longDescription + "' as long description but got " + theTextDB);
            }
            if (!loginNameDB.equals(assignee)) {
                throw new QTasteTestFailException("Expected to get '" + assignee + "' as assignee but got " + loginNameDB);
            }
        }
        jdbcClient.close();
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
