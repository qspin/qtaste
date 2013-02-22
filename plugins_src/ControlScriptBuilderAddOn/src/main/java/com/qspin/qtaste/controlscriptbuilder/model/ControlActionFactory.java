package com.qspin.qtaste.controlscriptbuilder.model;

public class ControlActionFactory {

	public static synchronized ControlAction createControlAction(String pControlActionType, String pParameters)
	{
		switch(ControlActionType.getControlActionType(pControlActionType))
		{
		case JAVA_PROCESS: return new JavaProcess();
		default : return new UnknownControlAction(pControlActionType, pParameters);
		}
	}
}
