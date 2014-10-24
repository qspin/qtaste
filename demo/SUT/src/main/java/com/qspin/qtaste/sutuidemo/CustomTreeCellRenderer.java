package com.qspin.qtaste.sutuidemo;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

final class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if ( value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode)value).getUserObject() instanceof Person )
		{
			Person p = (Person) ((DefaultMutableTreeNode)value).getUserObject();
			setText(p.getFirstName() + " " + p.getLastName() + " (" + p.getAge() + ")");
		}
		return this;
	}
	
}
