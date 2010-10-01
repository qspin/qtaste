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

package com.qspin.qtaste.kernel.campaign;

import java.util.ArrayList;

/**
 * A campaign run is an execution of several testsuites on a specified testbed
 * @author lvboque
 */
public class CampaignRun {
    String testbed;
    ArrayList<TestSuiteParams> testsuites;
    
    public CampaignRun() {
        testsuites = new ArrayList<TestSuiteParams>();
    }
    
    public String getTestbed() {
        return testbed;
    }
    
    public ArrayList<TestSuiteParams> getTestsuites() {
        return testsuites;
    }
}
