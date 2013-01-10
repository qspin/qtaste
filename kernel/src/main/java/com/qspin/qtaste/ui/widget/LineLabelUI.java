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

import com.qspin.qtaste.ui.tools.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

/**
 * The LineLabelUI is responsible for painting a label with a specific look.
 * The text is painted followed by a horizontal line.
 */
public class LineLabelUI extends MetalLabelUI
{
   private static final int SPACE_INC = 8;
   protected void paintDisabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      pG.setColor(ResourceManager.getInstance().getBackColor().darker());
      pG.drawString(pStr, pTextX, pTextY);

      FontMetrics fm = pG.getFontMetrics();
      Rectangle strrect = fm.getStringBounds(pStr, pG).getBounds();
      pG.setColor(ResourceManager.getInstance().getBackColor());

      int ypos = pTextY + (strrect.y + (strrect.height / 2));
      pG.drawLine(pTextX + strrect.width + SPACE_INC, ypos, pLabel.getWidth(), ypos);
   }

   protected void paintEnabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      pG.setColor(ResourceManager.getInstance().getNormalColor());
      pG.drawString(pStr, pTextX, pTextY);

      FontMetrics fm = pG.getFontMetrics();
      Rectangle strrect = fm.getStringBounds(pStr, pG).getBounds();
      pG.setColor(ResourceManager.getInstance().getBackColor());

      int ypos = pTextY + (strrect.y + (strrect.height / 2));
      pG.drawLine(pTextX + strrect.width + SPACE_INC, ypos, pLabel.getWidth(), ypos);
   }
}
