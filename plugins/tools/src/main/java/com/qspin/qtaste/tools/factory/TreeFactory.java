package com.qspin.qtaste.tools.factory;

import java.util.List;

import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.ComponentNameMapping;
import com.qspin.qtaste.tools.model.event.Event;

public abstract class TreeFactory {

	public abstract MutableTreeNode buildRootTree(List<Event> pEvents);

	
	protected String getComponentDisplayName(String pComponentName)
	{
		if( ComponentNameMapping.getInstance().hasAlias(pComponentName) )
		{
			return ComponentNameMapping.getInstance().getAliasFor(pComponentName);
		}
		else {
			return pComponentName;
		}
	}
}
