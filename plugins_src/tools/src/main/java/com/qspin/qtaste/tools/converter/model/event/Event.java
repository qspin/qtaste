package com.qspin.qtaste.tools.converter.model.event;

public class Event {

	public Event()
	{
		
	}
	
	public String toString()
	{
		return "Event on " + mComponentName + " (" + mSourceClass + ") of type " + mType + " (timestamp :" + mTimeStamp + ")";
	}
	
	public String getComponentName()
	{ 
		return mComponentName;
	}
	public void setComponentName(String pComponentName)
	{ 
		mComponentName = pComponentName;
	}
	public String getSourceClass()
	{ 
		return mSourceClass;
	}
	public void setSourceClass(String pSourceClass)
	{ 
		mSourceClass = pSourceClass;
	}
	public long getTimeStamp()
	{ 
		return mTimeStamp;
	}
	public void setTimeStamp(long pTime)
	{ 
		mTimeStamp = pTime;
	}
	public String getType()
	{
		return mType;
	}
	public void setType(String pType) {
		mType = pType;
	}
	public String getAlias()
	{
		return mAlias;
	}
	public void setAlias(String pAlias) {
		mAlias = pAlias;
	}
	public Class<?> getSourceEventClass()
	{
		return java.awt.Event.class;
	}
	
	protected String mComponentName;
	protected String mSourceClass;
	protected String mType;
	protected String mAlias;
	protected long mTimeStamp;
	
}
