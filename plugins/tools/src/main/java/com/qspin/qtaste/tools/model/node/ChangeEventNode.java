package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.ActionEvent;
import com.qspin.qtaste.tools.model.event.ChangeEvent;

public class ChangeEventNode extends EventNode {

	public ChangeEventNode(ActionEvent pEvent) {
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
