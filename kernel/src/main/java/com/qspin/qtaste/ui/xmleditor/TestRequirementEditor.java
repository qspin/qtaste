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

package com.qspin.qtaste.ui.xmleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.io.XMLFile;
import com.qspin.qtaste.testsuite.TestRequirement;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * @author simjan
 */
@SuppressWarnings("serial")
public class TestRequirementEditor extends JPanel {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestRequirementEditor.class);
    protected TestRequirementTableModel m_TestRequirementModel;
    protected JTable m_TestRequirementTable;
    private String currentXMLFile = "";
    private boolean isModified;
    private TableModelListener tableListener;
    private int ROW_HEIGHT = 20;
    private MyTableColumnModelListener m_TableColumnModelListener;
    private Clipboard m_systemClipboard;

    public TestRequirementEditor() {
        super(new BorderLayout());
        m_systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        genUI();
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean value) {
        boolean wasAlreadyModified = isModified;
        isModified = value;
        if (wasAlreadyModified != isModified) {
            firePropertyChange("isModified", wasAlreadyModified, isModified);
        }
        // recompute header and widths
        computeColumnWidths();
        m_TestRequirementTable.repaint();
        m_TestRequirementTable.doLayout();
    }

    class RenameVariable extends AbstractAction {
        String m_ColName;
        int m_ColIndex;

        public RenameVariable(String colName, int colIndex) {
            super("Rename variable");
            m_ColName = colName;
            m_ColIndex = colIndex;
        }

        @Override
        public boolean isEnabled() {
            return !m_ColName.equals(TestRequirement.ID) && !m_ColName.equals(TestRequirement.DESCRIPTION);
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            String defaultName = (String) m_TestRequirementTable.getColumnModel().getColumn(m_ColIndex).getHeaderValue();
            String varName = (String) JOptionPane.showInputDialog(null, "Give the new name of the variable '" + m_ColName + "' ?",
                  "TestData name", JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
            if (varName == null) {
                return;
            }
            //
            m_TestRequirementTable.getColumnModel().getColumn(m_ColIndex).setHeaderValue(varName);
            m_TestRequirementTable.getTableHeader().repaint();
            //computeColumnWidths();
            Vector<String> v = (Vector<String>) m_TestRequirementModel.getColumnIdentifiers();
            int columnIndex = m_TestRequirementTable.getColumnModel().getColumn(m_ColIndex).getModelIndex();
            v.set(columnIndex, varName);
            m_TestRequirementModel.setColumnIdentifiers(v);
            // update requirement data ID
            for (TestRequirement req : m_TestRequirementModel.getRequirements()) {
                req.changeDataId(m_ColName, varName);
            }
            m_TestRequirementModel.fireTableCellUpdated(TableModelEvent.HEADER_ROW, columnIndex);

            setModified(true);
        }
    }

    class AddVariableAction extends AbstractAction {

        public AddVariableAction() {
            super("Add variable");
        }

        public void actionPerformed(ActionEvent e) {
            //if (m_TestData== null) return;
            String varName = JOptionPane.showInputDialog(null, "Give the name of the new variable ?", "TestData name",
                  JOptionPane.QUESTION_MESSAGE);
            if (varName == null) {
                return;
            }
            addColumn(varName);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    public void reload() {
        loadXMLFile(currentXMLFile);
        setModified(false);
    }

    public void save() {
        File xmlFile = new File(currentXMLFile);
        String path = xmlFile.getParent();
        BufferedWriter output = null;

        // if the table is being edited, validate the modification before saving data
        if (m_TestRequirementTable.isEditing()) {
            m_TestRequirementTable.getCellEditor().stopCellEditing();
        }

        // save the requirements
        try {
            String outputFile = path + File.separator + StaticConfiguration.TEST_REQUIREMENTS_FILENAME;
            output = new BufferedWriter(new FileWriter(new File(outputFile)));
            output.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            output.newLine();
            output.write("<" + XMLFile.ROOT_ELEMENT + ">");
            for (TestRequirement req : m_TestRequirementModel.getRequirements()) {
                output.newLine();
                output.write("\t<" + XMLFile.REQUIREMENT_ELEMENT + " ");
                output.append(XMLFile.REQUIREMENT_ID + "=\"");
                output.append(req.getIdEscapeXml());
                output.append("\">");

                for (String dataId : req.getDataId()) {
                    if (dataId.equals(TestRequirement.ID)) {
                        continue;
                    }
                    output.newLine();
                    output.append("\t\t<" + dataId.replace(" ", XMLFile.SPACE_REPLACEMENT) + ">");
                    output.append(req.getDataEscapeXml(dataId));
                    output.append("</" + dataId.replace(" ", XMLFile.SPACE_REPLACEMENT) + ">");
                }

                output.newLine();
                output.append("\t</" + XMLFile.REQUIREMENT_ELEMENT + ">");
            }
            output.newLine();
            output.write("</" + XMLFile.ROOT_ELEMENT + ">");
            output.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }

        reload();
    }

    class RemoveColumnAction extends AbstractAction {

        String m_ColName;
        int m_ColIndex;

        public RemoveColumnAction(String colName, int colIndex) {
            super("Remove variable");
            m_ColName = colName;
            m_ColIndex = colIndex;
        }

        public void actionPerformed(ActionEvent e) {
            removeColumn(m_ColName, m_ColIndex);
        }

        @Override
        public boolean isEnabled() {
            return !m_ColName.equals(TestRequirement.ID) && !m_ColName.equals(TestRequirement.DESCRIPTION);
        }
    }

    public void loadXMLFile(String fileName) {
        try {
            m_TestRequirementModel.removeTableModelListener(tableListener);
            m_TestRequirementTable.getColumnModel().removeColumnModelListener(m_TableColumnModelListener);
            XMLFile xmlFile = new XMLFile(fileName);
            m_TestRequirementModel.setRowCount(0);
            m_TestRequirementModel.setColumnCount(0);

            currentXMLFile = fileName;
            m_TestRequirementModel.setRequirements(xmlFile.getXMLDataSet());

            Enumeration<TableColumn> columns = m_TestRequirementTable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn hcol = columns.nextElement();
                hcol.setHeaderRenderer(new MyTableHeaderRenderer());
                hcol.setCellEditor(new TestDataTableCellEditor());
            }
            computeColumnWidths();
            m_TestRequirementTable.doLayout();
            m_TestRequirementModel.addTableModelListener(tableListener);
            m_TestRequirementTable.getColumnModel().addColumnModelListener(m_TableColumnModelListener);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (SAXException ex) {
            logger.error(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void setFileName(String fileName) {
        currentXMLFile = fileName;
    }

    public void removeColumn(String header, int colIndex) {
        for (TestRequirement req : m_TestRequirementModel.getRequirements()) {
            req.removeDataId(header);
        }
        m_TestRequirementModel.fireTableDataChanged();
        setModified(true);
    }

    public void addColumn(String header) {
        m_TestRequirementModel.addColumn(header);
        TableColumn hcol = m_TestRequirementTable.getColumn(header);
        hcol.setHeaderRenderer(new MyTableHeaderRenderer());
        hcol.setCellEditor(new TestDataTableCellEditor());
        computeColumnWidths();
        setModified(true);
        // now add the needed rows
    }

    private void genUI() {
        getActionMap().put("Save", new SaveAction());
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
        m_TestRequirementTable = new JTable() {

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

            // overwrite cell content when typing on a selected cell
            @Override
            public Component prepareEditor(TableCellEditor editor, int row, int column) {
                Component c = super.prepareEditor(editor, row, column);

                if (c instanceof JTextComponent) {
                    ((JTextField) c).selectAll();
                }

                return c;
            }

            // select entire rows when selecting first column (row id)
            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                if (e.getFirstIndex() == 0 && e.getValueIsAdjusting()) {
                    setColumnSelectionInterval(1, getColumnCount() - 1);
                } else {
                    super.columnSelectionChanged(e);
                }
            }
        };
        m_TestRequirementTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        m_TestRequirementModel = new TestRequirementTableModel();

        m_TestRequirementTable.setModel(m_TestRequirementModel);

        m_TableColumnModelListener = new MyTableColumnModelListener();
        m_TestRequirementTable.setSurrendersFocusOnKeystroke(true);
        m_TestRequirementTable.setColumnSelectionAllowed(true);
        m_TestRequirementTable.addMouseListener(new TableMouseListener(m_TestRequirementTable));
        m_TestRequirementTable.getTableHeader().addMouseListener(new TableMouseListener(m_TestRequirementTable));
        m_TestRequirementTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_TestRequirementTable.getActionMap().put("Save", new SaveAction());
        m_TestRequirementTable.setDefaultEditor(String.class, new TestDataTableCellEditor());
        m_TestRequirementTable.setDefaultEditor(Integer.class, new TestDataTableCellEditor());
        m_TestRequirementTable.getTableHeader().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
              "Save");
        m_TestRequirementTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
        m_TestRequirementTable.setRowHeight(ROW_HEIGHT);

        m_TestRequirementTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                //
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // check if previous line is empty
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // if current row is the last one
                    if (m_TestRequirementTable.getSelectedRow() == m_TestRequirementTable.getRowCount() - 1) {
                        addNewRow();
                    }
                }
                if ((e.getKeyCode() == KeyEvent.VK_S) && (e.isControlDown())) {
                    save();
                }
                if ((e.getKeyCode() == KeyEvent.VK_C) && (e.isControlDown())) {
                    copySelectionToClipboard();
                }
                if ((e.getKeyCode() == KeyEvent.VK_V) && (e.isControlDown())) {
                    if (m_TestRequirementTable.getSelectedColumn() != 0) {
                        pasteSelectionFromClipboard();
                    }
                }
            }
        });

        tableListener = new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                // build the test data
                if (e.getType() == TableModelEvent.UPDATE) {
                    if (e.getFirstRow() >= 0) {
                        setModified(true);
                    }
                }
            }
        };
        m_TestRequirementModel.addTableModelListener(tableListener);

        JScrollPane sp = new JScrollPane(m_TestRequirementTable);
        sp.addMouseListener(new TableMouseListener(null));
        add(sp);
    }

    private String convertObjectToToolTip(Object obj) {
        String tip = null;
        if (obj != null) {
            if (obj instanceof ImageIcon) { // Status column
                tip = ((ImageIcon) obj).getDescription();
            } else {
                tip = obj.toString();
            }
        }
        return tip;

    }

    public String getCurrentXMLFile() {
        return currentXMLFile;
    }

    private void copySelectionToClipboard() {
        StringBuffer stringBuffer = new StringBuffer();
        int[] selectedRows = m_TestRequirementTable.getSelectedRows();
        int[] selectedCols = m_TestRequirementTable.getSelectedColumns();
        for (int i = 0; i < selectedRows.length; i++) {
            for (int j = 0; j < selectedCols.length; j++) {
                stringBuffer.append(m_TestRequirementTable.getValueAt(selectedRows[i], selectedCols[j]));
                if (j < selectedCols.length - 1) {
                    stringBuffer.append("\t");
                }
            }
            stringBuffer.append("\n");
        }
        StringSelection stringSelection = new StringSelection(stringBuffer.toString());
        m_systemClipboard.setContents(stringSelection, stringSelection);
    }

    private void pasteSelectionFromClipboard() {
        int startRow = (m_TestRequirementTable.getSelectedRows())[0];
        int startCol = (m_TestRequirementTable.getSelectedColumns())[0];
        try {
            String clipboardContent = (String) (m_systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
            StringTokenizer tokenizerRow = new StringTokenizer(clipboardContent, "\n");
            for (int i = 0; tokenizerRow.hasMoreTokens(); i++) {
                String rowString = tokenizerRow.nextToken();
                StringTokenizer tokenizerTab = new StringTokenizer(rowString, "\t");
                for (int j = 0; tokenizerTab.hasMoreTokens(); j++) {
                    String value = tokenizerTab.nextToken();
                    int row = startRow + i;
                    int col = startCol + j;
                    // add new row if necessary
                    if (row == m_TestRequirementTable.getRowCount()) {
                        addNewRow();
                    }
                    if (col < m_TestRequirementTable.getColumnCount()) {
                        m_TestRequirementTable.setValueAt(value, row, col);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error while pasting clipboard content into test requirement editor", e);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    // ///////////////////////////////////////////////////////////////////////////////////

    protected class MyTableHeaderRenderer extends JLabel implements TableCellRenderer {
        // This method is called each time a column header
        // using this renderer needs to be rendered.

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int
              row, int column) {
            // 'value' is column header value of column 'vColIndex'
            // rowIndex is always -1
            // isSelected is always false
            // hasFocus is always false
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            // Configure the component with the specified value
            setText(value.toString());

            // Set tool tip if desired
            setToolTipText((String) value);

            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        @Override
        public void validate() {
        }

        @Override
        public void revalidate() {
        }

        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }

    public class TableMouseListener extends MouseAdapter {

        protected JTable table;

        public TableMouseListener(JTable table) {
            this.table = table;
        }

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int clickedRow = -1;
                int clickedColumn = -1;
                if (table != null) {
                    // force selection of clicked cell
                    clickedRow = table.rowAtPoint(e.getPoint());
                    clickedColumn = table.columnAtPoint(e.getPoint());
                    if (clickedColumn != -1 && clickedRow != -1) {
                        table.setRowSelectionInterval(clickedRow, clickedRow);
                        table.setColumnSelectionInterval(clickedColumn, clickedColumn);
                    }
                }

                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new AddVariableAction());
                if (table != null && table.columnAtPoint(e.getPoint()) != -1) {
                    menu.add(new RenameVariable(table.getColumnName(clickedColumn), clickedColumn));
                    menu.add(new RemoveColumnAction(table.getColumnName(clickedColumn), clickedColumn));
                }
                menu.add(new AddRowAction());
                if (table != null && table.rowAtPoint(e.getPoint()) != -1) {
                    menu.add(new InsertRowAction());
                    menu.add(new DuplicateRowAction());
                    menu.add(new RemoveRowAction());
                }
                menu.add(new SaveAction());
                menu.add(new ReloadAction());
                Point point = e.getPoint();
                menu.show(e.getComponent(), point.x, point.y);
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

    class RemoveRowAction extends AbstractAction {

        public RemoveRowAction() {
            super("Remove row");
        }

        public void actionPerformed(ActionEvent e) {
            int selectedRow = m_TestRequirementTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            m_TestRequirementModel.removeRequirement(m_TestRequirementTable.convertRowIndexToModel(selectedRow));
            setModified(true);
        }
    }

    class AddRowAction extends AbstractAction {

        public AddRowAction() {
            super("Add row");
        }

        public void actionPerformed(ActionEvent e) {
            addNewRow();
        }
    }

    class InsertRowAction extends AbstractAction {

        public InsertRowAction() {
            super("Insert row");
        }

        public void actionPerformed(ActionEvent e) {
            insertNewRow();
        }
    }

    class DuplicateRowAction extends AbstractAction {

        public DuplicateRowAction() {
            super("Duplicate row");
        }

        public void actionPerformed(ActionEvent e) {
            int selectedRow = m_TestRequirementTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            selectedRow = m_TestRequirementTable.convertRowIndexToModel(selectedRow);
            addNewRow(selectedRow);
        }
    }

    /**
     * Reload the current requirement file (i.e revert unsaved modifications)
     */
    class ReloadAction extends AbstractAction {

        public ReloadAction() {
            super("Revert unsaved modifications");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.showConfirmDialog(null, "Do you want to revert unsaved modifications ?",
                  "Revert unsaved modifications", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                reload();
            }
        }

        @Override
        public boolean isEnabled() {
            return isModified();
        }
    }

    class SaveAction extends AbstractAction {

        public SaveAction() {
            super("Save");
        }

        public void actionPerformed(ActionEvent e) {
            save();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    private void addNewRow() {
        addNewRow(-1);
    }

    private void insertNewRow() {
        int rowIndex = m_TestRequirementTable.getSelectedRow();
        rowIndex = m_TestRequirementTable.convertRowIndexToModel(rowIndex);
        m_TestRequirementModel.addRequirement(new TestRequirement(""), rowIndex);
        setModified(true);
    }

    private void addNewRow(int rowToCopyIndex) {
        TestRequirement req;
        if (rowToCopyIndex >= 0) {
            req = new TestRequirement(m_TestRequirementModel.getRequirements().get(rowToCopyIndex));
        } else {
            req = new TestRequirement("");
        }
        m_TestRequirementModel.addRequirement(req, m_TestRequirementModel.getRowCount());
        m_TestRequirementTable.setVisible(true);
        setModified(true);
    }

    private void computeColumnWidths() {
        // horizontal spacing
        int hspace = 6;
        TableModel model = m_TestRequirementTable.getModel();

        // rows no
        int cols = model.getColumnCount();

        // columns no
        int rows = model.getRowCount();

        // width vector
        int w[] = new int[model.getColumnCount()];

        // computes headers widths
        for (int i = 0; i < cols; i++) {
            w[i] = (int) m_TestRequirementTable.getDefaultRenderer(String.class).getTableCellRendererComponent(
                  m_TestRequirementTable, m_TestRequirementModel.getColumnName(i), false, false, -1, i).getPreferredSize()
                  .getWidth() + hspace;
            TableColumn hcol = m_TestRequirementTable.getColumn(m_TestRequirementModel.getColumnName(i));
            hcol.setHeaderRenderer(new MyTableHeaderRenderer());

        }

        // check if cell values fit in their cells and if not
        // keep in w[i] the necessary with
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Object o = model.getValueAt(i, j);
                int width = 0;
                if (o != null) {
                    width = (int) m_TestRequirementTable.getCellRenderer(i, j).getTableCellRendererComponent(
                          m_TestRequirementTable, o, false, false, i, j).getPreferredSize().getWidth() + hspace;
                }
                if (w[j] < width) {
                    w[j] = width;
                }
            }
        }

        TableColumnModel colModel = m_TestRequirementTable.getColumnModel();

        // and finally setting the column widths
        for (int i = 0; i < cols; i++) {
            colModel.getColumn(i).setPreferredWidth(w[i]);
        }
    }

    public class TestDataTableCellEditor extends DefaultCellEditor implements FocusListener, KeyListener {

        public TestDataTableCellEditor() {
            super(new JTextField());

            getComponent().addFocusListener(this);
            getComponent().addKeyListener(this);
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
        }

        protected void fireEditingStopped() {
            if (isModified) {
                super.fireEditingStopped();
            } else {
                super.fireEditingCanceled();
            }
        }

        public void keyTyped(KeyEvent e) {

        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                // check if previous line is empty
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                // if current row is the last one
                if (m_TestRequirementTable.getSelectedRow() == m_TestRequirementTable.getRowCount() - 1) {
                    addNewRow();
                }
                return;
            }
            if (e.isControlDown()) {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    // validate the cell
                    stopCellEditing();
                    save();
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    // copy
                    // don't set modified
                    return;
                }
            }
            if ((e.getKeyCode() != KeyEvent.VK_TAB) && (e.getKeyCode() != KeyEvent.VK_CONTROL) && (e.getKeyCode()
                  != KeyEvent.VK_ALT) && (e.getKeyCode() != KeyEvent.VK_ALT_GRAPH) && (e.getKeyCode() != KeyEvent.VK_SHIFT) && (
                  e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) && (e.getKeyCode() != KeyEvent.VK_ENTER) && (e.getKeyCode()
                  != KeyEvent.VK_LEFT) && (e.getKeyCode() != KeyEvent.VK_RIGHT) && (e.getKeyCode() != KeyEvent.VK_HOME) && (
                  e.getKeyCode() != KeyEvent.VK_END) && (e.getKeyCode() != KeyEvent.VK_PAGE_UP) && (e.getKeyCode()
                  != KeyEvent.VK_PAGE_DOWN) && (e.getKeyCode() != KeyEvent.VK_NUM_LOCK) && (e.getKeyCode()
                  != KeyEvent.VK_SCROLL_LOCK) && (e.getKeyCode() != KeyEvent.VK_PRINTSCREEN) && (e.getKeyCode()
                  != KeyEvent.VK_PAUSE) && (e.getKeyCode() != KeyEvent.VK_ESCAPE)) {
                setModified(true);
            }
        }

        public void keyReleased(KeyEvent e) {
            // throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public class MyTableColumnModelListener implements TableColumnModelListener {

        @Override
        public void columnAdded(TableColumnModelEvent e) {
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            if (e.getFromIndex() != e.getToIndex()) {
                setModified(true);
            }
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
            // TODO Auto-generated method stub

        }

    }
}
