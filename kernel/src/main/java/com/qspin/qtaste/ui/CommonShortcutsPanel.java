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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;


/**
 *
 * @author vdubois
 */

@SuppressWarnings("serial")
public class CommonShortcutsPanel extends JPanel {
    public CommonShortcutsPanel() {
        super(new BorderLayout());
        genUI();
    }
    private void genUI() {
        showTestAPIButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/testAPIDoc"));
        showTestAPIButton.setToolTipText("Show test API documentation");
        showTestAPIButton.setVisible(true);
        showTestAPIButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String filename = StaticConfiguration.TEST_API_DOC_DIR + File.separator + "index.html";
                File resultsFile = new File(filename);
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(resultsFile);
                    } else {
                        logger.error("Feature not supported by this platform");
                    }
                } catch (IOException ex) {
                    logger.error("Could not open " + filename);
                }
            }
        });
        this.add(showTestAPIButton, BorderLayout.CENTER);
    }
 

    protected JButton showTestAPIButton = new JButton();
    private static Logger logger = Log4jLoggerFactory.getLogger(CommonShortcutsPanel.class);

}
