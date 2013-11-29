package com.qspin.qtaste.tools.converter.model.event;

public class DocumentEvent extends Event {

	public DocumentEvent(Event pEvent) {
		super();
		setComponentName(pEvent.getComponentName());
		setSourceClass(pEvent.getSourceClass());
		setTimeStamp(pEvent.getTimeStamp());
		setType(pEvent.getType());
	}
	
	public String getDocumentChangeType() {
		return mDocumentChangeType;
	}
	public void setDocumentChangeType(String documentChangeType) {
		this.mDocumentChangeType = documentChangeType;
	}
	public int getOffset() {
		return mOffset;
	}
	public void setOffset(int offset) {
		this.mOffset = offset;
	}
	public int getLenght() {
		return mLength;
	}
	public void setLength(int length) {
		this.mLength = length;
	}
	public String getChange() {
		return mChange;
	}
	public void setChange(String change) {
		this.mChange = change;
	}
	public Class<?> getSourceEventClass()
	{
		return javax.swing.event.DocumentEvent.class;
	}

	private String mDocumentChangeType;
	private int mOffset;
	private int mLength;
	private String mChange;
}
