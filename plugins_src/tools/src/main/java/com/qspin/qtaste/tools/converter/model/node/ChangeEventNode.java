package com.qspin.qtaste.tools.converter.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.converter.model.event.ChangeEvent;

public class ChangeEventNode extends EventNode {

	public ChangeEventNode(ChangeEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("Tab index : "
				+ ((ChangeEvent) mEvent).getTabIndex()), dataNode.getChildCount());
		return dataNode;
	}
}
