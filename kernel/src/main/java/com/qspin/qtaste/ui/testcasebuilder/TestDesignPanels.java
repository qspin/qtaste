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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.ui.testcasebuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.ui.TestAPIPanel;
import com.qspin.qtaste.ui.InteractiveLogPanel;
import com.qspin.qtaste.ui.panel.TitlePanel;
import com.qspin.qtaste.ui.tools.DirectoryDialog;
import com.qspin.qtaste.ui.tools.ResourceManager;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestDesignPanels extends TestAPIPanel {

    private JTabbedPane mDetailsTabs;
    private List<JPanel> mdDesignPanels = new ArrayList<JPanel>();
    private TestDesignSourcePanel mTestDesignSourcePanel;
    private TestDesignDataPanel mTestDesignDataPanel;
    private JPanel mTopMainPanel;
    private InteractiveLogPanel mLogPanel;
    private JTextField mTestcaseDirectoryTextField;
    private JTextField mTestcaseName;

    public TestDesignPanels() {
    }

    public TestDesignSourcePanel getDesignPanel() {
        return mTestDesignSourcePanel;
    }

    public TestDesignDataPanel getDataPanel() {
        return mTestDesignDataPanel;
    }

    public JPanel createMainTestDesignPanel() {
        TitlePanel main = new TitlePanel("Test design", "", false);
        main.getContentPanel().setLayout(new BorderLayout());

        // add the ability to save a testcase
        mTopMainPanel = new JPanel(new BorderLayout());
        JPanel mTopWMainPanel = new JPanel(new BorderLayout());
        JPanel mTopEMainPanel = new JPanel(new BorderLayout());

        mTestcaseDirectoryTextField = new JTextField();
        JButton saveButton = new JButton();
        saveButton.setText("Save");
        JButton browseButton = new JButton();
        browseButton.setText("Browse");

        JLabel labelTestCaseName = new JLabel();
        labelTestCaseName.setText("Name:");
        mTestcaseName = new JTextField();
        mTestcaseName.setText("InteractiveTest");

        /*ConfigManager cfgMgr = ConfigManager.getInstance();
        String initDirectory = cfgMgr.getConfig().getProperty("TESTSUITE_ROOT");
        String baseDir = System.getProperty("user.dir");
        initDirectory = baseDir + File.separator + initDirectory;   
        mTestcaseDirectoryTextField.setText(initDirectory);
         */

        mTopWMainPanel.add(saveButton, BorderLayout.EAST);
        mTopWMainPanel.add(labelTestCaseName, BorderLayout.WEST);
        mTopWMainPanel.add(mTestcaseName);
//      mTopMainPanel.add(browseButton, BorderLayout.EAST);
        //     mTopMainPanel.add(mTestcaseDirectoryTextField,BorderLayout.CENTER);

        browseButton.addActionListener(new BrowseButtonAction(this));

        JButton executeButton = new JButton();
        ExecuteButtonAction buttonListener = new ExecuteButtonAction(this);
        executeButton.addActionListener(buttonListener);
        executeButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/running_32"));
        executeButton.setToolTipText("Run Test(s)");

        mTopEMainPanel.add(executeButton);
        mTopMainPanel.add(mTopEMainPanel, BorderLayout.EAST);
        mTopMainPanel.add(mTopWMainPanel);

        main.getContentPanel().add(mTopMainPanel, BorderLayout.NORTH);
        // add the Test source/ Test Data Panel
        mTestDesignSourcePanel = new TestDesignSourcePanel();
        mTestDesignSourcePanel.init();
        mdDesignPanels.add(mTestDesignSourcePanel);

        mTestDesignDataPanel = new TestDesignDataPanel();
        mTestDesignDataPanel.init();
        mdDesignPanels.add(mTestDesignDataPanel);


        mLogPanel = new InteractiveLogPanel();
        mLogPanel.init();
        mdDesignPanels.add(mLogPanel);

        mDetailsTabs = new JTabbedPane();

        for (JPanel infoPanel : mdDesignPanels) {
            mDetailsTabs.addTab(infoPanel.getName(), infoPanel);
        }
        main.getContentPanel().add(mDetailsTabs);
        return main;
    }

    public void init() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createMainTestDesignPanel(), BorderLayout.CENTER);
        add(panel);
    }

    protected class BrowseButtonAction implements ActionListener {

        TestDesignPanels mPanel;

        protected BrowseButtonAction(TestDesignPanels parent) {
            this.mPanel = parent;
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {

            String initDirectory = TestEngineConfiguration.getInstance().getString("testsuite");

            String baseDir = System.getProperty("user.dir");
            initDirectory = baseDir + File.separator + initDirectory;
            File file = new File(initDirectory);

            DirectoryDialog dialog = new DirectoryDialog(null, "Define Testcase directory", file);
            String directory = dialog.getDirectory();
            if (directory != null) {
                mPanel.mTestcaseDirectoryTextField.setText(directory);
            }
        }
    }

    protected class ExecuteButtonAction implements ActionListener {

        TestDesignPanels mParent;

        protected ExecuteButtonAction(TestDesignPanels parent) {
            mParent = parent;
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
        // run the test line per line
        // define a testdirectory with the python script with testdata
        // then run QTaste with the temporary testsuite. Is that ok?
        // other alternative is to run in interactive mode but in that
        // case Python Interpreter or the clase JSR223TestScript must be used
        }
    }
}
