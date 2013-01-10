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
 * The BarLabelUI is responsible for painting a label using a specific look.
 * The background is painted in IBA green and the text is either in black for an enabled label
 * or in gray for a disabled one.
 */
public class BarLabelUI extends MetalLabelUI
{
   private static final int SPACE_INC = 12;

   protected void paintVerticalBar(JLabel pLabel, Graphics pG)
   {
      Rectangle r = pG.getClipBounds();
      Rectangle lblr = pLabel.getBounds();

      Rectangle bar = new Rectangle(lblr.width - SPACE_INC, r.y, 2, r.height);

      Rectangle inter = r.intersection(bar);

      if(inter.width > 0 && inter.height > 0)
      {
         pG.setColor(ResourceManager.getInstance().getNormalColor());
         pG.fillRect(inter.x, inter.y, inter.width, inter.height);
      }
   }

   protected void paintDisabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      pG.setColor(Color.GRAY);
      pG.drawString(pStr, pTextX, pTextY);

      paintVerticalBar(pLabel, pG);
   }
   protected void paintEnabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      pG.setColor(Color.BLACK);
      pG.drawString(pStr, pTextX, pTextY);

      paintVerticalBar(pLabel, pG);
   }
}
