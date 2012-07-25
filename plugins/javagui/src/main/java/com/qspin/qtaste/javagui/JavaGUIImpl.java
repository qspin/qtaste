/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.tcom.jmx.impl.JMXClient;

/**
 * 
 */
public abstract class JavaGUIImpl extends JMXClient implements
		MultipleInstancesComponent {

	public JavaGUIImpl(String url, String instanceId) throws Exception {
		super(url);
		mInstanceId = instanceId;
		connect();
	}

	@Override
	public String getInstanceId() {
		return mInstanceId;
	}

	protected String mInstanceId;

}
