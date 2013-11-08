/*
    Copyright 2007-2012 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.javagui.server;

import java.awt.AWTException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Robot;
import java.lang.instrument.Instrumentation;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tcom.jmx.impl.JMXAgent;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 *  JavaGUI is a java agent started with the same VM as the java GUI application.
 *  It implements all the JavaGUIMBean services using JMX.
 * @author lvboque
 */
public class JavaGUI extends JMXAgent implements JavaGUIMBean {
	
	public static void premain(String agentArgs, Instrumentation inst) {
		new JavaGUI();
	}
	
	private Robot bot;
	
	public JavaGUI() {
		init();
		try {
			bot = new Robot();
		}
		catch (AWTException e) {
			System.out.println("JavaGUI cannot instantiate java.awt.Robot!");
		}
		//new Thread(ComponentNamer.getInstance()).start();
	}
		
	/*
	 * public boolean clickOnButton(String name) { Component c =
	 * getComponentByName(name); if (c == null) { return false; } if (c
	 * instanceof AbstractButton) { ((AbstractButton) c).doClick(); } return
	 * true; }
	 */

	public String[] listComponents() throws QTasteException {
		LOGGER.trace("listComponents()");
		return new ComponentLister().executeCommand(COMPONENT_ENABLED_TIMEOUT, null);
	}

	public void clickOnButton(String componentName) throws QTasteException {
		LOGGER.trace("clickOnButton(\"" + componentName + "\")");
		clickOnButton(componentName, 68);
	}

