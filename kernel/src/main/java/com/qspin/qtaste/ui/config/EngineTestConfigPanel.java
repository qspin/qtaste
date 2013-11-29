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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.configuration.ConfigurationException;

import com.qspin.qtaste.config.TestEngineConfiguration;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class EngineTestConfigPanel extends ConfigPanelTemplate {

    private TestEngineConfiguration engineConfig;
    private JPanel scriptPanel, reportingPanel;
    private JComboBox reportingFormatLabelComboBox;
    private JTextField reportingDirLabelTextField;
    public EngineTestConfigPanel() {
        super();
        engineConfig = TestEngineConfiguration.getInstance();
        genUI();
        refreshData();
    }

    protected void Save() {
        try {
            engineConfig.save();
        } catch (ConfigurationException ex) {
            // report it to GUI
        }
    }
    protected void Apply() {
        // TO DO : reporting is now a list of reporters
/*
        switch (reportingFormatLabelComboBox.getSelectedIndex()) {
            case 0:
                engineConfig.setProperty("reporting.format", "HTML");
                break;
            case 1:
                engineConfig.setProperty("reporting.format", "XML");
                break;
        }
 */ 
        engineConfig.setProperty("reporting.generated_report_path", reportingDirLabelTextField.getText());
    }
    
    protected void refreshData() {
        // script_engine
            //scriptLanguageComboBox.setSelectedIndex(0);
        
        // reporting
        /*
        String reportingFormat = engineConfig.getString("reporting.format");
        if (reportingFormat.toUpperCase().equals("HTML"))
            reportingFormatLabelComboBox.setSelectedIndex(0);
        if (reportingFormat.toUpperCase().equals("XML"))
            reportingFormatLabelComboBox.setSelectedIndex(1);
        */
        reportingDirLabelTextField.setText(engineConfig.getString("reporting.generated_report_path"));
    }
    
    public void genUI() {
        scriptPanel = new JPanel();
        reportingPanel = new JPanel();

        JLabel reportingFormatLabel = new JLabel();
        reportingFormatLabelComboBox = new JComboBox();
        JLabel reportingDirLabel = new javax.swing.JLabel();
        reportingDirLabelTextField = new javax.swing.JTextField();

        reportingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reporting"));
        reportingFormatLabel .setText("Format");

        reportingFormatLabelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HTML", "XML" }));
        reportingDirLabel.setText("Directory:");

        reportingDirLabelTextField.setText("jTextField3");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(reportingPanel);
        reportingPanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(reportingFormatLabel)
                        .addGap(18, 18, 18)
                        .addComponent(reportingFormatLabelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(reportingDirLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportingDirLabelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reportingFormatLabel)
                    .addComponent(reportingFormatLabelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reportingDirLabel)
                    .addComponent(reportingDirLabelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(reportingPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scriptPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scriptPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reportingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        
    }
}
