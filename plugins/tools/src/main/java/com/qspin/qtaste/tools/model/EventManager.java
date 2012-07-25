package com.qspin.qtaste.tools.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qspin.qtaste.tools.model.event.Event;

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
		
		firePropertyChange(new PropertyChangeEvent(this, DATA_CHANGE_PROPERTY_NAME, old, pEvents));
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

	public void changeComponentName(String pOldName, String pNewName) {
		List<Event> events = new ArrayList<Event>(mEvents);
		for ( Event e : events )
		{
			if(  e.getComponentName().equals(pOldName) ) {
				e.setComponentName(pNewName);
			}
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
		setEvents(null);
	}

	private List<Event> mEvents;
	private Map<String,  List<Event>> mComponentNameMap;
	private Map<String,  List<Event>> mEventTypeMap;
	private List<PropertyChangeListener> mListener;
	
	protected static EventManager INSTANCE;
	
	public static final String DATA_CHANGE_PROPERTY_NAME = "dataUpdate";
}