	public void clickOnButton(final String componentName, final int pressTime) throws QTasteException {
		LOGGER.trace("clickOnButton(\"" + componentName + "\", " + pressTime + ")");
		new ButtonClicker().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, pressTime);
	}

	public boolean isEnabled(String componentName) throws QTasteException {
		LOGGER.trace("isEnabled(\"" + componentName + "\")");
		return new EnabledStateGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}

	public boolean isEditable(String componentName) throws QTasteException {
		LOGGER.trace("isEditable(\"" + componentName + "\")");
		return new EditableStateGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}

	public boolean isVisible(String componentName) throws QTasteException {
		LOGGER.trace("isVisible(\"" + componentName + "\")");
		return new ComponentVisibilityChecker().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}

	public void takeSnapShot(final String componentName, final String fileName) throws QTasteException {
		LOGGER.trace("takeSnapShot(\"" + componentName + "\", \"" + fileName + "\")");
		new Snapshotter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, fileName);
	}

	public String getText(String componentName) throws QTasteException {
		LOGGER.trace("getText(\"" + componentName + "\")");
		return new TextGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}
				
	// TODO: boolean returns is useless and confusing!
	public void setText(final String componentName, final String value) throws QTasteException {
		LOGGER.trace("setText(\"" + componentName + "\", \"" + value + "\")");
		new TextSetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}	
	
	public void selectComponent(final String componentName, final boolean value) throws QTasteException {
		LOGGER.trace("selectComponent(\"" + componentName + "\", " + value + ")");
		new ComponentSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}			

	public void selectValue(final String componentName, final String value) throws QTasteException {
		LOGGER.trace("selectValue(\"" + componentName + "\", \"" + value + "\")");
		new ValueSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}

	public void selectIndex(final String componentName, final int index) throws QTasteException {
		LOGGER.trace("selectIndex(\"" + componentName + "\", " + index + ")");
		new IndexSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, index);
	}

	@Override
	public void selectNode(String componentName, String nodeName, String nodeSeparator) throws QTasteException {
		LOGGER.trace("selectNode(\"" + componentName + "\", \"" + nodeName + "\", \"" + nodeSeparator + "\")");
		new TreeNodeSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, nodeName, nodeSeparator);
	}
	// Todo: getColor, awt?

	@Override
	public void selectTab(String tabbedPaneComponentName, int tabIndex) throws QTasteException {
		LOGGER.trace("selectTab(\"" + tabbedPaneComponentName + "\", " + tabIndex + ")");
		new TabSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, tabbedPaneComponentName, tabIndex);
	}
	
	public String whoAmI() throws QTasteTestFailException {	
		LOGGER.trace("whoAmI()");	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}		
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getName();	
	}
	

	public String getRawName(String name) throws QTasteException
	{
		LOGGER.trace("getRawName(\"" + name + "\")");
		return new ComponentRawNameGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, name);
	}
	
	public void setComponentName(String name) throws QTasteTestFailException {
		LOGGER.trace("setComponentName(\"" + name + "\")");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().setName(name);		
	}								    

	public void pressKey(int keycode, long delay) throws QTasteTestFailException {
		LOGGER.trace("pressKey(" + keycode + ", " + delay + ")");
		new KeyPresser().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, bot, keycode, delay);
	}
	
	public void pressKey(int keycode) throws QTasteTestFailException {
		LOGGER.trace("pressKey(" + keycode + ")");
		// 68 is the default delay for a keypress
		pressKey(keycode, 68);		
	}

	@Override
	public boolean exist(String pComponentName) {
		LOGGER.trace("exist(\"" + pComponentName + "\")");
		return new ExistenceChecker().executeCommand(COMPONENT_ENABLED_TIMEOUT, pComponentName);
	}

	@Override
	public int getEnabledComponentCount(boolean isEnabled) {
		LOGGER.trace("getEnabledComponentCount(" + isEnabled + ")");
		return new EnabledComponentCounter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, isEnabled);
	}

	@Override
	public int countTableRows(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		LOGGER.trace("countTableRows(\"" + pComponentName + "\", \"" + pColumnName + "\", \"" + pColumnValue + "\")");
		return new TableRowCounter().executeCommand(COMPONENT_ENABLED_TIMEOUT, pComponentName, pColumnName, pColumnValue);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		LOGGER.trace("selectInTable(\"" + pComponentName + "\", \"" + pColumnName + "\", \"" + pColumnValue + "\")");
		selectInTable(pComponentName, pColumnName, pColumnValue, 0);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue, int pOccurenceIndex) throws QTasteException {
		LOGGER.trace("selectInTable(\"" + pComponentName + "\", \"" + pColumnName + "\", \"" + pColumnValue + "\", " + pOccurenceIndex + ")");
		new TableRowSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, pComponentName, pColumnName, pColumnValue, pOccurenceIndex);		
	}
	
	public boolean isPopupDisplayed() throws QTasteException
	{
		LOGGER.trace("isPopupDisplayed()");
		return new PopupChecker().executeCommand(COMPONENT_ENABLED_TIMEOUT, null);
	}
	
	public String getPopupText() throws QTasteException
	{
		LOGGER.trace("getPopupText()");
		List<String> texts = new PopupTextGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, true);
		if (texts.isEmpty())
		{
			throw new QTasteTestFailException("No active popup found!");
		}
		return texts.get(0);
	}
	
	public String[] getAllPopupText() throws QTasteException
	{
		LOGGER.trace("getAllPopupText()");
		return new PopupTextGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, false).toArray(new String[0]);
	}
	
	public void setPopupValue(String value) throws QTasteException
	{
		LOGGER.trace("setPopupValue(\"" + value + "\")");
		new PopupTextSetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, value);
	}
	
	public void clickOnPopupButton(String buttonText) throws QTasteException
	{
		LOGGER.trace("clickOnPopupButton(\"" + buttonText + "\")");
		new PopupButtonClicker().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, buttonText);
	}

	@Override
	public void setComponentEnabledTimeout(int pTimeOut) throws IllegalArgumentException{
		LOGGER.trace("setComponentEnabledTimeout(" + pTimeOut + ")");
		if (pTimeOut < 0)
			throw new IllegalArgumentException("Cannot set a negative timeout value. Try to set " + pTimeOut);
		
		COMPONENT_ENABLED_TIMEOUT = pTimeOut;
	}
	
	@Override
	public double[] getComponentLocation(String componentName) throws QTasteException {
		LOGGER.trace("getComponentLocation(\"" + componentName + "\")");
		Point p = new ComponentLocationGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
		return new double[]{p.getX(), p.getY()};
	}

	@Override
	public String getComponentBackgroundColor(String componentName) throws QTasteException {
		LOGGER.trace("getComponentBackgroundColor(\"" + componentName + "\")");
		return new ComponentBackgroundColorGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}

	@Override
	public String getComponentForegroundColor(String componentName) throws QTasteException {
		LOGGER.trace("getComponentForegroundColor(\"" + componentName + "\")");
		return new ComponentForegroundColorGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName);
	}
	
	@Override
	public String dumpTreeContent(String treeComponentName, String separator) throws QTasteException
	{
		LOGGER.trace("dumpTreeContent(\"" + treeComponentName + "\", \"" + separator + "\")");
		return new TreeDumper().executeCommand(COMPONENT_ENABLED_TIMEOUT, treeComponentName, separator);
	}

	@Override
	public String[] getPopupRawNames() throws QTasteException {
		LOGGER.trace("getPopupRawNames()");
		return new PopupRowNameGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, false).toArray(new String[0]);
	}

	@Override
	public String getPopupRawName() throws QTasteException {
		LOGGER.trace("getPopupRawName()");
		List<String> names = new PopupRowNameGetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, null, true);
		if (names.isEmpty())
		{
			throw new QTasteTestFailException("No active popup found!");
		}
		return names.get(0);
	} 
	
	private static int COMPONENT_ENABLED_TIMEOUT = 10;
	private static Logger LOGGER = Logger.getLogger(JavaGUI.class);
}
