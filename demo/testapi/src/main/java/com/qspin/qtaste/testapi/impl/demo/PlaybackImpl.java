package com.qspin.qtaste.testapi.impl.demo;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.javagui.client.JavaGUIImpl;
import com.qspin.qtaste.testapi.api.Playback;
import com.qspin.qtaste.testsuite.QTasteException;

public class PlaybackImpl extends JavaGUIImpl implements Playback {

	public PlaybackImpl(String instanceId) throws Exception
    {
		super(TestBedConfiguration.getInstance().getMIString(instanceId, "Playback", "jmx_url"));
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
