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
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

/**
 * The StepLabelUI class is responsible for painting a label with antialiasing.
 * The text is black for an enabled label and gray for a disabled one.
 */
public class StepLabelUI extends MetalLabelUI
{
//   private static final int SPACE_INC = 12;
   protected void paintDisabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      Graphics2D g2 = (Graphics2D)pG;

      pG.setColor(Color.GRAY);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      pG.drawString(pStr, pTextX, pTextY);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

      /*FontMetrics fm = pG.getFontMetrics();
      Rectangle strrect = fm.getStringBounds(pStr, pG).getBounds();
      if(pLabel.getText().length() > 0)
      {
         pG.setColor(ResourceManager.getInstance().getNormalColor());
         pG.fillRect(pTextX + strrect.width + SPACE_INC, pTextY + strrect.y, 2, strrect.height);
      }*/
   }

   protected void paintEnabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      Graphics2D g2 = (Graphics2D)pG;

      pG.setColor(Color.BLACK);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      pG.drawString(pStr, pTextX, pTextY);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

      /*FontMetrics fm = pG.getFontMetrics();
      Rectangle strrect = fm.getStringBounds(pStr, pG).getBounds();
      if(pLabel.getText().length() > 0)
      {
         pG.setColor(ResourceManager.getInstance().getNormalColor());
         pG.fillRect(pTextX + strrect.width + SPACE_INC, pTextY + strrect.y, 2, strrect.height);
      }*/
   }
}
