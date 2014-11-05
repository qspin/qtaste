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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.qspin.qtaste.reporter.campaign.CampaignReportManager;
import com.qspin.qtaste.testsuite.TestScript;
import com.qspin.qtaste.ui.jedit.NonWrappingTextPane;
import com.qspin.qtaste.ui.log4j.Log4jPanel;
import com.qspin.qtaste.ui.log4j.TextAreaAppender;
import com.qspin.qtaste.ui.reporter.TestCampaignReporter;
import com.qspin.qtaste.ui.reporter.TestCaseReportTable;
import com.qspin.qtaste.ui.reporter.TestCaseReportTable.TCResultsSelectionListeners;
import com.qspin.qtaste.ui.reporter.TestCaseReporter;

@SuppressWarnings("serial")
public class TestCaseResultsPane extends JSplitPane {

    //private static Logger logger = Log4jLoggerFactory.getLogger(TestCaseResultsPane.class);
    private static final long serialVersionUID = 1L;
    protected Log4jPanel tcLogsPane = new Log4jPanel();
    protected ImageIcon passedImg,  failedImg,  runningImg,  snapShotImg,  naImg;
    private List<TestScript> results = new ArrayList<TestScript>();
    private TestCasePane mTestCasePane;
    private JTabbedPane runTabbedPane = new  JTabbedPane(JTabbedPane.TOP);
    private int runIndex=0;

    public TestCaseResultsPane(TestCasePane testCasePane) {
        super(JSplitPane.VERTICAL_SPLIT);
        mTestCasePane = testCasePane;
        setResizeWeight(0.3);
        setDividerSize(4);
        initResultsTable();
    }

    @Override
    protected void finalize() {
    }

    public void init() {
        setName("Test results");
    }

    protected void setTestCaseTree(TestCaseTree tcTree) {
        //this.tcTree = tcTree;
    }

    public void stopExecution() {
        //stopExecution = true;
    }

    public void proceedExecution() {
        //stopExecution = false;
    }

    public void setDebug(boolean debug) {
        //this.debug = debug;
    }

    protected void setStepNextTag(boolean stepNextTag) {
        //this.stepNextTag = stepNextTag;
    }

    private String convertObjectToToolTip(Object obj) {
        String tip = null;
        if (obj != null) {
            if (obj instanceof ImageIcon) { //Status column
                tip = ((ImageIcon) obj).getDescription();
            } else {
                tip = obj.toString();
            }
        }
        return tip;

    }

    public String getDefaultRun() {
        return "Run" + runIndex;
    }

    public Log4jPanel getLog4jPanel() {
        return tcLogsPane;
    }

