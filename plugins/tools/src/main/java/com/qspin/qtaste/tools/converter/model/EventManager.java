package com.qspin.qtaste.tools.converter.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.converter.model.event.Event;

public class EventManager {

	public synchronized static EventManager getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new EventManager();
		}
		return INSTANCE;
	}
	
	public void setEvents(List<Event> pEvents)
	{
		if(  pEvents == null ) {
			pEvents = new ArrayList<Event>();
		}
		mComponentNameMap.clear();
		mEventTypeMap.clear();
		mEventAliasMap.clear();
		
		for ( Event evt : pEvents )
		{
			if ( !mComponentNameMap.containsKey(evt.getComponentName()) )
			{
				mComponentNameMap.put(evt.getComponentName(), new ArrayList<Event>());
			}
			if ( !mEventTypeMap.containsKey(evt.getType() )) {
				mEventTypeMap.put(evt.getType(), new ArrayList<Event>());
			}
			mComponentNameMap.get(evt.getComponentName()).add(evt);
			mEventTypeMap.get(evt.getType()).add(evt);
		}
		List<Event> old = mEvents;
		mEvents = pEvents;
		
		firePropertyChange(new PropertyChangeEvent(this, DATA_CHANGE_PROPERTY_ID, old, pEvents));
	}
	
	public List<Event> getEventsForComponent(String pComponentName)
	{
		if ( mComponentNameMap.containsKey(pComponentName) )
		{
			return mComponentNameMap.get(pComponentName);
		}
		return new ArrayList<Event>();
	}
	
	public void addPropertyChangeListener( PropertyChangeListener pListener)
	{
		mListener.add(pListener);
	}
	
	public void removePropertyChangeListener( PropertyChangeListener pListener)
	{
		mListener.remove(pListener);
	}
	
	public List<Event> getEvents()
	{
		return mEvents;
	}

	public boolean isNameFree(String text) {
		return !mComponentNameMap.containsKey(text);
	}
	
	public Object[] getComponentNames()
	{
		Object[] array = mComponentNameMap.keySet().toArray();;
		Arrays.sort(array);
		return array;
	}
	
	public Object[] getEventTypes()
	{
		Object[] array = mEventTypeMap.keySet().toArray();;
		Arrays.sort(array);
		return array;
	}

	public void setComponentAlias(String pComponentName, String pAlias) {
		List<Event> events = new ArrayList<Event>(mEvents);
		for ( Event e : events )
		{
			if(  e.getComponentName().equals(pComponentName) ) {
				e.setAlias(pAlias);
			}
		}
		try {
			ComponentNameMapping.getInstance().setAliasFor(pComponentName, pAlias);
		} catch (IOException pExc)
		{
			LOGGER.warn(pExc);
		}
		setEvents(events);
	}
	
	protected void firePropertyChange(PropertyChangeEvent pEvt)
	{
		for ( PropertyChangeListener listener : mListener )
		{
			listener.propertyChange(pEvt);
		}
	}
	
	protected EventManager() {
		mComponentNameMap = new HashMap<String, List<Event>>();
		mEventTypeMap = new HashMap<String, List<Event>>();
		mListener = new ArrayList<PropertyChangeListener>();
		mEventAliasMap = new HashMap<String, String>();
		setEvents(null);
	}

	private List<Event> mEvents;
	private Map<String,  List<Event>> mComponentNameMap;
	private Map<String,  List<Event>> mEventTypeMap;
	private Map<String,  String> mEventAliasMap;
	private List<PropertyChangeListener> mListener;
	
	protected static EventManager INSTANCE;
	protected static final Logger LOGGER = Logger.getLogger(EventManager.class);

	public static final String DATA_CHANGE_PROPERTY_ID = "dataUpdate";
	public static final String ALIAS_CHANGE_PROPERTY_ID = "aliasUpdate";
}
