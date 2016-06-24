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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.debug.Breakpoint;
import com.qspin.qtaste.event.DumpPythonResultEvent;
import com.qspin.qtaste.event.DumpPythonResultEventHandler;
import com.qspin.qtaste.event.DumpPythonResultListener;
import com.qspin.qtaste.event.TestScriptBreakpointEvent;
import com.qspin.qtaste.event.TestScriptBreakpointHandler;
import com.qspin.qtaste.event.TestScriptBreakpointListener;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.testsuite.impl.DirectoryTestSuite;
import com.qspin.qtaste.ui.csveditor.TestDataEditor;
import com.qspin.qtaste.ui.debug.DebugVariable;
import com.qspin.qtaste.ui.debug.DebugVariablePanel;
import com.qspin.qtaste.ui.jedit.DebuggerShortcuts;
import com.qspin.qtaste.ui.jedit.GenericShortcuts;
import com.qspin.qtaste.ui.jedit.NonWrappingTextPane;
import com.qspin.qtaste.ui.log4j.Log4jPanel;
import com.qspin.qtaste.ui.log4j.TextAreaAppender;
import com.qspin.qtaste.ui.testcampaign.TestCampaignMainPanel;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.HTMLDocumentLoader;
import com.qspin.qtaste.ui.tools.PythonTestScript;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.xmleditor.TestRequirementEditor;
import com.qspin.qtaste.util.GeneratePythonlibDoc;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.ScriptCheckSyntaxValidator;
import com.qspin.qtaste.util.ThreadManager;

