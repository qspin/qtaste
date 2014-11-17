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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.kernel.campaign.CampaignManager;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResultImpl;
import com.qspin.qtaste.reporter.testresults.TestResultsReportManager;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.ui.tools.GridBagLineAdder;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.widget.BarLabelUI;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.versioncontrol.VersionControl;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class ConfigInfoPanel extends JPanel /*implements SmartSocketsListener */{

    private MainPanel parent;
    private JLabel mTestSuiteLabel = new LabelWithBar();
    private JLabel mTestResultsLabel = new LabelWithBar();
    private JLabel mTestReportingFormat = new LabelWithBar();
    private JComboBox mTestbedList = new JComboBox();
    private JButton m_startTestbed = new JButton("(Re)start testbed");
    private JButton m_stopTestbed = new JButton("Stop testbed");
    private JCheckBox m_ignoreControlScript = new JCheckBox("Ignore control script");
    private JTextField m_SUTVersion = new JTextField(VersionControl.getInstance().getSUTVersion(""));
    private TestEngineConfiguration engineConfig = TestEngineConfiguration.getInstance();
    private TestBedConfiguration testbedConfig = TestBedConfiguration.getInstance();
    private String testSuiteName;
    private boolean isStartingOrStoppingTestbed;
    private static final Logger logger = Log4jLoggerFactory.getLogger(ConfigInfoPanel.class);


    /**
     * Label with hyphen.
     */
    private class LabelWithBar extends JLabel {

        public LabelWithBar() {
            super("-");

            setUI(new BarLabelUI());
            setFont(ResourceManager.getInstance().getStandardFontLight());
        }
    }

    public ConfigInfoPanel(MainPanel parent) {
        super(new BorderLayout());

        this.parent = parent;
        TestSuite testSuite = TestEngine.getCurrentTestSuite();
        if (testSuite != null) {
            testSuiteName = testSuite.getName();
        } else {
            testSuiteName = "";
        }
    }

    public boolean isStartingOrStoppingTestbed() {
    	return isStartingOrStoppingTestbed;
    }

    public void setTestSuite(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getSUTVersion () {
        return m_SUTVersion.getText().trim();
    }

    public void refreshData() {

        mTestResultsLabel.setText(engineConfig.getString("reporting.generated_report_path"));
        mTestSuiteLabel.setText(testSuiteName.length() != 0 ? testSuiteName : "-");

        // TO DO : reporting is now a list of reporters
        int reportersCount = engineConfig.getMaxIndex("reporting.reporters.format") + 1;
        String reportFormat = "";
        for (int reporterIndex = 0; reporterIndex < reportersCount; reporterIndex++) {
            if (reportFormat.length() > 0) {
                reportFormat += " | ";
            }
            reportFormat += engineConfig.getString("reporting.reporters.format(" + reporterIndex + ")");
        }
        mTestReportingFormat.setText(reportFormat);
    }

    public void refreshTestBed() {
        // remove the extension
        String testbedFileName = testbedConfig.getFile().getName();
        String testbedName = testbedFileName.substring(0, testbedFileName.lastIndexOf('.'));
        mTestbedList.getModel().setSelectedItem(testbedName);
    }

    public void setControlTestbedButtonsEnabled() {
        boolean isExecutingTestCaseOrStartingOrStoppingTestbed = parent.getTestCasePanel().isExecuting || isStartingOrStoppingTestbed;
        boolean enabled = ignoreControlScript() && !isExecutingTestCaseOrStartingOrStoppingTestbed;
        m_startTestbed.setEnabled(enabled);
        m_stopTestbed.setEnabled(enabled);
        parent.getTestCasePanel().setExecuteButtonsEnabled(!isExecutingTestCaseOrStartingOrStoppingTestbed);
        parent.getTestCampaignPanel().setExecuteButtonsEnabled(!isExecutingTestCaseOrStartingOrStoppingTestbed);
        mTestbedList.setEnabled(!isExecutingTestCaseOrStartingOrStoppingTestbed);
        m_ignoreControlScript.setEnabled(!isExecutingTestCaseOrStartingOrStoppingTestbed);
    }

    public void init() {

        setLayout(new GridBagLayout());
        GridBagLineAdder adder = new GridBagLineAdder(this);
        adder.setWeight(1.0f, 0.0f);
        adder.setLength(6);

        //1st column - 1st row
        adder.setWeight(0.0, 0.0);
        adder.add(new JLabel("Test suite:"));
        adder.setWeight(1.0, 0.0);
        adder.add(mTestSuiteLabel);
        adder.setWeight(0.0, 0.0);

        //2d column - 1st row
        adder.add(new JLabel("Testbed config:"));
        // set the combobox as read only (not possible to modify the testbed from GUI at this time
        UIManager.put("ComboBox.disabledForeground", Color.BLACK);
        mTestbedList.setEnabled(true);
        adder.add(mTestbedList);

        // add testbed mouse listener, for the "Edit File" action
        TestbedMouseListener testbedMouseListener = new TestbedMouseListener();
        java.awt.Component[] mTestbedListComponents = mTestbedList.getComponents();
        for (int i = 0; i < mTestbedListComponents.length; i++) {
            mTestbedListComponents[i].addMouseListener(testbedMouseListener);
        }

        // go to second row
        adder.addSeparator();

        //1st column - 2d row
        adder.add(new JLabel("Test results directory:"));
        adder.add(mTestResultsLabel);


        //2d column - 2d row
        m_ignoreControlScript.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TestEngine.setIgnoreControlScript(ignoreControlScript());
                setControlTestbedButtonsEnabled();

                GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
                guiConfiguration.setProperty(StaticConfiguration.IGNORE_CONTROL_SCRIPT_PROPERTY, m_ignoreControlScript.isSelected());
                try {
                    guiConfiguration.save();
                } catch (ConfigurationException ex) {
                    logger.error("Error while saving GUI configuration: " + ex.getMessage());
                }
            }
        });
        adder.add(m_ignoreControlScript);
        JPanel sutPanel = new JPanel();
        JLabel sutVersion = new JLabel("SUT version: ");
        sutPanel.add(sutVersion);
        m_SUTVersion.setHorizontalAlignment(JTextField.RIGHT);
        m_SUTVersion.setPreferredSize(new Dimension(150, m_SUTVersion.getPreferredSize().height));
        sutPanel.add(m_SUTVersion);
        adder.add(sutPanel);
        m_SUTVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TestBedConfiguration.setSUTVersion(m_SUTVersion.getText());
            }
        });
        //create a 3d row
        adder.addSeparator();

        //1st column - 3d row
        adder.add(new JLabel("Reporting Format:"));
        adder.add(mTestReportingFormat);

        //2d column - 3d row
        // add a button to manually start the testbed
        m_startTestbed.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isStartingOrStoppingTestbed = true;
                setControlTestbedButtonsEnabled();
                parent.getTestCasePanel().setStopButtonEnabled(true, true);
                parent.getTestCasePanel().showTestcaseResultsTab();
                TestBedConfiguration.setSUTVersion(getSUTVersion());
                new SUTStartStopThread("start").start();
            }
        });
        m_startTestbed.setEnabled(false);
        adder.add(m_startTestbed);

        // add a button to manually stop the testbed
        m_stopTestbed.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                isStartingOrStoppingTestbed = true;
                setControlTestbedButtonsEnabled();
                parent.getTestCasePanel().showTestcaseResultsTab();
                TestBedConfiguration.setSUTVersion(getSUTVersion());
                new SUTStartStopThread("stop").start();
            }
        });
        m_stopTestbed.setEnabled(false);
        adder.add(m_stopTestbed);

        DefaultComboBoxModel model = (DefaultComboBoxModel) mTestbedList.getModel();
        model.removeAllElements();
        String testbedDir = testbedConfig.getFile().getParent();
        File fTestbedDir = new File(testbedDir);
        FileMask fileMask = new FileMask();
        fileMask.addExtension("xml");
        File[] fTestbedList = FileUtilities.listSortedFiles(fTestbedDir, fileMask);
        for (int i = 0; i < fTestbedList.length; i++) {
            // remove the extension
            String testbedName = fTestbedList[i].getName().substring(0,
                    fTestbedList[i].getName().lastIndexOf('.'));
            model.addElement(testbedName);
        }

        mTestbedList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (mTestbedList.getSelectedItem() != null) {
                    String selectedTestbed = (String) mTestbedList.getSelectedItem();
                    String configFile = StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/" + selectedTestbed + "." + StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION;
                    TestBedConfiguration.setConfigFile(configFile);

                    GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
                    guiConfiguration.setProperty(StaticConfiguration.LAST_SELECTED_TESTBED_PROPERTY, selectedTestbed);
                    try {
                        guiConfiguration.save();
                    } catch (ConfigurationException ex) {
                        logger.error("Error while saving GUI configuration: " + ex.getMessage());
                    }
                }
                refreshData();
            }
        });

        refreshTestBed();
        refreshData();
        if ( GUIConfiguration.getInstance().getBoolean(StaticConfiguration.IGNORE_CONTROL_SCRIPT_PROPERTY, false) )
        	m_ignoreControlScript.doClick();

        TestBedConfiguration.registerConfigurationChangeHandler(new TestBedConfiguration.ConfigurationChangeHandler() {

            public void onConfigurationChange() {
                testbedConfig = TestBedConfiguration.getInstance();
                String configFileName = testbedConfig.getFile().getName();
                mTestbedList.getModel().setSelectedItem(configFileName.substring(0, configFileName.lastIndexOf('.')));
            }
        });
    }

    private boolean ignoreControlScript() {
        return m_ignoreControlScript.isSelected();
    }

    private class SUTStartStopThread extends Thread {

        private String command = "";

        public SUTStartStopThread(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            TestBedConfiguration.reloadConfigFileIfModified();

            boolean start = command.equals("start");
	        TestResult tr = new TestResultImpl((start ? "(Re)start" : "Stop") + " SUT", null, null, 1, 1);
            tr.start();
            // TODO: Check this!
            TestResultsReportManager reportManager = TestResultsReportManager.getInstance(); //getReporters("Manual SUT " + (start ? "start" : "stop"));
            if ( CampaignManager.getInstance().getCurrentCampaign() != null )
            {
            	reportManager.startReport(CampaignManager.getInstance().getTimeStampCampaign(), "Manual SUT " + (start ? "start" : "stop"));
            }
            else
            {
            	reportManager.startReport(new Date(), "Manual SUT " + (start ? "start" : "stop"));
            }
            reportManager.putEntry(tr);
            TestEngine.tearDown();
            if (start) {
            	TestEngine.stopSUT(null);
                TestEngine.startSUT(tr, true);
            } else {
                TestEngine.stopSUT(tr);
            }
            tr.stop();
            reportManager.refresh();
            //reportManager.putEntry(tr);

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    isStartingOrStoppingTestbed = false;
                    setControlTestbedButtonsEnabled();
                    parent.getTestCasePanel().setStopButtonEnabled(false, false);
                    TestEngine.tearDown();
                }
            });
        }
    }

    public class TestbedMouseListener extends MouseAdapter {

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new ViewFileAction());
                menu.add(new EditFileAction());
                testbedConfig = TestBedConfiguration.getInstance();

               	menu.add(new EditControlScriptFileAction());
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), mTestbedList);
                menu.show(mTestbedList, pt.x, pt.y);
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

		class ViewFileAction extends AbstractAction {

            public ViewFileAction() {
                super("View File");
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(testbedConfig.getFile());
                } catch (IOException ex) {
                    logger.error("Error while calling Desktop open on " + testbedConfig.getFile());
                }
            }

            @Override
            public boolean isEnabled() {
                return Desktop.isDesktopSupported();
            }
        }

        class EditFileAction extends AbstractAction {

            public EditFileAction() {
                super("Edit File");
            }

            public void actionPerformed(ActionEvent e) {
                 parent.getTestCasePanel().loadTestCaseSource(testbedConfig.getFile(), true, false);
                 parent.getTreeTabsPanel().setSelectedIndex(0);
            }

            @Override
            public boolean isEnabled() {
                return Desktop.isDesktopSupported();
            }
        }
        class EditControlScriptFileAction extends AbstractAction {

            public EditControlScriptFileAction() {
                super("Edit Control script");
            }

            public void actionPerformed(ActionEvent e) {
                 testbedConfig = TestBedConfiguration.getInstance();
                 String scriptFilename = testbedConfig.getControlScriptFileName();
                 // use the internal editor
                 parent.getTestCasePanel().loadTestCaseSource(new File(scriptFilename), true, false);
                 parent.getTreeTabsPanel().setSelectedIndex(0);
            }

            @Override
            public boolean isEnabled() {
                testbedConfig = TestBedConfiguration.getInstance();
                if (testbedConfig == null) {
                	return false;
                }
                String scriptFilename = testbedConfig.getControlScriptFileName();
                return Desktop.isDesktopSupported() && scriptFilename != null;
            }
        }
    }
}
