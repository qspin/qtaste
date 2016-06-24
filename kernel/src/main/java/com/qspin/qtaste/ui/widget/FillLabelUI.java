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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.plaf.metal.MetalLabelUI;

import com.qspin.qtaste.ui.tools.ResourceManager;

/**
 * The FillLabelUI is responsible for painting a label with a specific look.
 * The background is painted in blue or using a texture named noise.png in the resource directory main.
 * The bottom right corner can be rounded if pEndRounded is true in the constructor of FillLabelUI.
 */
public class FillLabelUI extends MetalLabelUI {
    private boolean mEndRounded;
    private static final double WIDTH_RATIO = 0.15;
    private static final double HEIGHT_RATIO = 0.20;
    private Color mColor = ResourceManager.getInstance().getLightColor();

    public FillLabelUI() {
        mEndRounded = false;
    }

    public FillLabelUI(Color pColor) {
        mEndRounded = false;
        mColor = pColor;
    }

    public FillLabelUI(boolean pEndRounded) {
        mEndRounded = pEndRounded;
    }

    protected void paintElements(JLabel pLabel, Graphics pG, String pStr, int pTextX, int pTextY, Color pTextColor, Color
          pBackColor) {
        Rectangle r;
        Graphics2D g2D = (Graphics2D) pG;
        if (!mEndRounded) {
            r = pG.getClipBounds();
            g2D.setColor(pBackColor);
            pG.fillRect(r.x, r.y, r.width, r.height);
        } else {
            final int angle = -90;
            r = pLabel.getBounds();
            int stepx = (int) (r.width * WIDTH_RATIO);
            int step2x = stepx * 2;
            int startx = r.width - stepx;
            int stepy = (int) (r.height * HEIGHT_RATIO);
            int step2y = stepy * 2;
            int starty = r.height - stepy;
            g2D.setColor(pBackColor);
            pG.fillRect(r.x, r.y, startx, r.height);
            pG.fillRect(r.x + startx, r.y, stepx, starty);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pG.fillArc(r.width - step2x, r.height - step2y, step2x, step2y, 0, angle);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
        if (pLabel.getIcon() != null) {
            pLabel.getIcon().paintIcon(pLabel, pG, 0, 0);
            pG.setColor(pTextColor);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pG.drawString(pStr, pLabel.getIcon().getIconWidth() + 2, pTextY);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        } else {
            pG.setColor(pTextColor);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pG.drawString(pStr, pTextX, pTextY);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }

    protected void paintDisabledText(JLabel pLabel, Graphics pG, String pStr, int pTextX, int pTextY) {
        paintElements(pLabel, pG, pStr, pTextX, pTextY, mColor, Color.WHITE);
    }

    protected void paintEnabledText(JLabel pLabel, Graphics pG, String pStr, int pTextX, int pTextY) {
        paintElements(pLabel, pG, pStr, pTextX, pTextY, Color.WHITE, mColor);
    }
}
