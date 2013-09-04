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

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.versioncontrol.VersionControl;
import com.qspin.qtaste.util.versioncontrol.VersionControlInterface;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    protected static Logger logger = Log4jLoggerFactory.getLogger(AboutDialog.class);

    public AboutDialog(JFrame rootFrame) {
        super(rootFrame, "About QTaste", true);
        init();
        setVisible(true);
    }

    private void init() {
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(new JLabel("Description:"));
        JLabel descriptionLabel = new JLabel("<html><blockquote>QTaste is a data-driven automated testing tool.</blockquote></html>");
        descriptionLabel.setFont(ResourceManager.getInstance().getStandardFontLight());
        b.add(descriptionLabel);
        // Third products link

        JLabel thirdProductsLink = new URLLabel("<html><blockquote>Information about third products</blockquote></html>", "file://" + StaticConfiguration.QTASTE_ROOT + "/doc/third_products/ThirdProducts.htm");
        thirdProductsLink.setFont(ResourceManager.getInstance().getStandardFontLight());
        b.add(thirdProductsLink);

        // log version information
        b.add(new JLabel(" "));
        String kernelVersion = com.qspin.qtaste.kernel.Version.getInstance().getFullVersion();
        JLabel kernelVersionLabel = new JLabel("<html><b>Kernel version:</b> " + kernelVersion + "</html>");
        kernelVersionLabel.setFont(ResourceManager.getInstance().getStandardFontLight());
        b.add(kernelVersionLabel);
        if (!kernelVersion.equals("unknown") && !kernelVersion.endsWith("SNAPSHOT")) {
            JLabel kernelReleaseNotesLabel = new URLLabel("<html><blockquote>Release notes</blockquote></html>", "file://" + StaticConfiguration.QTASTE_RELEASE_NOTES_FILE);
            kernelReleaseNotesLabel.setFont(ResourceManager.getInstance().getStandardFontLight());
            thirdProductsLink.setFont(ResourceManager.getInstance().getStandardFontLight());
            b.add(kernelReleaseNotesLabel);
        }
        JLabel javaVersionVendor = new JLabel("<html><br><b>Java version:</b> " + System.getProperty("java.version") +
                           " from " + System.getProperty("java.vendor") + "</html>");
        javaVersionVendor.setFont(ResourceManager.getInstance().getStandardFontLight());
        b.add(javaVersionVendor);

        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        b.setBorder(emptyBorder);

        Container contentPane = getContentPane();

        b.add(Box.createGlue());
        contentPane.add(b, "Center");

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        contentPane.add(p2, "South");

        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
                dispose();
            }
        });


        setSize(450, 250);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("serial")
    public class URLLabel extends JLabel {

        private URL url;
        private Color unvisitedURL = Color.blue;

        public URLLabel(String text, String url) {
            super(text);
            setForeground(unvisitedURL);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            //setToolTipText(url);
            try {
                this.url = new URL(url);
                addMouseListener(new Clicked());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        class Clicked extends MouseAdapter {

            public void mouseClicked(MouseEvent me) {
                try {
                    Desktop.getDesktop().browse(url.toURI());
                } catch (IOException ex) {
                } catch (URISyntaxException ex) {
                    logger.error(ex);
                }
            }
        }
    }
}
