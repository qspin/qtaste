package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.TreeSelectionEvent;

public class TreeSelectionEventNode extends EventNode {

	public TreeSelectionEventNode(TreeSelectionEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("selected path : "
				+ ((TreeSelectionEvent) mEvent).getSelectedPath()),
				dataNode.getChildCount());
		return dataNode;
	}
}
