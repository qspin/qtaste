package com.qspin.qtaste.tools.converter.model.event;

public class PropertyChangeEvent extends Event {

	public PropertyChangeEvent(Event pEvent)
	{
		super();
		setComponentName( pEvent.getComponentName());
		setSourceClass(pEvent.getSourceClass());
		setTimeStamp(pEvent.getTimeStamp());
		setType(pEvent.getType());
	}

	public String getPropertyName() {
		return mPropertyName;
	}
	public void setPropertyName(String propertyName) {
		this.mPropertyName = propertyName;
	}
	public String getOldValue() {
		return mOldValue;
	}
	public void setOldValue(String oldValue) {
		this.mOldValue = oldValue;
	}
	public String getNewValue() {
		return mNewValue;
	}
	public void setNewValue(String newValue) {
		this.mNewValue = newValue;
	}
	public Class<?> getSourceEventClass()
	{
		return java.beans.PropertyChangeEvent.class;
	}

	private String mPropertyName;
	private String mOldValue;
	private String mNewValue;
}
