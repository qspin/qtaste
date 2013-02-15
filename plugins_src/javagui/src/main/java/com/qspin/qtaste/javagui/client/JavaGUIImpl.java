/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui.client;

import com.qspin.qtaste.javagui.JavaGUI;
import com.qspin.qtaste.tcom.jmx.impl.JMXClient;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * 
 */
public class JavaGUIImpl implements JavaGUI {

	public JavaGUIImpl(String url) throws Exception {
		mClient = new JMXClient(url);
		if ( mClient == null ) {
			throw new QTasteException("Unable to connect to the JMX client");
		}
		initialize();
	}
   
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

	public boolean clickOnButton(String pComponentName) throws QTasteTestFailException {
		return mProxy.clickOnButton(pComponentName);
	}

	public void takeSnapShot(String componentName, String fileName) throws QTasteTestFailException
	{
		mProxy.takeSnapShot(componentName, fileName);		
	}

	public boolean keyPressedOnComponent(String componentName, int vkEvent) throws QTasteTestFailException
	{
		return mProxy.keyPressedOnComponent(componentName, vkEvent);
	}

	public String[] listComponents() throws QTasteTestFailException
	{
		return mProxy.listComponents();
	}

	public boolean isEnabled(String componentName) throws QTasteTestFailException
	{
		return mProxy.isEnabled(componentName);
	}

	public boolean clickOnButton(String componentName, int pressTime) throws QTasteTestFailException
	{
		return mProxy.clickOnButton(componentName, pressTime);
	}

	public String getText(String componentName) throws QTasteTestFailException
	{
		return mProxy.getText(componentName);
	}

	public boolean setText(String componentName, String value) throws QTasteTestFailException
	{
		return mProxy.setText(componentName, value);
	}

	public boolean selectComponent(String componentName, boolean value) throws QTasteTestFailException
	{
		return mProxy.selectComponent(componentName, value);
	}

	public boolean selectValue(String componentName, String value) throws QTasteTestFailException
	{
		return mProxy.selectValue(componentName, value);
	}

	public boolean selectIndex(String componentName, int index) throws QTasteTestFailException
	{
		return mProxy.selectIndex(componentName, index);
	}

	public boolean selectNode(String componentName, String nodeName,
			String nodeSeparator) throws QTasteTestFailException
	{
		return mProxy.selectNode(componentName, nodeName, nodeSeparator);
	}

	public boolean selectTab(String tabbedPaneComponentName, int tabIndex)  throws QTasteTestFailException{
		return mProxy.selectTab(tabbedPaneComponentName, tabIndex);
	}

	public void terminate() throws QTasteException
	{
		try {
			mClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new QTasteException(e.getMessage());
		}
	}
	
	public String whoAmI() throws QTasteTestFailException {	
		return mProxy.whoAmI();	
    }
	
	public void setComponentName(String name) throws QTasteTestFailException {
		mProxy.setComponentName(name);
	}
	
	
	private JavaGUI getProxy() throws Exception {
		return (com.qspin.qtaste.javagui.JavaGUI) mClient.getProxy(BEAN_NAME, BEAN_INTERFACE);
	}
	
	public void pressKey(int keycode) throws QTasteTestFailException {
		mProxy.pressKey(keycode);
		
	}
	
	public void pressKey(int keycode, long delay) throws QTasteTestFailException {
		mProxy.pressKey(keycode, delay);	
	}
	
	public boolean exist(String pComponentName) {
		return mProxy.exist(pComponentName);
	}

	public int getEnabledComponentCount(boolean isEnabled) {
		return mProxy.getEnabledComponentCount(isEnabled);
	}
	
	
	protected JavaGUI mProxy;
	protected JMXClient mClient;
	private static final String BEAN_NAME = "com.qspin.qtaste.javagui.server:type=JavaGUI";
	private static final Class<?> BEAN_INTERFACE = com.qspin.qtaste.javagui.JavaGUI.class;
	
	
			
}
