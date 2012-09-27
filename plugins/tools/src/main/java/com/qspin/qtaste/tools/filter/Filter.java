package com.qspin.qtaste.tools.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Filter {

	public Filter()
	{
		mAcceptedEvents = new ArrayList<FilterRule>();
		mRejectedEvents = new ArrayList<FilterRule>();
	}
	
	public Filter(Filter pFilter) {
		setDescription(pFilter.getDescription());
		setSourceRule(pFilter.getSourceRule());
		setAcceptedEventRules(pFilter.getAcceptedEventRules());
		setRejectedEventRules(pFilter.getRejectedEventRules());
	}
	
	public boolean accept(Object o) {
		if ( mSourceRule != null && !mSourceRule.isRespected(o))
		{
			return true;
		}
		
		return checkRules(mAcceptedEvents, o) && checkRules(mRejectedEvents, o);
	}
	
	protected boolean checkRules(List<FilterRule> pRules, Object pTestedObject)
	{
		if ( pRules != null )
		{
			for ( FilterRule rule : pRules )
			{
				if ( !rule.isRespected(pTestedObject) )
				{
					return false;
				}	
			}
		}
		return true;
	}
	
	public void setSourceRule(Class<?> pTarget)
	{
		if ( pTarget != null )
		{
			setSourceRule(new RuleOnClass(pTarget, true));
		}
		else
		{
			setSourceRule((RuleOnClass)null);
		}
	}
	
	public void setSourceRule(RuleOnClass pSourceRule)
	{
		mSourceRule = pSourceRule;
	}
	
	public RuleOnClass getSourceRule()
	{
		return mSourceRule;
	}
	
	public void addRejectedEvent(Class<?> pRejectedEventClass)
	{
		mRejectedEvents.add(new RuleOnClass(pRejectedEventClass, false));
	}
	
	public List<FilterRule> getRejectedEventRules()
	{
		return mRejectedEvents;
	}
	
	public void setRejectedEventRules(List<FilterRule> pRules)
	{
		mRejectedEvents = pRules;
	}
	
	public void addAcceptedEvent(Class<?> pAcceptedEventClass)
	{
		mAcceptedEvents.add(new RuleOnClass(pAcceptedEventClass, true));
	}
	
	public List<FilterRule> getAcceptedEventRules()
	{
		return mAcceptedEvents;
	}
	
	public void setAcceptedEventRules(List<FilterRule> pRules)
	{
		mAcceptedEvents = pRules;
	}
	
	public void setDescription(String pDescription)
	{
		mDescription = pDescription;
	}
	
	public String getDescription()
	{
		return mDescription;
	}

	protected List<FilterRule> mAcceptedEvents;
	protected List<FilterRule> mRejectedEvents;
	protected RuleOnClass mSourceRule;
	protected String mDescription;
	
	/** Used for logging. */
	protected static final Logger LOGGER = Logger.getLogger(Filter.class);
}
