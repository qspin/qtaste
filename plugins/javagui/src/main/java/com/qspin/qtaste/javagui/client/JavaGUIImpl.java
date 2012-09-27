/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui.client;

import com.qspin.qtaste.javagui.server.JavaGUIMBean;
import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.tcom.jmx.impl.JMXClient;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 * 
 */
public class JavaGUIImpl implements MultipleInstancesComponent, JavaGUIMBean {

	public JavaGUIImpl(String url, String instanceId) throws Exception {
		mClient = new JMXClient(url);
		if ( mClient == null ) {
			throw new QTasteException("Unable to connect to the JMX client");
		}
		mInstanceId = instanceId;
		initialize();
	}
   
   @Override
   public void initialize() throws QTasteException
   {
	   try
	   {
			mClient.connect();
			mProxy = getProxy();
	   }
	   catch (Exception pExc)
	   {
		   pExc.printStackTrace();
		   throw new QTasteException("Unable to initialize the JMX client");
	   }
   }

	@Override
	public String getInstanceId() {
		return mInstanceId;
	}

	public boolean clickOnButton(String pComponentName) {
		return mProxy.clickOnButton(pComponentName);
	}

	public void takeSnapShot(String componentName, String fileName)
	{
		mProxy.takeSnapShot(componentName, fileName);		
	}

	public boolean keyPressedOnComponent(String componentName, int vkEvent)
	{
		return mProxy.keyPressedOnComponent(componentName, vkEvent);
	}

	public String[] listComponents()
	{
		return mProxy.listComponents();
	}

	public boolean isEnabled(String componentName)
	{
		return mProxy.isEnabled(componentName);
	}

	public boolean clickOnButton(String componentName, int pressTime)
	{
		return mProxy.clickOnButton(componentName, pressTime);
	}

	public String getText(String componentName)
	{
		return mProxy.getText(componentName);
	}

	public boolean setText(String componentName, String value)
	{
		return mProxy.setText(componentName, value);
	}

	public boolean selectComponent(String componentName, boolean value)
	{
		return mProxy.selectComponent(componentName, value);
	}

	public boolean selectValue(String componentName, String value)
	{
		return mProxy.selectValue(componentName, value);
	}

	public boolean selectIndex(String componentName, int index)
	{
		return mProxy.selectIndex(componentName, index);
	}

	public boolean selectNode(String componentName, String nodeName,
			String nodeSeparator)
	{
		return mProxy.selectNode(componentName, nodeName, nodeSeparator);
	}

	public boolean selectTab(String tabbedPaneComponentName, int tabIndex) {
		return mProxy.selectTab(tabbedPaneComponentName, tabIndex);
	}

	@Override
	public void terminate() throws QTasteException
	{
		try {
			mClient.disconnect();
			mClient = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new QTasteException(e.getMessage());
		}
	}

	private JavaGUIMBean getProxy() throws Exception {
		return (JavaGUIMBean) mClient.getProxy(BEAN_NAME, BEAN_INTERFACE);
	}

	protected String mInstanceId;
	protected JavaGUIMBean mProxy;
	protected JMXClient mClient;
	private static final String BEAN_NAME = "com.qspin.qtaste.javagui.server:type=JavaGUI";
	private static final Class<?> BEAN_INTERFACE = JavaGUIMBean.class;
}
