package com.qspin.qtaste.tools.converter.factory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.converter.model.ComponentNameMapping;
import com.qspin.qtaste.tools.converter.model.event.ActionEvent;
import com.qspin.qtaste.tools.converter.model.event.ChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.DocumentEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.model.event.ItemEvent;
import com.qspin.qtaste.tools.converter.model.event.PropertyChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.TreeSelectionEvent;
import com.qspin.qtaste.tools.converter.model.node.ActionEventNode;
import com.qspin.qtaste.tools.converter.model.node.ChangeEventNode;
import com.qspin.qtaste.tools.converter.model.node.DocumentEventNode;
import com.qspin.qtaste.tools.converter.model.node.ItemEventNode;
import com.qspin.qtaste.tools.converter.model.node.PropertyChangeEventNode;
import com.qspin.qtaste.tools.converter.model.node.TreeSelectionEventNode;

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
			value = new DefaultMutableTreeNode(getComponentDisplayName(pEvent.getComponentName()));
			source.insert(value, source.getChildCount());
		} else {
			node.setUserObject(pEvent.getType());
		}
		if ( pWithType ) {
			value = new DefaultMutableTreeNode(pEvent.getType());
			source.insert(value, source.getChildCount());
		} else {
			node.setUserObject(getComponentDisplayName(pEvent.getComponentName()));
		}
		value = new DefaultMutableTreeNode("Timestamp: " + pEvent.getTimeStamp());
		node.insert(value, node.getChildCount());
		value = new DefaultMutableTreeNode("Class: " + pEvent.getSourceClass());
		node.insert(value, node.getChildCount());
		
		if ( pEvent instanceof PropertyChangeEvent ) {
			return new PropertyChangeEventNode((PropertyChangeEvent)pEvent);
		} else if ( pEvent instanceof DocumentEvent ) {
			return new DocumentEventNode((DocumentEvent)pEvent);
		} else if ( pEvent instanceof ActionEvent ) {
			return new ActionEventNode((ActionEvent)pEvent);
		} else if ( pEvent instanceof ItemEvent ) {
			return new ItemEventNode((ItemEvent)pEvent);
		} else if ( pEvent instanceof TreeSelectionEvent ) {
			return new TreeSelectionEventNode((TreeSelectionEvent)pEvent);
		} else if ( pEvent instanceof ChangeEvent ) {
			return new ChangeEventNode((ChangeEvent)pEvent);
		}
		
		return node;
	}
	
	private String getComponentDisplayName(String pComponentName)
	{
		if( ComponentNameMapping.getInstance().hasAlias(pComponentName) )
		{
			return ComponentNameMapping.getInstance().getAliasFor(pComponentName);
		}
		else {
			return pComponentName;
		}
	}
	
	private static EventNodeFactory INSTANCE;
}
