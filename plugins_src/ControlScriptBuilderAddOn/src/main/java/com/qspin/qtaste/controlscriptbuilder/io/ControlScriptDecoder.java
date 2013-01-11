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
			is = new FileInputStream(pControlScriptFile);
			mParameters.load(is);
		}
		finally
		{
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
	
	private Properties mParameters;
	
	private static final Logger LOGGER = Logger.getLogger(ControlScriptDecoder.class);
}
