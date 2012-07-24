package com.qspin.qtaste.tools.model.node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.Event;

public class EventNode extends DefaultMutableTreeNode {

	public EventNode(Event pEvent)
	{
		super(pEvent.getType());
		mEvent = pEvent;
		insert(new DefaultMutableTreeNode("Type : " + pEvent.getType()), getChildCount());
		insert(new DefaultMutableTreeNode("Timestamp : " + pEvent.getTimeStamp()), getChildCount());
		insert(createSourceNode(), getChildCount());
	}
	
	private MutableTreeNode createSourceNode()
	{
		MutableTreeNode sourceNode = new DefaultMutableTreeNode("Source");
		sourceNode.insert(new DefaultMutableTreeNode("Name : " + mEvent.getComponentName()), sourceNode.getChildCount());
		sourceNode.insert(new DefaultMutableTreeNode("Class : " + mEvent.getSourceClass()), sourceNode.getChildCount());
		return sourceNode;
	}
	
	public Event getEvent()
	{
		return mEvent;
	}
	
	protected Event mEvent;
}
