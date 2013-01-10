package com.qspin.qtaste.tools.converter.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.converter.model.event.Event;

public final class EventTreeFactory extends TreeFactory {

	public synchronized static EventTreeFactory getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new EventTreeFactory();
		}
		return INSTANCE;
	}

	@Override
	public MutableTreeNode buildRootTree(List<Event> pEvents) {
		MutableTreeNode root = new DefaultMutableTreeNode("Events");
		Map<String, Map<String,  List<Event>>> sortedEvents = sortData(pEvents);
		
		for( String eventType : sortedEvents.keySet() )
		{ 
			MutableTreeNode eventNode = new DefaultMutableTreeNode(eventType);
			for ( String componentName : sortedEvents.get(eventType).keySet() )
			{
				MutableTreeNode componentNode = new DefaultMutableTreeNode(getComponentDisplayName(componentName));
				for (Event evt : sortedEvents.get(eventType).get(componentName) )
				{
					MutableTreeNode evtNode = EventNodeFactory.getInstance().createNode(evt, false, true);
					componentNode.insert(evtNode, componentNode.getChildCount());
				}
				eventNode.insert(componentNode, eventNode.getChildCount());
			}
			root.insert(eventNode, root.getChildCount());
		}
		return root;
	}
	
	private EventTreeFactory()
	{
		super();
	}
	
	private Map<String, Map<String,  List<Event>>> sortData(List<Event> pEvents)
	{
		Map<String, Map<String, List<Event>>> sortedEvents = new HashMap<String, Map<String, List<Event>>>();
		for ( Event evt : pEvents )
		{
			if ( !sortedEvents.containsKey(evt.getType()) )
			{
				sortedEvents.put(evt.getType(), new HashMap<String,  List<Event>>());
			}
			Map<String,  List<Event>> eventMap = sortedEvents.get(evt.getType());
			if ( !eventMap.containsKey(evt.getComponentName() )) {
				eventMap.put(evt.getComponentName(), new ArrayList<Event>());
			}
			eventMap.get(evt.getComponentName()).add(evt);
		}
		return sortedEvents;
	}
	
	private static EventTreeFactory INSTANCE;
}
