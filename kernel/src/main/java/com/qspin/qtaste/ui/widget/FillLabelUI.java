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
 * The FillLabelUI is responsible for painting a label with a specific look.
 * The background is painted in blue or using a texture named noise.png in the resource directory main.
 * The bottom right corner can be rounded if pEndRounded is true in the constructor of FillLabelUI.
 */
public class FillLabelUI extends MetalLabelUI
{
   private boolean mEndRounded;
   private boolean mUseTexture;
   private static final double WIDTH_RATIO = 0.15;
   private static final double HEIGHT_RATIO = 0.20;
   private static Paint mBackTexture = null;
   private Color mColor = ResourceManager.getInstance().getLightColor();

   public FillLabelUI()
   {
      mEndRounded = false;
      mUseTexture = false;
   }

   public FillLabelUI(Color pColor)
   {
      mEndRounded = false;
      mUseTexture = false;
      mColor = pColor;
   }

   public FillLabelUI(boolean pEndRounded)
   {
      mEndRounded = pEndRounded;

      /*if(mBackTexture == null)
      {
         Image img = ResourceManager.getInstance().getImageIcon("main/noise").getImage();
         BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
         Graphics2D big = bi.createGraphics();
         big.drawImage(img, 0, 0, null);
         mBackTexture = new TexturePaint(bi, new Rectangle(img.getWidth(null), img.getHeight(null)));
         mUseTexture = true;
      }*/
   }

   protected void paintElements(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY,
            Color pTextColor,
            Color pBackColor)
   {
      Rectangle r = null;
      Graphics2D g2D = (Graphics2D)pG;
      if(!mEndRounded)
      {
         r = pG.getClipBounds();
         if(mUseTexture && mBackTexture != null)
         {
            g2D.setPaint(mBackTexture);
         }
         else
         {
            g2D.setColor(pBackColor);
         }
         pG.fillRect(r.x, r.y, r.width, r.height);
      }
      else
      {
         final int angle = -90;
         r = pLabel.getBounds();
         int stepx = (int)(r.width * WIDTH_RATIO);
         int step2x = stepx * 2;
         int startx = r.width - stepx;
         int stepy = (int)(r.height * HEIGHT_RATIO);
         int step2y = stepy * 2;
         int starty = r.height - stepy;
         if(mUseTexture && mBackTexture != null)
         {
            g2D.setPaint(mBackTexture);
         }
         else
         {
            g2D.setColor(pBackColor);
         }
         pG.fillRect(r.x, r.y, startx, r.height);
         pG.fillRect(r.x + startx, r.y, stepx, starty);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         pG.fillArc(r.width - step2x, r.height - step2y, step2x, step2y, 0, angle);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
      }
      if(pLabel.getIcon() != null)
      {
         pLabel.getIcon().paintIcon(pLabel, pG, 0, 0);
         pG.setColor(pTextColor);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         pG.drawString(pStr, pLabel.getIcon().getIconWidth()+2, pTextY);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
      }
      else
      {
         pG.setColor(pTextColor);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         pG.drawString(pStr, pTextX, pTextY);
         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
      }
   }

   protected void paintDisabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      paintElements(pLabel, pG, pStr, pTextX, pTextY, mColor, Color.WHITE);
   }

   protected void paintEnabledText(JLabel pLabel,
            Graphics pG,
            String pStr,
            int pTextX,
            int pTextY)
   {
      paintElements(pLabel, pG, pStr, pTextX, pTextY, Color.WHITE, mColor);
   }
}
