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

package com.qspin.qtaste.ui.reporter;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.SwingUtilities;

import com.qspin.qtaste.ui.TestCaseResultsPane;


/**
 *
 * @author vdubois
 */
public class TestCampaignReporter  {
    
    private static TestCampaignReporter instance = null;
    static private ArrayList<TestCaseResultsPane> reportListeners = new ArrayList<TestCaseResultsPane> ();
    
    /**
     * Get an instance of the CampaignReportManager. 
     * @return The TestResultsReportManager.
     */
    synchronized public static TestCampaignReporter getInstance() {
        if (instance == null) {
            instance = new TestCampaignReporter();
        }
        return instance;
    }

    

   static public void addTestCampaignListener(TestCaseResultsPane panel) {
            reportListeners.add(panel);
    }
    
    static public void removeTestCaseReportTableListener(TestCaseResultsPane panel) {
        if (reportListeners.contains(panel))
            reportListeners.remove(panel);
    }
   
    public void refresh() {
       SwingUtilities.invokeLater(new Runnable() {
          public void run() {
             for (TestCaseResultsPane panel : reportListeners) {
                panel.refreshCampaign();
             }
          }
       });
    }
    public void startReport(Date timeStamp, final String name) {
       SwingUtilities.invokeLater(new Runnable() {
          public void run() {
             for (TestCaseResultsPane panel : reportListeners) {
                panel.startCampaign(name);
             }
          }
       });
    }

    public void stopReport() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (TestCaseResultsPane panel : reportListeners) {
                    panel.stopCampaign();
                }
            }
        });
    }
}