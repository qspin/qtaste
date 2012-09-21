package com.qspin.qtaste.tools.converter.model.event;

public class ItemEvent extends Event {
	
	public ItemEvent(Event pEvent) {
		super();
		setComponentName(pEvent.getComponentName());
		setSourceClass(pEvent.getSourceClass());
		setTimeStamp(pEvent.getTimeStamp());
		setType(pEvent.getType());
	}

	public String getId() {
		return mId;
	}
	public void setId(String pId) {
		mId = pId;
	}
	public String getState() {
		return mState;
	}
	public void setState(String pState) {
		mState = pState;
	}
	public String getSelectedItem() {
		return mSelectedItem;
	}
	public void setSelectedItem(String pSelectedItem) {
		mSelectedItem = pSelectedItem;
	}

	private String mId;
	private String mState;
	private String mSelectedItem;
}
