package com.qspin.qtaste.testapi.impl.demo;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.javagui.client.JavaGUIImpl;
import com.qspin.qtaste.testapi.api.Playback;

public class PlaybackImpl extends JavaGUIImpl implements Playback {

	public PlaybackImpl(String instanceId) throws Exception
    {
		super(TestBedConfiguration.getInstance().getMIString(instanceId, "Playback", "jmx_url"), instanceId);
	}

}
