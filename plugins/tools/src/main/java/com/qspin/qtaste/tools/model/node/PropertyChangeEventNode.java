package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.PropertyChangeEvent;

public class PropertyChangeEventNode extends EventNode {

	public PropertyChangeEventNode(PropertyChangeEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("Property name : "
				+ ((PropertyChangeEvent) mEvent).getPropertyName()),
				dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Old value : "
				+ ((PropertyChangeEvent) mEvent).getOldValue()),
				dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("New value : "
				+ ((PropertyChangeEvent) mEvent).getNewValue()),
				dataNode.getChildCount());
		return dataNode;
	}
}