    private void initResultsTable() {
        runTabbedPane.addMouseListener(new MouseAdapter() {
            private void evaluatePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    popupMenu.add(new TabRemoveAction());
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), TestCaseResultsPane.this);
                    popupMenu.show(TestCaseResultsPane.this, pt.x, pt.y);

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                evaluatePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                evaluatePopup(e);
            }
        });


        runIndex++;
        this.addRunTab("Run" + runIndex);
        setResizeWeight(0.65);
        setTopComponent(runTabbedPane);
        setBottomComponent(tcLogsPane);

        TextAreaAppender.addTextArea(tcLogsPane);
        TestCampaignReporter.addTestCampaignListener(this);
    }

    public void resetTables() {
        //
        results.clear();
        //stackTrace.setText("");
        int tabIndex = this.getTabIndex("Run1");
        if (tabIndex==-1) return;

        Object reportObject = runTabbedPane.getClientProperty("TestCaseReportTable_" + runTabbedPane.getTitleAt(tabIndex));
        if (reportObject instanceof TestCaseReportTable) {
            TestCaseReportTable reportTable = (TestCaseReportTable)reportObject;
            reportTable.resetTable();
        }

        tcLogsPane.clearLogs();
    }


    /**
     * Table cell renderer.
     */
    static class TableReasonCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // set text alignment
            if (column == 0) {
                cell.setHorizontalAlignment(RIGHT);
            } else {
                cell.setHorizontalAlignment(LEFT);
            }

            return cell;
        }
    }

    private int getTabIndex(String tabName) {
        for (int i=0; i< runTabbedPane.getTabCount() ; i++) {
            if (runTabbedPane.getTitleAt(i).equals(tabName))
                return i;
        }
        return -1;
    }

    public void setRunTab(String tabName) {
       runTabbedPane.setSelectedIndex(getTabIndex(tabName));
    }

    public void addRunTab(String tabName) {
        // if already added, clear the content of the table
        int tabIndex = this.getTabIndex(tabName);
        if (tabIndex!=-1) {
            // clear the result
            Object reportObject = runTabbedPane.getClientProperty("TestCaseReportTable_" + tabName);
            if (reportObject instanceof TestCaseReportTable) {
                TestCaseReportTable reportTable = (TestCaseReportTable)reportObject;
                reportTable.resetTable();
                // listen to events
                TestCaseReporter.addTestCaseReportTableListener(reportTable);
                this.runTabbedPane.setSelectedIndex(tabIndex);
            }
            return;
        }
        // create a new tab
        JTextArea nstackTrace = new JTextArea();
        DefaultTableModel ntcReasonModel = new DefaultTableModel(new Object[]{"Line", "File", "Method", "."}, 0) {

            @Override
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };

        TestCaseReportTable ntcTable = new TestCaseReportTable(tabName, ntcReasonModel, nstackTrace, mTestCasePane);
        TestCaseReporter.addTestCaseReportTableListener(ntcTable);

        JTable ntcReasonTable = new JTable(ntcReasonModel) {

            @Override
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        try {
	                        int realIndex =
	                                columnModel.getColumn(index).getModelIndex();
	                        return getColumnName(realIndex);
                        } catch (Exception ex){
                        	return null;
                        }
                    }
                };
            }
        };


        ntcReasonTable.getColumn("Line").setPreferredWidth(40);
        ntcReasonTable.getColumn("Line").setMaxWidth(40);
        ntcReasonTable.getColumn("File").setPreferredWidth(500);
        ntcReasonTable.getColumn("File").setMaxWidth(750);
        ntcReasonTable.getColumn("Method").setPreferredWidth(250);
        ntcReasonTable.getColumn("Method").setMaxWidth(500);
        ntcReasonTable.removeColumn(ntcReasonTable.getColumn("."));
        ntcReasonTable.setColumnSelectionAllowed(false);

        //tcReasonTable.getColumn("Failed Reason").setCellRenderer(new MultiLineCellRenderer());
        try {
            ntcReasonTable.setDefaultRenderer(Class.forName("java.lang.Object"), new TableReasonCellRenderer());
        } catch (ClassNotFoundException ex) {
        }

        ntcReasonTable.setRowSelectionAllowed(false);
        ntcReasonTable.setName("ntcReasonTable");
        ntcReasonTable.addMouseListener(new TableMouseListener(ntcReasonTable));

        nstackTrace.setRows(5);

        // add runTabbed panel
        JSplitPane resultMainpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel reasonMainPanel = new JPanel(new BorderLayout());
        JScrollPane reasonScrollPane = new JScrollPane(ntcReasonTable);
        reasonScrollPane.setPreferredSize(new Dimension(100, 100));
        JSplitPane reasonPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        reasonPanel.setResizeWeight(0.5);
        reasonPanel.setDividerSize(4);
        reasonPanel.setTopComponent(reasonScrollPane);

        JPanel stackTracePanel = new JPanel(new BorderLayout());
        JLabel stackTraceLabel = new JLabel("Stack trace:");
        stackTracePanel.add(stackTraceLabel, BorderLayout.NORTH);
        stackTracePanel.add(nstackTrace);

        reasonPanel.setBottomComponent(new JScrollPane(stackTracePanel));
        reasonMainPanel.add(reasonPanel, BorderLayout.CENTER);


        resultMainpanel.setResizeWeight(0.5);

        resultMainpanel.setTopComponent(new JScrollPane(ntcTable.getTable()));
        resultMainpanel.setBottomComponent(reasonMainPanel);

        runTabbedPane.putClientProperty("TestCaseReportTable_" + tabName, ntcTable);
        runTabbedPane.addTab(tabName, resultMainpanel);
        this.runTabbedPane.setSelectedIndex(this.runTabbedPane.getTabCount() -1);
    }

    public void refreshCampaign() {
    }

    public void startCampaign(String name) {
        addRunTab(name);
        tcLogsPane.clearLogs();
    }

    public void stopCampaign() {

     // get the current Campaign
        String report = CampaignReportManager.getInstance().getReportName();
        if (report==null) return;
        int tabIndex = this.getTabIndex(report);
        if (tabIndex!=-1) {
            // clear the result
            Object reportObject = runTabbedPane.getClientProperty("TestCaseReportTable_" + report);
            if (reportObject instanceof TestCaseReportTable) {
                TestCaseReportTable reportTable = (TestCaseReportTable)reportObject;
                TestCaseReporter.removeTestCaseReportTableListener(reportTable);
            }
            return;
        }
    }

    public String getCurrentRunName() {
        int selectedIndex = runTabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            return runTabbedPane.getTitleAt(selectedIndex);
        } else {
            return "";
        }
    }

    public class TabRemoveAction extends AbstractAction {

        public TabRemoveAction() {
            super("Close");
        }
        @Override
        public boolean isEnabled() {
            if (runTabbedPane.getSelectedIndex()>0)
                return true;
            else
                return false;
        }

        public void actionPerformed(ActionEvent e) {
            if (runTabbedPane.getSelectedIndex()>0)
            {
                int tabIndex = runTabbedPane.getSelectedIndex();
                runTabbedPane.remove(tabIndex);
            }
        }
    }

    public class TableMouseListener extends MouseAdapter {
        protected JTable table;

        public TableMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                // go to the test case source file editor at the error file
                if (table.getName().equals("ntcReasonTable")) {
                    // get the TR
                    // TO DO needs to open the correct document
                    // at this time, the current testscript file is opened
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    if (rowIndex != -1) {
                        String fileName = (String) table.getModel().getValueAt(rowIndex, TCResultsSelectionListeners.FILE_NAME);
                        int gotoLine = (Integer) table.getModel().getValueAt(rowIndex, TCResultsSelectionListeners.LINE);

                        // load the file into the tabbedPaned
                        File f = new File(fileName);
                        NonWrappingTextPane textPane = null;
                        if (f.exists() && f.canRead()) {
                            textPane = mTestCasePane.loadTestCaseSource(f, true, false);
                            //mTestCasepane.setTestCaseSourceURL(fileName);

                            //TestResult tr = (TestResult)tcReasonTable.getModel().getValueAt(rowIndex, TCResultsSelectionListeners.OBJECT);
                            //int gotoLine = tr.getFailedLineNumber();
                            mTestCasePane.tabbedPane.setSelectedIndex(TestCasePane.SOURCE_INDEX);
                            // now go to the given line
                            textPane.selectLine(gotoLine);
                        }
                    }
                }
            }
        }
    }
}
