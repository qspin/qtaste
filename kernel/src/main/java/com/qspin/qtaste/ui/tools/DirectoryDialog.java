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

/**
 *
 * @author vdubois
 */
import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class DirectoryDialog extends javax.swing.JDialog {

    public String directory;
    private JFileChooser chooser;

    public DirectoryDialog(Window owner, String title, File initDirectory) {
    
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initComponents(initDirectory);
        directory = "";

        this.setVisible(true);
        this.setLocation(100, 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }
    public DirectoryDialog(Window owner, String title) {
        this(owner, title, null);
    }

    private void initComponents(File initDirectory) {
        chooser = new javax.swing.JFileChooser();
        if (initDirectory != null)
            chooser.setSelectedFile(initDirectory);
        getContentPane().setLayout(new BorderLayout());

        setTitle("Select a folder");
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        chooser.setFont(new java.awt.Font("Dialog", 0, 12));
        chooser.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooserActionPerformed(evt);
            }
        });

        getContentPane().add(chooser);

        pack();
    }

    private void chooserActionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getActionCommand().startsWith("Approve")) {
            directory = chooser.getSelectedFile().getPath();
        } else {
            directory = null;
        }
        setVisible(false);
        dispose();
    }

    public String getDirectory() {
        return directory;
    }

    private void closeDialog(java.awt.event.WindowEvent evt) {
        directory = null;
        setVisible(false);
        dispose();
    }
}

