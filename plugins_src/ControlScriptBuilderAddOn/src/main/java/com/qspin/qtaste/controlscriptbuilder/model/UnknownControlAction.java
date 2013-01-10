package com.qspin.qtaste.controlscriptbuilder.model;

public class UnknownControlAction extends ControlAction {

	public UnknownControlAction(String pProcessType, String pParameters) {
		super();
		mProcessType = pProcessType;
		mParameters = pParameters;
	}
	
	@Override
	public ControlActionType getType() {
		return ControlActionType.UNKNOWN_PROCESS;
	}

	public String getRawParameters()
	{
		return mParameters;
	}
	
	public String getProcessType() {
		return mProcessType;
	}
	
	public String toString()
	{
		return getProcessType();
	}
	
	protected String mParameters;
	protected String mProcessType;
}
