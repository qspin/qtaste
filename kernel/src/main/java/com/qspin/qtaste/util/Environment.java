package com.qspin.qtaste.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.addon.AddOnManager;
import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.log.Log4jServer;
import com.qspin.qtaste.ui.MainPanel;
import com.qspin.qtaste.util.versioncontrol.VersionControl;


public class Environment {

	public static synchronized Environment getEnvironment()
	{
		if ( _INSTANCE == null )
		{
			_INSTANCE = new Environment();
		}
		return _INSTANCE;
	}
	
	public void initializeEnvironment(String[] pArgs) throws Exception
	{
        try {
        	Locale.setDefault(Locale.ENGLISH);
        	
        	// Log4j Configuration
            PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

            // log version information
            LOGGER.info("QTaste kernel version: " + com.qspin.qtaste.kernel.Version.getInstance().getFullVersion());
          	LOGGER.info("QTaste testAPI version: " + VersionControl.getInstance().getTestApiVersion(""));

          	// handle optional config file name
            if ((pArgs.length != 0) && (pArgs.length != 2) && (pArgs.length != 4) && (pArgs.length != 6) && (pArgs.length != 8)) {
                showUsage();
            }

            GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();

            boolean testBedArgumentPresent = false;
            String testSuiteDir = null;
            int numberLoops = 1;
            boolean loopsInHours = false;
//            String sutVersion = null;

            for (int i = 0; i < pArgs.length; i = i + 2) {
                if (pArgs[i].equals("-testsuite")) {
                	LOGGER.info("Using " + pArgs[i + 1] + " as test suite directory");
                    testSuiteDir = pArgs[i + 1];
                } else if (pArgs[i].equals("-testbed")) {
                    String testbedFileName = pArgs[i + 1];
                    LOGGER.info("Using " + testbedFileName + " as testbed configuration file");
                    TestBedConfiguration.setConfigFile(testbedFileName);
                    // save testbed
                    testbedFileName = new File(testbedFileName).getName();
                    String testbed = testbedFileName.substring(0, testbedFileName.lastIndexOf('.'));
                    guiConfiguration.setProperty(StaticConfiguration.LAST_SELECTED_TESTBED_PROPERTY, testbed);
                    testBedArgumentPresent = true;
                } else if (pArgs[i].equals("-engine")) {
                	LOGGER.info("Using " + pArgs[i + 1] + " as engine configuration file");
                    TestEngineConfiguration.setConfigFile(pArgs[i + 1]);
                } else if (pArgs[i].equals("-loop")) {
//                    String message = "Running test suite in loop";
                    numberLoops = -1;
                    if ((i + 1 < pArgs.length)) {
                        // more arguments, check if next argument is a loop argument
                        if (pArgs[i + 1].startsWith("-")) {
                            i++;
                        } else {
                            String countOrHoursStr;
                            if (pArgs[i + 1].endsWith("h")) {
                                loopsInHours = true;
                                countOrHoursStr = pArgs[i + 1].substring(0, pArgs[i + 1].length() - 1);
                            } else {
                                loopsInHours = false;
                                countOrHoursStr = pArgs[i + 1];
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
                } else if (pArgs[i].equals("-sutversion") && (i + 1 < pArgs.length)) {
                	LOGGER.info("Using " + pArgs[i + 1] + " as sutversion");
                    TestBedConfiguration.setSUTVersion(pArgs[i + 1]);
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
            mMainPanel = new MainPanel(testSuiteDir, numberLoops, loopsInHours);
            mMainPanel.launch();
            mAddOnManager = new AddOnManager();
            mAddOnManager.loadAddOns();

        } catch (Exception e) {
        	LOGGER.error(e);
            TestEngine.shutdown();
            System.exit(1);            
        }
	}
	
	public JMenuBar getMainMenuBar()
	{
		return mMainPanel.getJMenuBar();
	}
	
	public void addTreeTabPane(String pTitle, JTree pTree, JPanel pPanel)
	{
		JScrollPane js = new JScrollPane();
		js.getViewport().add(pTree);
		mMainPanel.getTreeTabsPanel().addTab(pTitle, js);
		mMainPanel.getTabsPanel().add(pTitle, pPanel);
	}
	
	public AddOnManager getAddOnManager()
	{
		return mAddOnManager;
	}
	
	public MainPanel getMainFrame()
	{
		return mMainPanel;
	}
	
	public void addTestEditor(JScrollPane pEditor, String pEditorTitle)
	{
		mMainPanel.getTestCasePanel().addTabPane(pEditor, pEditorTitle);
	}
	public void removeTestEditor(JScrollPane pEditor) {
		mMainPanel.getTestCasePanel().removeTabPane(pEditor);
	}

    private static void showUsage() {
        System.err.println("Usage: <command> [-testsuite testsuiteDirectory] [-testbed configFileName.xml] [-engine engineFileName.xml] [-sutversion <sut_version_identifier>]");
        TestEngine.shutdown();
        System.exit(1);
    }
	
	protected Environment(){}
	
	protected MainPanel mMainPanel;
	protected AddOnManager mAddOnManager;
	
	protected static Environment _INSTANCE;
	protected static Logger LOGGER = Logger.getLogger(Environment.class);
}
