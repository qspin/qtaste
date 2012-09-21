package com.qspin.qtaste.tools.converter.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.qspin.qtaste.tools.converter.factory.TreeFactory;
import com.qspin.qtaste.tools.converter.model.EventManager;
import com.qspin.qtaste.tools.converter.model.event.Event;

public class EventTree extends JTree implements PropertyChangeListener{

	public EventTree()
	{
		super(new DefaultMutableTreeNode());
		EventManager.getInstance().addPropertyChangeListener(this);
	}
	
	public void setTreeData(List<Event> pData)
	{
		mEvents = pData == null ? new ArrayList<Event>() : pData;
		rebuild();
	}
	
	public void setTreeBuilder(TreeFactory pFactory)
	{
		if ( pFactory != null )
		{
			mModelFactory = pFactory;
		}
		rebuild();
	}
	
	public void rebuild()
	{
		((DefaultTreeModel)getModel()).setRoot(mModelFactory.buildRootTree(EventManager.getInstance().getEvents()));
	}
	
	public void propertyChange(PropertyChangeEvent pEvt)
	{
		rebuild();
	}
	
	protected List<Event> mEvents = new ArrayList<Event>();
	protected TreeFactory mModelFactory;
}
