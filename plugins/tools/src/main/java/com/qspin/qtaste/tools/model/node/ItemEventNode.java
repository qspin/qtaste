package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.ItemEvent;

public class ItemEventNode extends EventNode {

	public ItemEventNode(ItemEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("Selected state : "
				+ ((ItemEvent) mEvent).getState()), dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Selected item : "
				+ ((ItemEvent) mEvent).getSelectedItem()), dataNode.getChildCount());
		return dataNode;
	}
}
