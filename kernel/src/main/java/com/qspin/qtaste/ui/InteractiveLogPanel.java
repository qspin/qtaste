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

package com.qspin.qtaste.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.qspin.qtaste.ui.reporter.TestCaseReportTable;
import com.qspin.qtaste.ui.reporter.TestCaseReporter;
import com.qspin.qtaste.ui.tools.ResourceManager;


/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class InteractiveLogPanel extends JPanel {

   protected DefaultTableModel tcModel, tcTraceEventModel;
   protected JTable tcTraceEventTable; 
   protected TestCaseInteractivePanel tcInteractivePanel;
   private TestCaseReportTable tcTable;

    protected ImageIcon passedImg, failedImg, runningImg, naImg;
    
    public InteractiveLogPanel() {
    	this(null);
    }
    
    public InteractiveLogPanel(TestCaseInteractivePanel tcInteractivePanel) {
        super(new BorderLayout());
        this.tcInteractivePanel = tcInteractivePanel;
    }
    /**
     * @param path The path to the image
     * @param description The description of the image
     * @return ImageIcon, or null if the path was invalid.
     */
    private void initIcons() {
        passedImg= ResourceManager.getInstance().getImageIcon("icons/passed");
        failedImg= ResourceManager.getInstance().getImageIcon("icons/failed");
        runningImg= ResourceManager.getInstance().getImageIcon("icons/running_32");
        naImg= ResourceManager.getInstance().getImageIcon("icons/na");
    }

    public void startTestCaseListener()
    {
        TestCaseReporter.addTestCaseReportTableListener(getTcTable());
    }
    public void stopTestCaseListener()
    {
        TestCaseReporter.removeTestCaseReportTableListener(getTcTable());
    }
    public void init() {
        initIcons();
        this.setName("Test results");
        
        tcTable = new TestCaseReportTable(tcInteractivePanel);
        
        this.add(new JScrollPane(getTcTable().getTable()), BorderLayout.CENTER);
    }

    public TestCaseReportTable getTcTable() {
        return tcTable;
    }
}
