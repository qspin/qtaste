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

package com.qspin.qtaste.util.versioncontrol;

/**
 * VersionControlInterface is responsible for providing version information facilities.
 *
 * @author lvboque
 */
public interface VersionControlInterface {
    /**
     * Returns the version of the SUT located at the path.
     *
     * @param path The path to the SUT.
     * @return the version of the SUT.
     */
    String getSUTVersion(String path);

    /**
     * Returns the version of the testapi located at the path.
     *
     * @param path The path to the testapi.
     * @return the version of the testapi.
     */
    String getTestApiVersion(String path);
}
