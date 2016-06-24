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

/*
 */
package com.qspin.qtaste.ui;

import static com.qspin.qtaste.config.StaticConfiguration.DEFAULT_TESTSUITES_DIR;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
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
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.csveditor.TestDataEditor;
import com.qspin.qtaste.ui.jedit.NonWrappingTextPane;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.HTMLDocumentLoader;
import com.qspin.qtaste.ui.tools.PythonTestScript;
import com.qspin.qtaste.ui.tools.TestCaseTreeCellRenderer;
import com.qspin.qtaste.ui.tools.TestScriptCreation;
import com.qspin.qtaste.ui.tools.TestSuiteRunDialog;
import com.qspin.qtaste.util.DirectoryUtilities;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.GeneratePythonlibDoc;
import com.qspin.qtaste.util.Log4jLoggerFactory;

@SuppressWarnings("serial")
public class TestCaseTree extends JTree implements DragSourceListener, DropTargetListener, DragGestureListener {

    static DataFlavor localObjectFlavor;

    static {
        try {
            localObjectFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    static DataFlavor[] supportedFlavors = {localObjectFlavor};

    private static Logger logger = Log4jLoggerFactory.getLogger(TestCaseTree.class);
    private TestCaseTree mTestCaseTree;
    protected TestCasePane testCasePane;
    private static final String TEST_CASE_TAB_ON_SELECT_PROPERTY = "test_case_tab_on_select";
    DragSource ds;
    DropTarget dt;

    Transferable transferable;

    public TestCaseTree(TestCasePane testCasePn) {
        super();
        mTestCaseTree = this;
        this.setCellRenderer(new TestCaseTreeCellRenderer());

        testCasePane = testCasePn;
        testCasePane.setTestCaseTree(this);
        ToolTipManager.sharedInstance().registerComponent(this);

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

        // drag drop initialization
        ds = new DragSource();
        dt = new DropTarget();

        ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        try {
            dt.setComponent(this);
            dt.addDropTargetListener(this);
        } catch (java.util.TooManyListenersException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (getRowForLocation(e.getX(), e.getY()) == -1) {
            return null;
        } else {
            TreePath tp = this.getPathForLocation(e.getX(), e.getY());
            TreeNode node = this.getTreeNode(tp);
            if (node != null) {
                TCTreeNode tcTreeNode = (TCTreeNode) node;
                if ((tcTreeNode.getUserObject() != null) && (tcTreeNode.getUserObject() instanceof FileNode)) {
                    FileNode fNode = (FileNode) tcTreeNode.getUserObject();
                    //node
                    if (fNode.isTestcaseDir()) {
                        if (fNode.isTestcaseCheckOk()) {
                            // compute the number of test cases (based on test data)
                            String text = fNode.getTestcaseCount() + " testcase(s) defined.";
                            String testcaseHeader = fNode.getTestcaseHeader();
                            if (!testcaseHeader.isEmpty()) {
                                text += "\n\nDescription:\n" + testcaseHeader;
                            }
                            return text;
                        } else {
                            return "no TestData file found or file is empty.";
                        }
                    }
                }
            }
            return null;
        }
    }

    public TestCasePane getTestCasePane() {
        return testCasePane;
    }

    public TCTreeNode generateScriptsTree() {
        return generateScriptsTree(createRootFileNode());
    }

    public TCTreeNode generateScriptsTree(FileNode rootFileNode) {
        // remove previous nodes if needed
        TCTreeNode rootNode = (TCTreeNode) getModel().getRoot();
        rootNode.removeAllChildren();
        rootNode.setUserObject(rootFileNode);
        addTreeToDir(rootFileNode.getFile(), rootNode);
        updateUI();
        return rootNode;
    }

    protected FileNode createRootFileNode() {
        String scriptDir = DEFAULT_TESTSUITES_DIR;
        return new FileNode(new File(scriptDir), "Test Cases", getTestCasePane().getTestSuiteDirectory());
    }

    public void generateScriptsTree(String testSuiteDir) {
        FileNode tcFn = new FileNode(new File(testSuiteDir), "Test Cases", getTestCasePane().getTestSuiteDirectory());
        generateScriptsTree(tcFn);
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
                if (childFile.getName().equals(StaticConfiguration.TEST_SCRIPT_FILENAME)) {
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
                FileNode childNode = new FileNode(childFile, childFile.getName(), getTestCasePane().getTestSuiteDirectory());
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
        FileNode fn = new FileNode(file, file.getName(), getTestCasePane().getTestSuiteDirectory());
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

    protected void setTestCaseDoc(final File f, final boolean activateSourceTab) {
        if (f == null) {
            testCasePane.setTestCaseInfo(null);
        } else {
            try {
                HTMLDocumentLoader loader = new HTMLDocumentLoader();
                HTMLDocument doc = loader.loadDocument(f.toURI().toURL());
                testCasePane.setTestCaseInfo(doc);
                if (activateSourceTab) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.DOC_INDEX);
                }
            } catch (IOException ex) {
                logger.error(ex);
                testCasePane.setTestCaseInfo(null);
            }
        }
    }

    protected void setTestCaseSource(final File f, final boolean activateSourceTab) {
        testCasePane.loadTestCaseSource(f, activateSourceTab, true);
    }

    protected void setTestCaseData(final File f, final boolean activateSourceTab) {
        if (f.exists() && f.canRead()) {
            try {
                testCasePane.loadCSVFile(f.getAbsoluteFile().getCanonicalPath());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    protected void setTestCaseRequirement(final File f, final boolean activateSourceTab) {
        if (f.exists() && f.canRead()) {
            try {
                testCasePane.loadXMLFile(f.getAbsoluteFile().getCanonicalPath());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void loadSelectedTestCase(TreePath path) {
        if (path != null) {
            FileNode fn = getFileNode(path);
            if (fn != null && fn.isTestcaseDir()) {
                File testcaseFile = fn.getTestcaseFile();
                if (testCasePane.getTestScripPane() != null) {
                    try {
                        if (testcaseFile.getAbsoluteFile().getCanonicalPath().equals(
                              testCasePane.getTestScripPane().getFileName())) {
                            return;
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return;
                    }
                }
                if (testcaseFile != null) {
                    testCasePane.setCurrentSelectedFileNode(fn);
                    if (fn.getFile().getName().equals("TestSuite")) {
                        testCasePane.setCurrentSelectedTestsuite("TestSuite");
                    } else {
                        String parentDir = testcaseFile.getParent();
                        testCasePane.setCurrentSelectedTestsuite(parentDir);
                    }
                    setTestCaseSource(testcaseFile, false);

                }
                File testcaseData = fn.getPythonTestScript().getTestcaseData();
                if (testcaseData != null) {
                    setTestCaseData(testcaseData, false);
                }
                File testcaseRequirement = fn.getPythonTestScript().getTestcaseRequirements();
                if (testcaseRequirement != null) {
                    setTestCaseRequirement(testcaseRequirement, false);
                }
                //  regenerate the doc if file date of script > file date of doc
                PythonTestScript script = fn.getPythonTestScript();

                if (GeneratePythonlibDoc.hasAlreadyRunOneTime()) {
                    boolean generateDoc = testCasePane.isDocTabSelected() && !script.isDocSynchronized();

                    if (generateDoc) {
                        testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        script.generateDoc();
                        // Generate the documentation
                        setTestCaseDoc(script.getTestcaseDoc(), false);
                        testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    } else
                    // update the screen with the doc of the selected test script
                    {
                        setTestCaseDoc(script.getTestcaseDoc(), false);
                    }
                } else {
                    setTestCaseDoc(null, false);
                }

                // Get the user preferences to display the testcase tab
                GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
                String testCaseTabOnSelect = "none"; // default
                if (guiConfiguration.containsKey(TEST_CASE_TAB_ON_SELECT_PROPERTY)) {
                    testCaseTabOnSelect = guiConfiguration.getString(TEST_CASE_TAB_ON_SELECT_PROPERTY).toLowerCase();
                } else {
                    guiConfiguration.setProperty(TEST_CASE_TAB_ON_SELECT_PROPERTY, testCaseTabOnSelect);
                    try {
                        guiConfiguration.save();
                    } catch (ConfigurationException ex) {
                        logger.error("Error while saving GUI configuration: " + ex.getMessage());
                    }
                }
                if (testCaseTabOnSelect.equals("doc")) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.DOC_INDEX);
                } else if (testCaseTabOnSelect.equals("source")) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.SOURCE_INDEX);
                } else if (testCaseTabOnSelect.equals("results")) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.RESULTS_INDEX);
                } else if (testCaseTabOnSelect.equals("logs")) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.LOGS_INDEX);
                } else if (!testCaseTabOnSelect.equals("none")) {
                    logger.warn("Invalid value for GUI configuration property " + TEST_CASE_TAB_ON_SELECT_PROPERTY + " ("
                          + guiConfiguration.getString(TEST_CASE_TAB_ON_SELECT_PROPERTY) + ")");
                }
            }
        }
    }

    protected TCTreeNode getSelectedTreeNode() {
        return getTreeNode(getSelectionPath());
    }

    protected TCTreeNode getTreeNode(TreePath path) {
        if (path == null) {
            return null;
        }
        Object obj = path.getLastPathComponent();
        if (obj instanceof TCTreeNode) {
            return (TCTreeNode) obj;
        } else {
            return null;
        }
    }

    protected FileNode getSelectedFileNode() {
        return getFileNode(getSelectionPath());
    }

    protected FileNode getFileNode(TreePath path) {
        TCTreeNode tn = getTreeNode(path);
        if (tn != null) {
            return (FileNode) tn.getUserObject();
        } else {
            return null;
        }
    }

    public void dragEnter(DragSourceDragEvent dsde) {
        //
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

    public void dragEnter(DropTargetDragEvent dtde) {
        //
    }

    public void dragOver(DropTargetDragEvent dtde) {
        Point dropPoint = dtde.getLocation();
        // int index = locationToIndex (dropPoint);
        TreePath path = getPathForLocation(dropPoint.x, dropPoint.y);
        if (path == null) {
            dtde.rejectDrag();
            return;
        }
        Object targetNode = path.getLastPathComponent();
        if (targetNode instanceof TCTreeNode) {
            TCTreeNode tcTreeNode = (TCTreeNode) targetNode;
            if (tcTreeNode.getUserObject() instanceof FileNode) {
                FileNode fn = (FileNode) tcTreeNode.getUserObject();
                if (fn.isTestcaseDir()) {
                    dtde.rejectDrag();
                } else {
                    try {
                        TCTreeNode sourceNode = (TCTreeNode) dtde.getTransferable().getTransferData(localObjectFlavor);
                        if (tcTreeNode.equals(sourceNode.getParent()) || sourceNode.isNodeDescendant(tcTreeNode)) {
                            // reject copy or move in same directory or in a descendant directory
                            dtde.rejectDrag();
                        } else {
                            dtde.acceptDrag(dtde.getDropAction());
                        }
                    } catch (Exception pE) {
                        dtde.rejectDrag();
                    }
                }
            }
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        //
    }

    public void dragExit(DropTargetEvent dte) {
        //
    }

    public void drop(DropTargetDropEvent dtde) {
        try {
            TCTreeNode tcTreeNode = (TCTreeNode) dtde.getTransferable().getTransferData(localObjectFlavor);
            Point dropPoint = dtde.getLocation();
            // int index = locationToIndex (dropPoint);
            TreePath path = getPathForLocation(dropPoint.x, dropPoint.y);
            Object targetNode = path.getLastPathComponent();
            if (targetNode instanceof TCTreeNode) {
                // rename the dragged dir into the new target one
                TCTreeNode tcTargetNode = (TCTreeNode) targetNode;
                FileNode fn = (FileNode) tcTargetNode.getUserObject();
                if (fn.isTestcaseDir()) {
                    dtde.rejectDrop();
                    return;
                }

                FileNode draggedFileNode = (FileNode) tcTreeNode.getUserObject();
                Path source = draggedFileNode.getFile().toPath();
                Path dest = fn.getFile().toPath().resolve(source.getFileName());
                if (dtde.getDropAction() == DnDConstants.ACTION_COPY) {
                    FileUtils.copyDirectory(source.toFile(), dest.toFile());
                } else {
                    Files.move(source, dest);
                }
                // update target tree

                testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                TCTreeNode parentTreeNode = (TCTreeNode) tcTargetNode.getParent();
                if (parentTreeNode != null) {
                    parentTreeNode.removeAllChildren();
                    FileNode parentFileNode = (FileNode) parentTreeNode.getUserObject();
                    addTreeToDir(parentFileNode.getFile(), parentTreeNode);
                    ((DefaultTreeModel) getModel()).reload(parentTreeNode);
                } else {
                    tcTargetNode.removeAllChildren();
                    FileNode targetFileNode = (FileNode) tcTargetNode.getUserObject();
                    addTreeToDir(targetFileNode.getFile(), tcTargetNode);
                    ((DefaultTreeModel) getModel()).reload(tcTargetNode);
                }
                // update source tree
                parentTreeNode = (TCTreeNode) tcTreeNode.getParent();
                if (parentTreeNode != null) {
                    parentTreeNode.removeAllChildren();
                    FileNode parentFileNode = (FileNode) parentTreeNode.getUserObject();
                    addTreeToDir(parentFileNode.getFile(), parentTreeNode);
                    ((DefaultTreeModel) getModel()).reload(parentTreeNode);
                } else {
                    tcTreeNode.removeAllChildren();
                    FileNode targetFileNode = (FileNode) tcTreeNode.getUserObject();
                    addTreeToDir(targetFileNode.getFile(), tcTreeNode);
                    ((DefaultTreeModel) getModel()).reload(tcTreeNode);
                }
                testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                dtde.getDropTargetContext().dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        Transferable trans = new TCTreeNodeTransferable(this.getSelectionPath().getLastPathComponent());
        ds.startDrag(dge, null, trans, this);
    }

    /**
     * Get the option pane component from the component tree.
     *
     * @param parent the component to check.
     * @return the option pane.
     */
    protected JOptionPane getOptionPane(JComponent parent) {
        JOptionPane pane;
        if (!(parent instanceof JOptionPane)) {
            pane = getOptionPane((JComponent) parent.getParent());
        } else {
            pane = (JOptionPane) parent;
        }
        return pane;
    }

    /**
     * Show an input dialog to enter a new script name.
     * This input dialog is initialized with the selected script name and
     * disables the 'OK' button while the new script name is empty or is the
     * same than the previous one.
     *
     * @param title title of the input dialog
     * @param message message to display inside the input dialog
     * @param initialValue initial value for the text field.
     * @return the new script name or null.
     */
    public String showOptionDialog(final String title, final String message, final String initialValue) {
        JPanel panel = new JPanel();
        final JButton okayBtn = new JButton("Ok");
        final JButton cancelBtn = new JButton("Cancel");
        final JTextField textField = new JTextField(initialValue);
        String newName = null;

        // create a custom panel to display the message and the text field
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(message));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(textField);

        // configure button action listeners to return the button instance when it is clicked.
        okayBtn.addActionListener(e -> {
            JOptionPane pane = getOptionPane((JComponent) e.getSource());
            pane.setValue(okayBtn);
        });
        okayBtn.setEnabled(false);

        cancelBtn.addActionListener(e -> {
            JOptionPane pane = getOptionPane((JComponent) e.getSource());
            pane.setValue(cancelBtn);
        });

        // configure the text field document listener to check that the new text entered is 
        // not empty and is not equal to the initial value.
        textField.getDocument().addDocumentListener(new DocumentListener() {
            protected void update() {
                okayBtn.setEnabled(!textField.getText().equals(initialValue) && (textField.getText().length() > 0));
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        // show the custom option dialog
        int result = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
              null, new Object[] {okayBtn, cancelBtn}, okayBtn);

        // showOptionDialog returns the index of the clicked button in the options array
        // here, 0 means 'OK' button and 1 means 'Cancel' button
        if (result == 0) {
            newName = textField.getText();
        }

        return newName;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////
    public class TCTreeListener extends MouseAdapter implements TreeWillExpandListener, TreeSelectionListener {

        public void treeWillCollapse(TreeExpansionEvent event) {
        }

        protected void addNodesToDir(TreePath path) {
            TCTreeNode tn = getTreeNode(path);
            if (tn != null) {
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
            TreePath path = e.getNewLeadSelectionPath();
            loadSelectedTestCase(path);
        }

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // force selection of clicked row
                int rowId = getRowForLocation(e.getX(), e.getY());
                setSelectionRow(rowId);

                // display a popup menu to run/debug a test
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new TestRunAction(false));
                menu.add(new TestRunAction(true));
                menu.add(new TestDebugAction());
                menu.add(new TestGenerateDocAction());
                menu.add(new TestExternalEditAction());
                menu.add(new TestOpenFolderAction());
                menu.addSeparator();
                Action createTestAction = new CreateNewTestSuite();
                Action removeTestAction = null;
                Action renameTestAction = null;
                TreePath selectedPath = getSelectionPath();
                if (selectedPath == null) {
                    return;
                }
                FileNode fn = getFileNode(selectedPath);
                if (fn != null) {
                    if (fn.isTestcaseDir()) {
                        createTestAction.putValue(Action.NAME, "Copy TestScript...");
                        removeTestAction = new RemoveTestScript();
                        renameTestAction = new RenameTestScript();
                        renameTestAction.putValue(Action.NAME, "Rename TestScript...");
                        removeTestAction.putValue(Action.NAME, "Remove TestScript...");

                    } else {
                        createTestAction.putValue(Action.NAME, "Create new TestScript...");
                        if (selectedPath.getParentPath() == null) {
                            menu.add(new ImportTestSuiteAction());
                        }
                    }
                }
                menu.add(createTestAction);
                if (renameTestAction != null) {
                    menu.add(renameTestAction);
                }
                if (removeTestAction != null) {
                    menu.add(removeTestAction);
                }
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), mTestCaseTree);
                TreePath path = mTestCaseTree.getPathForLocation(pt.x, pt.y);
                mTestCaseTree.setSelectionPath(path);

                menu.show(mTestCaseTree, pt.x, pt.y);
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

    class TestRunAction extends AbstractAction {

        private boolean mLoop;

        public TestRunAction(boolean loop) {
            super(loop ? "Run in loop..." : "Run");
            mLoop = loop;
        }

        public void actionPerformed(ActionEvent e) {
            if (mLoop) {
                // show RunDialog
                TestSuiteRunDialog runDlg = new TestSuiteRunDialog(new javax.swing.JFrame(), "Run options");
                runDlg.setAlwaysOnTop(true);
                runDlg.setVisible(true);
                if (!runDlg.IsCancelled) {
                    getTestCasePane().runTestSuite(false, runDlg.getNumberOfLoops(), runDlg.isLoopsInTime());
                }
            } else {
                getTestCasePane().runTestSuite(false);
            }
        }

        @Override
        public boolean isEnabled() {
            return !getTestCasePane().isExecuting && getTestCasePane().isEnabledToExecute;
        }
    }

    class TestDebugAction extends AbstractAction {

        public TestDebugAction() {
            super("Debug");
        }

        public void actionPerformed(ActionEvent e) {
            // get the selected table lines
            getTestCasePane().runTestSuite(true);
        }

        @Override
        public boolean isEnabled() {
            return !getTestCasePane().isExecuting && getTestCasePane().isEnabledToExecute;
        }
    }

    class TestGenerateDocAction extends AbstractAction {

        public TestGenerateDocAction() {
            super("Generate documentation");
        }

        public void actionPerformed(ActionEvent e) {
            FileNode fn = getSelectedFileNode();
            if (fn == null) {
                return;
            }
            if (fn.getPythonTestScript() == null) {
                return;
            }
            GeneratePythonlibDoc.generate();
            testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File testcaseDoc = fn.getPythonTestScript().generateDoc();
            testCasePane.parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // refresh the test case window
            setTestCaseDoc(testcaseDoc, true);
        }

        @Override
        public boolean isEnabled() {
            FileNode fn = getSelectedFileNode();
            return fn != null && fn.isTestcaseDir() && GeneratePythonlibDoc.hasAlreadyRunOneTime();
        }
    }

    class TestExternalEditAction extends AbstractAction {

        public TestExternalEditAction() {
            super("Edit in external editor");
        }

        public void actionPerformed(ActionEvent e) {
            FileNode fn = getSelectedFileNode();
            try {
                Desktop.getDesktop().edit(fn.getTestcaseFile().getAbsoluteFile());
            } catch (IOException ex) {
                logger.error("Error while calling Desktop edit on " + fn.getTestcaseFile().getAbsoluteFile());
            }
        }

        @Override
        public boolean isEnabled() {
            FileNode fn = getSelectedFileNode();
            return fn != null && fn.isTestcaseDir() && Desktop.isDesktopSupported();
        }
    }

    class TestOpenFolderAction extends AbstractAction {

        public TestOpenFolderAction() {
            super("Open folder");
        }

        public void actionPerformed(ActionEvent e) {
            FileNode fn = getSelectedFileNode();
            try {
                Desktop.getDesktop().open(fn.getFile().getAbsoluteFile());
            } catch (IOException ex) {
                logger.error("Error while calling Desktop open on " + fn.getFile().getAbsoluteFile());
            }
        }

        @Override
        public boolean isEnabled() {
            FileNode fn = getSelectedFileNode();
            return fn != null && Desktop.isDesktopSupported();
        }
    }

    class CreateTestFolder extends AbstractAction {

        public CreateTestFolder() {
            super("Create folder");
        }

        public void actionPerformed(ActionEvent e) {
            FileNode fn = getSelectedFileNode();
            String input = JOptionPane.showInputDialog(null, "Give the name of the folder", "folder name:",
                  JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return;
            }
            File testScriptFile = fn.getFile();
            testScriptFile.renameTo(new File(input));
        }
    }

    class RenameTestScript extends AbstractAction {

        public RenameTestScript() {
            super("Rename TestScript");
        }

        public void actionPerformed(ActionEvent e) {
            TCTreeNode tn = getSelectedTreeNode();

            String testName = tn.toString();
            FileNode fn = (FileNode) tn.getUserObject();

            String input = showOptionDialog("Rename the script " + testName, "Give the new name of the test " + testName,
                  testName);

            if (input == null) {
                return;
            }

            // if doc tab is opened, ensure close it first
            testCasePane.getDocPane().setText("");
            File newFile = new File(fn.getFile().getParent() + "/" + input);
            boolean result = fn.getFile().renameTo(newFile);
            if (!result) {
                logger.error("Impossible to rename " + fn.getFile().getName() + " to " + input);
                return;
            }
            String testScriptFileName;
            try {
                // rename necessary classes
                fn.setFile(newFile);
                testScriptFileName = fn.getFile().getCanonicalPath() + "/" + StaticConfiguration.TEST_SCRIPT_FILENAME;
                fn.getPythonTestScript().setTestScriptFile(new File(testScriptFileName));
                // rename the testscript name
                NonWrappingTextPane tcPane = testCasePane.getTcSourceTextPane();
                if (tcPane != null) {
                    tcPane.setFileName(testScriptFileName);
                }
                // rename the testdata name
                TestDataEditor tcDataPane = testCasePane.getTestDataPane();
                if (tcDataPane != null) {
                    tcDataPane.setFileName(fn.getPythonTestScript().getTestcaseData().getCanonicalPath());
                }

                TCTreeNode parentTreeNode = (TCTreeNode) tn.getParent();
                parentTreeNode.removeAllChildren();
                FileNode parentFileNode = (FileNode) parentTreeNode.getUserObject();
                addTreeToDir(parentFileNode.getFile(), parentTreeNode);
                ((DefaultTreeModel) getModel()).reload(parentTreeNode);
                // reload the doc is selected
                setTestCaseDoc(fn.getPythonTestScript().getTestcaseDoc(), false);
                //
            } catch (IOException e1) {
                logger.error(e1.getMessage());
            }

        }

    }

    class RemoveTestScript extends AbstractAction {

        public RemoveTestScript() {
            super("Remove TestScript");
        }

        public void actionPerformed(ActionEvent e) {
            // confirmation dialog
            TCTreeNode tn = getSelectedTreeNode();

            String testName = tn.toString();
            FileNode fn = (FileNode) tn.getUserObject();
            if (fn.isTestcaseDir()) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure to remove the script '" + testName + "'", "Confirmation",
                      JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                    // remove the test script directory
                    File testScriptFile = fn.getFile();
                    boolean deleted = DirectoryUtilities.deleteDirectory(testScriptFile);
                    if (deleted) {
                        TCTreeNode parentTreeNode = (TCTreeNode) tn.getParent();
                        parentTreeNode.removeAllChildren();
                        FileNode parentFileNode = (FileNode) parentTreeNode.getUserObject();
                        addTreeToDir(parentFileNode.getFile(), parentTreeNode);
                        ((DefaultTreeModel) getModel()).reload(parentTreeNode);
                    } else {
                        JOptionPane.showConfirmDialog(null, "Impossible to delete " + testName, "Error", JOptionPane.OK_OPTION,
                              JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    class ImportTestSuiteAction extends AbstractAction {

        public ImportTestSuiteAction() {
            super("Import Testsuite directory");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getTestCasePane().importTestSuites();

        }
    }

    class CreateNewTestSuite extends AbstractAction {

        public CreateNewTestSuite() {
            super("Create new TestScript");
        }

        public void actionPerformed(ActionEvent e) {
            TCTreeNode tn = getSelectedTreeNode();
            String testName = tn.toString();

            String input = showOptionDialog("Create a script from " + testName, "Give the new name of the test", testName);

            if (input == null) {
                return;
            }

            try {
                FileNode fn = (FileNode) tn.getUserObject();
                if (fn.isTestcaseDir()) {
                    // get the source Dir
                    TestScriptCreation createScriptTool = new TestScriptCreation(input,
                          fn.getFile().getParentFile().getAbsoluteFile().getCanonicalPath());
                    createScriptTool.copyTestSuite(fn.getFile().getAbsoluteFile().getCanonicalPath());
                    tn = (TCTreeNode) tn.getParent();
                    fn = (FileNode) tn.getUserObject();
                } else {
                    TestScriptCreation createScriptTool = new TestScriptCreation(input,
                          fn.getFile().getAbsoluteFile().getCanonicalPath());
                    createScriptTool.createTestSuite();
                }
                // update the tree view
                // add the tree

                tn.removeAllChildren();
                addTreeToDir(fn.getFile(), tn);
                ((DefaultTreeModel) getModel()).reload(tn);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        public boolean isEnabled() {
            FileNode fn = getSelectedFileNode();
            if (fn != null) {
                if (fn.isTestcaseDir()) {
                    this.putValue(Action.NAME, "Copy TestScript");
                    return true;
                } else {
                    this.putValue(Action.NAME, "Create new TestScript");
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public class TCTreeNodeTransferable implements Transferable {
        Object object;

        public TCTreeNodeTransferable(Object o) {
            object = o;
        }

        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(df)) {
                return object;
            } else {
                throw new UnsupportedFlavorException(df);
            }
        }

        public boolean isDataFlavorSupported(DataFlavor df) {
            return (df.equals(localObjectFlavor));
        }

        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }
    }
}
