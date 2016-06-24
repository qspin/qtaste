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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.ui.reporter;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.qspin.qtaste.reporter.campaign.CampaignReportManager;
import com.qspin.qtaste.reporter.testresults.TestResult;

/**
 * @author vdubois
 */
public class TestCaseReporter {

    private static TestCaseReporter instance = null;
    static private ArrayList<TestCaseReportTable> reportTableList = new ArrayList<>();

    synchronized public static TestCaseReporter getInstance() {
        if (instance == null) {
            instance = new TestCaseReporter();
        }
        return instance;
    }

    static public void addTestCaseReportTableListener(TestCaseReportTable table) {
        reportTableList.add(table);
    }

    static public void removeTestCaseReportTableListener(TestCaseReportTable table) {
        if (reportTableList.contains(table)) {
            reportTableList.remove(table);
        }
    }

    public void putEntry(final TestResult tr) {
        SwingUtilities.invokeLater(() -> {
            String reportName = CampaignReportManager.getInstance().getReportName();
            for (TestCaseReportTable reportTable : reportTableList) {
                reportTable.putEntry(tr, reportName);
            }
        });

    }
}
