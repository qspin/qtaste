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

package com.qspin.qtaste.ui.log4j;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.ui.tools.SpringUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class Log4jPanel extends JPanel {

    protected DefaultTableModel m_LogModel;
    protected JTable m_LogTable;
    protected static Logger logger = Log4jLoggerFactory.getLogger(Log4jPanel.class);
    private static final int LOG_TIME = 0;
    private static final int LOG_LEVEL = 1;
    private static final int LOG_SOURCE = 2;
    private static final int LOG_LOGGER = 3;
    private static final int LOG_STEP = 4;
    private static final int LOG_MESSAGE = 5;
    private static final int MAX_ROWS = 25000; // number of LOG4J rows displayed
    private static final Pattern BEGIN_STEP_PATTERN = Pattern.compile("^Begin of step ([\\w.]+) \\(([\\w.]+)\\)");
    protected Map<String, String> m_FilterMethod = new HashMap<String, String>();
    private ArrayList<JCheckBox> m_LevelFilterCheckBoxes, m_SourceFilterCheckBoxes, m_MessageFilterCheckBoxes;
    private TableRowSorter<TableModel> m_LogSorterTable;
    private Log4jRowFilter m_LogFilter;
    private JPanel m_LevelAndMessageFilterPanel, m_SourceFilterPanel;
    private JScrollPane m_SourceScrollPane;
    private boolean m_UserScrollPosition = false;
    private Stack<String> m_currentStepStack = new Stack<String>();
    private Collection<String> m_applications = new HashSet<String>();
    //private JTreeTable m_TreeLogTable;
    //private Log4JTableModel m_TreeLogModel;
    public Log4jPanel() {
        super(new GridBagLayout());
        synchronized (m_applications) {
            m_applications.add("QTaste");
        }
        genUI();
    }

    public DefaultTableModel getLogModel() {
        return m_LogModel;
    }

    public void clearLogs() {
        m_LogModel.setRowCount(0);
    }

    public void selectAndScrollToTestCase(TestResult tr) {
    	if (tr==null) return;
    	if (tr.getTestData()==null) return;
        int index = -1;
        String startTime = new Time(tr.getStartDate().getTime()).toString();
        String startMessage;
        if (tr.getRetryCount() > 0) {
            startMessage = "Retrying test script: " + tr.getName() + " (row " + tr.getTestData().getRowId() + ") after SUT restart";
        } else {
            startMessage = "Executing test script: " + tr.getName() + " (row " + tr.getTestData().getRowId() + ")";
        }

        // search for last log line with corresponding time
        for (int i = m_LogTable.getRowCount()-1; i >= 0; i--) {
            if (m_LogTable.getValueAt(i, LOG_TIME).equals(startTime)) {
                index = i;
                break;
            }
        }

        // search for start message
        for (int i = index; i >= 0; i--) {
            if (m_LogTable.getValueAt(i, LOG_MESSAGE).equals(startMessage)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // select row
            m_LogTable.setRowSelectionInterval(index, index);

            // scroll to row
            m_UserScrollPosition = true;
            Rectangle visibleRect = m_LogTable.getCellRect(index, 0, true);
            visibleRect.height = m_LogTable.getVisibleRect().height;
            m_LogTable.scrollRectToVisible(visibleRect);
        } else {
            m_LogTable.clearSelection();
        }
    }

    private JCheckBox addFilterLogCheckBox(String type, String name, String methodName, boolean defaultValue) {
        m_FilterMethod.put(type, methodName);
        JCheckBox checkBox = new JCheckBox(name);
        checkBox.setSelected(defaultValue);
        checkBox.setName(name);
        checkBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Rectangle visibleRect = m_LogTable.getVisibleRect();

                // get top fully visible row in view
                int topRowView = m_LogTable.rowAtPoint(new Point(0, (int) visibleRect.getMinY()));
                if ((topRowView != -1) && !visibleRect.contains(m_LogTable.getCellRect(topRowView, 0, true)) && (topRowView < (m_LogTable.getRowCount() - 1))) {
                    // use next fully visible row
                    topRowView++;
                }

                // get bottom fully visible row in view
                int bottomRowView = m_LogTable.rowAtPoint(new Point(0, (int) visibleRect.getMaxY()));
                if (bottomRowView == -1) {
                    // use last row
                    bottomRowView = m_LogTable.getRowCount() - 1;
                } else {
                    if (!visibleRect.contains(m_LogTable.getCellRect(bottomRowView, 0, true)) && (bottomRowView > 0)) {
                        // use previous fully visible row
                        bottomRowView--;
                    }
                }

                // compute difference of top/bottom rows in view
                int diffRowsView = bottomRowView - topRowView;

                // convert top row in model
                int topRowModel = topRowView == -1 ? -1 : m_LogTable.convertRowIndexToModel(topRowView);

                // get eventual selected row in view and model
                int selectedRowView = m_LogTable.getSelectedRow();
                int selectedRowModel = -1;
                if ((selectedRowView != -1) && (selectedRowView >= topRowView) && (selectedRowView <= bottomRowView)) {
                    selectedRowModel = m_LogTable.convertRowIndexToModel(selectedRowView);
                }

                // filter logs based on updated filters
                m_LogSorterTable.sort();

                if (topRowView != -1) {
                    // get top row in updated view
                    while (((topRowView = m_LogTable.convertRowIndexToView(topRowModel)) == -1) && (topRowModel < (m_LogModel.getRowCount() - 1))) {
                        topRowModel++;
                    }
                }

                // compute bottom row in updated view
                bottomRowView = Math.min(m_LogTable.getRowCount() - 1, topRowView + diffRowsView);

                // make top and bottom rows visible
                if (topRowView != -1) {
                    m_LogTable.scrollRectToVisible(m_LogTable.getCellRect(topRowView, 0, true));
                }
                if (bottomRowView != -1) {
                    m_LogTable.scrollRectToVisible(m_LogTable.getCellRect(bottomRowView, 0, true));
                }

                // if there was a selected row and it was visible, and it's not filtered out
                // then make it visible
                if (selectedRowModel != -1) {
                    selectedRowView = m_LogTable.convertRowIndexToView(selectedRowModel);
                    if (selectedRowView != -1) {
                        m_LogTable.scrollRectToVisible(m_LogTable.getCellRect(selectedRowView, 0, true));
                    }
                }
            }
        });
        if (type.equals("Level")) {
            m_LevelFilterCheckBoxes.add(checkBox);
            m_LevelAndMessageFilterPanel.add(checkBox);
        } else if (type.equals("TestCaseOrVerb")) {
            m_MessageFilterCheckBoxes.add(checkBox);
            m_LevelAndMessageFilterPanel.add(checkBox);
        } else if (type.equals("Source")) {
            m_SourceFilterCheckBoxes.add(checkBox);
            m_SourceFilterPanel.add(checkBox);
        }
        return checkBox;
    }

    private void genUI() {
        try {
            m_LevelAndMessageFilterPanel = new JPanel(new SpringLayout());
            m_SourceFilterPanel = new JPanel(new SpringLayout());
            m_LevelFilterCheckBoxes = new ArrayList<JCheckBox>();
            m_MessageFilterCheckBoxes = new ArrayList<JCheckBox>();
            m_SourceFilterCheckBoxes = new ArrayList<JCheckBox>();

            m_LevelAndMessageFilterPanel.add(new JLabel("Level: "));
            addFilterLogCheckBox("Level", "Trace", "doLogLevelFilter", false);
            addFilterLogCheckBox("Level", "Debug", "doLogLevelFilter", false);
            addFilterLogCheckBox("Level", "Info", "doLogLevelFilter", true);
            addFilterLogCheckBox("Level", "Warn", "doLogLevelFilter", true);
            addFilterLogCheckBox("Level", "Error", "doLogLevelFilter", true);
            addFilterLogCheckBox("Level", "Fatal", "doLogLevelFilter", true);
            final JCheckBox testCaseOrVerbCheckBox = addFilterLogCheckBox("TestCaseOrVerb", "Test case or verb", "filterMessageTestCaseOrVerb", true);
            final JCheckBox qtasteCheckBox = addFilterLogCheckBox("Source", "QTaste", "filterMessageSource", true);

            // when selecting "Invoking" force "QTaste" to be selected
            testCaseOrVerbCheckBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (testCaseOrVerbCheckBox.isSelected()) {
                        qtasteCheckBox.setSelected(true);
                    }
                }
            });

            // when unselecting "QTaste" force "Invoking" to be unselected
            qtasteCheckBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (!qtasteCheckBox.isSelected()) {
                        testCaseOrVerbCheckBox.setSelected(false);
                    }
                }
            });

            m_LogModel = new DefaultTableModel(new Object[]{"Time", "Level", "Source", "@", "Step", "Message"}, 0) {

                @Override
                public boolean isCellEditable(int rowIndex, int mColIndex) {
                    return false;
                }
            };


            m_LogTable = new JTable(m_LogModel) {

                @Override
                public String getToolTipText(MouseEvent e) {
                    Point p = e.getPoint();
                    int rowIndex = rowAtPoint(p);
                    int colIndex = columnAtPoint(p);
    				if (colIndex < 0) {
    					return null;
    				}
    				return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
                }

                //Implement table header tool tips.
                @Override
                protected JTableHeader createDefaultTableHeader() {
                    return new JTableHeader(columnModel) {
                        public String getToolTipText(MouseEvent e) {
                            java.awt.Point p = e.getPoint();
                            int index = columnModel.getColumnIndexAtX(p.x);
                            if (index < 0) {
                            	return null;
                            }
                            int realIndex = columnModel.getColumn(index).getModelIndex();
                            if (realIndex == LOG_LOGGER) {
                               return "Logger name";
                            } else {
                               return m_LogTable.getModel().getColumnName(realIndex);
                            }
                        }
                    };
                }
            };
            m_LogTable.setColumnSelectionAllowed(false);
            TableColumnModel logTableColumnModel = m_LogTable.getColumnModel();
            TableColumn logTimeColumn = logTableColumnModel.getColumn(LOG_TIME);
            TableColumn logLevelColumn = logTableColumnModel.getColumn(LOG_LEVEL);
            TableColumn logSourceColumn = logTableColumnModel.getColumn(LOG_SOURCE);
            TableColumn logLoggerColumn = logTableColumnModel.getColumn(LOG_LOGGER);
            TableColumn logStepColumn = logTableColumnModel.getColumn(LOG_STEP);
            logTimeColumn.setPreferredWidth(60);
            logTimeColumn.setMaxWidth(200);
            logLevelColumn.setPreferredWidth(60);
            logLevelColumn.setMaxWidth(200);
            logSourceColumn.setPreferredWidth(80);
            logSourceColumn.setMaxWidth(200);
            logLoggerColumn.setPreferredWidth(10);
            logLoggerColumn.setMaxWidth(600);
            logStepColumn.setPreferredWidth(160);
            logStepColumn.setMaxWidth(240);

            try {
                m_LogTable.setDefaultRenderer(Class.forName("java.lang.Object"), new QTasteTableCellRenderer());
            } catch (ClassNotFoundException ex) {
            }
            m_LogFilter = new Log4jRowFilter();


            m_LogSorterTable = new TableRowSorter<TableModel>(m_LogModel);
            m_LogSorterTable.setRowFilter(m_LogFilter);
            // disable row sorting
            for (int i = 0; i < m_LogTable.getColumnCount(); i++) {
                m_LogSorterTable.setSortable(i, false);
            }

            m_LogTable.setName("LOG_TABLE");
            m_LogTable.setRowSorter(m_LogSorterTable);
            m_LogTable.addMouseListener(new TableMouseListener());

            GridBagConstraints constraint = new GridBagConstraints();
            constraint.anchor = GridBagConstraints.NORTH;
            constraint.fill = GridBagConstraints.HORIZONTAL;
            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.weightx = 0.0;
            constraint.weighty = 0.0;
            add(m_LevelAndMessageFilterPanel, constraint);

            constraint.ipady = 10;
            constraint.insets = new Insets(0,0,4,0);
            m_SourceScrollPane = new JScrollPane(m_SourceFilterPanel,
            		  ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            		  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(new JLabel("Source: "));
			add(m_SourceScrollPane, constraint);

            constraint.ipady = 0;
            constraint.insets = new Insets(0,0,0,0);
            constraint.fill = GridBagConstraints.BOTH;
            constraint.gridheight = GridBagConstraints.REMAINDER;
            constraint.weightx = 1.0;
            constraint.weighty = 1.0;
            add(new JScrollPane(m_LogTable), constraint);

            SpringUtilities.makeCompactGrid(m_LevelAndMessageFilterPanel, 1, m_LevelFilterCheckBoxes.size() + m_SourceFilterCheckBoxes.size() + 1, 5, 5, 2, 0);
            SpringUtilities.makeCompactGrid(m_SourceFilterPanel, 1, m_SourceFilterCheckBoxes.size(), 5, -5, 2, 2);

        /*
        m_TreeLogModel = new Log4JTableModel();
        m_TreeLogTable  = new JTreeTable(m_TreeLogModel);
        this.add(new JScrollPane(m_TreeLogTable));
        this.add(m_FilterPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = (JScrollPane) m_TreeLogTable.getParent().getParent();
        SpringUtilities.makeCompactGrid(m_FilterPanel, 1, m_FilterCheckBoxes.size(), 5, 5, 2, 2);
         */
        } catch (SecurityException ex) {
            logger.error(ex);
        }

        // disable tooltip auto-dismiss
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    public void appendLog(LoggingEvent loggingEvent) {
        Object[] cols = new Object[m_LogTable.getColumnCount()];
        Time t = new Time(loggingEvent.getTimeStamp());
        cols[LOG_TIME] = t.toString();
        cols[LOG_LEVEL] = loggingEvent.getLevel().toString();
        // TO DO : improve the way to detect source log
        String application = loggingEvent.getProperty("application");
        if (application != null) {
            synchronized (m_applications) {
                if (!m_applications.contains(application)) {
                    m_applications.add(application);
                    addFilterLogCheckBox("Source", application, "filterMessageSource", true);
                    SpringUtilities.makeCompactGrid(m_SourceFilterPanel, 1, m_SourceFilterCheckBoxes.size(), 5, -5, 2, 2);
                    m_SourceScrollPane.validate();
                }
            }
            cols[LOG_SOURCE] = application;
        } else {
            cols[LOG_SOURCE] = "QTaste";
        }
        cols[LOG_LOGGER] = loggingEvent.getLoggerName();
        Object message = loggingEvent.getMessage();
        String messageString = null;
        if (message != null) {
            messageString = message.toString();
            String[] throwableStrRep = loggingEvent.getThrowableStrRep();
            if (throwableStrRep != null) {
                for (int i = 0; i < throwableStrRep.length; i++) {
                    if (i == 0 && throwableStrRep[i].equals(messageString)) {
                        continue;
                    }
                    messageString += "\n " + throwableStrRep[i];
                }
            }
            cols[LOG_MESSAGE] = messageString;
        } else {
            cols[LOG_MESSAGE] = null;
        }
        if (messageString != null) {
            Matcher matcher = BEGIN_STEP_PATTERN.matcher(messageString);
            if (matcher.matches()) {
                m_currentStepStack.push(matcher.group(1) + " - " + matcher.group(2));
            }
        }
        cols[LOG_STEP] = m_currentStepStack.empty() ? null : m_currentStepStack.peek();
        if ((messageString != null) && messageString.startsWith("End of step ")) {
            m_currentStepStack.pop();
        }

        long currentScrollBarMax = 0;
        //int lastRow = m_LogModel.getRowCount();
        // check if the user has change the scrolling position
        JScrollPane scrollPane = (JScrollPane) m_LogTable.getParent().getParent();
        JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
        if (scrollbar != null) {
            if (scrollbar.getMouseListeners().length == 1) {
                scrollPane.addMouseWheelListener(new MouseWheelListener() {

                    public void mouseWheelMoved(MouseWheelEvent e) {
                        m_UserScrollPosition = true;
                    }
                });
                scrollbar.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        m_UserScrollPosition = true;
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        m_UserScrollPosition = true;
                    }
                });
            }
            currentScrollBarMax = scrollbar.getMaximum() - scrollbar.getSize().height - m_LogTable.getRowHeight();
            m_LogModel.addRow(cols);
            // check if the first row must be remove (memory handling issue)
            if (m_LogModel.getRowCount() > MAX_ROWS) {
                boolean wasFirstRowVisible = m_LogSorterTable.convertRowIndexToView(0) != -1;
                m_LogModel.removeRow(0);
                if (wasFirstRowVisible && m_UserScrollPosition) {
                    Rectangle visibleRect = m_LogTable.getVisibleRect();
                    if (!visibleRect.contains(0, 0)) {
                        visibleRect.y -= m_LogTable.getScrollableUnitIncrement(visibleRect, SwingConstants.VERTICAL, -1);
                        m_LogTable.scrollRectToVisible(visibleRect);
                    }
                }
            }
            if (!m_UserScrollPosition) {
                m_LogTable.scrollRectToVisible(
                        m_LogTable.getCellRect(m_LogTable.getRowCount() - 1, 0, true));
                return;
            } else if (scrollbar.getValue() >= currentScrollBarMax) {
                m_LogTable.scrollRectToVisible(
                        m_LogTable.getCellRect(m_LogTable.getRowCount() - 1, 0, true));
                m_UserScrollPosition = false;
                return;
            }
        } else {
            this.m_LogModel.addRow(cols);
        /*
        if ((m_LogTable.getSelectedRow()== -1) || (m_LogTable.getSelectedRow()== lastRow))
        {
        //if (m_LogTable.getSelectedRow()!=-1)
        // System.out.println("lastRow=" +lastRow +";selectedRow="+ m_LogTable.getSelectedRow());
        m_LogTable.scrollRectToVisible(
        m_LogTable.getCellRect(m_LogModel.getRowCount(),0,true));
        lastRow = m_LogModel.getRowCount();
        if (m_LogTable.getSelectedRow()!= -1)
        m_LogTable.getSelectionModel().setSelectionInterval(lastRow,lastRow);
         */
        //}
        }
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

    public class Log4jRowFilter extends RowFilter<Object, Object> {

        /**
         * Returns false if message must not be filtered out because it starts with the name of a checkbox,
         *         true otherwise.
         * @param level
         * @param source
         * @param message
         * @return false if message must not be filtered out because it starts with the name of a checkbox,
         *         true otherwise
         */
        public boolean filterMessageStartWith(String level, String source, String message) {
            for (JCheckBox checkBox : m_MessageFilterCheckBoxes) {
                if (checkBox.isSelected() && message.startsWith(checkBox.getName())) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns true if message must be filtered out because the checkbox corresponding to its source is unselected,
         *         false otherwise.
         * @param level
         * @param source
         * @param message
         * @return true if message must be filtered out because the checkbox corresponding to its source is unselected,
         *         false otherwise
         */
        public boolean filterMessageSource(String level, String source, String message) {
            for (JCheckBox checkBox : m_SourceFilterCheckBoxes) {
                if (!checkBox.isSelected() && source.equals(checkBox.getName())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns true if message must be filtered out because the checkbox corresponding to its level is unselected,
         *         false otherwise.
         * @param level
         * @param source
         * @param message
         * @return true if message must be filtered out because the checkbox corresponding to its level is unselected,
         *         false otherwise
         */
        public boolean doLogLevelFilter(String level, String source, String message) {
            for (JCheckBox checkBox : m_LevelFilterCheckBoxes) {
                if (!checkBox.isSelected() && checkBox.getName().equalsIgnoreCase(level)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns false if message must not be filtered out because it is the start of of a test case or a verb invoke.
         * @param level
         * @param source
         * @param message
         * @return false if message must not be filtered out because it is the start of of a test case or a verb invoke
         */
        public boolean filterMessageTestCaseOrVerb(String level, String source, String message) {
            for (JCheckBox checkBox : m_MessageFilterCheckBoxes) {
                if (checkBox.isSelected() && source.equals("QTaste") && (message.startsWith("Invoking ") || message.startsWith("Executing test script: ") || message.startsWith("Retrying test script: "))) {
                    return false;
                }
            }
            return true;
        }

		@Override
        public boolean include(Entry entry) {
            String type = ((String) entry.getValue(1)).toLowerCase();
            String message = ((String) entry.getValue(LOG_MESSAGE));
            String source = ((String) entry.getValue(LOG_SOURCE));
            if (m_FilterMethod.containsKey("Level")) {
                try {
                    // get level method
                    String methodName = m_FilterMethod.get("Level");
                    Method method = this.getClass().getMethod(methodName, new Class[]{String.class, String.class, String.class});
                    Object returnValue = method.invoke(this, type, source, message);
                    boolean filterLevel = Boolean.parseBoolean(returnValue.toString());

                    // get "TestCaseOrVerb" method
                    methodName = m_FilterMethod.get("TestCaseOrVerb");
                    method = this.getClass().getMethod(methodName, new Class[]{String.class, String.class, String.class});
                    returnValue = method.invoke(this, type, source, message);
                    boolean filterTestCaseOrVerb = Boolean.parseBoolean(returnValue.toString());

                    methodName = m_FilterMethod.get("Source");
                    method = this.getClass().getMethod(methodName, new Class[]{String.class, String.class, String.class});
                    returnValue = method.invoke(this, type, source, message);
                    boolean filterSource = Boolean.parseBoolean(returnValue.toString());

                    return !(filterSource || (filterTestCaseOrVerb && filterLevel));
                } catch (IllegalAccessException ex) {
                    logger.equals(ex);
                } catch (IllegalArgumentException ex) {
                    logger.equals(ex);
                } catch (InvocationTargetException ex) {
                    logger.equals(ex);
                } catch (NoSuchMethodException ex) {
                    logger.equals(ex);
                } catch (SecurityException ex) {
                    logger.equals(ex);
                }
                return true;
            } else {
                return true;
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
                int clickedRow = m_LogTable.rowAtPoint(e.getPoint());
                if (!m_LogTable.isRowSelected(clickedRow)) {
                    m_LogTable.setRowSelectionInterval(clickedRow, clickedRow);
                }

                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new ClearAction());
                menu.add(new ClearAllAction());
                menu.add(new CopyMessageToClipboardAction());
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), m_LogTable);
                menu.show(m_LogTable, pt.x, pt.y);
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

    class ClearAction extends AbstractAction {

        public ClearAction() {
            super("Clear");
        }

        public void actionPerformed(ActionEvent e) {
            // get the selected table lines

            int[] selectedRows = m_LogTable.getSelectedRows();
            for (int row = selectedRows.length - 1; row >= 0; row--) {
                m_LogModel.removeRow(m_LogTable.convertRowIndexToModel(selectedRows[row]));
            }
        }

        @Override
        public boolean isEnabled() {
            return m_LogTable.getSelectedRowCount() > 0;
        }
    }

    class ClearAllAction extends AbstractAction {

        public ClearAllAction() {
            super("Clear All");
        }

        public void actionPerformed(ActionEvent e) {
            clearLogs();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    class CopyMessageToClipboardAction extends AbstractAction {

        public CopyMessageToClipboardAction() {
            super("Copy Message(s) to Clipboard");
        }

        public void actionPerformed(ActionEvent e) {
            StringBuilder messages = new StringBuilder();
            final String eol = System.getProperty("line.separator");
            // get the selected table lines
            int[] selectedRows = m_LogTable.getSelectedRows();
            for (int row = 0; row < selectedRows.length; row++) {
                String message = (String)m_LogTable.getValueAt(selectedRows[row], LOG_MESSAGE);
                if (message != null) {
                    if (messages.length() > 0) {
                        messages.append(eol);
                    }
                    messages.append(message);
                }
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(messages.toString());
            clipboard.setContents(data, data);
        }

        @Override
        public boolean isEnabled() {
            return m_LogTable.getSelectedRowCount() > 0;
        }
    }

    /**
     * Table cell renderer.
     */
    static class QTasteTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // display the color depending on level and message
            String level = (String) table.getValueAt(row, LOG_LEVEL);
            Color foregroundColor;
            if (level.equals("ERROR")) {
                foregroundColor = Color.RED;
            } else if (level.equals("FATAL")) {
                foregroundColor = Color.RED;
            } else if (level.equals("WARN")) {
                foregroundColor = Color.ORANGE.darker();
            } else if (level.equals("DEBUG")) {
                foregroundColor = Color.GRAY;
            } else if (level.equals("TRACE")) {
                foregroundColor = Color.DARK_GRAY;
            } else {
                String message = (String) table.getValueAt(row, LOG_MESSAGE);
                if (message.startsWith("Executing test script: ") || message.startsWith("Retrying test script: ")) {
                    foregroundColor = Color.BLUE;
                } else {
                    foregroundColor = Color.BLACK;
                }
            }
            setForeground(foregroundColor);

            // set text alignment
            if ((column == LOG_MESSAGE) || (column == LOG_STEP)) {
                cell.setHorizontalAlignment(LEFT);
            } else {
                cell.setHorizontalAlignment(CENTER);
            }

            return cell;
        }
    }
}
