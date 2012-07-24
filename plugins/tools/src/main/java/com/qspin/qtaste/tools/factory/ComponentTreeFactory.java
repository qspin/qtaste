package com.qspin.qtaste.tools.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.node.ComponentNode;

public final class ComponentTreeFactory extends TreeFactory {

	public synchronized static ComponentTreeFactory getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new ComponentTreeFactory();
		}
		return INSTANCE;
	}

	@Override
	public MutableTreeNode buildRootTree(List<Event> pEvents) {
		MutableTreeNode root = new DefaultMutableTreeNode("Components");
		Map<String, Map<String,  List<Event>>> sortedEvents = sortData(pEvents);
		
		for( String componentName : sortedEvents.keySet() )
		{ 
			MutableTreeNode componentNode = null;
			boolean first=true;
			for ( String eventType : sortedEvents.get(componentName).keySet() )
			{
				MutableTreeNode eventTypeNode = new DefaultMutableTreeNode(eventType);
				for (Event evt : sortedEvents.get(componentName).get(eventType) )
				{
					if ( first )
					{
						first = false;
						MutableTreeNode classNode = new DefaultMutableTreeNode(evt.getSourceClass());
						componentNode  = new ComponentNode(componentName, evt.getSourceClass());
						componentNode.insert(classNode, componentNode.getChildCount());
					}
					MutableTreeNode eventNode = EventNodeFactory.getInstance().createNode(evt, false, true);
					componentNode.insert(eventNode, componentNode.getChildCount());
				}
				componentNode.insert(eventTypeNode, componentNode.getChildCount());
			}
			root.insert(componentNode, root.getChildCount());
		}
		return root;
	}
	
	private ComponentTreeFactory()
	{
		super();
	}
	
	private Map<String, Map<String,  List<Event>>> sortData(List<Event> pEvents)
	{
		Map<String, Map<String, List<Event>>> sortedEvents = new HashMap<String, Map<String, List<Event>>>();
		for ( Event evt : pEvents )
		{
			if ( !sortedEvents.containsKey(evt.getComponentName()) )
			{
				sortedEvents.put(evt.getComponentName(), new HashMap<String,  List<Event>>());
			}
			Map<String,  List<Event>> componentMap = sortedEvents.get(evt.getComponentName());
			if ( !componentMap.containsKey(evt.getType() )) {
				componentMap.put(evt.getType(), new ArrayList<Event>());
			}
			componentMap.get(evt.getType()).add(evt);
		}
		return sortedEvents;
	}
	
	private static ComponentTreeFactory INSTANCE;
}
