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

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TreeNodeSelector extends UpdateComponentCommander {

	@Override
	protected void prepareActions() throws QTasteTestFailException {
	}

	/**
	 * Build a tree path (an array of objects) from a node path string and a node path separator.
	 * @throws QTasteTestFailException
	 */
	protected void prepareDoActions() throws QTasteTestFailException {
		String nodePath = mData[0].toString();
		String nodePathSeparator = mData[1].toString();
		
		// Split node path into an array of node path elements
		// Be careful that String.split() method takes a regex as argument. 
		// Here, the token 'nodePathSeparator' is escaped using the Pattern.quote() method.
		String[] nodePathElements = nodePath.split(Pattern.quote(nodePathSeparator));
		
		if (nodePathElements.length <= 0) {
			throw new QTasteTestFailException("Unable to split the node path in elements (nodePath: " + nodePath + " separator: " + nodePathSeparator + ").");
		}

		LOGGER.trace("nodePath: " + nodePath + " separator: " + nodePathSeparator + " splitted in " + nodePathElements.length + " element(s).");

		if (component instanceof JTree) {
			JTree 		 tree  = (JTree) component;
			TreeModel	 treeModel = tree.getModel();
			List<Object> treePath = new ArrayList<Object>();
			int	      	 currentNodePathItemIndex = 0;
			
			// if the root is visible, check it regarding the first node path item
			if ( tree.isRootVisible() )
			{
				String rootNodeText = getNodeText(tree, treeModel.getRoot());
				
				if (!rootNodeText.equals(nodePathElements[0])) {
					LOGGER.trace("rootNodeText: " + rootNodeText + " != nodePathElement: " + nodePathElements[0]);
					throw new QTasteTestFailException("Unable to select a node with the following path : " + nodePath);
				}

				currentNodePathItemIndex++;
			}
		
			// loop on all node path elements
			Object currentNode = treeModel.getRoot();
			treePath.add(currentNode);
						
			for (;currentNodePathItemIndex < nodePathElements.length; currentNodePathItemIndex++) {
				
				// search the current node path element in the current node children list
				Boolean bFound  = false;
				
				for (int currentChildIndex = 0; currentChildIndex < treeModel.getChildCount(currentNode); currentChildIndex++) {
					Object currentChild = treeModel.getChild(currentNode, currentChildIndex);
					
					if (getNodeText(tree, currentChild).equals(nodePathElements[currentNodePathItemIndex])) {
						currentNode = currentChild;
						treePath.add(currentNode);
						bFound = true;
						break;
					}
				}
				
				// check if the current node path element has been found in the current node children list
				if (!bFound) {
					LOGGER.trace(nodePathElements[currentNodePathItemIndex] + " not found in the tree.");
					throw new QTasteTestFailException("Unable to select a node with the following path : " + nodePath);
				}
			}
			
			// set the final tree path
			mPath = new Object[treePath.size()];
			treePath.toArray(mPath);

			LOGGER.trace("tree path successfully built!");
		} 
		else {
			throw new QTasteTestFailException("Invalid component class (expected: JTree, got: " + component.getClass().getName() + ").");
		}
	}

	/**
	 * Get the text of a node.
	 * @param tree tree component which contains the node.
	 * @param node the node.
	 * @return the text of the node.
	 */
	private String getNodeText(JTree tree, Object node) {
		Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
		if (nodeComponent instanceof JLabel) {
			System.out.println("component extend JLabel");
			return ((JLabel) nodeComponent).getText();
		} 
		else if (nodeComponent instanceof Label) {
			System.out.println("component extend TextComponent");
			return ((Label) nodeComponent).getText();
		} 
		else {
			System.out.println("component extend something else");
			return node.toString();
		}
	}

	@Override
	protected void doActionsInSwingThread() throws QTasteException {
		prepareDoActions();
		JTree tree = (JTree) component;
		TreePath path = new TreePath(mPath);
		tree.expandPath(path);
		tree.setExpandsSelectedPaths(true);
		tree.setSelectionPath(path);
	}

	protected volatile Object[] mPath;
}
