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

package com.qspin.qtaste.ui.testcampaign;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.TCTreeNode;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.TestCaseTreeCellRenderer;
import com.qspin.qtaste.ui.treetable.JTreeTable;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

@SuppressWarnings("serial")
public class TestCaseTree extends JTree implements DragSourceListener, DropTargetListener, DragGestureListener {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestCaseTree.class);
    private final String TESTUITE_DIR = "TestSuites";
    private TestCaseTree mTestCaseTree;
    private JTreeTable treeTable;

    DragSource ds;
    DropTarget dt;

    StringSelection transferable;

    public TestCaseTree(JTreeTable destinationComponent) {
        super();
        ds = new DragSource();
        dt = new DropTarget();
        mTestCaseTree = this;
        this.treeTable = destinationComponent;

        ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
        try {
            dt.setComponent(destinationComponent);
            dt.addDropTargetListener(this);
        } catch (java.util.TooManyListenersException e) {
            System.out.println("Too many Listeners");
        }

        this.setCellRenderer(new TestCaseTreeCellRenderer());

        FileNode rootFileNode = createRootFileNode();

        TCTreeNode rootNode = new TCTreeNode(rootFileNode, true);
        DefaultTreeModel tm = new DefaultTreeModel(rootNode);
        setModel(tm);
        generateScriptsTree(rootFileNode);
        TCTreeListener listener = new TCTreeListener();
        this.addMouseListener(listener);
        addTreeWillExpandListener(listener);
        addTreeSelectionListener(listener);
        TreeSelectionModel selModel = this.getSelectionModel();
        selModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public TCTreeNode generateScriptsTree() {
        return generateScriptsTree(createRootFileNode());
    }

    public TCTreeNode generateScriptsTree(FileNode rootFileNode) {
        TCTreeNode rootNode = (TCTreeNode) getModel().getRoot();
        rootNode.removeAllChildren();
        rootNode.setUserObject(rootFileNode);
        addTreeToDir(rootFileNode.getFile(), rootNode);
        updateUI();
        return rootNode;
    }

    protected FileNode createRootFileNode() {
        String scriptDir = TESTUITE_DIR;
        return new FileNode(new File(scriptDir), "Test Cases", TESTUITE_DIR);
    }

    protected void addTreeToDir(File file, DefaultMutableTreeNode parentNode) {
        if (file.isDirectory()) {
            File[] childFiles = FileUtilities.listSortedFiles(file);
            for (File childFile : childFiles) {
                addChildToTree(childFile, parentNode);
            }
        }
    }

    protected boolean isTestcaseDir(File file) {
        if (file.isDirectory()) {
            File[] childFiles = FileUtilities.listSortedFiles(file);
            for (File childFile : childFiles) {
                if (StaticConfiguration.TEST_SCRIPT_FILENAME.equals(childFile.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean checkIfDirectoryContainsTestScriptFile(File file) {
        File[] childFiles = FileUtilities.listSortedFiles(file);
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                FileNode childNode = new FileNode(childFile, childFile.getName(), TESTUITE_DIR);
                if (childNode.isTestcaseDir()) {
                    return true;
                } else {
                    // go recursively into its directory
                    boolean result = checkIfDirectoryContainsTestScriptFile(childFile);
                    if (result) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void addChildToTree(File file, DefaultMutableTreeNode parent) {
        if (!file.isDirectory()) {
            return;
        }
        FileNode fn = new FileNode(file, file.getName(), TESTUITE_DIR);
        // check if the directory is the child one containing data files
        boolean nodeToAdd = fn.isTestcaseDir();
        if (!fn.isTestcaseDir()) {
            // go recursilvely to its child and check if it must be added
            nodeToAdd = checkIfDirectoryContainsTestScriptFile(file);
        }
        if (!nodeToAdd) {
            return;
        }
        TCTreeNode node = new TCTreeNode(fn, !fn.isTestcaseDir());
        final int NON_EXISTENT = -1;
        if (parent.getIndex(node) == NON_EXISTENT && !file.isHidden()) {
            parent.add(node);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////
    public class TCTreeListener extends MouseAdapter implements TreeWillExpandListener, TreeSelectionListener {

        public void treeWillCollapse(TreeExpansionEvent event) {
        }

        protected void addNodesToDir(TreePath path) {
            Object obj = path.getLastPathComponent();
            if (obj instanceof TCTreeNode) {
                TCTreeNode tn = (TCTreeNode) obj;
                FileNode fn = (FileNode) tn.getUserObject();
                if (fn.isDir()) {
                    tn.removeAllChildren();
                    addTreeToDir(fn.getFile(), tn);
                    ((DefaultTreeModel) getModel()).reload(tn);
                }
            }
        }

        public void treeWillExpand(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            addNodesToDir(path);
        }

        public void valueChanged(TreeSelectionEvent e) {
            //TreePath path = e.getNewLeadSelectionPath();
        }

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // display a popup menu to run/debug a test
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new SelectAllAction(true));
                menu.add(new SelectAllAction(false));
                menu.addSeparator();
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), mTestCaseTree);
                TreePath path = mTestCaseTree.getPathForLocation(pt.x, pt.y);
                mTestCaseTree.setSelectionPath(path);

                menu.show(mTestCaseTree, pt.x, pt.y);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (e.getClickCount() == 2) { // double click
                    // add the testcase into the test campaign
                    String selectedPath = ((FileNode) ((TCTreeNode) getSelectionPath().getLastPathComponent()).getUserObject())
                          .getFile().getPath();
                    TestCaseTree.this.addSelectedTestdirectoryToMetaCampaign(selectedPath);
                }
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

    public void dragEnter(DragSourceDragEvent dsde) {

    }

    public void dragOver(DragSourceDragEvent dsde) {
        //
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        //
    }

    public void dragExit(DragSourceEvent dse) {
        //
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        //
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        transferable = new StringSelection(
              ((FileNode) ((TCTreeNode) this.getSelectionPath().getLastPathComponent()).getUserObject()).getFile().getPath());
        //dge.startDrag(DragSource.DefaultCopyDrop, transferable);
        ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        //
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        //
    }

    public void dragExit(DropTargetEvent dte) {
        //
    }

    public void addSelectedTestdirectoryToMetaCampaign(String directory) {
        TestCampaignTreeModel model = (TestCampaignTreeModel) treeTable.getTreeTableModel();
        directory = directory.replace('\\', '/');
        model.addTestSuite(directory);
        treeTable.expandPath(directory);
        treeTable.repaint();
    }

    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable tr = dtde.getTransferable();
            if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String data = (String) tr.getTransferData(DataFlavor.stringFlavor);
                // create the new TreeTable containing the selected dragged path
                this.addSelectedTestdirectoryToMetaCampaign(data);
                dtde.getDropTargetContext().dropComplete(true);
            } else {
                System.err.println("Rejected");
                dtde.rejectDrop();
            }
        } catch (IOException | UnsupportedFlavorException io) {
            logger.error(io);
            dtde.rejectDrop();
        }
    }

    class SelectAllAction extends AbstractAction {

        boolean select;

        public SelectAllAction(boolean select) {
            super(select ? "Select all" : "Unselect all");
        }

        public void actionPerformed(ActionEvent e) {
            // get the selected table lines
            // show RunDialog
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
