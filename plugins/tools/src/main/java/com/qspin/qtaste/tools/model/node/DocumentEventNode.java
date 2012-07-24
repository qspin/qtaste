package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.DocumentEvent;

public class DocumentEventNode extends EventNode {

	public DocumentEventNode(DocumentEvent pEvent) {
		super(pEvent);
		insert(createDataNode(), getChildCount());
	}

	private MutableTreeNode createDataNode() {
		MutableTreeNode dataNode = new DefaultMutableTreeNode("Data");
		dataNode.insert(new DefaultMutableTreeNode("Document change type : "
				+ ((DocumentEvent) mEvent).getDocumentChangeType()),
				dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Offset : "
				+ ((DocumentEvent) mEvent).getOffset()),
				dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Lenght"
				+ ((DocumentEvent) mEvent).getLenght()),
				dataNode.getChildCount());
		dataNode.insert(new DefaultMutableTreeNode("Change : "
				+ ((DocumentEvent) mEvent).getChange()),
				dataNode.getChildCount());
		return dataNode;
	}
}
