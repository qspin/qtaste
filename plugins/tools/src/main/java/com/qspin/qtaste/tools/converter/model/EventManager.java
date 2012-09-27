package com.qspin.qtaste.tools.converter.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.filter.Filter;

/**
 * Responsible for the management/filtering  of the events.
 * Implemented with the singleton pattern.
 * @author simjan
 *
 */
public class EventManager {

	/**
	 * Returns the unique instance of the manager. If non exist, creates one.
	 * @return the unique instance of the manager.
	 */
	public synchronized static EventManager getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new EventManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Loads and filters the events into the manager.
	 * @param pEvents
	 */
	public void setEvents(List<Event> pEvents)
	{
		if(  pEvents == null ) {
			pEvents = new ArrayList<Event>();
		}
		List<Event> old = mEvents;
		mEvents.clear();
		mUnfilteredEvents = pEvents;
		mComponentNameMap.clear();
		mEventTypeMap.clear();
		mEventAliasMap.clear();
		
		for ( Event evt : pEvents )
		{
			if ( mFilters != null )
			{
				boolean canbeUsed = true;
				for ( Filter f : mFilters )
				{
					if ( !f.accept(evt) )
					{
						canbeUsed = false;
						break;
					}
				}
				if ( !canbeUsed )
				{
					continue;
				}
			}
			if ( !mComponentNameMap.containsKey(evt.getComponentName()) )
			{
				mComponentNameMap.put(evt.getComponentName(), new ArrayList<Event>());
			}
			if ( !mEventTypeMap.containsKey(evt.getType() )) {
				mEventTypeMap.put(evt.getType(), new ArrayList<Event>());
			}
			mComponentNameMap.get(evt.getComponentName()).add(evt);
			mEventTypeMap.get(evt.getType()).add(evt);
			mEvents.add(evt);
		}
		
		firePropertyChange(new PropertyChangeEvent(this, DATA_CHANGE_PROPERTY_ID, old, pEvents));
	}
	
	/**
	 * Returns all events registered for the component identified by the name.
	 * @param pComponentName
	 * @return all events registered for the component identified by the name.
	 */
	public List<Event> getEventsForComponent(String pComponentName)
	{
		if ( mComponentNameMap.containsKey(pComponentName) )
		{
			return mComponentNameMap.get(pComponentName);
		}
		return new ArrayList<Event>();
	}
	
	/**
	 * Registers the listener to receive an event when events, filters or alias change.
	 * @param pListener
	 */
	public void addPropertyChangeListener( PropertyChangeListener pListener)
	{
		mListener.add(pListener);
	}
	
	public void removePropertyChangeListener( PropertyChangeListener pListener)
	{
		mListener.remove(pListener);
	}
	
	public List<Event> getFilteredEvents()
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
		List<Event> events = new ArrayList<Event>(mUnfilteredEvents);
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
	
	public void setEventsFilter(List<Filter> pFilter) {
		List<Filter> old = mFilters;
		mFilters = pFilter;
		
		setEvents(mUnfilteredEvents);
		firePropertyChange(new PropertyChangeEvent(this, FILTER_CHANGE_PROPERTY_ID, old, pFilter));
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
		mEvents = new ArrayList<Event>();
		setEvents(null);
	}

	private List<Event> mEvents;
	private List<Event> mUnfilteredEvents;
	private Map<String,  List<Event>> mComponentNameMap;
	private Map<String,  List<Event>> mEventTypeMap;
	private Map<String,  String> mEventAliasMap;
	private List<Filter> mFilters;
	private List<PropertyChangeListener> mListener;
	
	protected static EventManager INSTANCE;
	protected static final Logger LOGGER = Logger.getLogger(EventManager.class);

	public static final String DATA_CHANGE_PROPERTY_ID = "dataUpdate";
	public static final String ALIAS_CHANGE_PROPERTY_ID = "aliasUpdate";
	public static final String FILTER_CHANGE_PROPERTY_ID = "filterUpdate";
}
