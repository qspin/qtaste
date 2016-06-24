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

package com.qspin.qtaste.ui.widget;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalSeparatorUI;

import com.qspin.qtaste.ui.tools.ResourceManager;

/**
 * A separator ui is responsible to draw a line in the back color given by the ResourceManager.
 */
public class SeparatorUI extends MetalSeparatorUI {
    private boolean mVisible;

    public SeparatorUI(boolean pVisible) {
        mVisible = pVisible;
    }

    public void paint(Graphics pG, JComponent pComp) {
        if (mVisible) {
            pG.setColor(ResourceManager.getInstance().getBackColor());
        } else {
            pG.setColor(Color.WHITE);
        }
        pG.drawLine(0, 0, pComp.getWidth(), 0);
    }
}
