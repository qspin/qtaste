package com.qspin.qtaste.tools.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


public class ComponentNameMapping {
	
	public static synchronized ComponentNameMapping getInstance()
	{
		if ( INSTANCE == null )
		{
			INSTANCE = new ComponentNameMapping();
		}
		return INSTANCE;
	}
	
	public void setAliasFor(String pComponentName, String pAlias) throws IOException
	{
		if ( pAlias == null || pAlias.isEmpty() ) {
			removeAliasFor(pComponentName);
		} else {
			mComponentMap.put(pComponentName, pAlias);
			mAliasMap.put(pAlias, pComponentName);
			save();
		}
	}
	
	public void removeAliasFor(String pComponentName)
	{
		if ( hasAlias(pComponentName) ) {
			mAliasMap.remove(getAliasFor(pComponentName));
		}
		mComponentMap.remove(pComponentName);
	}
	
	public String getAliasFor(String pComponentName)
	{
		return mComponentMap.get(pComponentName);
	}
	
	public String getComponentNameFor(String pAlias)
	{
		return mAliasMap.containsKey(pAlias) ? mAliasMap.get(pAlias) : pAlias;
	}
	
	public boolean hasAlias(String pComponentName)
	{
		return mComponentMap.containsKey(pComponentName) 
				&& mComponentMap.get(pComponentName) != null 
				&& !mComponentMap.get(pComponentName).isEmpty();
	}
	
	public void clear()
	{
		mComponentMap.clear();
		mAliasMap.clear();
	}
	
	public void load() throws IOException
	{
		clear();
		if ( mFilePath == null ) {
			return;
		}
		Properties prop = new Properties();
		prop.load(new FileInputStream(mFilePath));
		for ( Object key : prop.keySet() )
		{
			String componentName = key.toString();
			String alias = prop.getProperty(componentName);
			EventManager.getInstance().setComponentAlias(componentName, alias);
		}
	}
	
	public void save() throws IOException
	{
		if ( mFilePath == null ) {
			return;
		}
		
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(mFilePath);
			writer = new BufferedWriter(fw);
			for ( String componentName : mComponentMap.keySet() )
			{
				writer.write(componentName + " = " + getAliasFor(componentName) );
				writer.newLine();
			}
			writer.flush();
		}
		finally
		{
			IOUtils.closeQuietly(fw);
			IOUtils.closeQuietly(writer);
		}
	}
	
	public void setFilePath(String pFilePath)
	{
		mFilePath = pFilePath;
		if ( mFilePath != null )
		{
			try {
				File f = new File(mFilePath);
				if ( !f.exists() && !f.createNewFile() )
				{
					mFilePath = null;
					LOGGER.warn("Unable to create/find the aliasFile");
				}
			} catch (IOException pExc)
			{
				LOGGER.warn("Unable to create/find the aliasFile");
			}
		}
	}
	
	protected ComponentNameMapping()
	{
		mAliasMap = new HashMap<String, String>();
		mComponentMap = new HashMap<String, String>();
	}
	
	private static ComponentNameMapping INSTANCE;
	protected Map<String, String> mAliasMap;
	protected Map<String, String> mComponentMap;
	protected String mFilePath;
	
	public static final String ALIAS_FILE_NAME = "alias.properties";
	protected static final Logger LOGGER = Logger.getLogger(ComponentNameMapping.class);
}
