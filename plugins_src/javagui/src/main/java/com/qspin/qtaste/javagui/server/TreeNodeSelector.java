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
		String[] nodeNames = mData[2].toString().split(mData[3].toString());
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
			mPath = new Object[pathLength];
			String value = getNodeText(tree, node);
			if ( tree.isRootVisible() )
			{
				System.out.println("compare node (" + value + ") with root (" + nodeNames[0] + ")");
			}
			if (!tree.isRootVisible() || value.equals(nodeNames[0])) {
				mPath[0] = tree.getModel().getRoot();
				for (int i = 0; i < nodeNames.length; i++) {
					for (int childIndex = 0; childIndex < model.getChildCount(node); childIndex++) {
						Object child = model.getChild(node, childIndex);
						value = getNodeText(tree, child);;
						System.out.println("compare node (" + value + ") with value (" + nodeNames[i] + ")");
						if (value.equals(nodeNames[i])) {
							node = child;
							if ( tree.isRootVisible() )
							{
								mPath[i] = node;
							} else {
								mPath[i+1] = node;	
							}
							break;
						}
					}
					if (mPath[i] == null) {
						throw new QTasteTestFailException("Unabled to find node named " + nodeNames[i]);
					}
				}
			}
		} else {
			throw new QTasteTestFailException("Unabled to find node named " + nodeNames[0]);
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
	protected void doActionsInSwingThread() {
		JTree tree = (JTree) component;
		tree.setSelectionPath(new TreePath(mPath));
		tree.expandPath(new TreePath(mPath));
		tree.setExpandsSelectedPaths(true);
	}

	protected Object[] mPath;
}
