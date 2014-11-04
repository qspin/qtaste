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

package com.qspin.qtaste.ui.csveditor;

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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.io.CSVFile;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.impl.TestDataImpl;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestDataEditor extends JPanel {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestDataEditor.class);
    protected TestDataTableModel m_TestDataModel;
    protected JTable m_TestDataTable;
    protected TestData m_TestData = null;
    private String currentCSVFile = "";
    private boolean isModified;
    private TableModelListener tableListener;
    private boolean m_forInteractiveMode;
    private int ROW_HEIGHT = 20;
    private MyTableColumnModelListener m_TableColumnModelListener;
    private Clipboard m_systemClipboard;

    public TestDataEditor() {
        this(false);
    }

    public TestDataEditor(boolean forInteractiveMode) {
        super(new BorderLayout());
        m_forInteractiveMode = forInteractiveMode;
        m_systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        genUI();
    }

    public void addVariables(String[] dataNames) {
        for (String dataname : dataNames) {
            addTestData(dataname);
        }
        m_TestDataTable.repaint();
        m_TestDataTable.doLayout();
        setModified(true);
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
        m_TestDataTable.repaint();
        m_TestDataTable.doLayout();
    }

    public TestData getTestData() {
        return m_TestData;
    }

    public void save() {
        int rowCount = m_TestDataModel.getRowCount();
        int colCount = m_TestDataModel.getColumnCount();
        File csvFile = new File(currentCSVFile);
        String path = csvFile.getParent();
        BufferedWriter output = null;
        try {
            String outputFile = path + File.separator + StaticConfiguration.TEST_DATA_FILENAME;

            //output = new BufferedWriter(new FileWriter(new File(outputFile)));
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));

            // retrieve the header
            for (int col = 1; col < colCount; col++) {
                if (col > 1) {
                    output.append(';');
                }
                output.append(m_TestDataTable.getColumnModel().getColumn(col).getHeaderValue().toString());
            }
            output.append('\n');
            for (int row = 0; row < rowCount; row++) {
                for (int col = 1; col < colCount; col++) {
                    if (col > 1) {
                        output.append(';');
                    }
                    int columnIndex = m_TestDataTable.getColumnModel().getColumn(col).getModelIndex();
                    if (m_TestDataModel.getValueAt(row, columnIndex)==null)
                    	output.append("");
                    else
                    	output.append(m_TestDataModel.getValueAt(row, columnIndex).toString());


                }
                output.append('\n');
            }
            output.close();
            setModified(false);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        //
        } finally {
            try {
                setModified(false);
                if (output!=null)
                	output.close();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }

        // reload
        loadCSVFile(currentCSVFile);
    }

    public void loadCSVFile(String fileName) {
        try {
            m_TestDataModel.removeTableModelListener(tableListener);
            m_TestDataTable.getColumnModel().removeColumnModelListener(m_TableColumnModelListener);
            CSVFile csvFile = new CSVFile(fileName);
            List<String> columnNames = csvFile.getColumnNames();
            List<LinkedHashMap<String, String>> csvDataSet = csvFile.getCSVDataSet();
            m_TestDataModel.setRowCount(0);
            m_TestDataModel.setColumnCount(0);

            m_TestDataModel.addColumn("#");
            for (String name : columnNames) {
                m_TestDataModel.addColumn(name);
            }

            int rowCount = 0;
            for (LinkedHashMap<String, String> csvData : csvDataSet) {
                LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
                data.put("#", String.valueOf(rowCount + 1));
                data.putAll(csvData);
                m_TestDataModel.addRow(data.values().toArray());
                rowCount++;
            }

            Enumeration<TableColumn> columns = m_TestDataTable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn hcol = columns.nextElement();
                hcol.setHeaderRenderer(new MyTableHeaderRenderer());
                hcol.setCellEditor(new TestDataTableCellEditor());
            }
            computeColumnWidths();
            currentCSVFile = fileName;
            m_TestDataTable.doLayout();
            m_TestDataModel.addTableModelListener(tableListener);
            m_TestDataTable.getColumnModel().addColumnModelListener(m_TableColumnModelListener);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void setFileName(String fileName) {
    	currentCSVFile = fileName;
    }

    public void setTestData(TestData data) {
        m_TestData = data;
        m_TestDataModel.setRowCount(0);
        m_TestDataModel.setColumnCount(0);
        m_TestDataModel.addColumn("Comment");
        ArrayList<String> dataRow = new ArrayList<String>();
        dataRow.add("QTaste_interactive");

        for (Entry<String, String> dataEntry : data.getDataHash().entrySet()) {
            m_TestDataModel.addColumn(dataEntry.getKey());
            dataRow.add(dataEntry.getValue());
        }
        m_TestDataModel.addRow(dataRow.toArray());
        for (int i=0; i < m_TestDataModel.getColumnCount();i++) {
            m_TestDataTable.getColumn(m_TestDataTable.getColumnName(i)).setCellEditor(new TestDataTableCellEditor());

        }
    }

    public void removeTestData(String header, int colIndex) {
        try {
            if (m_TestData != null) {
                TestDataImpl testData = (TestDataImpl) m_TestData;
                HashMap<String, String> dataHash = testData.getDataHash();
                if (dataHash.containsKey(header)) {
                    dataHash.remove(header);
                }

                setTestData(m_TestData);
                computeColumnWidths();
            } else {

                removeColumnAndData(m_TestDataTable, colIndex);
                computeColumnWidths();
            }
            setModified(true);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    public void addTestData(String header) {
        try {
            if (m_TestData != null) // interactive mode
            {
                m_TestData.setValue(header, header);
            }
            m_TestDataModel.addColumn(header);
            TableColumn hcol = m_TestDataTable.getColumn(header);
            hcol.setHeaderRenderer(new MyTableHeaderRenderer());
            hcol.setCellEditor(new TestDataTableCellEditor());
            computeColumnWidths();
            setModified(true);
        // now add the needed rows

        } catch (QTasteDataException ex) {
            logger.error(ex.getMessage());
        }

    }

    private void genUI() {
        getActionMap().put("Save", new SaveAction());
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
        m_TestDataTable = new JTable() {

            @Override
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (colIndex == 0)
                {
                   // no tooltip on first column (row id)
                   return null;
                }
                else
                {
                   return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
                }
            }

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex =
                                columnModel.getColumn(index).getModelIndex();
                        return getColumnName(realIndex);
                    }
                };
            }

            // first column (row id) is read-only
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            // overwrite cell content when typing on a selected cell
            @Override
            public Component prepareEditor(TableCellEditor editor, int row, int column)
            {
               Component c = super.prepareEditor(editor, row, column);

               if (c instanceof JTextComponent)
               {
                  ((JTextField) c).selectAll();
               }

               return c;
            }

            // select entire rows when selecting first column (row id)
            @Override
            public void columnSelectionChanged(ListSelectionEvent e)
            {
               if (e.getFirstIndex() == 0 && e.getValueIsAdjusting())
               {
                  setColumnSelectionInterval(1, getColumnCount() - 1);
               }
               else
               {
                  super.columnSelectionChanged(e);
               }
            }
        };
        m_TestDataTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        m_TestDataModel = new TestDataTableModel(m_TestDataTable);

        m_TestDataTable.setModel(m_TestDataModel);

        m_TableColumnModelListener = new MyTableColumnModelListener();
        m_TestDataTable.setSurrendersFocusOnKeystroke(true);
        m_TestDataTable.setColumnSelectionAllowed(true);
        m_TestDataTable.addMouseListener(new TableMouseListener(m_TestDataTable));
        m_TestDataTable.getTableHeader().addMouseListener(new TableMouseListener(m_TestDataTable));
        m_TestDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_TestDataTable.getActionMap().put("Save", new SaveAction());
        m_TestDataTable.setDefaultEditor(String.class, new TestDataTableCellEditor());
        m_TestDataTable.setDefaultEditor(Integer.class, new TestDataTableCellEditor());
        m_TestDataTable.getTableHeader().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
        m_TestDataTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
        m_TestDataTable.setRowHeight(ROW_HEIGHT);

        m_TestDataTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                //
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // check if previous line is empty
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // if current row is the last one
                    if (m_TestDataTable.getSelectedRow() ==
                            m_TestDataTable.getRowCount() - 1) {
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
                   if (m_TestDataTable.getSelectedColumn() != 0)
                   {
                      pasteSelectionFromClipboard();
                   }
                }
            }
        });

        m_TestDataModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                // build the test data
                if (e.getType() == TableModelEvent.UPDATE) {
                    try {
                        if (e.getFirstRow() >= 0) {
                            setModified(true);
                        }
                        if (m_TestData == null) {
                            return;
                        }
                        if (e.getColumn() > 0) {
                            String header = m_TestDataModel.getColumnName(e.getColumn());
                            String value = (String) m_TestDataModel.getValueAt(0, e.getColumn());
                            m_TestData.setValue(header, value);
                        }
                    } catch (QTasteDataException ex) {
                        logger.error(ex.getMessage());
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(m_TestDataTable);
        sp.addMouseListener(new TableMouseListener(null));
        add(sp);
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

    public String getCurrentCSVFile() {
        return currentCSVFile;
    }

    private void copySelectionToClipboard()
    {
       StringBuffer stringBuffer = new StringBuffer();
       int[] selectedRows = m_TestDataTable.getSelectedRows();
       int[] selectedCols = m_TestDataTable.getSelectedColumns();
       for (int i=0; i < selectedRows.length; i++)
       {
          for (int j=0; j < selectedCols.length; j++)
          {
             stringBuffer.append(m_TestDataTable.getValueAt(selectedRows[i],selectedCols[j]));
             if (j < selectedCols.length-1)
             {
                stringBuffer.append("\t");
             }
          }
          stringBuffer.append("\n");
       }
       StringSelection stringSelection  = new StringSelection(stringBuffer.toString());
       m_systemClipboard.setContents(stringSelection, stringSelection);
    }

    private void pasteSelectionFromClipboard()
    {
       int startRow = (m_TestDataTable.getSelectedRows())[0];
       int startCol = (m_TestDataTable.getSelectedColumns())[0];
       try
       {
          String clipboardContent= (String)(m_systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
          StringTokenizer tokenizerRow = new StringTokenizer(clipboardContent, "\n");
          for (int i=0; tokenizerRow.hasMoreTokens(); i++)
          {
             String rowString=tokenizerRow.nextToken();
             StringTokenizer tokenizerTab = new StringTokenizer(rowString, "\t");
             for (int j=0; tokenizerTab.hasMoreTokens(); j++)
             {
                String value = tokenizerTab.nextToken();
                int row = startRow + i;
                int col = startCol + j;
                // add new row if necessary
                if (row == m_TestDataTable.getRowCount())
                {
                   addNewRow();
                }
                if (col < m_TestDataTable.getColumnCount())
                {
                   m_TestDataTable.setValueAt(value, row, col);
                }
            }
         }
      }
      catch(Exception e)
      {
         logger.warn("Error while pasting clipboard content into test data editor", e);
      }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////

    protected class MyTableHeaderRenderer extends JLabel implements TableCellRenderer {
        // This method is called each time a column header
        // using this renderer needs to be rendered.

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
                    if (clickedRow != -1) {
                    	table.setRowSelectionInterval(clickedRow, clickedRow);
                    }
                    clickedColumn = table.columnAtPoint(e.getPoint());
                    if (clickedColumn != -1) {
                    	table.setColumnSelectionInterval(clickedColumn, clickedColumn);
                    }
                }

                boolean isCellSelected = ((clickedRow != -1) && (clickedColumn != -1));
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new AddVariableAction());
                if (table != null && isCellSelected) {
                    menu.add(new RenameVariable(table.getColumnName(clickedColumn), clickedColumn));
                    menu.add(new RemoveVariableAction(table.getColumnName(clickedColumn), clickedColumn));
                }
                if (m_forInteractiveMode) {
                    menu.add(new LoadFromFileAction());
                } else {
                    menu.add(new AddRowAction());
                    if (table != null && isCellSelected) {
                        menu.add(new InsertRowAction());
                        menu.add(new DuplicateRowAction());
                        menu.add(new RemoveRowAction());
                    }
                    menu.add(new SaveAction());
                }
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
        	int selectedRow = m_TestDataTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            m_TestDataModel.removeRow(selectedRow);
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
        	int selectedRow = m_TestDataTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            addNewRow(selectedRow);
        }
    }

    class RenameVariable extends AbstractAction {
        String m_ColName;
        int m_ColIndex;

        public RenameVariable(String colName, int colIndex) {
            super("Rename variable");
            m_ColName = colName;
            m_ColIndex = colIndex;
        }

        @SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
        	String defaultName = (String)m_TestDataTable.getColumnModel().getColumn(m_ColIndex).getHeaderValue();
            String varName = (String) JOptionPane.showInputDialog(null,
                    "Give the new name of the variable '" + m_ColName + "' ?",
                    "TestData name",
                    JOptionPane.QUESTION_MESSAGE, null, null, defaultName);
            if (varName == null) {
                return;
            }
            //
            m_TestDataTable.getColumnModel().getColumn(m_ColIndex).setHeaderValue(varName);
            m_TestDataTable.getTableHeader().repaint();
            //computeColumnWidths();
            Vector<String> v = (Vector<String>)m_TestDataModel.getColumnIdentifiers();
            int columnIndex = m_TestDataTable.getColumnModel().getColumn(m_ColIndex).getModelIndex();
            v.set(columnIndex, varName);
            m_TestDataModel.setColumnIdentifiers(v);
            m_TestDataModel.fireTableCellUpdated(TableModelEvent.HEADER_ROW, columnIndex);
            // rename column in the model

            setModified(true);
        }
    }

    class AddVariableAction extends AbstractAction {

        public AddVariableAction() {
            super("Add variable");
        }

        public void actionPerformed(ActionEvent e) {
            //if (m_TestData== null) return;
            String varName = JOptionPane.showInputDialog(null,
                    "Give the name of the new variable ?",
                    "TestData name",
                    JOptionPane.QUESTION_MESSAGE);
            if (varName == null) {
                return;
            }
            addTestData(varName);
        }

        @Override
        public boolean isEnabled() {
            return true;
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

    class LoadFromFileAction extends AbstractAction {

        public LoadFromFileAction() {
            super("Load from file...");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser("TestSuites");
            chooser.setDialogTitle("Load test data CSV file...");
            chooser.setFileHidingEnabled(true);
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".csv") || f.getName().endsWith(".CSV");
                }

                @Override
                public String getDescription() {
                    return "Test data CSV files";
                }
            });
            int returnVal = chooser.showDialog(TestDataEditor.this, "Load");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    CSVFile csvFile = new CSVFile(chooser.getSelectedFile().getAbsoluteFile().getCanonicalPath());
                    List<LinkedHashMap<String, String>> csvDataSet = csvFile.getCSVDataSet();
                    int numberRows = csvDataSet.size();
                    if (numberRows > 0) {
                        int rowIndex = 0;
                        if (numberRows != 1) {
                            String[] possibleValues = new String[numberRows];
                            int row = 1;
                            for (LinkedHashMap<String, String> datas : csvDataSet) {
                                possibleValues[row - 1] = row + ": " + datas.get("COMMENT");
                                row++;
                            }
                            String selectedValue = (String) JOptionPane.showInputDialog(TestDataEditor.this, "Select test data row to load:", "Test data row selection", JOptionPane.QUESTION_MESSAGE, null, possibleValues, possibleValues[0]);
                            if (selectedValue == null) {
                                return;
                            }
                            String rowIdStr = selectedValue.substring(0, selectedValue.indexOf(':'));
                            int rowId = Integer.parseInt(rowIdStr);
                            rowIndex = rowId - 1;
                        }
                        LinkedHashMap<String, String> csvData = csvDataSet.get(rowIndex);
                        csvData.remove("COMMENT");
                        setTestData(new TestDataImpl(1, csvData));
                    } else {
                        JOptionPane.showMessageDialog(TestDataEditor.this, "No test data found in file!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    class RemoveVariableAction extends AbstractAction {

        String m_ColName;
        int m_ColIndex;

        public RemoveVariableAction(String colName, int colIndex) {
            super("Remove variable");
            m_ColName = colName;
            m_ColIndex = colIndex;
        }

        public void actionPerformed(ActionEvent e) {
            removeTestData(m_ColName, m_ColIndex);
        }

        @Override
        public boolean isEnabled() {
            return !m_ColName.equals("Comment");
        }
    }


    private void addNewRow() {
    	addNewRow(-1);
    }

    private void insertNewRow() {
       int rowIndex = m_TestDataTable.getSelectedRow();
       if (rowIndex == -1) {
           return;
       }
       Object[] dataValues = new String[m_TestDataModel.getColumnCount()];
       dataValues[0] = Integer.toString(rowIndex + 1);
       for (int i = 1; i < dataValues.length; i++) {
           dataValues[i] = "";
       }
       m_TestDataModel.insertRow(rowIndex, dataValues);
       m_TestDataTable.setRowSelectionInterval(rowIndex, rowIndex);
       for (int row = rowIndex + 1; row < m_TestDataModel.getRowCount(); row++) {
          m_TestDataModel.setValueAt(Integer.toString(row + 1), row, 0);
       }
       setModified(true);
     }

    private void addNewRow(int rowToCopyIndex) {
        Object[] dataValues = new String[m_TestDataModel.getColumnCount()];
        dataValues[0] = Integer.toString(m_TestDataModel.getRowCount() + 1);
        for (int i = 1; i < dataValues.length; i++) {
            dataValues[i] = rowToCopyIndex >= 0 ? m_TestDataModel.getValueAt(rowToCopyIndex, i) : "";
        }
        m_TestDataModel.addRow(dataValues);
        setModified(true);
    }

    private void computeColumnWidths() {
        //horizontal spacing
        int hspace = 6;
        TableModel model = m_TestDataTable.getModel();

        //rows no
        int cols = model.getColumnCount();

        //columns no
        int rows = model.getRowCount();

        //width vector
        int w[] = new int[model.getColumnCount()];

        //computes headers widths
        for (int i = 0; i < cols; i++) {
            w[i] = (int) m_TestDataTable.getDefaultRenderer(String.class).
                    getTableCellRendererComponent(m_TestDataTable, m_TestDataTable.getColumnName(i), false, false, -1, i).
                    getPreferredSize().getWidth() + hspace;
		            TableColumn hcol = m_TestDataTable.getColumn(m_TestDataTable.getColumnName(i));
		            hcol.setHeaderRenderer(new MyTableHeaderRenderer());

        }

        //check if cell values fit in their cells and if not
        //keep in w[i] the necessary with
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Object o = model.getValueAt(i, j);
                int width = 0;
                if (o != null) {
                    width = (int) m_TestDataTable.getCellRenderer(i, j).
                            getTableCellRendererComponent(m_TestDataTable, o, false, false, i, j).
                            getPreferredSize().getWidth() + hspace;
                }
                if (w[j] < width) {
                    w[j] = width;
                }
            }
        }

        TableColumnModel colModel = m_TestDataTable.getColumnModel();

        //and finally setting the column widths
        for (int i = 0; i < cols; i++) {
            colModel.getColumn(i).setPreferredWidth(w[i]);
        }
    }
