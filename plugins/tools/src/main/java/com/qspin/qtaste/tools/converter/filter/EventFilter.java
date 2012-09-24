package com.qspin.qtaste.tools.converter.filter;

import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.filter.Filter;

public class EventFilter extends Filter {


	public EventFilter(Filter pFilter) {
		super();
		setDescription(pFilter.getDescription());
		setSourceRule(pFilter.getSourceRule());
		setAcceptedEventRules(pFilter.getAcceptedEventRules());
		setRejectedEventRules(pFilter.getRejectedEventRules());
	}

	@Override
	public boolean accept(Object o) {
		if ( o instanceof Event )
		{
			Event evt = (Event)o;
			if (checkSource(evt))
				return checkEvent(evt);
			
			return true;
		}
		else
		{
			return super.accept(o);
		}
	}
	
	private boolean checkEvent(Event pEvent)
	{
		return checkRules(mAcceptedEvents, pEvent.getSourceEventClass())
				&& checkRules(mRejectedEvents, pEvent.getSourceEventClass());
	}

	private boolean checkSource(Event pEvent)
	{
		if (mSourceRule != null)
		{
			try
			{
				return mSourceRule.isRespected(getClass().getClassLoader().loadClass(pEvent.getSourceClass()));
			}
			catch (ClassNotFoundException pExc)
			{
				LOGGER.warn("Cannot load the class " + pEvent.getSourceClass() + ". The filter will not filtered this class.");
				return true;
			}
		}
		return true;
	}
	
}
