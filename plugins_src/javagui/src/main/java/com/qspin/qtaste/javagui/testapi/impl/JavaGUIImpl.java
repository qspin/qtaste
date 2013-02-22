package com.qspin.qtaste.javagui.testapi.impl;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.testsuite.QTasteException;

public class JavaGUIImpl extends com.qspin.qtaste.javagui.client.JavaGUIImpl implements com.qspin.qtaste.javagui.testapi.api.JavaGUI {

	public JavaGUIImpl(String instanceId) throws Exception
    {
		super(TestBedConfiguration.getInstance().getMIString(instanceId, "JavaGUI", "jmx_url"));
		mInstanceId = instanceId;
	}

	@Override
	public String getInstanceId() {
		return mInstanceId;
	}
	
	@Override
	public void terminate() throws QTasteException
	{
		super.terminate();
	}
	
	@Override
	public void initialize() throws QTasteException
	{
		super.initialize();
	}

	protected String mInstanceId;

}
