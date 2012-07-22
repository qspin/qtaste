package com.qspin.qtaste.tools.factory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.DocumentEvent;
import com.qspin.qtaste.tools.model.Event;
import com.qspin.qtaste.tools.model.PropertyChangeEvent;

public final class EventNodeFactory {

	public static synchronized EventNodeFactory getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new EventNodeFactory();
		}
		return INSTANCE;
	}

	private EventNodeFactory(){}
	
	public MutableTreeNode createNode(Event pEvent, boolean pWithComponent, boolean pWithType) {
		MutableTreeNode node = new DefaultMutableTreeNode();
		MutableTreeNode source = new DefaultMutableTreeNode("Source");

		node.insert(source, node.getChildCount());
		
		MutableTreeNode value;
		
		if ( pWithComponent ) {
			value = new DefaultMutableTreeNode(pEvent.getComponentName());
			source.insert(value, source.getChildCount());
		} else {
			node.setUserObject(pEvent.getType());
		}
		if ( pWithType ) {
			value = new DefaultMutableTreeNode(pEvent.getType());
			source.insert(value, source.getChildCount());
		} else {
			node.setUserObject(pEvent.getComponentName());
		}
		value = new DefaultMutableTreeNode("Timestamp: " + pEvent.getTimeStamp());
		node.insert(value, node.getChildCount());
		value = new DefaultMutableTreeNode("Class: " + pEvent.getSouceClass());
		node.insert(value, node.getChildCount());
		
		if ( pEvent instanceof PropertyChangeEvent ) {
			createNode((PropertyChangeEvent)pEvent, node);
		} else if ( pEvent instanceof DocumentEvent ) {
			createNode((DocumentEvent)pEvent, node);
		}
		
		return node;
	}
	
	private MutableTreeNode createNode(PropertyChangeEvent pEvent, MutableTreeNode pNode) {
		MutableTreeNode data = new DefaultMutableTreeNode("Data");
		pNode.insert(data, pNode.getChildCount());

		MutableTreeNode property;
		MutableTreeNode value;

		property = new DefaultMutableTreeNode("Property name");
		value = new DefaultMutableTreeNode(pEvent.getPropertyName());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Old value");
		value = new DefaultMutableTreeNode(pEvent.getOldValue());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("New value");
		value = new DefaultMutableTreeNode(pEvent.getNewValue());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());
		
		return pNode;
	}
	
	private MutableTreeNode createNode(DocumentEvent pEvent, MutableTreeNode pNode) {
		MutableTreeNode data = new DefaultMutableTreeNode("Data");
		pNode.insert(data, pNode.getChildCount());

		MutableTreeNode property;
		MutableTreeNode value;

		property = new DefaultMutableTreeNode("Type ");
		value = new DefaultMutableTreeNode(pEvent.getDocumentChangeType());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Offset");
		value = new DefaultMutableTreeNode(pEvent.getOffset());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Length");
		value = new DefaultMutableTreeNode(pEvent.getLength());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Change");
		value = new DefaultMutableTreeNode(pEvent.getChange());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());
		
		return pNode;
	}
	
	private static EventNodeFactory INSTANCE;
}
