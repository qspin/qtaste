package com.qspin.qtaste.controlscriptbuilder.model;

import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class ControlAction {

	protected ControlAction()
	{
		mParameters = new SortedProperties();
		setDescription("Undefined");
	}
	
	public abstract ControlActionType getType();
	
	public void addParameter(String pParameterName, String pParameterValue)
	{
		mParameters.setProperty(pParameterName, pParameterValue);
	}
	
	public Properties getParameters()
	{
		return mParameters;
	}
	
//	protected String[] divideParameters(String pParameters)
//	{
//		List<String> parameters = new ArrayList<String>();
//		String param = "";
//		boolean previousCharIsBS = false;
//		boolean inQuote = false;
//		char quoteChar = ' ';
//		for ( int i =0; i< pParameters.length(); ++i)
//		{
//			char c = pParameters.charAt(i);
//			if ( c == ',' && !inQuote )
//			{
//				if (!param.trim().isEmpty())
//				{
//					parameters.add(param);
//				}
//				previousCharIsBS = false;
//				param = "";
//			}
//			else
//			{
//				param += c;
//				if ( c == '\\' )
//					previousCharIsBS = !previousCharIsBS;
//				else
//				{
//					if (!inQuote)
//					{
//						if (c == '"' || c == '\'')
//						{
//							inQuote = true;
//							quoteChar = c;
//						}
//					}
//					else if ( c == quoteChar && !previousCharIsBS )
//					{
//						inQuote = false;
//					}
//					previousCharIsBS = false;
//				}
//			}
//		}
//		if (!param.trim().isEmpty())
//		{
//			parameters.add(param);
//		}
//		return parameters.toArray(new String[0]);
//	}
//	
//	protected String cleanParameter(String pParameter)
//	{
//		String clean = pParameter.trim();
//		if (clean.startsWith("\"") || clean.startsWith("'"))
//			clean = clean.substring(1);
//		if (clean.endsWith("\"") || clean.endsWith("'"))
//			clean = clean.substring(0, clean.length()-1);
//		return clean;
//	}
//	
//	protected Entry<String, String> getKeyValueFromParameter(String pParameter)
//	{
//		if ( !pParameter.contains("=") )
//			return null;
//		
//		if ( pParameter.contains("'") )
//		{
//			if ( pParameter.indexOf("=") > pParameter.indexOf("'"))
//				return null;
//		}
//		
//		if ( pParameter.contains("\"") )
//		{
//			if ( pParameter.indexOf("=") > pParameter.indexOf("\""))
//				return null;
//		}
//		
//		String key = pParameter.substring(0, pParameter.indexOf("="));
//		String value = cleanParameter(pParameter.substring(key.length() + 1));
//		
//		return new SimpleEntry<String, String>(key.trim(), value.trim()) ;
//	}
	
	public String getDescription()
	{
		return mParameters.getProperty(DESCRIPTION);
	}
	public void setDescription(String pDescrition)
	{
		mParameters.setProperty(DESCRIPTION, pDescrition);
	}
	
	public String toString()
	{
		return getDescription();
	}
	
	protected static final String DESCRIPTION = "description";
	protected Properties mParameters;
	
	protected static final Logger LOGGER = Logger.getLogger(ControlAction.class);
}
