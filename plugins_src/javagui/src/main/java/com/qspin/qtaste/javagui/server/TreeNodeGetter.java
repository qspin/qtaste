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
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class TreeNodeGetter extends ComponentCommander {
	
	private String m_commandResult;
	private QTasteException m_commandError;
	
	@Override
	String executeCommand(final int timeout, final String componentName, final Object... data) throws QTasteException {
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try
					{
						Component component = getComponentByName(componentName);
						//LOGGER.trace("Component : " + component);
						String separator = data[0].toString();
		
						if (component instanceof JTree)
						{
							JTree    tree = (JTree) component;
				            TreePath selectedPath = tree.getSelectionPath();
				            String   nodePath = "";
				            int      currentTreePathIndex = 0;
				            
				            // check if a node has been selected
				            if (selectedPath == null) {
				            	//LOGGER.debug("No node selected");
				            	m_commandResult = null;
				            	return;
				            }
				            
				            // if the tree root is not visible, ignore it
				            if (!tree.isRootVisible()) {
				            	currentTreePathIndex++;
				            }
				            
				            // loop on the tree path to build the node path string
				            Object[] treePath = selectedPath.getPath();
				            //LOGGER.debug("array : " + Arrays.toString(treePath));
				            for(; currentTreePathIndex < treePath.length - 1; currentTreePathIndex++)
				            {
				            	nodePath += getNodeText(tree, treePath[currentTreePathIndex]);
				        		nodePath += separator;
				            }
		
				            // add the last node text without separator
				            if (currentTreePathIndex < treePath.length) {
				            	nodePath += getNodeText(tree, treePath[currentTreePathIndex]);
				            }
				            
				            m_commandResult = nodePath;
				            return;
		
						} else {
							throw new QTasteTestFailException("The component \"" + componentName + "\" is not a JTree");
						}
					}
					catch(QTasteException ex)
					{
						m_commandError = ex;
					}
				}
			});
		}
		catch(Exception ex)
		{
			LOGGER.fatal(ex.getMessage(), ex);
		}
		
		if(  m_commandError != null )
		{
			throw m_commandError;
		}
		
		return m_commandResult;
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
