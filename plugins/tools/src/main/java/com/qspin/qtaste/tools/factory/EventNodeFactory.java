package com.qspin.qtaste.tools.factory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.ActionEvent;
import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.ItemEvent;
import com.qspin.qtaste.tools.model.event.PropertyChangeEvent;
import com.qspin.qtaste.tools.model.event.TreeSelectionEvent;
import com.qspin.qtaste.tools.model.node.ActionEventNode;
import com.qspin.qtaste.tools.model.node.DocumentEventNode;
import com.qspin.qtaste.tools.model.node.ItemEventNode;
import com.qspin.qtaste.tools.model.node.PropertyChangeEventNode;
import com.qspin.qtaste.tools.model.node.TreeSelectionEventNode;

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
		value = new DefaultMutableTreeNode("Class: " + pEvent.getSourceClass());
		node.insert(value, node.getChildCount());
		
		if ( pEvent instanceof PropertyChangeEvent ) {
			return new PropertyChangeEventNode((PropertyChangeEvent)pEvent);
//			createNode((PropertyChangeEvent)pEvent, node);
		} else if ( pEvent instanceof DocumentEvent ) {
			return new DocumentEventNode((DocumentEvent)pEvent);
//			createNode((DocumentEvent)pEvent, node);
		} else if ( pEvent instanceof ActionEvent ) {
			return new ActionEventNode((ActionEvent)pEvent);
//			createNode((ActionEvent)pEvent, node);
		} else if ( pEvent instanceof ItemEvent ) {
			return new ItemEventNode((ItemEvent)pEvent);
//			createNode((ItemEvent)pEvent, node);
		} else if ( pEvent instanceof TreeSelectionEvent ) {
			return new TreeSelectionEventNode((TreeSelectionEvent)pEvent);
//			createNode((ItemEvent)pEvent, node);
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
		value = new DefaultMutableTreeNode(pEvent.getLenght());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Change");
		value = new DefaultMutableTreeNode(pEvent.getChange());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());
		
		return pNode;
	}
	
	private MutableTreeNode createNode(ActionEvent pEvent, MutableTreeNode pNode) {
		MutableTreeNode data = new DefaultMutableTreeNode("Data");
		pNode.insert(data, pNode.getChildCount());

		MutableTreeNode property;
		MutableTreeNode value;

		property = new DefaultMutableTreeNode("ID");
		value = new DefaultMutableTreeNode(pEvent.getId());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("action command");
		value = new DefaultMutableTreeNode(pEvent.getActionCommand());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());
		
		return pNode;
	}
	
	private MutableTreeNode createNode(ItemEvent pEvent, MutableTreeNode pNode) {
		MutableTreeNode data = new DefaultMutableTreeNode("Data");
		pNode.insert(data, pNode.getChildCount());

		MutableTreeNode property;
		MutableTreeNode value;

		property = new DefaultMutableTreeNode("ID");
		value = new DefaultMutableTreeNode(pEvent.getId());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("State");
		value = new DefaultMutableTreeNode(pEvent.getState());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());

		property = new DefaultMutableTreeNode("Selected item");
		value = new DefaultMutableTreeNode(pEvent.getSelectedItem());
		property.insert(value, property.getChildCount());
		data.insert(property, data.getChildCount());
		
		return pNode;
	}
	
	private static EventNodeFactory INSTANCE;
}
