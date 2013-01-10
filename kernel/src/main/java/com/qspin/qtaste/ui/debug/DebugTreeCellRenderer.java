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

package com.qspin.qtaste.ui.debug;

import com.qspin.qtaste.ui.tools.ResourceManager;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


@SuppressWarnings("serial")
public class DebugTreeCellRenderer extends DefaultTreeCellRenderer{
	// ///////////////////////////////////////////////////////////////////

	   private static final Font SMALL_FONT = new Font("Dialog", Font.PLAIN, 10);
	   //private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);


	   public DebugTreeCellRenderer()
	   {
//	      setBorderSelectionColor(mGreen);
	//      setBackgroundSelectionColor(mLightGreen);
	   }

	   public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pSel, boolean pExpanded, boolean pLeaf, int pRow, boolean pHasFocus)
	   {
	      super.getTreeCellRendererComponent(pTree, pValue, pSel, pExpanded, pLeaf, pRow, pHasFocus);
	      formatNode((DefaultMutableTreeNode) pValue, pTree);
	      return this;
	   }

	   private void formatNode(DefaultMutableTreeNode pNode, JTree pTree)
	   {
	      setFont(SMALL_FONT);
	      if (pNode instanceof VariableNode)
	      {
//	    	  VariableNode node = (VariableNode) pNode;
//	    	  if (node.getChildren().length==0) {
//	              Icon icon = ResourceManager.getInstance().getImageIcon("icons/debugvar");
//	              setIcon(icon);
//	    	  }
//	    	  else {
              Icon icon = ResourceManager.getInstance().getImageIcon("icons/debugvar");
              setIcon(icon);
//	    	  }
	      }
	      else if (pNode instanceof DebugRootNode)
	   	  {
	    	  setIcon(null);
	   	  }
	      if (getText() != null)
	      {
	         setToolTipText(getText());
	      }

	   }

}
