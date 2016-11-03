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

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.campaign.TestSuiteParams;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.testsuite.impl.MetaTestSuite;
import com.qspin.qtaste.ui.SortableJTable;
import com.qspin.qtaste.ui.TestCaseInteractivePanel;
import com.qspin.qtaste.ui.TestCasePane;
import com.qspin.qtaste.ui.testcampaign.CampaignWriter;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.tools.TableSorter;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestCaseReportTable {

    protected static Logger logger = Log4jLoggerFactory.getLogger(TestCaseReportTable.class);
    protected Map<TestResult, Integer> testCases = new HashMap<>();
    protected TestCasePane tcPane;
    protected DefaultTableModel tcModel;
    protected DefaultTableModel tcReasonModel;
    protected JTable tcTable;
    protected JTextArea stackTrace;
    protected TestCaseInteractivePanel tcInteractivePanel;
    public static final int STATUS = 0;
    public static final int TEST_CASE = 1;
    public static final int DETAILS = 2;
    public static final int RESULT = 3;
    public static final int TESTBED = 4;
    public static final int EXEC_TIME = 5;
    public static final int TC = 6;
    protected ImageIcon passedImg, failedImg, runningImg, snapShotImg, naImg;
    private boolean interactive;
    private boolean userScrollPosition = false;
    private Timer timer = new Timer("TestCaseReportTable Thread"); // timer for updating elapsed time
    private final static String INTERACTIVE_TABLE_LAYOUT_PROPERTY = "interactive_test_case_report_table_layout";
    private final static String EXECUTION_TABLE_LAYOUT_PROPERTY = "execution_test_case_report_table_layout";
    private String runName;

    private void initIcons() {
        ResourceManager resourceManager = ResourceManager.getInstance();
        passedImg = resourceManager.getImageIcon("icons/passed");
        passedImg.setDescription("Passed");
        failedImg = resourceManager.getImageIcon("icons/failed");
        failedImg.setDescription(("Failed"));
        runningImg = resourceManager.getImageIcon("icons/running");
        runningImg.setDescription(("Running"));
        naImg = resourceManager.getImageIcon("icons/na");
        naImg.setDescription(("Test in error"));
        /*snapShotImg = resourceManager.getImageIcon("icons/snapshot");
        snapShotImg.setDescription(("Failed"));*/
    }

    public JTable getTable() {
        return tcTable;
    }

    public TestCaseReportTable(String runName, DefaultTableModel tcReasonModel, JTextArea stackTrace, TestCasePane tcPane) {
        this.interactive = (tcPane == null);
        this.tcPane = tcPane;
        this.tcReasonModel = tcReasonModel;
        this.stackTrace = stackTrace;
        this.runName = runName;
        init();
        initIcons();
    }

    public TestCaseReportTable(TestCaseInteractivePanel tcInteractivePanel) {
        this("interactive", null, null, null);
        this.tcInteractivePanel = tcInteractivePanel;
    }

    public void displayTableForInteractiveMode() {
        tcTable.getColumnModel().getColumn(RESULT).setPreferredWidth(150);
        tcTable.getColumnModel().getColumn(RESULT).setMinWidth(100);
        tcTable.getColumnModel().getColumn(RESULT).setMaxWidth(500);
    }

    public void displayTableForExecutionMode() {
        tcTable.getColumnModel().getColumn(RESULT).setPreferredWidth(0);
        tcTable.getColumnModel().getColumn(RESULT).setMinWidth(0);
        tcTable.getColumnModel().getColumn(RESULT).setMaxWidth(0);
    }

    private void init() {
        final String tableLayoutProperty = interactive ? INTERACTIVE_TABLE_LAYOUT_PROPERTY : EXECUTION_TABLE_LAYOUT_PROPERTY;
        final String statusColumnProperty = tableLayoutProperty + ".status";
        final String testCaseColumnProperty = tableLayoutProperty + ".test_case";
        final String detailsColumnProperty = tableLayoutProperty + ".details";
        final String testbedColumnProperty = tableLayoutProperty + ".testbed";
        final String resultColumnProperty = tableLayoutProperty + ".result";

        tcModel = new DefaultTableModel(new Object[] {"Status", "Test Case", "Details", "Result", "Testbed", "Time", "."}, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                Class<?> dataType = super.getColumnClass(columnIndex);
                if (columnIndex == STATUS) {
                    dataType = Icon.class;
                }
                return dataType;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        tcTable = new SortableJTable(new TableSorter(tcModel)) {

            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (colIndex < 0) {
                    return null;
                }
                return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
            }
        };
        tcTable.setColumnSelectionAllowed(false);
        tcTable.getTableHeader().setReorderingAllowed(false);

        int tcWidth = interactive ? 360 : 480;
        int tcStatusWidth = 40;
        int tcTestbedWidth = 100;
        int tcDetailsWidth = 360;
        int tcResultWidth = 150;
        GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
        List<?> list = guiConfiguration.configurationsAt(tableLayoutProperty);
        if (!list.isEmpty()) {
            try {
                tcWidth = guiConfiguration.getInt(testCaseColumnProperty);
            } catch (NoSuchElementException ex) {
                guiConfiguration.setProperty(testCaseColumnProperty, tcWidth);
            }
            try {
                tcStatusWidth = guiConfiguration.getInt(statusColumnProperty);
            } catch (NoSuchElementException ex) {
                guiConfiguration.setProperty(statusColumnProperty, tcStatusWidth);
            }
            try {
                tcDetailsWidth = guiConfiguration.getInt(detailsColumnProperty);
            } catch (NoSuchElementException ex) {
                guiConfiguration.setProperty(detailsColumnProperty, tcDetailsWidth);
            }
            try {
                tcTestbedWidth = guiConfiguration.getInt(testbedColumnProperty);
            } catch (NoSuchElementException ex) {
                guiConfiguration.setProperty(testbedColumnProperty, tcTestbedWidth);
            }
            if (interactive) {
                try {
                    tcResultWidth = guiConfiguration.getInt(resultColumnProperty);
                } catch (NoSuchElementException ex) {
                    guiConfiguration.setProperty(resultColumnProperty, tcResultWidth);
                }
            }
        } else {
            tcWidth = interactive ? 360 : 480;

            guiConfiguration.setProperty(testCaseColumnProperty, tcWidth);
            guiConfiguration.setProperty(statusColumnProperty, tcStatusWidth);
            guiConfiguration.setProperty(detailsColumnProperty, tcDetailsWidth);
            guiConfiguration.setProperty(testbedColumnProperty, tcTestbedWidth);
            if (interactive) {
                guiConfiguration.setProperty(resultColumnProperty, tcResultWidth);
            }
            try {
                guiConfiguration.save();
            } catch (ConfigurationException ex) {
                logger.error("Error while saving GUI configuration: " + ex.getMessage());
            }
        }

        TableColumnModel tcTableColumnModel = tcTable.getColumnModel();
        tcTableColumnModel.getColumn(TEST_CASE).setPreferredWidth(tcWidth);
        tcTableColumnModel.getColumn(STATUS).setPreferredWidth(tcStatusWidth);
        tcTableColumnModel.getColumn(STATUS).setMaxWidth(40);
        tcTableColumnModel.getColumn(DETAILS).setPreferredWidth(tcDetailsWidth);
        tcTableColumnModel.getColumn(TESTBED).setPreferredWidth(tcTestbedWidth);
        tcTableColumnModel.getColumn(EXEC_TIME).setPreferredWidth(70);
        tcTableColumnModel.getColumn(EXEC_TIME).setMinWidth(70);
        tcTableColumnModel.getColumn(EXEC_TIME).setMaxWidth(70);
        tcTableColumnModel.removeColumn(tcTableColumnModel.getColumn(TC));
        if (!interactive) {
            tcTable.getSelectionModel().addListSelectionListener(new TCResultsSelectionListeners());
        }
        tcTable.setName("tcTable");
        tcTableColumnModel.addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
                try {
                    // save the current layout
                    int tcStatusWidth = tcTable.getColumnModel().getColumn(STATUS).getWidth();
                    int tcWidth = tcTable.getColumnModel().getColumn(TEST_CASE).getWidth();
                    int tcDetailsWidth = tcTable.getColumnModel().getColumn(DETAILS).getWidth();
                    int tcResultWidth = tcTable.getColumnModel().getColumn(RESULT).getWidth();
                    int tcTestbedWidth = tcTable.getColumnModel().getColumn(TESTBED).getWidth();
                    // save it into the settings
                    GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
                    guiConfiguration.setProperty(statusColumnProperty, tcStatusWidth);
                    guiConfiguration.setProperty(testCaseColumnProperty, tcWidth);
                    guiConfiguration.setProperty(detailsColumnProperty, tcDetailsWidth);
                    guiConfiguration.setProperty(testbedColumnProperty, tcTestbedWidth);
                    if (interactive) {
                        guiConfiguration.setProperty(resultColumnProperty, tcResultWidth);
                    }
                    guiConfiguration.save();
                } catch (ConfigurationException ex) {
                    logger.error("Error while saving GUI configuration: " + ex.getMessage());
                }
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });

        try {
            tcTable.setDefaultRenderer(Class.forName("java.lang.Object"), new TableCellRenderer());
        } catch (ClassNotFoundException ex) {
        }

        if (interactive) {
            displayTableForInteractiveMode();
        } else {
            displayTableForExecutionMode();
        }

        tcTable.addMouseListener(new TableMouseListener());

        // use timer for updating elapsed time every seconds
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateRunningTestCaseElapsedTime();
            }
        }, 1000, 1000);
    }

    private String convertToUniqueTc(TestResult tr) {
        String result = tr.getId();
        if (!tr.getComment().isEmpty()) {
            result += " (" + tr.getComment() + ")";
        }
        return result;
    }

    public void putEntry(TestResult tr, String campaignReportName) {
        boolean isInteractiveCommand = tr.getTestCaseDirectory().endsWith("QTaste_interactive");
        String testReportName = TestResultsReportManager.getInstance().getReportName();
        boolean isManualSUTStartOrStop = testReportName != null && testReportName.startsWith("Manual SUT");
        if (interactive) {
            if (!isInteractiveCommand && !isManualSUTStartOrStop) {
                return;
            }
        } else {
            if (isInteractiveCommand) {
                return;
            }
            if (campaignReportName != null && !campaignReportName.equals(runName)) {
                return;
            }
            if (campaignReportName == null && !runName.equals("Run1")) {
                return;
            }
        }
        if (!testCases.containsKey(tr)) {
            Object[] cols = new Object[7];
            cols[TEST_CASE] = convertToUniqueTc(tr);
            cols[DETAILS] = tr.getExtraResultDetails();
            if (tr.getReturnValue() != null) {
                cols[RESULT] = tr.getReturnValue();
            }
            cols[STATUS] = getImage(tr.getStatus());
            if (tr.getStatus() != TestResult.Status.RUNNING) {
                cols[EXEC_TIME] = tr.getFormattedElapsedTime(false);
            }

            TestBedConfiguration testbed = TestBedConfiguration.getInstance();
            cols[TESTBED] = testbed.getFile().getName().replace("." + StaticConfiguration.CAMPAIGN_FILE_EXTENSION, "");

            cols[TC] = tr;
            //tcModel.addRow(cols);
            Integer rowNum = tcModel.getRowCount();
            testCases.put(tr, rowNum);
            int currentScrollBarMax;

            JScrollPane scrollPane = (JScrollPane) tcTable.getParent().getParent();
            JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
            if (scrollbar != null) {
                if (scrollbar.getMouseListeners().length == 1) {
                    scrollPane.addMouseWheelListener(e -> userScrollPosition = true);
                    scrollbar.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            userScrollPosition = true;
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            userScrollPosition = true;
                        }
                    });
                }
                currentScrollBarMax = scrollbar.getMaximum() - scrollbar.getSize().height - tcTable.getRowHeight();
                tcModel.addRow(cols);
                if (!userScrollPosition) {
                    tcTable.scrollRectToVisible(tcTable.getCellRect(tcModel.getRowCount() - 1, 0, true));
                } else if (scrollbar.getValue() >= currentScrollBarMax) {
                    tcTable.scrollRectToVisible(tcTable.getCellRect(tcModel.getRowCount() - 1, 0, true));
                    userScrollPosition = false;
                } else {
                    System.out.println(
                          "Scrollbar pos=" + scrollbar.getValue() + "; max=" + currentScrollBarMax + "height=" + scrollbar
                                .getSize().height);
                }
            } else {
                tcModel.addRow(cols);
            }
        } else {
            updateTestCaseRow(tr);
        }

    }

    protected void updateRunningTestCaseElapsedTime() {
        int lastRow = tcModel.getRowCount() - 1;
        if (lastRow >= 0) {
            TestResult tr = (TestResult) tcModel.getValueAt(lastRow, TC);
            if (tr.getStatus() == TestResult.Status.RUNNING) {
                tcModel.setValueAt(tr.getFormattedElapsedTime(false), lastRow, EXEC_TIME);
            }
        }
    }

    protected void updateTestCaseRow(TestResult tr) {

        int rowNum = testCases.get(tr);
        if (rowNum == -1) {
            // means that testcases has not been emptied ..

            // TO DO
            return;
        }
        tcModel.setValueAt(convertToUniqueTc(tr), rowNum, TEST_CASE);
        tcModel.setValueAt(tr.getFormattedElapsedTime(false), rowNum, EXEC_TIME);
        tcModel.setValueAt(tr.getExtraResultDetails(), rowNum, DETAILS);
        if (tr.getReturnValue() != null) {
            tcModel.setValueAt(tr.getReturnValue(), rowNum, RESULT);
        }

        TestResult.Status testCaseStatus = tr.getStatus();
        ImageIcon statusImg = getImage(testCaseStatus);
        tcModel.setValueAt(statusImg, rowNum, STATUS);

        TestBedConfiguration testbed = TestBedConfiguration.getInstance();
        tcModel.setValueAt(testbed.getFile().getName().replace("." + StaticConfiguration.CAMPAIGN_FILE_EXTENSION, ""), rowNum,
              TESTBED);

        if ((testCaseStatus == TestResult.Status.FAIL) || ((testCaseStatus == TestResult.Status.NOT_AVAILABLE))) {
            int selectedRow = tcTable.getSelectedRow();
            // update the failedReason if the current selected testcase is
            //   the one to be updated
            if (selectedRow == rowNum) {
                if (tcReasonModel != null) {
                    // clear
                    while (tcReasonModel.getRowCount() > 0) {
                        tcReasonModel.removeRow(0);
                    }
                    ArrayList<StackTraceElement> stack = tr.getStack();
                    for (StackTraceElement stackElement : stack) {
                        if (stackElement.getFileName().equals("embedded_jython")) {
                            continue;
                        }
                        Object[] row = new Object[6];
                        row[TCResultsSelectionListeners.LINE] = stackElement.getLineNumber();
                        row[TCResultsSelectionListeners.FILE_NAME] = stackElement.getFileName();
                        String methodName = stackElement.getMethodName();
                        if (methodName.equals("f$0")) {
                            methodName = "main";
                        } else {
                            // remove $i suffix from method name
                            int dollarIndex = methodName.indexOf("$");
                            if (dollarIndex > 0) {
                                methodName = methodName.substring(0, dollarIndex);
                            }
                        }
                        row[TCResultsSelectionListeners.FUNCTION_ID] = methodName;
                        //row[TCResultsSelectionListeners.ERR_MSG] = tr.getExtraResultDetails();
                        row[TCResultsSelectionListeners.OBJECT] = stackElement;
                        //row[TCResultsSelectionListeners.ROW] = tr.getTestData() != null ? tr.getTestData().getRowId() : null;;
                        tcReasonModel.addRow(row);
                    }
                }
                if (stackTrace != null) {
                    stackTrace.setText(tr.getStackTrace());
                }
            }
        }
    }

    protected ImageIcon getImage(TestResult.Status testCaseStatus) {
        switch (testCaseStatus) {
            case SUCCESS:
                return passedImg;
            case FAIL:
                return failedImg;
            case NOT_AVAILABLE:
                return naImg;
            default:
                return runningImg;
        }
    }

    /**
     * @param path The path to the image
     * @param description The description of the image
     * @return ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String path, String description) {
        //URL imgURL = this.getClass().getResource(path);
        ImageIcon icon = null;
        if (path != null) {
            icon = new ImageIcon(path, description);
        }
        return icon;
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

    public void resetTable() {
        tcModel.setRowCount(0);
        testCases.clear();
        clearReasonTable();
    }

    public void clearReasonTable() {
        if (tcReasonModel != null) {
            tcReasonModel.setRowCount(0);
        }
        if (stackTrace != null) {
            stackTrace.setText("");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          Event Listeners                                                  //
    ///////////////////////////////////////////////////////////////////////////////
    public class TCResultsSelectionListeners implements ListSelectionListener {

        //public static final int ROW = 0;
        public static final int LINE = 0;
        public static final int FILE_NAME = 1;
        public static final int FUNCTION_ID = 2;
        //public static final int ERR_MSG = 4;
        public static final int OBJECT = 3;

        /**
         * Called whenever the value of the selection changes.
         *
         * @param e the event that characterizes the change.
         */
        public void valueChanged(ListSelectionEvent e) {
            clearReasonTable();
            int selectedRow = tcTable.getSelectedRow();
            if (selectedRow > -1) {
                TestResult tr = (TestResult) tcTable.getModel().getValueAt(selectedRow, TestCaseReportTable.TC);
                if (tr.getStatus() == TestResult.Status.NOT_AVAILABLE || tr.getStatus() == TestResult.Status.FAIL) {
                    if (tcReasonModel != null) {
                        ArrayList<StackTraceElement> stack = tr.getStack();
                        for (StackTraceElement stackElement : stack) {
                            if (stackElement.getFileName().equals("embedded_jython")) {
                                continue;
                            }
                            Object[] row = new Object[6];
                            row[LINE] = stackElement.getLineNumber();
                            row[FILE_NAME] = stackElement.getFileName();
                            String methodName = stackElement.getMethodName();
                            if (methodName.equals("f$0")) {
                                methodName = "main";
                            } else {
                                // remove $i suffix from method name
                                int dollarIndex = methodName.indexOf("$");
                                if (dollarIndex > 0) {
                                    methodName = methodName.substring(0, dollarIndex);
                                }
                            }
                            row[TCResultsSelectionListeners.FUNCTION_ID] = methodName;

                            //row[ERR_MSG] = tr.getExtraResultDetails();
                            row[OBJECT] = stackElement;
                            //row[ROW] = tr.getTestData() != null ? tr.getTestData().getRowId() : null;;
                            tcReasonModel.addRow(row);
                        }
                    }
                    if (stackTrace != null) {
                        stackTrace.setText(tr.getStackTrace());
                    }
                }
                tcPane.getResultsLog4jPanel().selectAndScrollToTestCase(tr);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////
    public class TableMouseListener extends MouseAdapter {

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // force selection of clicked row if not selected
                int clickedRow = tcTable.rowAtPoint(e.getPoint());
                if (!tcTable.isRowSelected(clickedRow)) {
                    tcTable.clearSelection();
                    tcTable.addRowSelectionInterval(clickedRow, clickedRow);
                }

                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new CopyDetailsToClipboardAction());
                if (interactive) {
                    menu.add(new ClearListAction());
                    menu.add(new ClearAllListAction());
                    menu.add(new ReExecuteCommandsAction());
                } else {
                    menu.add(new ReExecuteTestsAction());
                    menu.add(new GenerateTestCampaignAction());
                }
                /*
                if (table.getName().equals("tcTable")) {
                    menu.addSeparator();
                	menu.add(new SaveAsAction(table));
                }
                */
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), tcTable);
                menu.show(tcTable, pt.x, pt.y);
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
    }

    class ClearListAction extends AbstractAction {

        public ClearListAction() {
            super("Clear");
        }

        public void actionPerformed(ActionEvent e) {
            // get the selected table lines

            int selNum = tcTable.getSelectedRowCount();
            while (selNum > 0) {
                int[] selectedRowsId = tcTable.getSelectedRows();
                testCases.remove(tcModel.getValueAt(selectedRowsId[0], TC));
                tcModel.removeRow(selectedRowsId[0]);
                selNum = tcTable.getSelectedRowCount();
            }
        }

        @Override
        public boolean isEnabled() {
            return tcTable.getSelectedRowCount() > 0;
        }
    }

    class ClearAllListAction extends AbstractAction {

        public ClearAllListAction() {
            super("Clear all");
        }

        public void actionPerformed(ActionEvent e) {
            tcModel.setRowCount(0);
            testCases.clear();
        }

        public boolean isEnabled() {
            return true;
        }
    }

    class CopyDetailsToClipboardAction extends AbstractAction {

        public CopyDetailsToClipboardAction() {
            super("Copy details to clipboard");
        }

        public void actionPerformed(ActionEvent e) {
            // get the selected table line
            int selectedRow = tcTable.getSelectedRow();
            String message = (String) tcTable.getValueAt(selectedRow, DETAILS);
            if (message == null) {
                message = "";
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(message);
            clipboard.setContents(data, data);
        }

        @Override
        public boolean isEnabled() {
            return tcTable.getSelectedRowCount() == 1;
        }
    }

    class GenerateTestCampaignAction extends AbstractAction {

        public GenerateTestCampaignAction() {
            super("Generate test campaign from failed tests");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // get the current testbed
            TestBedConfiguration testbed = TestBedConfiguration.getInstance();
            // get the list of failed tests
            CampaignWriter campaign = new CampaignWriter();
            for (int i = 0; i < tcTable.getRowCount(); i++) {
                TestResult tr = (TestResult) tcModel.getValueAt(i, TC);
                if (tr.getStatus() == TestResult.Status.FAIL) {
                    campaign.addCampaign(
                          testbed.getFile().getName().replace("." + StaticConfiguration.CAMPAIGN_FILE_EXTENSION, ""),
                          tr.getTestCaseDirectory(), tr.getTestData().getRowId());
                }
            }
            // ask for name of test campaign
            String newCampaign = JOptionPane.showInputDialog(null, "Campaign name", "Campaign name:",
                  JOptionPane.QUESTION_MESSAGE);
            if (newCampaign != null) {
                // add the new campaign in the list
                if (tcPane.getMainPanel().getTestCampaignPanel().addTestCampaign(newCampaign) >= 0) {
                    String fileName = StaticConfiguration.CAMPAIGN_DIRECTORY + File.separator + newCampaign + "."
                          + StaticConfiguration.CAMPAIGN_FILE_EXTENSION;
                    campaign.save(fileName, newCampaign);
                    JOptionPane.showMessageDialog(null, "Campaign file has been saved in " + fileName + ".", "Information",
                          JOptionPane.INFORMATION_MESSAGE);
                }
            }

        }

    }

    class ReExecuteTestsAction extends AbstractAction {

        public ReExecuteTestsAction() {
            super("Re-execute test(s)");
        }

        public void actionPerformed(ActionEvent e) {
            int[] selectedRowsId = tcTable.getSelectedRows();
            final LinkedHashMap<String, SortedSet<Integer>> testScripts = new LinkedHashMap<>();
            // group test cases by test script
            for (int rowId : selectedRowsId) {
                TestResult tr = (TestResult) tcModel.getValueAt(rowId, TC);
                SortedSet<Integer> dataRows;
                String testCaseDirectory = tr.getTestCaseDirectory();
                if (testScripts.containsKey(testCaseDirectory)) {
                    dataRows = testScripts.get(testCaseDirectory);
                } else {
                    dataRows = new TreeSet<>();
                    testScripts.put(testCaseDirectory, dataRows);
                }
                dataRows.add(tr.getTestData().getRowId());
            }
            // build meta test suite
            List<TestSuiteParams> testSuitesParams = new LinkedList<>();
            for (Map.Entry<String, SortedSet<Integer>> testCases : testScripts.entrySet()) {
                TestSuiteParams testSuiteParams = new TestSuiteParams();
                testSuiteParams.setDirectory(testCases.getKey());
                testSuiteParams.setDataRows(testCases.getValue());
                testSuitesParams.add(testSuiteParams);
            }
            final TestSuite testSuite = MetaTestSuite.createMetaTestSuite("Test(s) re-execution", testSuitesParams);
            if (testSuite != null) {
                Thread reExecuteTestsThread = new Thread() {
                    @Override
                    public void run() {
                        tcPane.runTestSuite(testSuite, false);
                    }
                };
                reExecuteTestsThread.start();
            }
        }

        @Override
        public boolean isEnabled() {
            if (tcPane.isExecuting) {
                return false;
            } else {
                boolean enabled = false;
                int[] selectedRowsId = tcTable.getSelectedRows();
                for (int rowId : selectedRowsId) {
                    TestResult tr = (TestResult) tcModel.getValueAt(rowId, TC);
                    if (tr.getTestData() != null) {
                        enabled = true;
                    }
                }
                return enabled;
            }
        }
    }

    class ReExecuteCommandsAction extends AbstractAction {

        public ReExecuteCommandsAction() {
            super("Re-execute command(s)");
        }

        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = tcTable.getSelectedRows();
            final String[] commands = new String[selectedRows.length];
            for (int i = 0; i < selectedRows.length; i++) {
                String tc = (String) tcModel.getValueAt(selectedRows[i], TestCaseReportTable.TEST_CASE);
                commands[i] = tc.substring(0, tc.lastIndexOf(" - "));
            }
            new Thread() {
                @Override
                public void run() {
                    for (final String command : commands) {
                        tcInteractivePanel.executeCommand(command);
                    }
                }
            }.start();
        }

        public boolean isEnabled() {
            int[] selectedRows = tcTable.getSelectedRows();
            for (int selectedRow : selectedRows) {
                String tc = (String) tcModel.getValueAt(selectedRow, TestCaseReportTable.TEST_CASE);
                if (tc.endsWith(" SUT")) {
                    // disable if contains "Re(start) SUT" or "Stop SUT"
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Table cell renderer.
     */
    static class TableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int
              row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // set text alignment
            if ((column == STATUS) || (column == EXEC_TIME) || (column == TESTBED)) {
                cell.setHorizontalAlignment(CENTER);
            } else {
                cell.setHorizontalAlignment(LEFT);
            }

            return cell;
        }
    }
}
