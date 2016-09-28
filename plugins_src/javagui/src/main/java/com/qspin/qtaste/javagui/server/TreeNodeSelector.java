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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.SwingUtilities;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TreeNodeSelector extends UpdateComponentCommander {

    /**
     * Type of node selection
     */
    public enum SelectorIdentifier {
        SELECT_BY_STRING,
        SELECT_BY_REGEX,
        CLEAR_SELECTION
    }

    private SelectorIdentifier mSelectorIdentifier;
    /**
     * < type of node selection
     */
    protected volatile Object[] mPath;    /**< tree path built in prepareDoActions() to select the node */

    /**
     * Constructor.
     *
     * @param selectorIdentifier type of node selection
     */
    public TreeNodeSelector(SelectorIdentifier selectorIdentifier) {
        mSelectorIdentifier = selectorIdentifier;
    }

    /**
     * Compare a node path element (provided as argument of the selectNode method) to a node name (from a JTree).
     *
     * @param nodePathElement the node path element to compare with the node name
     * @param nodeName the node name
     * @return true if both match, false otherwise.
     */
    protected boolean compareNodeNames(String nodePathElement, String nodeName) {
        return mSelectorIdentifier == SelectorIdentifier.SELECT_BY_REGEX ? Pattern.matches(nodePathElement, nodeName) :
              nodePathElement.equals(nodeName);
    }

    /**
     * Build a tree path (an array of objects) from a node path string and a node path separator.
     *
     * @throws QTasteTestFailException
     */
    protected void prepareActions() throws QTasteTestFailException {

			if (mSelectorIdentifier == SelectorIdentifier.CLEAR_SELECTION) {
				// nothing special to do for CLEAR_SELECTION action
				return;
			}

			String nodePath = mData[0].toString();
			String nodePathSeparator = mData[1].toString();

			// Split node path into an array of node path elements
			// Be careful that String.split() method takes a regex as argument.
			// Here, the token 'nodePathSeparator' is escaped using the Pattern.quote() method.
			String[] nodePathElements = nodePath.split(Pattern.quote(nodePathSeparator));

			if (nodePathElements.length == 0) {
				throw new QTasteTestFailException(
                  "Unable to split the node path in elements (nodePath: '" + nodePath + "' separator: '" + nodePathSeparator
                        + "')");
			}

			LOGGER.trace("nodePath: " + nodePath + " separator: " + nodePathSeparator + " splitted in " + nodePathElements.length
				+ " element(s).");

			if (component instanceof JTree) {
				JTree tree = (JTree) component;
			} else {
				throw new QTasteTestFailException(
						"Invalid component class (expected: JTree, got: " + component.getClass().getName() + ").");
			}
	}


    /**
     * Get the text of a node.
     *
     * @param tree tree component which contains the node.
     * @param node the node.
     * @return the text of the node.
     */
    private String getNodeText(JTree tree, Object node) {
        Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
        if (nodeComponent instanceof JLabel) {
            LOGGER.trace("component extend JLabel");
            return ((JLabel) nodeComponent).getText();
        } else if (nodeComponent instanceof Label) {
            LOGGER.trace("component extend TextComponent");
            return ((Label) nodeComponent).getText();
        } else {
            LOGGER.trace("component extend something else");
            return node.toString();
        }
    }

    @Override
    protected void doActionsInSwingThread() throws QTasteTestFailException {
        JTree tree = (JTree) component;
		String nodePath = mData[0].toString();
		String nodePathSeparator = mData[1].toString();
		// Split node path into an array of node path elements
		// Be careful that String.split() method takes a regex as argument.
		// Here, the token 'nodePathSeparator' is escaped using the Pattern.quote() method.
		String[] nodePathElements = nodePath.split(Pattern.quote(nodePathSeparator));
		TreeModel treeModel = tree.getModel();
		List<Object> treePath = new ArrayList<>();
		int currentNodePathItemIndex = 0;
		// if the root is visible, check it regarding the first node path item
		if (tree.isRootVisible()) {
			String rootNodeText = getNodeText(tree, treeModel.getRoot());

			if (!compareNodeNames(nodePathElements[0], rootNodeText)) {
				LOGGER.trace("rootNodeText: " + rootNodeText + " != nodePathElement: " + nodePathElements[0]);
				throw new QTasteTestFailException("Unable to select a node with the following path : '" + nodePath + "'");
			}

			currentNodePathItemIndex++;
		}

		// loop on all node path elements
		Object currentNode = treeModel.getRoot();
		treePath.add(currentNode);

		for (; currentNodePathItemIndex < nodePathElements.length; currentNodePathItemIndex++) {

			// search the current node path element in the current node children list
			Boolean bFound = false;

			for (int currentChildIndex = 0; currentChildIndex < treeModel.getChildCount(currentNode); currentChildIndex++) {
				Object currentChild = treeModel.getChild(currentNode, currentChildIndex);

				if (compareNodeNames(nodePathElements[currentNodePathItemIndex], getNodeText(tree, currentChild))) {
					currentNode = currentChild;
					treePath.add(currentNode);
					bFound = true;
					break;
				}
			}
			// check if the current node path element has been found in the current node children list
			if (!bFound) {
				LOGGER.trace(nodePathElements[currentNodePathItemIndex] + " not found in the tree.");
				throw new QTasteTestFailException("Unable to select a node with the following path : '" + nodePath + "'");
			}
		}
		// set the final tree path
		mPath = new Object[treePath.size()];
		treePath.toArray(mPath);

        if (mSelectorIdentifier == SelectorIdentifier.CLEAR_SELECTION) {
            tree.clearSelection();
        } else {
            TreePath path = new TreePath(mPath);
            tree.expandPath(path);
            tree.setExpandsSelectedPaths(true);
            tree.setSelectionPath(path);
        }
    }
}
