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

package com.qspin.qtaste.ui.jedit;

/**
 * @author vdubois
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.ui.tools.SpringUtilities;

@SuppressWarnings("serial")
public class FindDialog extends JFrame {

    private JTextComponent component;
    private JTextField txtFind = new JTextField(20);
    private JLabel findLabel = new JLabel("Find what:");
    private JButton btnFind = new JButton("Find");
    private JButton btnCancel = new JButton("Cancel");

    public FindDialog(JTextComponent component) {
        this.component = component;
        setTitle("Find");
        setupLayout();
        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void setupLayout() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createMainFindPanel());
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void doFind() {
        try {
            int start = component.getCaretPosition();
            String textToFind = txtFind.getText();
            int found = component.getText().indexOf(textToFind, start + 1);
            if (found >= 0) {
                component.select(found, found + textToFind.length());
            } else {
                JOptionPane.showMessageDialog(null, "'" + textToFind + "' not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new SpringLayout());

        btnCancel.addActionListener(new CloseActionListener());
        btnFind.addActionListener(new DoFindAction());
        buttonPanel.add(btnFind);
        buttonPanel.add(btnCancel);
        SpringUtilities.makeCompactGrid(buttonPanel, 1, 2, 5, 5, 5, 5);
        return buttonPanel;

    }

    private JPanel createMainFindPanel() {
        JPanel fieldPanel = new JPanel(new SpringLayout());
        txtFind.setColumns(25);

        findLabel.setLabelFor(txtFind);
        fieldPanel.add(findLabel);
        fieldPanel.add(txtFind);

        // do find on ENTER keypress and close dialog on ESCAPE keypress
        txtFind.getActionMap().put("DoFind", new DoFindAction());
        txtFind.getActionMap().put("Close", new CloseActionListener());
        txtFind.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "DoFind");
        txtFind.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "Close");
        SpringUtilities.makeCompactGrid(fieldPanel, 2, 1, 5, 5, 5, 5);

        return fieldPanel;
    }

    private class DoFindAction extends AbstractAction {

        public DoFindAction() {
            super("Find");
        }

        public void actionPerformed(ActionEvent e) {
            doFind();
        }
    }

    private class CloseActionListener extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
}
