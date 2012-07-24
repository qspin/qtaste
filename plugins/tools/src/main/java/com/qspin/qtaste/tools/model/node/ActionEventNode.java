package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.ActionEvent;

public class ActionEventNode extends EventNode {

	public ActionEventNode(ActionEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("ID : "
				+ ((ActionEvent) mEvent).getId()), dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Action command : "
				+ ((ActionEvent) mEvent).getActionCommand()),
				dataNode.getChildCount());
		return dataNode;
	}
}