// Removes the specified column from the table and the associated
    // call data from the table model.

    public void removeColumnAndData(JTable table, int vColIndex) {
        TestDataTableModel model = (TestDataTableModel) table.getModel();
        TableColumn col = table.getColumnModel().getColumn(vColIndex);
        int columnModelIndex = col.getModelIndex();
        Vector<?> data = model.getDataVector();
        Vector<?> colIds = model.getColumnIdentifiers();

        // Remove the column from the table
        table.removeColumn(col);

        // Remove the column header from the table model
        colIds.removeElementAt(columnModelIndex);

        // Remove the column data
        for (int r = 0; r < data.size(); r++) {
            Vector<?> row = (Vector<?>) data.get(r);
            row.removeElementAt(columnModelIndex);
        }
        model.setDataVector(data, colIds);

        // Correct the model indices in the TableColumn objects
        // by decrementing those indices that follow the deleted column
        Enumeration<TableColumn> enumCols = table.getColumnModel().getColumns();
        for (; enumCols.hasMoreElements();) {
            TableColumn c = enumCols.nextElement();
            if (c.getModelIndex() >= columnModelIndex) {
                c.setModelIndex(c.getModelIndex() - 1);
            }
        }
        model.fireTableStructureChanged();
    }


        public class TestDataTableCellEditor extends DefaultCellEditor  implements FocusListener,KeyListener {


            public TestDataTableCellEditor()
            {
                 super( new JTextField() );

                 getComponent().addFocusListener( this );
                 getComponent().addKeyListener(this);
            }
            public void focusGained( FocusEvent e )
            {
            }
            public void focusLost( FocusEvent e )
            {
            }

            protected void fireEditingStopped()
            {
               if (isModified)
               {
                  super.fireEditingStopped();
               }
               else
               {
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
                            if (m_TestDataTable.getSelectedRow() ==
                                    m_TestDataTable.getRowCount() - 1) {
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
                        if ((e.getKeyCode() != KeyEvent.VK_TAB) &&
                            (e.getKeyCode() != KeyEvent.VK_CONTROL) &&
                            (e.getKeyCode() != KeyEvent.VK_ALT) &&
                            (e.getKeyCode() != KeyEvent.VK_ALT_GRAPH) &&
                            (e.getKeyCode() != KeyEvent.VK_SHIFT) &&
                            (e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) &&
                            (e.getKeyCode() != KeyEvent.VK_ENTER) &&
                            (e.getKeyCode() != KeyEvent.VK_LEFT) &&
                            (e.getKeyCode() != KeyEvent.VK_RIGHT) &&
                            (e.getKeyCode() != KeyEvent.VK_HOME) &&
                            (e.getKeyCode() != KeyEvent.VK_END) &&
                            (e.getKeyCode() != KeyEvent.VK_PAGE_UP) &&
                            (e.getKeyCode() != KeyEvent.VK_PAGE_DOWN) &&
                            (e.getKeyCode() != KeyEvent.VK_NUM_LOCK) &&
                            (e.getKeyCode() != KeyEvent.VK_SCROLL_LOCK) &&
                            (e.getKeyCode() != KeyEvent.VK_PRINTSCREEN) &&
                            (e.getKeyCode() != KeyEvent.VK_PAUSE) &&
                            (e.getKeyCode() != KeyEvent.VK_ESCAPE)
                            )
                        {
                            setModified(true);
                        }
        }

        public void keyReleased(KeyEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
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
		   if (e.getFromIndex() != e.getToIndex())
		   {
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
