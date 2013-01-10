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

package com.qspin.qtaste.ui.util;

import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import org.apache.log4j.Logger;

/**
 * The QSpinTheme is a theme overriding the DefaultMetalTheme and modifying primary colors
 * when displaying gui objects. The white and blue look of all the graphical elements is
 * managed by this object.
 */
public class QSpinTheme extends DefaultMetalTheme
{
   protected static Logger logger = Log4jLoggerFactory.getLogger(QSpinTheme.class);
   private FontUIResource mFontRes = null;
   public QSpinTheme()
   {
   }

   public String getName()
   {
      return "QSpin";
   }
   public final String toString()
   {
      return getName();
   }
   protected ColorUIResource getPrimary1()
   {
      return mPrimary1;
   }
   protected ColorUIResource getPrimary2()
   {
      return mPrimary2;
   }
   protected ColorUIResource getPrimary3()
   {
      return mPrimary3;
   }

   private final ColorUIResource mPrimary1 = new ColorUIResource(ResourceManager.getInstance().getNormalColor());
   private final ColorUIResource mPrimary2 = new ColorUIResource(ResourceManager.getInstance().getLightColor());
   private final ColorUIResource mPrimary3 = new ColorUIResource(ResourceManager.getInstance().getLightColor());

   protected ColorUIResource getSecondary1()
   {
      return mSecondary1;
   }
   protected ColorUIResource getSecondary2()
   {
      return mSecondary2;
   }
   protected ColorUIResource getSecondary3()
   {
      return mSecondary3;
   }

   private final ColorUIResource mSecondary1 = new ColorUIResource(ResourceManager.getInstance().getBackColor());
   private final ColorUIResource mSecondary2 = new ColorUIResource(ResourceManager.getInstance().getBackColor());
   private final ColorUIResource mSecondary3 = new ColorUIResource(ResourceManager.getInstance().getBackgroundColor());

   public FontUIResource getControlTextFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getControlTextFont();
   }

   public FontUIResource getSystemTextFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getSystemTextFont();
   }

   public FontUIResource getUserTextFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getUserTextFont();
   }

   public FontUIResource getMenuTextFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getMenuTextFont();
   }

   public FontUIResource getWindowTitleFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getWindowTitleFont();
   }

   public FontUIResource getSubTextFont()
   {
      if(mFontRes != null)
      {
         return mFontRes;
      }
      return super.getSubTextFont();
   }
}
