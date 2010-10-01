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

package com.qspin.qtaste.ui.testcasebuilder;

import java.awt.Color;
import java.awt.Font;
import java.text.Format;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.qspin.qtaste.ui.widget.FillLabelUI;

/**
 *
 * @author vdubois
 */
public final class PanelHelper {
   private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);
   private static final Color mBackground = new Color(250, 250, 250);

   private PanelHelper()
   {
   }

   /**
    * create title line in panel.
    * @param pText , title string
    * @param pAdder , jgoodies form builder
    */
   public static void createTitle(String pText, DefaultFormBuilder pAdder)
   {
      JLabel lbl = new JLabel("  " + pText, JLabel.LEADING);
      lbl.setFont(NORMAL_FONT);
      lbl.setUI(new FillLabelUI(mBackground.darker()));
      pAdder.append(lbl, pAdder.getColumnCount());
      pAdder.nextLine();
      pAdder.appendRow("3dlu");
      pAdder.nextLine();
   }

   /**
    * Return a JTextField with specifial features.
    */
   public static JTextField createTextField()
   {
      JTextField textField = new JTextField();
      textField.setEditable(false);
      textField.setFocusable(false);
      return textField;
   }

   /**
    * Return a JTextArea which looks similar to the JTextField. This method is needed because just setting the Theme colors is not enough: TextArea
    * have a different bg color and no border around it
    */
   public static JTextArea createTextArea(int pRow, int pCol)
   {
      JTextArea textArea = new JTextArea(pRow, pCol);
      textArea.setEditable(false);
      textArea.setFocusable(false);
      textArea.setLineWrap(true);
      textArea.setBackground(mBackground);
      // textArea.setBorder(BorderFactory.createLineBorder(ResourceManager.getInstance().getBackColor())); Not needed anymore if placed inside
      // JScrollPane
      return textArea;
   }

   /**
    * Bind a JTextField with a Jgoodies property.
    * @param pTextField
    * @param pBean
    * @param pPropertyName
    * @param pFormat
    */
   public static void bind(JTextField pTextField, Object pBean, String pPropertyName, Format pFormat)
   {
      PropertyAdapter propertyAdapter = new PropertyAdapter(pBean, pPropertyName, true);
      Bindings.bind(pTextField, ConverterFactory.createStringConverter(propertyAdapter, pFormat), true);
   }
}
