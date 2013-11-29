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

package com.qspin.qtaste.ui.tools;
 
import com.qspin.qtaste.ui.widget.*;
import java.awt.*;
import javax.swing.*;
/**
 * The GridBagLineAdder object is an object displaying gui objects in a grid bag layout like a typewriter.
 * You add gui objects from left to right on the same line when you call add().
 * You go to the next line when you call next().
 *
 * You can add a separator (a green line) by calling addSeparator()
 */
public class GridBagLineAdder
{
   public GridBagLineAdder(Container pContainer)
   {
      mContainer = pContainer;
      mConstaints.insets = mNormalInsets;
   }
   public void add(Component pComp)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = 1;
      mConstaints.fill = GridBagConstraints.HORIZONTAL;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.CENTER;
      if(mOffsetInsets != null)
      {
         mConstaints.insets = mOffsetInsets;
      }
      mContainer.add(pComp, mConstaints);
      ++mCurrentX;
   }
   public void addWithVerticalFill(Component pComp)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = 1;
      mConstaints.fill = GridBagConstraints.BOTH;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.CENTER;
      if(mOffsetInsets != null)
      {
         mConstaints.insets = mOffsetInsets;
      }
      mContainer.add(pComp, mConstaints);
      ++mCurrentX;
   }
   public void add(Component pComp, int pLength)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = pLength;
      mConstaints.fill = GridBagConstraints.HORIZONTAL;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.CENTER;
      if(mOffsetInsets != null)
      {
         mConstaints.insets = mOffsetInsets;
      }
      mContainer.add(pComp, mConstaints);
      mCurrentX += pLength;
   }
   public void addNoFillLeft(Component pComp, int pLength)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = pLength;
      mConstaints.fill = GridBagConstraints.NONE;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.WEST;
      if(mOffsetInsets != null)
      {
         mConstaints.insets = mOffsetInsets;
      }
      mContainer.add(pComp, mConstaints);
      mCurrentX += pLength;
   }
   private void add(Component pComp, int pLength, Insets pInsets)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = pLength;
      mConstaints.fill = GridBagConstraints.HORIZONTAL;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.CENTER;
      mConstaints.insets = pInsets;
      mContainer.add(pComp, mConstaints);
      mCurrentX += pLength;
   }
   public void addWithVerticalFill(Component pComp, int pLength)
   {
      mConstaints.gridx = mCurrentX;
      mConstaints.gridy = mCurrentY;
      mConstaints.gridheight = 1;
      mConstaints.gridwidth = pLength;
      mConstaints.fill = GridBagConstraints.BOTH;
      mConstaints.weightx = mWeightX;
      mConstaints.weighty = mWeightY;
      mConstaints.anchor = GridBagConstraints.CENTER;
      if(mOffsetInsets != null)
      {
         mConstaints.insets = mOffsetInsets;
      }
      mContainer.add(pComp, mConstaints);
      mCurrentX += pLength;
   }
   public void addFiller()
   {
      ++mCurrentX;
   }
   public void addLineFiller()
   {
      next();
      Separator sep = new Separator(false);
      addToEnd(sep);
   }
   public void addTitle(String pTitle)
   {
      next();
      JLabel lbl = new JLabel("  " + pTitle, JLabel.LEADING);
      //JLabel lbl = new JLabel(pTitle, JLabel.LEADING);
      lbl.setFont(ResourceManager.getInstance().getLargeFont());
      //lbl.setUI(new FillLabelUI(ResourceManager.getInstance().getLightColor()));
      lbl.setUI(new FillLabelUI(ResourceManager.getInstance().getBackColor().darker()));
      addToEnd(lbl, mTitleInsets);
      //lbl.setUI(new StepLabelUI());
      //add(lbl, 1, mNormalInsets);
      next();
   }
   public void addToEnd(Component pComp)
   {
      add(pComp, mLength);
      next();
   }
   private void addToEnd(Component pComp, Insets pInsets)
   {
      add(pComp, mLength, pInsets);
      next();
   }
   public void addSeparator()
   {
      next();
      add(new Separator(), mLength, mNormalInsets);
      next();
   }
   public void addSeparator(String pTitle)
   {
      next();
      JLabel lbl = new JLabel(pTitle, JLabel.LEADING);
      lbl.setEnabled(false);
      lbl.setUI(new LineLabelUI());
      addToEnd(lbl, mNormalInsets);
   }
   public void setLength(int pLength)
   {
      mLength = pLength;
   }
   public void next()
   {
      if(mCurrentX > mLength)
      {
         mLength = mCurrentX;
      }
      mCurrentX = 0;
      ++mCurrentY;
   }
   public void setWeight(double pWX, double pWY)
   {
      mWeightX = pWX;
      mWeightY = pWY;
   }
   public void setOffset(int pPixels)
   {
      mOffsetInsets = new Insets(PIXELNBR, pPixels, PIXELNBR, PIXELNBR);
   }
   private static final int PIXELNBR = 4;
   private static final int HEIGHTNBR = 12;
   private Container mContainer;
   private int mCurrentX = 0;
   private int mCurrentY = 0;
   private int mLength = 0;
   private double mWeightX = 0.0;
   private double mWeightY = 0.0;
   private GridBagConstraints mConstaints = new GridBagConstraints();
   private static Insets mNormalInsets = new Insets(PIXELNBR, PIXELNBR, PIXELNBR, PIXELNBR);
   private static Insets mTitleInsets = new Insets(HEIGHTNBR, PIXELNBR, PIXELNBR, PIXELNBR);
   private Insets mOffsetInsets;
}
