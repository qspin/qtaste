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
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.log.Log4jServer;
import com.qspin.qtaste.testsuite.impl.DirectoryTestSuite;
import com.qspin.qtaste.ui.config.MainConfigFrame;
import com.qspin.qtaste.ui.testcampaign.TestCampaignMainPanel;
import com.qspin.qtaste.ui.tools.GridBagLineAdder;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.tools.WrappedToolTipUI;
import com.qspin.qtaste.ui.util.QSpinTheme;
import com.qspin.qtaste.ui.widget.FillLabelUI;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 * 
 */
@SuppressWarnings("serial")
public class MainPanel extends JFrame {

    protected static Logger logger = Log4jLoggerFactory.getLogger(MainPanel.class);
    protected String title = "QSpin Tailored Automated System Test Environment";
    protected static String mTestSuiteDir;
    protected static int mNumberLoops;
    protected static boolean mLoopsInHour;
    protected static final int TREE_TABS_WIDTH = 285; // To do must be a parameter
    private static final String MAIN_HORIZONTAL_SPLIT_DIVIDER_LOCATION_PROPERTY = "main_horizontal_split_divider_location";
    private QSpinTheme mTheme;
    private ConfigInfoPanel mHeaderPanel;
    private JTabbedPane mTreeTabsPanel;
    private TestCasePane mTestCasePanel;
    private TestCampaignMainPanel mTestCampaignPanel;
    
    public MainPanel(String testSuiteDir, int numberLoops, boolean loopsInHour) {
        super();
        setTitle(title);
        setUpFrame();
        mTestSuiteDir = testSuiteDir;
        mNumberLoops = numberLoops;
        mLoopsInHour = loopsInHour;
    }

