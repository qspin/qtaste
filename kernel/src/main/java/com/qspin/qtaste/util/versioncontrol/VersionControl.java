package com.qspin.qtaste.util.versioncontrol;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.util.versioncontrol.impl.DefaultVersionControl;

public abstract class VersionControl implements VersionControlInterface {

	public static synchronized VersionControlInterface getInstance()
	{
		if ( _INSTANCE == null ) 
		{
			_INSTANCE = new DefaultVersionControl();
			try {
	            String classVersionControl = TestEngineConfiguration.getInstance().getString("version_control");
	            _INSTANCE = (VersionControlInterface) Class.forName(classVersionControl).newInstance();
	        } catch (Exception e) {
	        	System.err.println("Error loading version control plugin");
	            e.printStackTrace();
	        }
		}
		return _INSTANCE;
	}
	
	private static VersionControlInterface _INSTANCE;
}
