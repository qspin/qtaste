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
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class InfoComboBox extends JComboBox implements PopupMenuListener
{
   private class InfoRenderer extends JLabel implements ListCellRenderer
   {
      public InfoRenderer(InfoComboBox pBox, ResourceBundle pRes)
      {
         mBox = pBox;
         mRes = pRes;
      }

      public void setBundle(ResourceBundle pRes)
      {
         mRes = pRes;
      }

      public void highlight(boolean pEnable)
      {
         if(pEnable && !mHighlight)
         {
            mHighlight = pEnable;

            Thread th = new Thread()
            {
               public void run()
               {
                  int i = 0;
                  while(i < 10 && mHighlight)
                  {
                     if((i % 2) == 0)
                     {
                        mForeground = ResourceManager.getInstance().getNormalColor();
                     }
                     else
                     {
                        mForeground = ResourceManager.getInstance().getDarkColor();
                     }
                     mBox.repaint();
                     ++i;
                     try
                     {
                        sleep(500);
                     }
                     catch (InterruptedException e)
                     {
                        i = 10;
                     }
                  }
                  mHighlight = false;
                  mForeground = Color.BLACK;
                  mBox.repaint();
               }
            };
            th.start();
         }
         mHighlight = pEnable;
      }

      public Color getForeground()
      {
          return mForeground;
      }

      public Component getListCellRendererComponent(JList pList, Object pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus)
      {
         if(pValue != null)
         {
            Info i = (Info)pValue;

            StringBuffer msg = new StringBuffer();
            try
            {
               String translated = mRes.getString(i.getMessage());
               msg.append(translated);
            }
            catch(MissingResourceException e)
            {
               msg.append("NOT LOCALIZED!!   ");
               msg.append(i.getMessage());
            }

            if(i.getParams()!= null && i.getParams().length > 0)
            {
               msg.append(": ");
               for(int c = 0; c < i.getParams().length; ++c)
               {
                  msg.append(i.getParams()[c]);
                  if(c < i.getParams().length-1)
                  {
                     msg.append(", ");
                  }
               }
            }

            setText(msg.toString());
            setIcon(ResourceManager.getInstance().getImageIcon("main/badsmall"));
         }
         else
         {
            setText("");
            setIcon(null);
         }
         return this;
      }

      private ResourceBundle mRes;
      private InfoComboBox mBox;
      private boolean mHighlight = false;
      private Color mForeground = Color.BLACK;
   }

   public class Info
   {
      public Info(int pLevel, String pMsg, Object[] pParams)
      {
         mLevel = pLevel;
         mMessage = pMsg;
         mParams = pParams;
      }

      public int getLevel()
      {
         return mLevel;
      }

      public String getMessage()
      {
         return mMessage;
      }

      public Object[] getParams()
      {
         return mParams;
      }

      private int mLevel;
      private String mMessage;
      private Object[] mParams;
   }

   public InfoComboBox(ResourceBundle pRes)
   {
      super(new InfoComboBoxModel());
      mRenderer = new InfoRenderer(this, pRes);
      setRenderer(mRenderer);

      addPopupMenuListener(this);
   }

   public void addInfo(Info pInfo)
   {
      ((InfoComboBoxModel)getModel()).addInfo(pInfo);
      mRenderer.highlight(true);
   }

   public void popupMenuWillBecomeVisible(PopupMenuEvent pE)
   {
      mRenderer.highlight(false);
      repaint();
   }

   public void setBundle(ResourceBundle pRes)
   {
      mRenderer.setBundle(pRes);
   }

   public void popupMenuWillBecomeInvisible(PopupMenuEvent pE)
   {

   }

   public void popupMenuCanceled(PopupMenuEvent pE)
   {

   }

   private InfoRenderer mRenderer;
}