    private void setUpFrame() {
        setName(title);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                // Perhaps ask user if they want to save any unsaved files first.
                mTestCasePanel.closeAllTabs();
                System.exit(0);
            }
        });
    }

    public void launch() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                setQSpinTheme();
                genUI();
            }
        });
    }

    public void setQSpinTheme() {
        mTheme = new QSpinTheme();
        MetalLookAndFeel.setCurrentTheme(mTheme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            ((WrappedToolTipUI) WrappedToolTipUI.createUI(null)).setMaxWidth(200);
            UIManager.put("ToolTipUI", "com.qspin.qtaste.ui.tools.WrappedToolTipUI");
        } catch (UnsupportedLookAndFeelException ex) {
        }
    }

    public void setTestSuite(String testSuite) {
        mHeaderPanel.setTestSuite(testSuite);
        mHeaderPanel.refreshData();
    }

    public String getSUTVersion() {
        return mHeaderPanel.getSUTVersion();
    }

    public void refreshParams() {
        mHeaderPanel.refreshData();
        mHeaderPanel.refreshTestBed();
    }

    public JTabbedPane getTreeTabsPanel() {
        return mTreeTabsPanel;
    }
    
    public TestCasePane getTestCasePanel() {
        return mTestCasePanel;
    }
    
    public ConfigInfoPanel getHeaderPanel() {
        return mHeaderPanel;
    }
    
    public TestCampaignMainPanel getTestCampaignPanel() {
        return mTestCampaignPanel;
    }
    
    public void genUI() {
        try {
            getContentPane().setLayout(new BorderLayout());
            // prepare the top panel that contains the following panes:
            //   - logo
            //   - ConfigInfopanel
            //   - Current Date/time
            JPanel topanel = new JPanel(new BorderLayout());
            JPanel center = new JPanel(new GridBagLayout());
            ImageIcon topLeftLogo = ResourceManager.getInstance().getImageIcon("main/qspin");
            JLabel iconlabel = new JLabel(topLeftLogo);

            mHeaderPanel = new ConfigInfoPanel(this);
            mHeaderPanel.init();

            GridBagLineAdder centeradder = new GridBagLineAdder(center);
            JLabel sep = new JLabel("  ");
            sep.setFont(ResourceManager.getInstance().getSmallFont());
            sep.setUI(new FillLabelUI(ResourceManager.getInstance().getLightColor()));
            centeradder.setWeight(1.0f, 0.0f);
            centeradder.add(mHeaderPanel);

            // prepare the right panels containg the main information:
            // the right pane is selected through the tabbed pane:
            //    - Test cases: management of test cases and test suites
            //    - Test campaign: management of test campaigns
            //    - Interactive: ability to invoke QTaste verbs one by one

            final JPanel rightPanels = new JPanel(new CardLayout());
            mTestCasePanel = new TestCasePane(this);
            rightPanels.add(mTestCasePanel, "Test Cases");

            mTestCampaignPanel = new TestCampaignMainPanel(this);
            rightPanels.add(mTestCampaignPanel, "Test Campaign");

            final TestCaseInteractivePanel testInterractivePanel = new TestCaseInteractivePanel();
            rightPanels.add(testInterractivePanel, "Interactive");

            mTreeTabsPanel = new JTabbedPane(JTabbedPane.BOTTOM);
            mTreeTabsPanel.setPreferredSize(new Dimension(TREE_TABS_WIDTH, HEIGHT));

            TestCaseTree tct = new TestCaseTree(mTestCasePanel);
            JScrollPane sp2 = new JScrollPane(tct);
            mTreeTabsPanel.addTab("Test Cases", sp2);
            
            // add tree view for test campaign definition
            com.qspin.qtaste.ui.testcampaign.TestCaseTree mtct = new com.qspin.qtaste.ui.testcampaign.TestCaseTree(mTestCampaignPanel.getTreeTable());
            JScrollPane sp3 = new JScrollPane(mtct);
            mTreeTabsPanel.addTab("Test Campaign", sp3);
            
            genMenu(tct);

            // add another tab contain used for Interactive mode
            TestAPIDocsTree jInteractive = new TestAPIDocsTree(testInterractivePanel);
            JScrollPane spInter = new JScrollPane(jInteractive);
            mTreeTabsPanel.addTab("Interactive", spInter);

            // init will do the link between the tree view and the pane
            testInterractivePanel.init();

            // Define the listener to display the pane depending on the selected tab
            mTreeTabsPanel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    String componentName = mTreeTabsPanel.getTitleAt(mTreeTabsPanel.getSelectedIndex());
                    CardLayout rcl = (CardLayout) rightPanels.getLayout();
                    rcl.show(rightPanels, componentName);
                }
            });
            mTestCampaignPanel.addTestCampaignActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (e.getID()== TestCampaignMainPanel.RUN_ID) {
                        if (e.getActionCommand().equals(TestCampaignMainPanel.STARTED_CMD)) {
                            // open the tab test cases 
                        	mTreeTabsPanel.setSelectedIndex(0);
                            mTestCasePanel.setSelectedTab(TestCasePane.RESULTS_INDEX);
                            // update the buttons                            
                            mTestCasePanel.setExecutingTestCampaign(true, ((TestCampaignMainPanel)e.getSource()).getExecutionThread());
                            mTestCasePanel.updateButtons();
                        }
                        if (e.getActionCommand().equals(TestCampaignMainPanel.STOPPED_CMD)) {
                            mTestCasePanel.setExecutingTestCampaign(false, null);
                            mTestCasePanel.updateButtons();
                        }
                    }
                }
            });

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mTreeTabsPanel, rightPanels);
            splitPane.setDividerSize(4);
            GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
            int mainHorizontalSplitDividerLocation= guiConfiguration.getInt(MAIN_HORIZONTAL_SPLIT_DIVIDER_LOCATION_PROPERTY, 285);
            splitPane.setDividerLocation(mainHorizontalSplitDividerLocation);
            
            splitPane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("dividerLocation")) {
                        GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
                        if (evt.getSource() instanceof JSplitPane) {
                            JSplitPane splitPane= (JSplitPane)evt.getSource();
                            guiConfiguration.setProperty(MAIN_HORIZONTAL_SPLIT_DIVIDER_LOCATION_PROPERTY, splitPane.getDividerLocation());
                            try {
                                guiConfiguration.save();
                            } catch (ConfigurationException ex) {
                                logger.error("Error while saving GUI configuration: " + ex.getMessage());
                            }
                        }
                    }
                }
            });
            
            topanel.add(iconlabel, BorderLayout.WEST);
            topanel.add(center);

            getContentPane().add(topanel, BorderLayout.NORTH);
            getContentPane().add(splitPane);
            this.pack();

            this.setExtendedState(Frame.MAXIMIZED_BOTH);
            if (mTestSuiteDir != null) {
                DirectoryTestSuite testSuite = new DirectoryTestSuite(mTestSuiteDir);
                testSuite.setExecutionLoops(mNumberLoops, mLoopsInHour);
                setTestSuite(testSuite.getName());
                mTestCasePanel.runTestSuite(testSuite, false);
            }
            setVisible(true);
        //treeTabs.setMinimumSize(new Dimension(100, this.HEIGHT));

        } catch (Exception e) {
            logger.fatal(e);
            e.printStackTrace();
            TestEngine.shutdown();
            System.exit(1);
        }

    }

    protected void genMenu(final TestCaseTree tct) {
        final JFrame owner = this;
        JMenuBar menuBar = new JMenuBar();
        JMenu tools = new JMenu("Tools");
        tools.setMnemonic(KeyEvent.VK_T);

        // Tools|Config menu item
        JMenuItem config = new JMenuItem("Config", KeyEvent.VK_D);
        config.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //ATEConfigEditPanel configPanel = new  ATEConfigEditPanel(null);
                //configPanel.setVisible(true);
                MainConfigFrame configFrame = new MainConfigFrame();
                configFrame.launch();
                configFrame.addWindowListener(new WindowListener() {

                    public void windowOpened(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                        // refresh the Configuration information display
                        refreshParams();
                    }

                    public void windowIconified(WindowEvent e) {
                    }

                    public void windowDeiconified(WindowEvent e) {
                    }

                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowDeactivated(WindowEvent e) {
                    }
                });

            }
        });
        tools.add(config);

        // Tools|delete results menu item
        JMenuItem deleteResults = new JMenuItem("Delete Results", KeyEvent.VK_D);
        final MainPanel ui = this;
        deleteResults.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String baseDir = TestEngineConfiguration.getInstance().getString("reporting.generated_report_path");
                new File(baseDir, baseDir);
                // TO DO : delete really the files
                JOptionPane.showMessageDialog(ui, "Results have been deleted");

            }
        });
        tools.add(deleteResults);
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem importTestSuites = new JMenuItem("Import TestSuites");
        importTestSuites.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
               //
            	mTestCasePanel.importTestSuites();
            	
            }
        });
        fileMenu.add(importTestSuites);
        
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About", KeyEvent.VK_A);
        about.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new AboutDialog(owner);
            }
        });
        help.add(about);
        
        JMenuItem ateUserManuel = new JMenuItem("User Manual");
        ateUserManuel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
               viewQTasteUserManuel();
            }
        });
        help.add(ateUserManuel);
        
        //menuBar.add(tools); // not to be used at this time!!!!!!!!!
        //menuBar.add(fileMenu);
        menuBar.add(help);
        setJMenuBar(menuBar);
    }

    private static void showUsage() {
        System.err.println("Usage: <command> [-testsuite testsuiteDirectory] [-testbed configFileName.xml] [-engine engineFileName.xml] [-sutversion <sut_version_identifier>]");
        TestEngine.shutdown();
        System.exit(1);
    }

    public static void main(String args[]) throws Exception {
        try {
        	Locale.setDefault(Locale.ENGLISH);
        	
        	// Log4j Configuration
            PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

            // log version information
          	logger.info("QTaste kernel version: " + com.qspin.qtaste.kernel.Version.getInstance().getFullVersion());
      		logger.info("QTaste testAPI version: " + StaticConfiguration.VERSION_CONTROL.getTestApiVersion(""));

          	// handle optional config file name
            if ((args.length != 0) && (args.length != 2) && (args.length != 4) && (args.length != 6) && (args.length != 8)) {
                showUsage();
            }

            GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();

            boolean testBedArgumentPresent = false;
            String testSuiteDir = null;
            int numberLoops = 1;
            boolean loopsInHours = false;
//            String sutVersion = null;

            for (int i = 0; i < args.length; i = i + 2) {
                if (args[i].equals("-testsuite")) {
                    logger.info("Using " + args[i + 1] + " as test suite directory");
                    testSuiteDir = args[i + 1];
                } else if (args[i].equals("-testbed")) {
                    String testbedFileName = args[i + 1];
                    logger.info("Using " + testbedFileName + " as testbed configuration file");
                    TestBedConfiguration.setConfigFile(testbedFileName);
                    // save testbed
                    testbedFileName = new File(testbedFileName).getName();
                    String testbed = testbedFileName.substring(0, testbedFileName.lastIndexOf('.'));
                    guiConfiguration.setProperty(StaticConfiguration.LAST_SELECTED_TESTBED_PROPERTY, testbed);
                    testBedArgumentPresent = true;
                } else if (args[i].equals("-engine")) {
                    logger.info("Using " + args[i + 1] + " as engine configuration file");
                    TestEngineConfiguration.setConfigFile(args[i + 1]);
                } else if (args[i].equals("-loop")) {
//                    String message = "Running test suite in loop";
                    numberLoops = -1;
                    if ((i + 1 < args.length)) {
                        // more arguments, check if next argument is a loop argument
                        if (args[i + 1].startsWith("-")) {
                            i++;
                        } else {
                            String countOrHoursStr;
                            if (args[i + 1].endsWith("h")) {
                                loopsInHours = true;
                                countOrHoursStr = args[i + 1].substring(0, args[i + 1].length() - 1);
                            } else {
                                loopsInHours = false;
                                countOrHoursStr = args[i + 1];
                            }
                            try {
                                numberLoops = Integer.parseInt(countOrHoursStr);
                                if (numberLoops <= 0) {
                                    throw new NumberFormatException();
                                }
//                                message += (loopsInHours ? " during " : " ") + numberLoops + " " + (loopsInHours ? "hour" : "time") + (numberLoops > 1 ? "s" : "");
                                i += 2;
                            } catch (NumberFormatException e) {
                                showUsage();
                            }
                        }
                    }
                } else if (args[i].equals("-sutversion") && (i + 1 < args.length)) {
                    logger.info("Using " + args[i + 1] + " as sutversion");
                    TestBedConfiguration.setSUTVersion(args[i + 1]);
                    i += 2;
                } else {
                    // no more arguments
                    i++;
                }
            }
            if (!testBedArgumentPresent) {
                String lastSelectedTestbed = guiConfiguration.getString(StaticConfiguration.LAST_SELECTED_TESTBED_PROPERTY, "default");
                String testbedConfigFileName = StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/" + lastSelectedTestbed + "." + StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION;
                if (!new File(testbedConfigFileName).exists()) {
                    // if last selected testbed doesn't exist use the first one found
                    File[] testbedConfigFiles = FileUtilities.listSortedFiles(new File(StaticConfiguration.TESTBED_CONFIG_DIRECTORY),new FileFilter() {

                        public boolean accept(File pathname) {
                            return pathname.getName().toLowerCase().endsWith(".xml");
                        }
                    });
                    if (testbedConfigFiles == null) {
                        throw new RuntimeException("Testbed configuration directory (" + StaticConfiguration.TESTBED_CONFIG_DIRECTORY + ") not found.");
                    }
                    if (testbedConfigFiles.length > 0) {
                        testbedConfigFileName = testbedConfigFiles[0].getCanonicalPath();
                        // save testbed
                        final String testbedFileName = testbedConfigFiles[0].getName();
                        guiConfiguration.setProperty(StaticConfiguration.LAST_SELECTED_TESTBED_PROPERTY, testbedFileName.substring(0, testbedFileName.lastIndexOf('.')));
                    } else {
                        throw new RuntimeException("No testbed config file available.");
                    }
                }
                TestBedConfiguration.setConfigFile(testbedConfigFileName);
            }
            
            // start the log4j server
            Log4jServer.getInstance().start();
            
            // initialize the Python interpreter (used for Doc generation)
            Properties props = new Properties();
            //Le chemin des librairies python            
            
            props.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
            props.setProperty("python.path", StaticConfiguration.JYTHON_LIB + File.pathSeparator + StaticConfiguration.TEST_SCRIPT_DOC_TOOLS_DIR);
            PythonInterpreter.initialize(System.getProperties(), props, new String[]{""});
            MainPanel me = new MainPanel(testSuiteDir, numberLoops, loopsInHours);
            me.launch();

        } catch (Exception e) {
            logger.error(e);
            TestEngine.shutdown();
            System.exit(1);            
        }
    }
    private void viewQTasteUserManuel() {
        try {
            Desktop.getDesktop().open(new File(StaticConfiguration.QTASTE_USER_MANUAL_FILE));
        } catch (IOException ex) {
            logger.error("Unable to open QTaste user manual document at the following location:'" +
                    StaticConfiguration.QTASTE_USER_MANUAL_FILE + "'");
        }
        
    }
}
