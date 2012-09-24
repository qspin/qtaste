package com.qspin.qtaste.tools.converter.model.event;

public class ActionEvent extends Event {
	
	public ActionEvent(Event pEvent) {
		super();
		setComponentName(pEvent.getComponentName());
		setSourceClass(pEvent.getSourceClass());
		setTimeStamp(pEvent.getTimeStamp());
		setType(pEvent.getType());
	}

	public String getId() {
		return mId;
	}
	public void setId(String id) {
		this.mId = id;
	}
	public String getActionCommand() {
		return mActionCommand;
	}
	public void setActionCommand(String actionCommand) {
		this.mActionCommand = actionCommand;
	}
	public Class<?> getSourceEventClass()
	{
		return java.awt.event.ActionEvent.class;
	}

	private String mId;
	private String mActionCommand;
}
