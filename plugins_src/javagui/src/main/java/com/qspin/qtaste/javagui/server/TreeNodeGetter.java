/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class TreeNodeGetter extends ComponentCommander {
	
	@Override
	String executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component component = getComponentByName(componentName);
		String    separator = data[0].toString();

		if (component instanceof JTree)
		{
			JTree    tree = (JTree) component;
            TreePath selectedPath = tree.getSelectionPath();
            String   nodePath = "";
            int      currentTreePathIndex = 0;
            
            // check if a node has been selected
            if (selectedPath == null) {
            	throw new QTasteTestFailException("No node selected");
            }
            
            // if the tree root is not visible, ignore it
            if (!tree.isRootVisible()) {
            	currentTreePathIndex++;
            }
            
            // loop on the tree path to build the node path string
            Object[] treePath = selectedPath.getPath();
            for(; currentTreePathIndex < treePath.length - 1; currentTreePathIndex++)
            {
            	nodePath += getNodeText(tree, treePath[currentTreePathIndex]);
        		nodePath += separator;
            }

            // add the last node text without separator
            if (currentTreePathIndex < treePath.length) {
            	nodePath += getNodeText(tree, treePath[currentTreePathIndex]);
            }
            
            return nodePath;

		} else {
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not a JTree");
		}
	}
		
	private String getNodeText(JTree tree, Object node)
	{
		Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
		if (nodeComponent instanceof JLabel) {
			return ((JLabel) nodeComponent).getText();
		} else if (nodeComponent instanceof Label) {
			return ((Label) nodeComponent).getText();
		} else {
			return node.toString();
		}
	}
}
