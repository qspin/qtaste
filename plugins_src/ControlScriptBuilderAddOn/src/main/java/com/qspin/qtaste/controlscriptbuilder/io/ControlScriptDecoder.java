package com.qspin.qtaste.controlscriptbuilder.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
import com.qspin.qtaste.controlscriptbuilder.model.ControlActionFactory;


public final class ControlScriptDecoder {

	public static synchronized List<ControlAction> decode(File pControlScriptFile) throws IOException
	{
		ControlScriptDecoder decoder = new ControlScriptDecoder();
		decoder.readFile(pControlScriptFile);
		return decoder.parseParameters();
	}
	
	private ControlScriptDecoder(){}
	
	private void readFile(File pControlScriptFile) throws IOException
	{
		mParameters = new Properties();
		InputStream is = null;
		try
		{
			LOGGER.info("Start reading");
			is = new FileInputStream(pControlScriptFile);
			mParameters.load(is);
		}
		finally
		{
			LOGGER.info("Stop reading.");
			IOUtils.closeQuietly(is);
		}
	}
	
	private List<ControlAction> parseParameters()
	{
		Map<String, ControlAction> mapAction = new HashMap<String, ControlAction>();
		for (String s : mParameters.getProperty("processes", "").split("\\|") )
		{
			LOGGER.debug("Process found : " + s);
			mapAction.put(s, ControlActionFactory.createControlAction(mParameters.getProperty(s+".type"), ""));
		}
		
		for ( Object key : mParameters.keySet() )
		{
			if (key.equals("processes"))
				continue;
			//get the process name
			String processName = key.toString().substring(0, key.toString().lastIndexOf("."));
			if ( !mapAction.containsKey(processName) )
			{
				LOGGER.warn("Unknown process : " + processName);
			}
			else
			{
				String parameterName = key.toString().substring(processName.length() + 1);
				mapAction.get(processName).addParameter(parameterName, mParameters.getProperty(key.toString()));
			}
		}
		
		return new ArrayList<ControlAction>(mapAction.values());
	}
	
//	private List<ControlAction> parseControlScriptDataLine()
//	{
//		List<ControlAction> actions = new ArrayList<ControlAction>();
//		if ( mLineToParse == null)
//			return actions;
//		mLineToParse = mLineToParse.trim();
//		LOGGER.info("will parse : " + mLineToParse);
//		//divide by process type
//		while ( !mLineToParse.isEmpty() )
//		{
//			mLineToParse = mLineToParse.trim();
//			//search the process type
//			String processType = mLineToParse.substring(0, mLineToParse.indexOf("("));
//			mLineToParse = mLineToParse.substring(processType.length()+1);
//			//clean the process type
//			processType = processType.trim();
//			if (processType.startsWith(","))
//				processType = processType.substring(1);
//			processType = processType.trim();
//			
//			//get process parameters
//			int parCpt = 1;
//			String params = "";
//			for ( int i=0; parCpt > 0; i++ )
//			{
//				char c = mLineToParse.charAt(i);
//				if ( c == '(' )
//					parCpt ++;
//				else if ( c == ')' )
//					parCpt --;
//				params += c;
//			}
//			
//			mLineToParse = mLineToParse.substring(params.length());
//			//remove last )
//			params = params.substring(0, params.lastIndexOf(')'));
//			
//			LOGGER.info("process found !");
//			LOGGER.info("process type : " + processType);
//			LOGGER.info("process parameters : " + params);
//			
//			ControlAction action = ControlActionFactory.createControlAction(processType, params);
//			LOGGER.info("Control action created : " + action);
//			if ( action != null )
//			{
//				actions.add(action);
//			}
//		}
//		return actions;
//	}

//	private String mLineToParse; 
//	private int mBracketCounter = -1;
//	private int mParentheseCounter = -1;
	private Properties mParameters;
	
	private static final Logger LOGGER = Logger.getLogger(ControlScriptDecoder.class);
}
