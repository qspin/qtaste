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

package com.qspin.qtaste.ui.treetable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.qspin.qtaste.ui.TCTreeNode;
import com.qspin.qtaste.ui.testcampaign.TestCampaignTreeModel;
import com.qspin.qtaste.ui.tools.CheckBoxJList;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.JTreeNode;
import com.qspin.qtaste.ui.tools.TestDataNode;
import com.qspin.qtaste.ui.tools.TristateCheckBox;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class JTreeTable extends JTable {

    private static Logger logger = Log4jLoggerFactory.getLogger(JTreeTable.class);
    protected TreeTableCellRenderer tree;
    private TreeTableModel mTreeTableModel;
    private boolean hasChanged = false;

    public JTreeTable(TreeTableModel treeTableModel) {
        super();
        mTreeTableModel = treeTableModel;

        mTreeTableModel.addTreeModelListener(new TreeModelListener() {

            public void treeNodesChanged(TreeModelEvent e) {
                hasChanged = true;
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
            }
        });

        // Create the tree. It will be used as a renderer and editor.
        tree = new TreeTableCellRenderer(treeTableModel);

        TCTreeListener listener = new TCTreeListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);

        // Install a tableModel representing the visible rows in the tree.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        // Force the JTable and JTree to share their row selection models.

        tree.setSelectionModel(new DefaultTreeSelectionModel() {
            // Extend the implementation of the constructor, as if:
	/*  public this()*/ {
                setSelectionModel(listSelectionModel);
            }
        });
        // Make the tree and table row heights the same.
        tree.setRowHeight(getRowHeight());
        // Install the tree editor renderer and editor.
        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        TristateRenderer dcr = new TristateRenderer();
        setDefaultRenderer(Boolean.class, dcr);
        //setDefaultEditor( Boolean.class, new TristateEditor() );
        TristateEditor dce = new TristateEditor();
        dce.addCellEditorListener(new CellEditorListener() {

            public void editingStopped(ChangeEvent e) {
                if (e.getSource() instanceof TristateEditor) {
                    // update the model with the current editing value
                    TristateEditor editorCell = (TristateEditor) e.getSource();
                    setValueAt(editorCell.getCellEditorValue(), editorCell.getCurrentRow(), editorCell.getCurrentColumn());

                    // update status of the parent row and its model
                    TreePath currentPath = tree.getPathForRow(editorCell.getCurrentRow());
                    updateChildCells(currentPath, JTreeTable.this.convertColumnIndexToModel(editorCell.getCurrentColumn()));
                    updateParentCells(editorCell.getCurrentRow(), JTreeTable.this.convertColumnIndexToModel(editorCell.getCurrentColumn()));
                }
            }

            public void editingCanceled(ChangeEvent e) {
            }
        });

        setDefaultEditor(Boolean.class, dce);
        getColumnModel().getColumn(0).setPreferredWidth(200);
        getColumnModel().getColumn(0).setMinWidth(100);

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        displayNecessaryColumns();
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                if (index < 0) {
                	return null;
                }
                int realIndex =
                        columnModel.getColumn(index).getModelIndex();
                return JTreeTable.this.getModel().getColumnName(realIndex);
            }
        };
    }

    public void displayNecessaryColumns() {
        //
        if (tree.getModel() instanceof TestCampaignTreeModel) {
            TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
            TCTreeNode rootNode = (TCTreeNode) model.getRoot();
            int colcount = this.getColumnCount();
            for (int viewColIndex = 1; viewColIndex < colcount; viewColIndex++) {
                this.removeColumn(getColumnModel().getColumn(1));
            }
            for (int i = 1; i < getModel().getColumnCount(); i++) {
                TristateCheckBox.State state = (TristateCheckBox.State) model.getValueAt(rootNode, i);
                if (state != TristateCheckBox.NOT_SELECTED) {
                    // add the column
                    this.addColumn(new TableColumn(i));
                }
            }
        }
    }

    public void expandPath(String pathString) {
        //Utils.expandJTree(tree, 10);

        TCTreeNode rootNode = (TCTreeNode) tree.getModel().getRoot();
        Enumeration<?> children = rootNode.children();
        while (children.hasMoreElements()) {
            TCTreeNode childNode = (TCTreeNode) children.nextElement();
            JTreeNode childFileNode = (JTreeNode) childNode.getUserObject();
            if (childFileNode.getFile().getPath().equals(pathString)) {
                TreeNode[] nodes = childNode.getPath();
                TreePath treepath = new TreePath(nodes);
                tree.expandPath(treepath);
            }
        }
    }

    public void expandSelected() {

    	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        TCTreeNode rootNode = (TCTreeNode) model.getRoot();
        expandSelected(rootNode);
    }

    public void expandSelected(TCTreeNode node) {
    	if (tree.getModel() instanceof TestCampaignTreeModel) {
	        TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	        Enumeration<?> children = node.children();
	        while (children.hasMoreElements()) {
	            TCTreeNode childNode = (TCTreeNode) children.nextElement();
	            for (int i = 1; i < model.getColumnCount(); i++) {
	                TristateCheckBox.State state = (TristateCheckBox.State) model.getValueAt(childNode, i);
	                if (state == TristateCheckBox.DONT_CARE) {
	                    TreeNode[] nodes = childNode.getPath();
	                    TreePath treepath = new TreePath(nodes);
	                    tree.expandPath(treepath);
	                }
	            }
	            expandSelected(childNode);
	        }
    	}
    }

    public void updateParentCells(int childRow, int col) {
    	if (tree.getModel() instanceof TestCampaignTreeModel) {
	        TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	        // retrieve Treepath associated to the child row
	        TreePath treePath = tree.getPathForRow(childRow);
	        // get its node
	        TCTreeNode node = (TCTreeNode) treePath.getLastPathComponent();
	        // only applicable if the child is a JTreeNode (otherwise there is no parent (rootNode)
	        if (node.getUserObject() instanceof JTreeNode) {
	            String testbedName = model.getColumnName(col);
	            model.updateParent((JTreeNode) node.getUserObject(), testbedName);
	        }
    	}
    }

    public void removeAll() {
        TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
        model.removeAll();
        // remove also testbeds
        for (int colIndex = 1; colIndex < this.getModel().getColumnCount(); colIndex++) {
            String colName = this.getModel().getColumnName(colIndex);
            if (this.convertColumnIndexToView(colIndex) == -1) {
                continue;
            }
            TableColumn col = this.getColumnModel().getColumn(this.convertColumnIndexToView(colIndex));
            this.getColumnModel().removeColumn(col);
            // remove it from the model
            model.removeTestbed(colName);
        }
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void save(String fileName, String campaignName) {
    	if (tree.getModel() instanceof TestCampaignTreeModel) {
	        TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	        model.save(fileName, campaignName);
	        hasChanged = false;
	        displayNecessaryColumns();
    	}
    }

    public void load(String fileName) {
        try {
        	if (tree.getModel() instanceof TestCampaignTreeModel) {
	            TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	            model.load(fileName);
	            hasChanged = false;
	            displayNecessaryColumns();
	            expandSelected();
        	}
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public void updateChildCells(TreePath treePath, int col) {
    	if (tree.getModel() instanceof TestCampaignTreeModel) {
	        TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	        // get the node associated to the treepath
	        TCTreeNode node = (TCTreeNode) treePath.getLastPathComponent();
	        JTreeNode fNode = null;
	        // retrieve userObject only if based on a JTreeNode
	        if (node.getUserObject() instanceof FileNode || node.getUserObject() instanceof TestDataNode) {
	            fNode = (JTreeNode) node.getUserObject();
	        }
	        int childCount = node.getChildCount();
	        if (fNode != null) {
	            childCount = fNode.getChildren().length;
	        } // it can be different is user didn't expand the tree view


	        String testbedName = model.getColumnName(col);
	        if (fNode != null) {
	            model.updateChild(fNode, testbedName);
	            return;
	        } else {
	            // update child from root
	            for (int i = 0; i < childCount; i++) {
	                TCTreeNode childNode = (TCTreeNode) node.getChildAt(i);
	                JTreeNode childFileNode = (JTreeNode) childNode.getUserObject();
	                // set the value to child from its parent
	                TristateCheckBox.State rootState = model.getNodeState(node.toString(), testbedName);
	                model.setNodeState(childFileNode, testbedName, rootState);
	                model.updateChild(childFileNode, testbedName);
	            }
	        }
    	}
    }

    public TCTreeNode getTreeNode(TCTreeNode parentNode, JTreeNode childNode) {
        Enumeration<?> childrenNodeEnum = parentNode.children();
        while (childrenNodeEnum.hasMoreElements()) {
            TCTreeNode childNodeTC = (TCTreeNode) childrenNodeEnum.nextElement();
            JTreeNode childFileNode = (JTreeNode) childNodeTC.getUserObject();
            if (childFileNode.equals(childNode)) {
                return childNodeTC;
            }
        }
        return null;

    }

    /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to
     * paint the editor. The UI currently uses different techniques to
     * paint the renderers and editors and overriding setBounds() below
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     */
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
    }

    //
    // The renderer used to display the tree nodes, a JTree.
    //
    public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

        protected int visibleRow;

        public TreeTableCellRenderer(TreeModel model) {
            super(model);
        }

        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, JTreeTable.this.getHeight());
        }

        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            visibleRow = row;
            return this;
        }
    }

    //
    // The editor used to interact with tree nodes, a JTree.
    //
    public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int r, int c) {
            return tree;
        }

        /**
         * Overridden to return false, and if the event is a mouse event
         * it is forwarded to the tree.<p>
         * The behavior for this is debatable, and should really be offered
         * as a property. By returning false, all keyboard actions are
         * implemented in terms of the table. By returning true, the
         * tree would get a chance to do something with the keyboard
         * events. For the most part this is ok. But for certain keys,
         * such as left/right, the tree will expand/collapse where as
         * the table focus should really move to a different column. Page
         * up/down should also be implemented in terms of the table.
         * By returning false this also has the added benefit that clicking
         * outside of the bounds of the tree node, but still in the tree
         * column will select the row, whereas if this returned true
         * that wouldn't be the case.
         * <p>By returning false we are also enforcing the policy that
         * the tree will never be editable (at least by a key sequence).
         */
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                for (int counter = getColumnCount() - 1; counter >= 0;
                        counter--) {
                    if (getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent me = (MouseEvent) e;
                        MouseEvent newME = new MouseEvent(tree, me.getID(),
                                me.getWhen(), me.getModifiers(),
                                me.getX() - getCellRect(0, counter, true).x,
                                me.getY(), me.getClickCount(),
                                me.isPopupTrigger());
                        tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            return false;
        }
    }

    public TreeTableModel getTreeTableModel() {
        return mTreeTableModel;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////
    public class TCTreeListener extends MouseAdapter {
    	JPopupMenu menu;

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // show the pop-up menu...
                menu = new JPopupMenu();
                menu.add(new SelectAllAction(true));
                menu.add(new SelectAllAction(false));
                menu.addSeparator();
                menu.add(new RemoveTestSuite());
                menu.addSeparator();
                menu.add(new AddRemoveTestbedColumn("Remove testbed", false));
                menu.add(new AddRemoveTestbedColumn("Add testbed", true));
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), JTreeTable.this);
                menu.show(JTreeTable.this, pt.x, pt.y);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
                // do select All
                SelectAllAction action = new SelectAllAction(true);
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SelectAll"));
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
        	selectRow(e);
            evaluatePopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        	selectRow(e);
            evaluatePopup(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        	if (menu == null || !menu.isVisible()) {
        		selectRow(e);
        	}
        }

        @Override
        public void mouseExited(MouseEvent e) {
        	if (menu == null || !menu.isVisible()) {
        		tree.clearSelection();
        	}
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        	if (menu == null || !menu.isVisible()) {
        		selectRow(e);
        	}
        }

        private void selectRow(MouseEvent e) {
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), JTreeTable.this);
            int row = tree.getClosestRowForLocation(pt.x, pt.y);
            tree.setSelectionRow(row);
        }
    }

    public void removeSelectedTestSuites() {
    	if (tree.getModel() instanceof TestCampaignTreeModel) {
	        TreePath[] selectedPaths = tree.getSelectionPaths();
	        for (TreePath selectedPath : selectedPaths) {
	            TCTreeNode node = (TCTreeNode) selectedPath.getLastPathComponent();
	            //
	            //remove it from the model
	            TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
	            model.removeTestSuite(node);
	        }
    	}
    }

    class RemoveTestSuite extends AbstractAction {

        public RemoveTestSuite() {
            super("Remove test suite");
        }

        public void actionPerformed(ActionEvent e) {
            //
            // get the selected row
            removeSelectedTestSuites();
        }
    }

    class AddRemoveTestbedColumn extends AbstractAction {

        boolean addColumn;
        String title;

        public AddRemoveTestbedColumn(String title, boolean addtestbed) {
            super(title);
            this.addColumn = addtestbed;
            this.title = title;
        }

        public void actionPerformed(ActionEvent e) {
            // show testbed list
            final CheckBoxJList lb = new CheckBoxJList();
            final DefaultListModel lm = new DefaultListModel();

            if (addColumn) {
                for (int i = 1; i < JTreeTable.this.getModel().getColumnCount(); i++) {
                    if (JTreeTable.this.convertColumnIndexToView(i) == -1) {
                        lm.addElement(JTreeTable.this.getModel().getColumnName(i));
                    }
                }
            } else {
                for (int i = 1; i < JTreeTable.this.getColumnCount(); i++) {
                    lm.addElement(JTreeTable.this.getColumnName(i));
                }

            }
            lb.setModel(lm);

            // open a dialog showing this

            final JDialog dlg = new JDialog();
            dlg.setLayout(new BorderLayout());
            dlg.setTitle(title);
            dlg.add(new JScrollPane(lb));
            JPanel buttonPanel = new JPanel(new BorderLayout());
            JButton okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    // remove the columns
                    for (int index = 0; index < lm.getSize(); index++) {
                        String testbedName = (String) lm.getElementAt(index);

                        if (lb.isIndexSelected(index)) {
                            // check the correct
                            for (int colIndex = 1; colIndex < JTreeTable.this.getModel().getColumnCount(); colIndex++) {
                                String colName = JTreeTable.this.getModel().getColumnName(colIndex);
                                if (colName.equals(testbedName)) {
                                    // remove the column
                                    if (addColumn) {
                                        TableColumn col = new TableColumn(colIndex);
                                        JTreeTable.this.addColumn(col);
                                    } else {
                                        TableColumn col = JTreeTable.this.getColumnModel().getColumn(JTreeTable.this.convertColumnIndexToView(colIndex));
                                        JTreeTable.this.getColumnModel().removeColumn(col);
                                        // remove it from the model
                                    	if (tree.getModel() instanceof TestCampaignTreeModel) {
                                    		TestCampaignTreeModel model = (TestCampaignTreeModel) tree.getModel();
                                    		model.removeTestbed(colName);
                                    	}

                                    }
                                    break;

                                }
                            }
                        }
                    }
                    dlg.setVisible(false);
                }
            });

            JButton cancelButton = new JButton("Cancel");

            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dlg.setVisible(false);
                }
            });
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton, BorderLayout.EAST);
            dlg.add(buttonPanel, BorderLayout.SOUTH);
            dlg.pack();
            dlg.setLocationRelativeTo(null);
            dlg.setVisible(true);

        }
    }

    class SelectAllAction extends AbstractAction {

        boolean select;

        public SelectAllAction(boolean select) {
            super(select ? "Select all" : "Unselect all");
            this.select = select;
        }

        public void actionPerformed(ActionEvent e) {
            // select all (or unselect) all columns of the selected tree
            // get selected path
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath == null) {
                return;
            }
            Object obj = selectedPath.getLastPathComponent();
            if (obj instanceof TCTreeNode) {
                TCTreeNode treeNode = (TCTreeNode) obj;
                int selectedRow = tree.getRowForPath(selectedPath);
                for (int i = 1; i < JTreeTable.this.getColumnCount(); i++) {
                    TristateCheckBox.State state = select ? TristateCheckBox.SELECTED : TristateCheckBox.NOT_SELECTED;
                    mTreeTableModel.setValueAt(state, treeNode, convertColumnIndexToModel(i));
                    // refresh the table content
                    updateChildCells(selectedPath, i);
                    updateParentCells(selectedRow, i);
                }
                repaint();
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

