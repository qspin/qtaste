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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.qspin.qtaste.ui.util.QSpinTheme;

/**
 * @author vdubois
 */
@SuppressWarnings("serial")
public class MainConfigFrame extends JFrame {

    protected String title = "QTaste Configuration settings";
    private QSpinTheme mTheme;

    public MainConfigFrame() {
        super();
        setTitle(title);
        setUpFrame();
    }

    private void setUpFrame() {
        setName(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void setQSpinTheme() {
        mTheme = new QSpinTheme();
        MetalLookAndFeel.setCurrentTheme(mTheme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {

        }
    }

    public void launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setQSpinTheme();
                genUI();
            }
        });
    }

    public void genUI() {
        MainConfigPanel mainPanel = new MainConfigPanel(this);
        this.add(mainPanel);
        setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

}
