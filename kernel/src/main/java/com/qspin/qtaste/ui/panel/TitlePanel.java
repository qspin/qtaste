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

package com.qspin.qtaste.ui.panel;

import com.qspin.qtaste.ui.tools.GridBagLineAdder;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.widget.StepLabelUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * A TitlePanel is a standard panel used to gather multiple graphical components related together.
 * It is enclosed with a simple border. It displays a title and an icon.
 */
@SuppressWarnings("serial")
public class TitlePanel extends JPanel
{
   public TitlePanel(String pTitle, String pIcon, boolean pPanes)
   {
      super(new GridBagLayout());
      Border b = BorderFactory.createLineBorder(ResourceManager.getInstance().getBackColor());
      setBorder(b);
      GridBagLineAdder adder = new GridBagLineAdder(this);
      JLabel title = new JLabel(ResourceManager.getInstance().getImageIcon(pIcon));
      title.setText(pTitle);
      title.setFont(ResourceManager.getInstance().getLargerFont());
      title.setUI(new StepLabelUI());

      mContentPanel = new JPanel();

      JPanel titlepanel = new JPanel(new BorderLayout());
      titlepanel.add(title, BorderLayout.WEST);

      adder.setWeight(1.0f, 0.0f);
      adder.add(titlepanel);
      adder.next();

      if(pPanes)
      {
         JTabbedPane pane = new JTabbedPane();
         pane.addTab("Main", mContentPanel);

         adder.setWeight(1.0f, 1.0f);
         adder.addWithVerticalFill(pane);
      }
      else
      {
         adder.addSeparator();
         adder.setWeight(1.0f, 1.0f);
         adder.addWithVerticalFill(mContentPanel);
      }
   }

   public JPanel getContentPanel()
   {
      return mContentPanel;
   }

   private JPanel mContentPanel;
}