@SuppressWarnings("serial")
public class TestCasePane extends JPanel implements TestScriptBreakpointListener, DumpPythonResultListener {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestCasePane.class);
    protected JTextPane tcDocsPane = new JTextPane();
    protected Log4jPanel tcLogsPane;
    private NonWrappingTextPane tcSourceTextPane = null;
    protected JButton executeButton = new JButton();
    protected JButton startExecutionButton = new JButton();
    protected JButton stepOverExecutionButton = new JButton();
    protected JButton stepIntoExecutionButton = new JButton();
    protected JButton stopExecutionButton = new JButton();
    protected JButton saveButton = new JButton();
    protected JButton resultsButton = new JButton("View Test Report");
    protected JButton debugButton = new JButton();
    protected JTabbedPane editorTabbedPane;
    protected JPanel tcSourcePanel = new JPanel(new BorderLayout());
    protected DebugVariablePanel debugPanel;
    public TestCaseTree tcTree;
    protected TestCaseResultsPane resultsPane;
    protected JTabbedPane tabbedPane;
    protected MainPanel parent;
    protected JSplitPane sourcePanel;
    protected boolean stopExecution = false;
    private static final String DOC = "Documentation";
    private static final String SOURCE = "Test Case Source";
    private static final String RESULTS = "Results";
    private static final String LOGS = "Logs";
    private static final String SHOW_LOGS_TAB = "show_logs_tab";
    public static final int DOC_INDEX = 0;
    public static final int SOURCE_INDEX = 1;
    public static final int RESULTS_INDEX = 2;
    public static final int LOGS_INDEX = 3;
    public static final int SRC_TAB_SIZE = 2;
    private TestExecutionThread testExecutionHandler = null;
    private TestCampaignMainPanel.CampaignExecutionThread testCampaignExecutionHandler = null;
    private TestScriptBreakpointHandler breakPointEventHandler;
    public boolean isExecuting = false;
    public boolean isEnabledToExecute = true;
    private String currentSelectedTestsuite = "TestSuite";
    private FileNode currentSelectedFileNode = null;
    protected DumpPythonResultEventHandler pythonResultEventHandler = DumpPythonResultEventHandler.getInstance();
    private String mTestCaseSuitesRootDir = StaticConfiguration.DEFAULT_TESTSUITES_DIR;
    protected static final HTMLDocument EMPTY_DOC = new HTMLDocument();

    public TestCasePane(MainPanel parent) {
        super(new BorderLayout());

        GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
        boolean showLogsTab = guiConfiguration.getBoolean(SHOW_LOGS_TAB, true);
        if (showLogsTab) {
            tcLogsPane = new Log4jPanel();
        }

        this.parent = parent;
        resultsPane = new TestCaseResultsPane(this);
        breakPointEventHandler = TestScriptBreakpointHandler.getInstance();
        breakPointEventHandler.addTestScriptBreakpointListener(this);
        pythonResultEventHandler.addPythonResultListener(this);
        genUI();
    }

    public void addTabPane(JScrollPane pPanel, String pTabTile) {
        tabbedPane.addTab(pTabTile, pPanel);
    }

    public void removeTabPane(JScrollPane pEditor) {
        tabbedPane.remove(pEditor);
    }

    @Override
    protected void finalize() {
        breakPointEventHandler.removeTestScriptBreakpointListener(this);
    }

    public void setTestCaseTree(TestCaseTree tcTree) {
        this.tcTree = tcTree;
        resultsPane.setTestCaseTree(tcTree);
    }

    public TestCaseTree getTestCaseTree() {
        return tcTree;
    }

    public boolean isDocTabSelected() {
        return tabbedPane.getSelectedIndex() == DOC_INDEX;
    }

    public void importTestSuites() {

        JFileChooser chooser = new JFileChooser(mTestCaseSuitesRootDir);
        chooser.setDialogTitle("Load TestSuites directory ...");
        //chooser.setFileHidingEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String selectedTestSuites = chooser.getSelectedFile().getAbsoluteFile().getCanonicalPath();
                setTestSuiteDirectory(selectedTestSuites);
                tcTree.generateScriptsTree(selectedTestSuites);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public void setExecuteButtonsEnabled(boolean enabled) {
        executeButton.setEnabled(enabled);
        debugButton.setEnabled(enabled);
        isEnabledToExecute = enabled;
    }

    public void setStopButtonEnabled(boolean enabled, boolean visible) {
        stopExecutionButton.setEnabled(enabled);
        stopExecutionButton.setVisible(visible);
    }

    protected void genUI() {
        executeButton.setMnemonic(KeyEvent.VK_R);
        saveButton.setMnemonic(KeyEvent.VK_C);
        startExecutionButton.setMnemonic(KeyEvent.VK_N);
        tcDocsPane.setEditable(false);
        tcDocsPane.setContentType("text/html");

        if (tcLogsPane != null) {
            TextAreaAppender.addTextArea(tcLogsPane);
        }
        tcDocsPane.setEditorKit(new HTMLEditorKit());
        tcDocsPane.addHyperlinkListener(e -> {
        });

        ExecuteButtonAction buttonListener = new ExecuteButtonAction();
        executeButton.addActionListener(buttonListener);
        executeButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/running_32"));
        executeButton.setToolTipText("Run Test(s)");

        startExecutionButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/start"));
        startExecutionButton.setToolTipText("Continue test case execution (F8)");
        startExecutionButton.setVisible(false);
        startExecutionButton.addActionListener(e -> continueDebug());

        stepOverExecutionButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/stepover"));
        stepOverExecutionButton.setToolTipText("Step over the script execution (F6)");
        stepOverExecutionButton.setVisible(false);
        stepOverExecutionButton.addActionListener(e -> continueStep());

        stepIntoExecutionButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/stepinto"));
        stepIntoExecutionButton.setToolTipText("Step into the script execution (F5)");
        stepIntoExecutionButton.setVisible(false);
        stepIntoExecutionButton.addActionListener(e -> continueStepInto());

        stopExecutionButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/stop"));
        stopExecutionButton.setToolTipText("Stop execution");
        stopExecutionButton.setVisible(false);
        stopExecutionButton.addActionListener(e -> stopExecution());

        debugButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/debug"));
        debugButton.setToolTipText("Debug Test(s)");
        debugButton.addActionListener(e -> runTestSuite(true, 1, false));
        saveButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/save_32"));
        saveButton.setToolTipText("Save and check script(s) syntax");
        saveButton.setName("save button");
        saveButton.addActionListener(e -> {
                if (checkScriptsSyntax()) // display dialog when syntax is correct
                {
                    JOptionPane.showMessageDialog(null, "Syntax checked successfully");
                }
        });

        resultsButton.addActionListener(e -> {
                boolean showTestSuiteReport = resultsPane.getCurrentRunName().equals("Run1");
                String resDir = TestEngineConfiguration.getInstance().getString("reporting.generated_report_path");
                String baseDir = System.getProperty("user.dir");
                String filename =
                      baseDir + File.separator + resDir + File.separator + (showTestSuiteReport ? "index.html" : "campaign.html");
                File resultsFile = new File(filename);
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(resultsFile);
                    } else {
                        logger.error("Feature not supported by this platform");
                    }
                } catch (IOException ex) {
                    logger.error("Could not open " + filename);
                }
        });

        resultsButton.setToolTipText("View the HTML Test Run Summary Results");
        resultsButton.setName("test run results button");

        FormLayout layout = new FormLayout(
              "6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px:grow",
              "6px, fill:pref, 6px");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        int rowIndex = 2;
        int colIndex = 2;
        builder.add(executeButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(saveButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(debugButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(resultsButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(new CommonShortcutsPanel(), cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(startExecutionButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(stepOverExecutionButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(stepIntoExecutionButton, cc.xy(colIndex, rowIndex));
        colIndex += 2;
        builder.add(stopExecutionButton, cc.xy(colIndex, rowIndex));

        JPanel northP = builder.getPanel();
        add(northP, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        editorTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        editorTabbedPane.addMouseListener(new TabMouseListener());
        editorTabbedPane.setFocusable(false);
        sourcePanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        getTcSourcePane().add(editorTabbedPane);
        sourcePanel.setTopComponent(getTcSourcePane());
        sourcePanel.setFocusable(false);

        sourcePanel.setDividerSize(4);

        debugPanel = new DebugVariablePanel();
        debugPanel.setPreferredSize(new Dimension(100, 150));
        sourcePanel.setResizeWeight(0.9);
        sourcePanel.setBottomComponent(debugPanel);
        ////////////////////////
        debugPanel.setVisible(false);
        //sourcePanel.add(debugPanel, BorderLayout.SOUTH);
        tabbedPane.addChangeListener(e -> {
                // transfer focus to editor if necessary
                if (tabbedPane.getSelectedIndex() == SOURCE_INDEX) {
                    Component tab = editorTabbedPane.getSelectedComponent();
                    if (tab != null && tab instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) tab;
                        tab = scrollPane.getViewport().getView();
                        if (tab != null) {
                            tab.requestFocusInWindow();
                        }
                    }
                } else {
                    Component tab = tabbedPane.getSelectedComponent();
                    if (tab != null && tab instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) tab;
                        tab = scrollPane.getViewport().getView();
                        if (tab != null) {
                            tab.requestFocusInWindow();
                        }
                    }
                }
                if (!isDocTabSelected()) {
                    return;
                }
                // generate doc if necessary
                NonWrappingTextPane tsPane = getTcSourceTextPane();
                if (tsPane != null) {
                    File tsFile = new File(tsPane.getFileName());
                    PythonTestScript pScript = new PythonTestScript(tsFile, getTestSuiteDirectory());
                    if (pScript.isDocSynchronized() || !GeneratePythonlibDoc.hasAlreadyRunOneTime()) {
                        return;
                    }
                    // re-generate the doc
                    parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    pScript.generateDoc();
                    HTMLDocumentLoader loader = new HTMLDocumentLoader();
                    HTMLDocument doc;
                    try {
                        File tcDoc = pScript.getTestcaseDoc();
                        if (tcDoc != null) {
                            doc = loader.loadDocument(tcDoc.toURI().toURL());
                            setTestCaseInfo(doc);
                        } else {
                            setTestCaseInfo(null);
                        }
                    } catch (IOException e1) {
                        logger.error(e1);
                        setTestCaseInfo(null);
                    }
                    parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
        });

        tabbedPane.add(DOC, new JScrollPane(tcDocsPane));
        tabbedPane.add(SOURCE, sourcePanel);

        tabbedPane.add(RESULTS, resultsPane);
        if (tcLogsPane != null) {
            tabbedPane.add(LOGS, tcLogsPane);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void stopExecution() {
        setStopButtonEnabled(false, true);
        stopDebug();
        stopExecution = true;
        TestEngine.setAbortedByUser(); // Flag to terminate test suites
        if (testExecutionHandler != null) {
            testExecutionHandler.stop();
        } else if (testCampaignExecutionHandler != null) {
            testCampaignExecutionHandler.stop();
        }

        if (!TestEngine.isSUTStartedManually()) {
            Thread t = new Thread(TestEngine::cancelStartStopSUT);
            t.start();
        }
    }

    public boolean checkScriptsSyntax() {
        // check the test scripts syntax
        // save and check syntax of the documents
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            NonWrappingTextPane tabTextPane = getTextPane(i);
            if (tabTextPane != null) {
                if (tabTextPane.isModified()) {
                    tabTextPane.save();
                }
                // check only opened Python files
                if (tabTextPane.getFileName().endsWith(".py")) {
                    ScriptCheckSyntaxValidator scriptCheckSyntaxValidator = new ScriptCheckSyntaxValidator(
                          tabTextPane.getFileName(), tabTextPane.getText());
                    if (!scriptCheckSyntaxValidator.check()) {
                        return false;
                    }
                }
            }

            TestDataEditor tabDataPane = this.getTestDataPane(i);
            if (tabDataPane != null && tabDataPane.isModified()) {
                tabDataPane.save();
            }

            TestRequirementEditor requirementPane = this.getTestRequirementPane(i);
            if (requirementPane != null && requirementPane.isModified()) {
                requirementPane.save();
            }
        }

        // check the module imported found in pythonlib

        // check all testdata
        return true;
    }

    public void stopDebug() {
        logger.trace("Test case pane: stop debug");
        breakPointEventHandler.stop();
    }

    public void continueDebug() {
        logger.trace("Test case pane: continue");
        breakPointEventHandler.continue_();
    }

    public void continueStep() {
        logger.trace("Test case pane: continue step");
        breakPointEventHandler.step();
    }

    public void continueStepInto() {
        logger.trace("Test case pane: continue step into");
        breakPointEventHandler.stepInto();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public Log4jPanel getResultsLog4jPanel() {
        return resultsPane.getLog4jPanel();
    }

    public Document getCurrentTestCaseDoc() {
        return tcDocsPane.getDocument();
    }

    public JTextPane getDocPane() {
        return tcDocsPane;
    }

    public void setTestCaseInfo(Document htmlDoc) {
        tcDocsPane.setDocument(htmlDoc == null ? EMPTY_DOC : htmlDoc);
        tcDocsPane.setCaretPosition(0);
        tcDocsPane.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
        tcDocsPane.setEditable(false);
    }

    /**
     * loadTestCaseSource method loads the file contents if necessary. If the
     * file is already loaded, this method activates the correct tab based
     * on the filename
     *
     * @param f : file to load in the JTextPane
     * @param activateSourceTab : specifies if the tab "TestCase Source" must be activated
     * @param isTestScript : specifies if the file to load is the main TestScript
     * @return the JTextPane(NonWrappingTextPane) containing the file content
     */
    public NonWrappingTextPane loadTestCaseSource(final File f, final boolean activateSourceTab, final boolean isTestScript) {
        return this.loadTestCaseSource(f, activateSourceTab, isTestScript, false);
    }

    /**
     * loadTestCaseSource method loads the file contents if necessary. If the
     * file is already loaded, this method activates the correct tab based
     * on the filename
     *
     * @param f : file to load in the JTextPane
     * @param activateSourceTab : specifies if the tab "TestCase Source" must be activated
     * @param isTestScript : specifies if the file to load is the main TestScript
     * @param force : specifies if the file must be read from source
     * @return the JTextPane(NonWrappingTextPane) containing the file content
     */
    public NonWrappingTextPane loadTestCaseSource(final File f, final boolean activateSourceTab, final boolean isTestScript,
          final boolean force) {
        String absolutePath;
        try {
            absolutePath = f.getAbsoluteFile().getCanonicalPath();
        } catch (IOException e) {
            logger.fatal("Cannot load testcase source", e);
            return null;
        }
        NonWrappingTextPane textPane = activateSourceIfAlreadyLoaded(absolutePath, isTestScript);
        if (!force & textPane != null) {
            return textPane;
        }
        if (f.exists() && f.canRead()) {
            StringBuilder contents = new StringBuilder();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                while (in.ready()) {
                    contents.append(in.readLine()).append("\n");
                }
                boolean newTab = textPane == null;
                if (textPane == null) {
                    textPane = new NonWrappingTextPane(isTestScript);
                }
                if (newTab) {
                    JScrollPane sp = new JScrollPane(textPane);
                    if (absolutePath.endsWith(".py")) {
                        textPane.init("text/python");
                    } else if (absolutePath.endsWith(".xml")) {
                        textPane.init("text/xml");
                    } else if (absolutePath.endsWith(".sh")) {
                        textPane.init("text/bash");
                    } else if (absolutePath.endsWith(".bat") || absolutePath.endsWith(".cmd")) {
                        textPane.init("text/dosbatch");
                    } else {
                        textPane.init("text/plain");
                    }
                    textPane.setFileName(absolutePath);

                    if (isTestScript) {
                        tcSourceTextPane = textPane;
                    }
                    textPane.setTestCasePane(this);

                    editorTabbedPane.addTab(f.getName(), null, sp, absolutePath);
                    editorTabbedPane.setSelectedIndex(editorTabbedPane.getTabCount() - 1);
                    textPane.addDocumentListener();
                    textPane.addPropertyChangeListener("isModified", evt -> {
                            if (evt.getNewValue().equals(true)) {
                                String currentTitle = editorTabbedPane.getTitleAt(editorTabbedPane.getSelectedIndex());
                                if (!currentTitle.contains("*")) {
                                    currentTitle += " *";
                                    editorTabbedPane.setTitleAt(editorTabbedPane.getSelectedIndex(), currentTitle);
                                }
                            } else {
                                String currentTitle = editorTabbedPane.getTitleAt(editorTabbedPane.getSelectedIndex());
                                if (currentTitle.contains(" *")) {
                                    currentTitle = currentTitle.replace(" *", "");
                                    editorTabbedPane.setTitleAt(editorTabbedPane.getSelectedIndex(), currentTitle);
                                }
                            }
                    });
                }

                // if needed, replace tabulations by spaces in the file content
                String fileContent = contents.toString();
                boolean isAutoModified = false;

                if (absolutePath.endsWith(".py")) {
                    if (fileContent.contains("\t")) {
                        fileContent = fileContent.replaceAll("\t", StaticConfiguration.PYTHON_INDENT_STRING);
                        isAutoModified = true;

                        JOptionPane.showMessageDialog(null, "Tabulations have been automatically replaced by spaces",
                              "Python indentation", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                setTestCaseSource(textPane, absolutePath, fileContent, isTestScript);
                textPane.setModified(isAutoModified);
                textPane.clearUndos();

                if (absolutePath.endsWith(".py")) {
                    new DebuggerShortcuts(this);  // TO MOVE TO NONWRAPPINGTEXTPANE
                }
                new GenericShortcuts(this); // TO MOVE TO NONWRAPPINGTEXTPANE
                if (activateSourceTab) {
                    tabbedPane.setSelectedIndex(TestCasePane.SOURCE_INDEX);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ioe) {
                    //So I couldn't close the connection?
                }
            }
        }
        return textPane;
    }

    /**
     * This method gets the current activated textPane
     *
     * @return null if there is no tab or the current tab is not a TextPane
     */
    public NonWrappingTextPane getActiveTextPane() {
        int tabIndex = editorTabbedPane.getSelectedIndex();
        if (tabIndex == -1) {
            return null;
        }
        return this.getTextPane(tabIndex);
    }

    /**
     * This method return the textPane in which the main TestScript is loaded
     *
     * @return NonWrappingTextPane object containing the TestScript file
     */
    public NonWrappingTextPane getTcSourceTextPane() {
        return tcSourceTextPane;
    }

    /**
     * This method activates the python script based on the file name if already loaded
     *
     * @param fileName of the script file
     * @param isTestScript specifies if the python script is the main TestScript file
     * @return TextPane is already loaded otherwise null
     */
    public NonWrappingTextPane activateSourceIfAlreadyLoaded(String fileName, boolean isTestScript) {
        // check if already loaded
        boolean tcTextFound = false;

        int testScriptTabIndex = getTestScriptTabIndex();
        if (testScriptTabIndex != -1) {
            tcTextFound = true;
        }
        int textPaneTabIndex = getTextPane(fileName);
        if (textPaneTabIndex != -1 && (!isTestScript || textPaneTabIndex == 0)) {
            editorTabbedPane.setSelectedIndex(textPaneTabIndex);
            tabbedPane.setSelectedIndex(TestCasePane.SOURCE_INDEX);
            return getTextPane(textPaneTabIndex);
        }

        if (tcTextFound && isTestScript) {
            // remove all tabs
            closeAllTabs();
        }
        return null;
    }

    /**
     * Close all tabs and ask user confirmation if a file is modified
     */
    public void closeAllTabs() {
        while (editorTabbedPane.getTabCount() > 0) {
            // check if the current tab must be saved
            NonWrappingTextPane currentPane = getTextPane(0);
            if (currentPane != null) {
                if (currentPane.isModified()) {

                    if (JOptionPane.showConfirmDialog(null,
                          "Do you want to save your current modification in '" + currentPane.getFileName() + "?'",
                          "Save confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        currentPane.save();
                    }
                }
                // remove all breakpoints and listeners if any
                currentPane.removeAllBreakpoints();
            } else {
                TestDataEditor currentDataPane = getTestDataPane(0);
                if (currentDataPane != null) {
                    if (currentDataPane.isModified()) {

                        if (JOptionPane.showConfirmDialog(null,
                              "Do you want to save your current modification in '" + currentDataPane.getCurrentCSVFile() + "?'",
                              "Save confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            currentDataPane.save();
                        }
                    }
                } else {
                    TestRequirementEditor currentrequirementPane = getTestRequirementPane(0);
                    if (currentrequirementPane != null) {
                        if (currentrequirementPane.isModified()) {

                            if (JOptionPane.showConfirmDialog(null,
                                  "Do you want to save your current modification in '" + currentrequirementPane
                                        .getCurrentXMLFile() + "?'", "Save confirmation", JOptionPane.YES_NO_OPTION)
                                  == JOptionPane.YES_OPTION) {
                                currentrequirementPane.save();
                            }
                        }
                    }
                }
            }
            editorTabbedPane.removeTabAt(0);
        }
    }

    /**
     * This method saves the current document only if the current pane is a textpane containing a python script
     */
    public void saveActiveDocument() {
        NonWrappingTextPane activeTextPane = getActiveTextPane();
        if (activeTextPane != null) {
            activeTextPane.save();
        }

    }

    public void setTestCaseSource(NonWrappingTextPane textPane, String fileName, String contents, boolean isTestScript) {
        textPane.setText(contents);
        textPane.setCaretPosition(0);
    }

    public NonWrappingTextPane getTestScripPane() {
        int testScriptTabIndex = this.getTestScriptTabIndex();
        if (testScriptTabIndex != -1) {
            return this.getTextPane(testScriptTabIndex);
        }
        return null;

    }

    public int getTestScriptTabIndex() {
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            NonWrappingTextPane tabTextPane = getTextPane(i);
            if (tabTextPane != null) {
                if (tabTextPane.isTestScript) {
                    return i;
                }
            }
        }
        return -1; // not found
    }

    public NonWrappingTextPane[] getVisibleTextPanes() {
        ArrayList<NonWrappingTextPane> textPanes = new ArrayList<>();
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            NonWrappingTextPane tabTextPane = getTextPane(i);
            if (tabTextPane == null) {
                continue;
            }
            textPanes.add(tabTextPane);

        }
        NonWrappingTextPane[] array = new NonWrappingTextPane[textPanes.size()];

        textPanes.toArray(array);
        return array;
    }

    public int getTextPane(String fileName) {
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            NonWrappingTextPane tabTextPane = getTextPane(i);
            if (tabTextPane == null) {
                continue;
            }
            if (tabTextPane.getFileName().equals(fileName)) {
                return i;
            }
        }
        return -1; // not found

    }

    public NonWrappingTextPane getTextPane(int tabIndex) {
        Component comp = editorTabbedPane.getComponentAt(tabIndex);
        if (comp instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) comp;
            if (sp.getViewport() != null) {
                for (int j = 0; j < sp.getViewport().getComponentCount(); j++) {
                    comp = sp.getViewport().getComponent(j);
                    if (comp instanceof NonWrappingTextPane) {
                        return (NonWrappingTextPane) comp;
                    }
                }
            }
        }
        return null;
    }

    public TestDataEditor getTestDataPane(int tabIndex) {
        Component comp = editorTabbedPane.getComponentAt(tabIndex);
        if (comp instanceof TestDataEditor) {
            return (TestDataEditor) comp;
        }
        return null;
    }

    public TestRequirementEditor getTestRequirementPane(int tabIndex) {
        Component comp = editorTabbedPane.getComponentAt(tabIndex);
        if (comp instanceof TestRequirementEditor) {
            return (TestRequirementEditor) comp;
        }
        return null;
    }

    public TestDataEditor getTestDataPane() {
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            TestDataEditor testDataEditor = getTestDataPane(i);
            if (testDataEditor != null) {
                return testDataEditor;
            }
        }
        return null;
    }

    public int getTestDataTabIndex() {
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            TestDataEditor tabTestDataPane = getTestDataPane(i);
            if (tabTestDataPane != null) {
                return i;
            }
        }
        return -1; // not found
    }

    public int getTestRequirementTabIndex() {
        for (int i = 0; i < editorTabbedPane.getTabCount(); i++) {
            TestRequirementEditor tabTestRequirementPane = getTestRequirementPane(i);
            if (tabTestRequirementPane != null) {
                return i;
            }
        }
        return -1; // not found
    }

    public void activateTestcaseSourceTab() {
        int testScriptTabIndex = getTestScriptTabIndex();
        if (testScriptTabIndex != -1) {
            editorTabbedPane.setSelectedIndex(testScriptTabIndex);
        }
    }

    public void showTestcaseResultsTab() {
        //resultsPane.setVisible(true);
        tabbedPane.setSelectedIndex(RESULTS_INDEX);
    }

    public void loadCSVFile(String fileName) {
        // load also the test data CSV file
        int testDataTabIndex = getTestDataTabIndex();
        if (testDataTabIndex != -1) {
            TestDataEditor currentTestDataEditor = this.getTestDataPane(testDataTabIndex);
            if (currentTestDataEditor != null) {
                if (currentTestDataEditor.getCurrentCSVFile().equals(fileName)) {
                    return;
                }
            }
        }
        TestDataEditor dataEditor = new TestDataEditor();
        dataEditor.loadCSVFile(fileName);
        editorTabbedPane.addTab("TestData", null, dataEditor, fileName);
        dataEditor.addPropertyChangeListener("isModified", evt -> {
                if (evt.getNewValue().equals(true)) {
                    String currentTitle = editorTabbedPane.getTitleAt(editorTabbedPane.getSelectedIndex());
                    if (!currentTitle.contains("*")) {
                        currentTitle += " *";
                        editorTabbedPane.setTitleAt(editorTabbedPane.getSelectedIndex(), currentTitle);
                    }
                } else {
                    String currentTitle = editorTabbedPane.getTitleAt(editorTabbedPane.getSelectedIndex());
                    if (currentTitle.contains(" *")) {
                        currentTitle = currentTitle.replace(" *", "");
                        editorTabbedPane.setTitleAt(editorTabbedPane.getSelectedIndex(), currentTitle);
                    }
                }
        });

    }

    public void loadXMLFile(String fileName) {
        int testRequirementTabIndex = getTestRequirementTabIndex();
        if (testRequirementTabIndex != -1) {
            TestRequirementEditor currentTestRequirementEditor = this.getTestRequirementPane(testRequirementTabIndex);
            if (currentTestRequirementEditor != null) {
                if (currentTestRequirementEditor.getCurrentXMLFile().equals(fileName)) {
                    return;
                }
            }
        }
        TestRequirementEditor requirementEditor = new TestRequirementEditor();
        requirementEditor.loadXMLFile(fileName);
        editorTabbedPane.addTab("TestRequirements", null, requirementEditor, fileName);
        requirementEditor.addPropertyChangeListener("isModified", new PropertyChangeListener() {

            private static final String MODIFIED_SUFFIX = " *";

            public void propertyChange(PropertyChangeEvent evt) {
                String currentTitle = editorTabbedPane.getTitleAt(editorTabbedPane.getSelectedIndex());
                if (evt.getNewValue().equals(true)) {
                    if (!currentTitle.endsWith(MODIFIED_SUFFIX)) {
                        currentTitle += MODIFIED_SUFFIX;
                    }
                } else if (currentTitle.endsWith(MODIFIED_SUFFIX)) {
                    currentTitle = currentTitle.replace(MODIFIED_SUFFIX, "");
                }
                editorTabbedPane.setTitleAt(editorTabbedPane.getSelectedIndex(), currentTitle);
            }
        });

    }

    public JPanel getTcSourcePane() {
        return tcSourcePanel;
    }

    public MainPanel getMainPanel() {
        return parent;
    }

    public String getCurrentSelectedTestsuite() {
        return currentSelectedTestsuite;
    }

    public void setCurrentSelectedTestsuite(String currentSelectedTestsuite) {
        this.currentSelectedTestsuite = currentSelectedTestsuite;
    }

    public FileNode getCurrentSelectedFileNode() {
        return currentSelectedFileNode;
    }

    public void setCurrentSelectedFileNode(FileNode currentSelectedFileNode) {
        this.currentSelectedFileNode = currentSelectedFileNode;
    }

    protected class ExecuteButtonAction implements ActionListener {

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            runTestSuite(false);
        }
    }

    public void runTestSuite(boolean debug) {
        runTestSuite(debug, 1, false);
    }

    public void runTestSuite(final boolean debug, final int numberLoops, final boolean loopsInHours) {
        // change the testsuite to run
        final TreePath[] selectedTcs = tcTree.getSelectionPaths();
        if (selectedTcs == null) {
            logger.warn("No testsuite selected");
            return;
        }
        Object[] obj = selectedTcs[0].getPath();
        // this directory is saved into the root testcase tree
        String testSuiteDir = mTestCaseSuitesRootDir;
        for (Object o : obj) {
            if (o instanceof TCTreeNode) {
                TCTreeNode tcTreeNode = (TCTreeNode) o;
                // remove the root path (test cases)
                if (tcTreeNode.getParent() != null) {
                    testSuiteDir += File.separator + o.toString();
                }
            }
        }
        DirectoryTestSuite testSuite = DirectoryTestSuite.createDirectoryTestSuite(testSuiteDir);
        if (testSuite != null) {
            testSuite.setExecutionLoops(numberLoops, loopsInHours);
            runTestSuite(testSuite, debug);
        }
    }

    public void setTestSuiteDirectory(String dir) {
        mTestCaseSuitesRootDir = dir;
    }

    public String getTestSuiteDirectory() {
        return mTestCaseSuitesRootDir;
    }

    public void runTestSuite(final TestSuite testSuite, final boolean debug) {
        if (!this.checkScriptsSyntax()) {
            return;
        }
        resultsPane.setRunTab("Run1");
        resultsPane.resetTables();
        tabbedPane.setSelectedIndex(RESULTS_INDEX);

        resultsPane.setVisible(true);
        stopExecution = false;
        resultsPane.proceedExecution();
        // update the config panel
        parent.setTestSuite(testSuite.getName());
        parent.refreshParams();
        TestBedConfiguration.setSUTVersion(parent.getSUTVersion());
        isExecuting = true;
        stopExecution = false;
        setStopButtonEnabled(true, true);

        parent.getHeaderPanel().setControlTestbedButtonsEnabled();
        parent.getTestCampaignPanel().setExecuteButtonsEnabled(!isExecuting);

        testExecutionHandler = new TestExecutionThread(testSuite, debug);
        Thread t = new Thread(testExecutionHandler);
        t.start();
    }

    public void doAction(TestScriptBreakpointEvent event) {
        switch (event.getAction()) {
            case CONTINUE:
            case STEP:
            case STEPINTO:
            case STOP:
                startExecutionButton.setVisible(false);
                //getVarButton.setVisible(false);
                //                stopExecutionButton.setVisible(false);
                stepOverExecutionButton.setVisible(false);
                stepIntoExecutionButton.setVisible(false);
                tabbedPane.setSelectedIndex(RESULTS_INDEX);
                debugPanel.setVisible(false);
                //this.getTe
                NonWrappingTextPane[] panes = this.getVisibleTextPanes();
                for (NonWrappingTextPane textPane : panes) {
                    textPane.getDefaultLineNumberPanel().update(event);
                }
                break;
            case BREAK:
                startExecutionButton.setVisible(true);
                stepOverExecutionButton.setVisible(true);
                stepIntoExecutionButton.setVisible(true);
                debugPanel.setVisible(true);
                sourcePanel.setDividerLocation(sourcePanel.getDividerLocation() - 150);
                // update the button status
                //                stopExecutionButton.setVisible(true);
                //getVarButton.setVisible(true);
                tabbedPane.setSelectedIndex(SOURCE_INDEX);
                getTcSourcePane().setVisible(true);
                // set the current line highlighted
                Object extraData = event.getExtraData();
                if (extraData instanceof Breakpoint) {
                    Breakpoint breakpoint = (Breakpoint) extraData;

                    // load the file into the tabbedPaned
                    File f = new File(breakpoint.getFileName());
                    if (f.exists() && f.canRead()) {
                        NonWrappingTextPane textPane = loadTestCaseSource(f, true,
                              f.getName().equals(StaticConfiguration.TEST_SCRIPT_FILENAME));

                        tabbedPane.setSelectedIndex(TestCasePane.SOURCE_INDEX);
                        // now go to the given line
                        textPane.selectLine(breakpoint.getLineIndex());
                        textPane.getLineNumberPanel().update(event);
                    }
                }
                break;
        }
    }

    public void setExecutingTestCampaign(boolean executing, TestCampaignMainPanel.CampaignExecutionThread
          testCampaignExecutionHandler) {
        isExecuting = executing;
        this.testCampaignExecutionHandler = testCampaignExecutionHandler;
    }

    public void updateButtons() {
        SwingUtilities.invokeLater(new UpdateButtons());
    }

    public void updateButtons(boolean enableStopButton) {
        SwingUtilities.invokeLater(new UpdateButtons(enableStopButton));
    }

    public void setSelectedTab(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    @SuppressWarnings("unchecked")
    public void pythonResult(DumpPythonResultEvent event) {
        if (event.getSource() instanceof ArrayList) {
            ArrayList<DebugVariable> debugVariables = (ArrayList<DebugVariable>) event.getSource();
            debugPanel.setDebugVariables(debugVariables);
        }
    }

    public class UpdateButtons implements Runnable {

        private boolean enableStopButton;

        public UpdateButtons() {
            this.enableStopButton = false;
        }

        public UpdateButtons(final boolean enableStopButton) {
            this.enableStopButton = enableStopButton;
        }

        public void run() {
            executeButton.setEnabled(!isExecuting);
            stopExecutionButton.setVisible(isExecuting);
            if (this.enableStopButton) {
                stopExecutionButton.setEnabled(true);
            }
            debugButton.setEnabled(!isExecuting);
            parent.getHeaderPanel().setControlTestbedButtonsEnabled();
            parent.getTestCampaignPanel().setExecuteButtonsEnabled(!isExecuting);
            if (!isExecuting) {
                // set the focus to the test results
                tabbedPane.setSelectedIndex(RESULTS_INDEX);
            }
        }
    }

    public class TestExecutionThread implements Runnable {

        private TestSuite testSuite;
        private boolean debug;

        public TestExecutionThread(final TestSuite testSuite, final boolean debug) {
            this.testSuite = testSuite;
            this.debug = debug;
        }

        public void stop() {
            ThreadGroup root = Thread.currentThread().getThreadGroup();
            ThreadManager.stopThread(root, 0);
        }

        public void run() {
            SwingUtilities.invokeLater(new UpdateButtons());
            isExecuting = true;

            try {
                TestEngine.execute(testSuite, debug);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                TestEngine.tearDown();
                isExecuting = false;
                SwingUtilities.invokeLater(new UpdateButtons());
                testExecutionHandler = null;
                debugPanel.setVisible(false);
            }
        }
    }

    public void closeCurrentEditorFile() {
        NonWrappingTextPane textPane = getActiveTextPane();
        if (textPane != null) {
            if (!textPane.isTestScript) {
                int tabIndex = editorTabbedPane.getSelectedIndex();
                if (tabIndex != -1) {
                    // check if the textpane must be saved

                    if (textPane.isModified()) {
                        if (JOptionPane.showConfirmDialog(null,
                              "Do you want to save your current modification in '" + textPane.getFileName() + "?'",
                              "Save confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            textPane.save();
                        }
                    }
                    editorTabbedPane.remove(tabIndex);
                }
            }
        }

    }

    public class TabMouseListener extends MouseAdapter {

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // force selection of clicked row
                // display a popup menu to run/debug a test
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new FileCloseAction());
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), TestCasePane.this);
                menu.show(TestCasePane.this, pt.x, pt.y);
                e.consume();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            evaluatePopup(e);
            if (e.isConsumed()) {
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                closeCurrentEditorFile();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            evaluatePopup(e);
        }
    }

    class FileCloseAction extends AbstractAction {

        public FileCloseAction() {
            super("Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeCurrentEditorFile();
        }

        @Override
        public boolean isEnabled() {
            NonWrappingTextPane textPane = getActiveTextPane();
            if (textPane != null) {
                if (!textPane.isTestScript) {
                    return true;
                }
            }
            return false;
        }
    }

}
