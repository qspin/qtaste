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
	public void run() {
		try {
			String[] nodeNames = mData[1].toString().split(mData[2].toString());
			JTree tree = (JTree) component;
			if (component instanceof JTree && nodeNames.length > 0) {
				TreeModel model = tree.getModel();
				Object node = model.getRoot();
				Object[] path = new Object[nodeNames.length];
				Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
				String value = null;
				System.out.println("component is " + nodeComponent);
				if (nodeComponent instanceof JLabel) {
					System.out.println("component extend JLabel");
					value = ((JLabel) nodeComponent).getText();
				} else if (nodeComponent instanceof Label) {
					System.out.println("component extend TextComponent");
					value = ((Label) nodeComponent).getText();
				} else {
					System.out.println("component extend something else");
					value = node.toString();
				}
				System.out.println("compare node (" + value + ") with root (" + nodeNames[0] + ")");
				if (!tree.isRootVisible() || value.equals(nodeNames[0])) {
					path[0] = node;
					for (int i = tree.isRootVisible() ? 1 : 0; i < nodeNames.length; i++) {
						for (int childIndex = 0; childIndex < model.getChildCount(node); childIndex++) {
							Object child = model.getChild(node, childIndex);
							nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, child, true, false, true, i, false);
							value = null;
							if (nodeComponent instanceof JLabel) {
								System.out.println("component extend JLabel");
								value = ((JLabel) nodeComponent).getText();
							} else if (nodeComponent instanceof Label) {
								System.out.println("component extend TextComponent");
								value = ((Label) nodeComponent).getText();
							} else {
								System.out.println("component extend something else");
								value = child.toString();
							}
							System.out.println("compare node (" + value + ") with value (" + nodeNames[i] + ")");
							if (value.equals(nodeNames[i])) {
								node = child;
								path[i] = node;
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
			}
			throw new QTasteTestFailException("Unabled to find node named " + nodeNames[0]);
		} catch (Exception pExc) {
			mError = pExc;
		}
	}

}
