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

package com.qspin.qtaste.util.versioncontrol.impl;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.util.versioncontrol.VersionControlInterface;

/**
 * DefaultVersionControl is the implementation by default when no SCM tool is configured.
 *
 * @author lvboque
 */
public class DefaultVersionControl implements VersionControlInterface {

    /**
     * Returns the version of the SUT used in GUI screens and Test Reports
     *
     * @param path The path to the SUT. (Not used in this context)
     * @return the version number
     */
    public String getSUTVersion(String path) {
        return TestBedConfiguration.getSUTVersion();
    }

    /**
     * Returns the version of the test API. This information is only used in Test reports. By default, the version of the testapi
     * is the same as the sutVersion.
     *
     * @param path The path to the testapi. (Not used in this context)
     * @return the version number
     */
    public String getTestApiVersion(String path) {
        return TestBedConfiguration.getSUTVersion();
    }
}
