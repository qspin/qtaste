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
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TreeNodeSelector extends UpdateComponentCommander {	

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		String[] nodeNames = mData[0].toString().split(mData[1].toString());
		if (component instanceof JTree && nodeNames.length > 0) {
			JTree tree = (JTree) component;
			TreeModel model = tree.getModel();
			Object node = model.getRoot();
			int pathLength = nodeNames.length;
			if ( !tree.isRootVisible() )
			{
				//root is not present in the list.
				pathLength += 1;
			}
			Object [] lmPath = new Object[pathLength];		
			String value = getNodeText(tree, node);
			if ( tree.isRootVisible() )
			{
				System.out.println("compare node (" + value + ") with root (" + nodeNames[0] + ")");
			}
			if (!tree.isRootVisible() || value.equals(nodeNames[0]))
			{
				lmPath[0] = tree.getModel().getRoot();
				// If Root is visible, skip it
				int i = tree.isRootVisible() ? 1 : 0;
				for (; i < nodeNames.length; i++)
				{
					boolean nodeFound = false;
					do
					{
						for (int childIndex = 0; childIndex < model.getChildCount(node); childIndex++)
						{
							Object child = model.getChild(node, childIndex);
							value = getNodeText(tree, child);;
							System.out.println("compare node (" + value + ") with value (" + nodeNames[i] + ")");
							if (value.equals(nodeNames[i]))
							{
								node = child;
								if ( tree.isRootVisible() )
								{
									lmPath[i] = node;
								} else {
									lmPath[i+1] = node;	
								}
								nodeFound = true;
								mPath = lmPath;
								break;
							}
						}

					} while ( System.currentTimeMillis() < m_maxTime && !nodeFound);
					
					if (!nodeFound)
					{
						throw new QTasteTestFailException("Unabled to find node named " + nodeNames[i]);
					}
				}
			}
		} else {
			throw new QTasteTestFailException("Unabled to find the first node named " + nodeNames[0]);
		}
	}
	
	private String getNodeText(JTree tree, Object node)
	{
		Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
		if (nodeComponent instanceof JLabel) {
			System.out.println("component extend JLabel");
			return ((JLabel) nodeComponent).getText();
		} else if (nodeComponent instanceof Label) {
			System.out.println("component extend TextComponent");
			return ((Label) nodeComponent).getText();
		} else {
			System.out.println("component extend something else");
			return node.toString();
		}
	}

	@Override
	protected void doActionsInSwingThread()
	{
		try {
			prepareActions();
			JTree tree = (JTree) component;
			TreePath path = new TreePath(mPath);
			tree.expandPath(path);
			tree.setExpandsSelectedPaths(true);			
			tree.setSelectionPath(path);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected volatile Object[] mPath;
}
