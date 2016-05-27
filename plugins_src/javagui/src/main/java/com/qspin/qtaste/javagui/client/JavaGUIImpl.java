/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui.client;

import com.qspin.qtaste.javagui.JavaGUI;
import com.qspin.qtaste.tcom.jmx.impl.JMXClient;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 *
 */
public class JavaGUIImpl implements JavaGUI {

	public JavaGUIImpl(String url) throws Exception {
		mClient = new JMXClient(url);
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

	public void clickOnButton(String pComponentName) throws QTasteException {
		mProxy.clickOnButton(pComponentName);
	}

	public void takeSnapShot(String componentName, String fileName) throws QTasteException
	{
		mProxy.takeSnapShot(componentName, fileName);
	}

	public String[] listComponents() throws QTasteException
	{
		return mProxy.listComponents();
	}

	public boolean isEnabled(String componentName) throws QTasteException
	{
		return mProxy.isEnabled(componentName);
	}

	public boolean isEditable(String componentName) throws QTasteException
	{
		return mProxy.isEditable(componentName);
	}

	public boolean isVisible(String componentName) throws QTasteException
	{
		return mProxy.isVisible(componentName);
	}

	public void clickOnButton(String componentName, int pressTime) throws QTasteException
	{
		mProxy.clickOnButton(componentName, pressTime);
	}

	public String getText(String componentName) throws QTasteException
	{
		return mProxy.getText(componentName);
	}

	public String getToolTip(String componentName) throws QTasteException
	{
		return mProxy.getToolTip(componentName);
	}

	public void setText(String componentName, String value) throws QTasteException
	{
		mProxy.setText(componentName, value);
	}

	public void selectComponent(String componentName, boolean value) throws QTasteException
	{
		mProxy.selectComponent(componentName, value);
	}

	public void selectValue(String componentName, String value) throws QTasteException
	{
		mProxy.selectValue(componentName, value);
	}

	public void selectIndex(String componentName, int index) throws QTasteException
	{
		mProxy.selectIndex(componentName, index);
	}

	public void selectNode(String componentName, String nodePath, String nodePathSeparator) throws QTasteException
	{
		mProxy.selectNode(componentName, nodePath, nodePathSeparator);
	}

	public void selectNodeRe(String componentName, String nodePath, String nodePathSeparator) throws QTasteException
	{
		mProxy.selectNodeRe(componentName, nodePath, nodePathSeparator);
	}

	public void clearNodeSelection(String componentName) throws QTasteException
	{
		mProxy.clearNodeSelection(componentName);
	}

	public void selectTab(String tabbedPaneComponentName, int tabIndex)  throws QTasteException{
		mProxy.selectTab(tabbedPaneComponentName, tabIndex);
	}

	public void selectTabTitled(String tabbedPaneComponentName, String tabTitle) throws QTasteException{
		mProxy.selectTabTitled(tabbedPaneComponentName, tabTitle);
	}

	public void selectTabId(String tabbedPaneComponentName, String tabComponentId) throws QTasteException{
		mProxy.selectTabId(tabbedPaneComponentName, tabComponentId);
	}
	
	public int getSelectedTabIndex(String tabbedPaneComponentName) throws QTasteException {
		return mProxy.getSelectedTabIndex(tabbedPaneComponentName);
	}

	public String getSelectedTabTitle(String tabbedPaneComponentName) throws QTasteException {
		return mProxy.getSelectedTabTitle(tabbedPaneComponentName);
	}

	public String getSelectedTabId(String tabbedPaneComponentName) throws QTasteException {
		return mProxy.getSelectedTabId(tabbedPaneComponentName);
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

	public String whoAmI() throws QTasteException {
		return mProxy.whoAmI();
    }

	public void setComponentName(String name) throws QTasteException {
		mProxy.setComponentName(name);
	}


	private JavaGUI getProxy() throws Exception {
		return (com.qspin.qtaste.javagui.JavaGUI) mClient.getProxy(BEAN_NAME, BEAN_INTERFACE);
	}

	public void pressKey(int keycode) throws QTasteException {
		mProxy.pressKey(keycode);

	}

	public void pressKey(int keycode, long delay) throws QTasteException {
		mProxy.pressKey(keycode, delay);
	}

	public boolean exist(String pComponentName) {
		return mProxy.exist(pComponentName);
	}

	public int getEnabledComponentCount(boolean isEnabled) {
		return mProxy.getEnabledComponentCount(isEnabled);
	}
	@Override
	public int countTableRows(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		return mProxy.countTableRows(pComponentName, pColumnName, pColumnValue);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		mProxy.selectInTable(pComponentName, pColumnName, pColumnValue);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue, int pOccurenceIndex) throws QTasteException {
		mProxy.selectInTable(pComponentName, pColumnName, pColumnValue, pOccurenceIndex);
	}

	@Override
	public void setComponentEnabledTimeout(int pTimeOut) throws IllegalArgumentException {
		mProxy.setComponentEnabledTimeout(pTimeOut);
	}

	public boolean isPopupDisplayed() throws QTasteException
	{
		return mProxy.isPopupDisplayed();
	}

	public String getPopupText() throws QTasteException
	{
		return mProxy.getPopupText();
	}

	public String[] getAllPopupText() throws QTasteException
	{
		return mProxy.getAllPopupText();
	}

	public void setPopupValue(String value) throws QTasteException
	{
		mProxy.setPopupValue(value);
	}

	public void clickOnPopupButton(String buttonText) throws QTasteException
	{
		mProxy.clickOnPopupButton(buttonText);
	}

	@Override
	public double[] getComponentLocation(String componentName) throws QTasteException {
		return mProxy.getComponentLocation(componentName);
	}

	@Override
	public String getComponentBackgroundColor(String componentName) throws QTasteException {
		return mProxy.getComponentBackgroundColor(componentName);
	}

	@Override
	public String getComponentForegroundColor(String componentName) throws QTasteException {
		return mProxy.getComponentForegroundColor(componentName);
	}

	@Override
	public String getRawName(String name) throws QTasteException {
		return mProxy.getRawName(name);
	}

	@Override
	public String dumpTreeContent(String treeComponentName, String separator) throws QTasteException
	{
		return mProxy.dumpTreeContent(treeComponentName, separator);
	}
	@Override
	public String[] getPopupRawNames() throws QTasteException {
		return mProxy.getPopupRawNames();
	}

	@Override
	public String getPopupRawName() throws QTasteException {
		return mProxy.getPopupRawName();
	}

	public String [] getListContent(String componentName) throws QTasteException {
		return mProxy.getListContent(componentName);
	}

    @Override
    public Object getSelectedValue(String componentName) throws QTasteException {
        return mProxy.getSelectedValue(componentName);
    }

    @Override
    public int getSelectedIndex(String componentName) throws QTasteException {
        return mProxy.getSelectedIndex(componentName);
    }

    @Override
    public String getSelectedNode(String componentName, String nodeSeparator) throws QTasteException {
        return mProxy.getSelectedNode(componentName, nodeSeparator);
    }

	@Override
	public void selectFileThroughFileChooser(String fileChooserComponentName, String filepath) throws QTasteException {
		mProxy.selectFileThroughFileChooser(fileChooserComponentName, filepath);
	}

	@Override
	public void analyzeStructure(String fileName) throws QTasteException
	{
		mProxy.analyzeStructure(fileName);
	}

    protected JavaGUI mProxy;
	protected JMXClient mClient;
	private static final String BEAN_NAME = "com.qspin.qtaste.javagui.server:type=JavaGUI";
	private static final Class<?> BEAN_INTERFACE = com.qspin.qtaste.javagui.JavaGUI.class;

}
