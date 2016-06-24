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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.util.FileUtilities;

/**
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestBedConfigurationPanel extends ConfigPanelTemplate {

    private TestBedConfiguration testbedConfig;

    public TestBedConfigurationPanel() {
        super();
        testbedConfig = TestBedConfiguration.getInstance();
        //genUI();
        // add listeners
        jMultipleInstancesComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        DefaultComboBoxModel model = (DefaultComboBoxModel) jTestbedComboBox.getModel();
        model.removeAllElements();
        File fTestbedDir = new File(StaticConfiguration.TESTBED_CONFIG_DIRECTORY);
        FileMask fileMask = new FileMask();
        fileMask.addExtension("xml");
        File[] fTestbedList = FileUtilities.listSortedFiles(fTestbedDir, fileMask);
        for (int i = 0; i < fTestbedList.length; i++) {
            // remove the extension
            String testbedName = fTestbedList[i].getName().substring(0, fTestbedList[i].getName().lastIndexOf("."));
            model.addElement(testbedName);
        }

        refreshTestBed();
        refreshData();

        TestBedConfiguration.registerConfigurationChangeHandler(new TestBedConfiguration.ConfigurationChangeHandler() {

            public void onConfigurationChange() {
                testbedConfig = TestBedConfiguration.getInstance();
                refreshTestBed();
                refreshData();
            }
        });
    }

    private void refreshTestBed() {
        String testbedFileName = testbedConfig.getFile().getName();
        String testbedName = testbedFileName.substring(0, testbedFileName.lastIndexOf('.'));
        jTestbedComboBox.getModel().setSelectedItem(testbedName);
    }

    public void refreshData() {
        // retrieve the current testbed
        DefaultComboBoxModel model = (DefaultComboBoxModel) jTestbedComboBox.getModel();
        String str = model.getSelectedItem().toString();
        String testbedFilename = StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/" + str + ".xml";
        TestBedConfiguration.setConfigFile(testbedFilename);
        // reload the file
        testbedConfig.reload();
    }

    /*protected void Apply() {
        
    }
    protected void Save() {
        try {
            testbedConfig.save();
        } catch (ConfigurationException ex) {
            // open error dialog frame
            
        }
    }*/
    private javax.swing.JComboBox jMultipleInstancesComboBox;
    private javax.swing.JLabel jMultipleInstancesLabel;
    private javax.swing.JPanel jMultipleInstancesPanel;
    private javax.swing.JComboBox jTestbedComboBox;
}