package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Label;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TreeNodeSelector extends UpdateComponentCommander {

	@Override
	public void run() {
		try {
			String[] nodeNames = mData[1].toString().split(mData[2].toString());
			JTree tree = (JTree) component;
			if (component instanceof JTree && nodeNames.length > 0) {
				TreeModel model = tree.getModel();
				Object node = model.getRoot();
				int pathLength = nodeNames.length;
				if ( !tree.isRootVisible() )
				{
					//root is not present in the list.
					pathLength += 1;
				}
				Object[] path = new Object[pathLength];
				String value = getNodeText(tree, node);
				if ( tree.isRootVisible() )
				{
					System.out.println("compare node (" + value + ") with root (" + nodeNames[0] + ")");
				}
				if (!tree.isRootVisible() || value.equals(nodeNames[0])) {
					path[0] = tree.getModel().getRoot();
					for (int i = 0; i < nodeNames.length; i++) {
						for (int childIndex = 0; childIndex < model.getChildCount(node); childIndex++) {
							Object child = model.getChild(node, childIndex);
							value = getNodeText(tree, child);;
							System.out.println("compare node (" + value + ") with value (" + nodeNames[i] + ")");
							if (value.equals(nodeNames[i])) {
								node = child;
								if ( tree.isRootVisible() )
								{
									path[i] = node;
								} else {
									path[i+1] = node;	
								}
								break;
							}
						}
						if (path[i] == null) {
							throw new QTasteTestFailException("Unabled to find node named " + nodeNames[i]);
						}
					}
					tree.setSelectionPath(new TreePath(path));
					tree.expandPath(new TreePath(path));
					tree.setExpandsSelectedPaths(true);
				}
			} else {
				throw new QTasteTestFailException("Unabled to find node named " + nodeNames[0]);
			}
		} catch (Exception pExc) {
			mError = pExc;
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

}
