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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;




/**
 * The PatientTreeCellRenderer is responsible for displaying each cell of the tree with a specific look.
 * 
 * This class relies on PrescriptionValidator.
 * 
 * @author cbauvir
 * 
 */

@SuppressWarnings("serial")
public class TestCaseTreeCellRenderer extends DefaultTreeCellRenderer
{
   private final Color mLightBlue = new Color(0.78f, 0.67f, 0.87f);
   private final Color mBlue = new Color(0.38f, 0.12f,  0.69f);
   //private final Color mDarkGreen = new Color(0.19f, 0.345f, 0.06f);
   //private final Color mLightRed = new Color(255, 65, 65);
   //private final Color mDarkRed = new Color(157, 0, 0);
   //private final Color mLightGray = new Color(215, 215, 215);
   //private final Color mOrange = new Color(241, 171, 0);
   //private final Color mBackground = new Color(250, 250, 250);
   private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);


   public TestCaseTreeCellRenderer()
   {
      setBorderSelectionColor(mBlue);
      setBackgroundSelectionColor(mLightBlue);
   }

   public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pSel, boolean pExpanded, boolean pLeaf, int pRow, boolean pHasFocus)
   {
      super.getTreeCellRendererComponent(pTree, pValue, pSel, pExpanded, pLeaf, pRow, pHasFocus);
      formatNode((DefaultMutableTreeNode) pValue, pTree);
      return this;
   }

   private void formatNode(DefaultMutableTreeNode pNode, JTree pTree)
   {
      setFont(NORMAL_FONT);
      if (pNode.getUserObject() instanceof FileNode)
      {
         FileNode node = (FileNode) pNode.getUserObject();
         formatFileNode(node);
         setText(node.getFile().getName());
      }
      if (getText() != null)
      {
         setToolTipText(getText());
      }

   }

   private void formatFileNode(FileNode pNode)
   {
       if (pNode.isTestcaseDir())
       {
           if (pNode.isTestcaseCheckOk())
           {
               Icon icon = ResourceManager.getInstance().getImageIcon("icons/tc16");
               setIcon(icon);
           }
           else
           {
               setIcon(ResourceManager.getInstance().getImageIcon("icons/tc16_nodata"));
               this.setToolTipText("no TestData.csv file found");
           }
       }
       else
       {
         setIcon(ResourceManager.getInstance().getImageIcon("icons/dir16"));
       }
   }

   
}
