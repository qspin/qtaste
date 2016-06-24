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
 * @author vdubois
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

@SuppressWarnings("serial")
public class WrappedToolTipUI extends BasicToolTipUI implements Serializable {
    public static ComponentUI createUI(JComponent c) {
        return sharedInstance_;
    }

    public Dimension getPreferredSize(JComponent c) {
        // Setup the font to use
        Font font = c.getFont();
        FontMetrics metrics = c.getFontMetrics(font);

        // Work out the width and height
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null) {
            return new Dimension(border_ * 2, metrics.getHeight());
        } else {
            StringTokenizer st = new StringTokenizer(tipText, "\n");
            int height = 0;
            int width = 0;
            while (st.hasMoreTokens()) {
                String s_part = st.nextToken();
                height += metrics.getHeight();
                while (s_part.length() > maxWidth_) {
                    String s = s_part.substring(0, maxWidth_).trim();
                    int w = metrics.stringWidth(s);
                    if (width < w) {
                        width = w;
                    }
                    s_part = s_part.substring(maxWidth_);
                    height += metrics.getHeight();
                }
                int w = metrics.stringWidth(s_part);
                if (width < w) {
                    width = w;
                }
            }
            return new Dimension(width + border_ * 2, height + border_ * 2);
        }
    }

    public void paint(Graphics g, JComponent c) {
        // Setup the font to use
        Font font = c.getFont();
        FontMetrics metrics = c.getFontMetrics(font);
        g.setFont(font);

        // Fill the background
        Dimension size = c.getSize();
        g.setColor(c.getBackground());
        g.fillRect(0, 0, size.width, size.height);

        // Write the text in the foreground colour
        g.setColor(c.getForeground());
        String tipText = ((JToolTip) c).getTipText();
        if (tipText != null) {
            StringTokenizer st = new StringTokenizer(tipText, "\n");
            int y = metrics.getAscent() + border_;
            while (st.hasMoreTokens()) {
                String s_part = st.nextToken();
                while (s_part.length() > maxWidth_) {
                    g.drawString(s_part.substring(0, maxWidth_).trim(), border_, y);
                    s_part = s_part.substring(maxWidth_);
                    y += metrics.getHeight();
                }
                g.drawString(s_part, border_, y);
                y += metrics.getHeight();
            }
        }
    }

    /**
     * Set the maximum width of a ToolTip box in number of chars
     *
     * @param newValue int
     */
    public void setMaxWidth(int newValue) {
        this.maxWidth_ = newValue;
    }

    /**
     * Get the maximum width of a ToolTip box in number of chars
     *
     * @return int
     */
    public int getMaxWidth() {
        return maxWidth_;
    }

    protected static WrappedToolTipUI sharedInstance_ = new WrappedToolTipUI();

    protected int maxWidth_ = 500;

    private int border_ = 2;
}

