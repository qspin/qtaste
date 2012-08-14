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
public abstract class JavaGUIImpl extends JMXClient implements MultipleInstancesComponent, JavaGUIMBean {

	public JavaGUIImpl(String url, String instanceId) throws Exception {
		super(url);
		mInstanceId = instanceId;
		connect();
		mProxy = getProxy();
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

	private JavaGUIMBean getProxy() throws Exception {
		return (JavaGUIMBean) getProxy(BEAN_NAME, BEAN_INTERFACE);
	}

	protected String mInstanceId;
	protected JavaGUIMBean mProxy;
	private static final String BEAN_NAME = "com.qspin.qtaste.javagui:type=JavaGUI";
	private static final Class<?> BEAN_INTERFACE = JavaGUIMBean.class;
}
