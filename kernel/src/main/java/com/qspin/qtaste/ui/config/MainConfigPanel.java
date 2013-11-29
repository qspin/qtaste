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

package com.qspin.qtaste.ui.config;

import com.qspin.qtaste.ui.panel.TitlePanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class MainConfigPanel extends JPanel {

    protected JFrame parent;
    
    protected EngineTestConfigPanel engineTestConfigPanel;
    protected TestBedConfigurationPanel testBedConfigPanel;

    private static final String TESTBED_CONFIG = "Test Bed Configuration";
    private static final String ENGINE_CONFIG = "Engine configuration";
    
    public static final int TESTBED_CONFIG_INDEX = 0;
    public static final int ENGINE_CONFIG_INDEX = 1;
    public static final int TESTSUITE_CONFIG_INDEX = 2;
    
    public MainConfigPanel(JFrame parent) {
        super(new BorderLayout());
        this.parent = parent;

        genUI();
    }
    protected void genUI() {
//        this.setLayout(new GridBagLayout());

        TitlePanel titlePanel = new TitlePanel("QTaste Configuration", "main/qspin", false);
        // create a tabbed pane
        JTabbedPane selectionConfig = new JTabbedPane();
        
        engineTestConfigPanel = new EngineTestConfigPanel();
        testBedConfigPanel = new TestBedConfigurationPanel();
        
        selectionConfig.add(TESTBED_CONFIG, new JScrollPane(testBedConfigPanel));
        selectionConfig.add(ENGINE_CONFIG, new JScrollPane(engineTestConfigPanel));
        //selectionConfig.add(TESTSUITE_CONFIG, new JScrollPane(tcDataCSVPane));
        
        JPanel buttonPanel = new JPanel();
        JButton applyButton = new JButton("Apply");
        buttonPanel.add(applyButton);
        applyButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                engineTestConfigPanel.Apply();
                testBedConfigPanel.Apply();
                        
            }
        });

        JButton saveButton = new JButton("Save");
        buttonPanel.add(saveButton);
        saveButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                engineTestConfigPanel.Save();
                testBedConfigPanel.Save();
                        
            }
        });
        
        this.add(titlePanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.add(selectionConfig);
        this.setVisible(true);
    }
}
