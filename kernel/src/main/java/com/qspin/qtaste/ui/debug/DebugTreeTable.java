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

package com.qspin.qtaste.ui.debug;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;

import com.qspin.qtaste.ui.treetable.AbstractCellEditor;
import com.qspin.qtaste.ui.treetable.TreeTableModel;
import com.qspin.qtaste.ui.treetable.TreeTableModelAdapter;

@SuppressWarnings("serial")
public class DebugTreeTable extends JTable {
	    //private static Logger logger = Log4jLoggerFactory.getLogger(DebugTreeTable.class);
	    protected TreeTableCellRenderer tree;
	    private TreeTableModel mTreeTableModel;
	    private static final Font SMALL_FONT = new Font("Dialog", Font.PLAIN, 10);
		//private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);	    
	    //private DebugTreeCellRenderer tree;

	    public DebugTreeTable(TreeTableModel treeTableModel) {
	        super();
	        mTreeTableModel = treeTableModel;

	        // Create the tree. It will be used as a renderer and editor. 
	        tree = new TreeTableCellRenderer(treeTableModel);
	        //tree = new DebugTreeCellRenderer();

	        // Install a tableModel representing the visible rows in the tree. 
	        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

	        // Force the JTable and JTree to share their row selection models. 
	        tree.setSelectionModel(new DefaultTreeSelectionModel() {
	        	{
	                setSelectionModel(listSelectionModel);
	            }
	        });
	        // Make the tree and table row heights the same. 
	        tree.setRowHeight(getRowHeight());
	        // Install the tree editor renderer and editor. 
	        setDefaultRenderer(TreeTableModel.class, tree);
	        this.setFont(SMALL_FONT);
	        getColumnModel().getColumn(0).setPreferredWidth(50);
	        getColumnModel().getColumn(0).setMinWidth(100);

	        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
	        
	        setShowGrid(false);
	        setIntercellSpacing(new Dimension(0, 0));
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

	    public JTree getTree() {
	    	return tree;
	    }
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
	    // 
	    // The renderer used to display the tree nodes, a JTree.  
	    //
	    public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

	        protected int visibleRow;

	        public TreeTableCellRenderer(TreeModel model) {
	            super(model);
	            setCellRenderer(new DebugTreeCellRenderer());
	        }

	        public void setBounds(int x, int y, int w, int h) {
	            super.setBounds(x, 0, w, DebugTreeTable.this.getHeight());
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

	            this.setFont(SMALL_FONT);  

	            visibleRow = row;
	            return this;
	        }
	        
	        
	    }

	    public TreeTableModel getTreeTableModel() {
	        return mTreeTableModel;
	    }
}
