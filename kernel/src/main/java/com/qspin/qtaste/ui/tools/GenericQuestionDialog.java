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

package com.qspin.qtaste.ui.tools;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.impl.TestDataImpl;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class GenericQuestionDialog extends JDialog {
    private String[] mNames;
    private TestData mData = new TestDataImpl(1, new LinkedHashMap<String, String>());
    private ArrayList<JLabel> mLabels;
    private ArrayList<JTextField> mTextFields;
    JCheckBox mAddToCSVCheckBox;

public GenericQuestionDialog(Window owner, String title, String[] names)
{
    super(owner, title, ModalityType.APPLICATION_MODAL);
    mNames =  names;
    
}

public void init() {
    
    GridLayout layout = new GridLayout(mNames.length+1, 2, 10,10);
    this.setLayout(new BorderLayout());
    JPanel mainPanel = new JPanel(layout);
    
    mLabels = new ArrayList<JLabel>();
    mTextFields = new ArrayList<JTextField>();

    for (String name : mNames) {
        JLabel  label = new JLabel();
        label.setText(name);
        JTextField textField = new JTextField();
        textField.setSize(400, 25);
        mLabels.add(label);
        mTextFields.add(textField);
        
    }
    this.addLabelTextRows(mLabels, mTextFields,mainPanel);
 
    
    JPanel buttonPanel = new JPanel(new GridLayout(2,2,20,20));
    mAddToCSVCheckBox = new JCheckBox("Add to csv");
    buttonPanel.add(mAddToCSVCheckBox);
    JLabel dummylabel = new JLabel();
    buttonPanel.add(dummylabel);
    
    JButton okBtn = new JButton();
    okBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JButton okBtn = (JButton)e.getSource();
                GenericQuestionDialog dialog = (GenericQuestionDialog)okBtn.getTopLevelAncestor();
                dialog.ok();
            }
        });
    okBtn.setText("Ok");
    
    JButton cancelBtn = new JButton();
    cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JButton okBtn = (JButton)e.getSource();
                JPanel buttonPanel = (JPanel)okBtn.getParent();
                GenericQuestionDialog dialog = (GenericQuestionDialog)buttonPanel.getParent();
                dialog.cancel();
                
            }
        });
    cancelBtn.setText("Cancel");
    
    buttonPanel.add(okBtn);
    buttonPanel.add(cancelBtn);
    
    this.add(mainPanel);
    this.add(buttonPanel, BorderLayout.SOUTH);
    
    this.pack();
    int height = this.getSize().height;
    this.setPreferredSize(new Dimension (400, height));
    this.pack();
    this.setModal(true);
    //Dimension screenSize  = Toolkit.getDefaultToolkit().getScreenSize(); 
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    
}
public boolean isTestDataInCSV() {
    return mAddToCSVCheckBox.isSelected();
}
        
public TestData getTestData() {
    return mData;
}
public void cancel() {
    this.setVisible(false);
}

public void ok() {
    // retrieve all 
    for (int i=0 ; i < mLabels.size(); i++)
    {
        try {
            mData.setValue(mLabels.get(i).getText(), mTextFields.get(i).getText());
        } catch (QTasteDataException e) {
            JOptionPane.showMessageDialog(this,e.getMessage(),"Invalid data",JOptionPane.ERROR_MESSAGE);
        }
    }
    this.setVisible(false);
}
private void  addLabelTextRows(ArrayList<JLabel> labels,
            ArrayList<JTextField> textFields,
            Container container) {
     int nitem = labels.size();    // nombre de lignes

     for (int i = 0; i < nitem; i++) {
          container.add(labels.get(i));
          container.add(textFields.get(i));
     }
    
}
    
}
