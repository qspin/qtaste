package com.qspin.qtaste.tools;

import java.util.Properties;

public class ComponentNameMapping extends Properties {
	
	public static synchronized ComponentNameMapping getInstance()
	{
		if ( INSTANCE == null )
		{
			INSTANCE = new ComponentNameMapping();
		}
		return INSTANCE;
	}
	
	protected ComponentNameMapping()
	{
		super();
	}
	
	private static ComponentNameMapping INSTANCE;
}
