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
import java.awt.Robot;
import java.lang.instrument.Instrumentation;

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
		return new ComponentLister().executeCommand();
	}

	public boolean clickOnButton(String componentName) throws QTasteException {
		return clickOnButton(componentName, 68);
	}

	public boolean clickOnButton(final String componentName, final int pressTime) throws QTasteException {
		return new ButtonClicker().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, pressTime);
	}

	public boolean isEnabled(String componentName) throws QTasteException {
		return new EnabledStateGetter().executeCommand(componentName);
	}

	public boolean isVisible(String componentName) throws QTasteException {
		return new ComponentVisibilityChecker().executeCommand(componentName);
	}

	public void takeSnapShot(final String componentName, final String fileName) throws QTasteException {
		new Snapshotter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, fileName);
	}

	public String getText(String componentName) throws QTasteException {
		return new TextGetter().executeCommand(componentName);
	}
				
	// TODO: boolean returns is useless and confusing!
	public boolean setText(final String componentName, final String value) throws QTasteException {
		return new TextSetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}	
	
	public boolean selectComponent(final String componentName, final boolean value) throws QTasteException {
		return new ComponentSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}			

	public boolean selectValue(final String componentName, final String value) throws QTasteException {
		return new ValueSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, value);
	}

	public boolean selectIndex(final String componentName, final int index) throws QTasteException {
		return new IndexSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, index);
	}

	@Override
	public boolean selectNode(String componentName, String nodeName, String nodeSeparator) throws QTasteException {
		return new TreeNodeSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, componentName, nodeName, nodeSeparator);
	}
	// Todo: getColor, awt?

	@Override
	public boolean selectTab(String tabbedPaneComponentName, int tabIndex) throws QTasteException {
		return new TabSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, tabbedPaneComponentName, tabIndex);
	}
	
	public String whoAmI() throws QTasteTestFailException {		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}		
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getName();	
	}
	

	public String getRawName(String name) throws QTasteException
	{
		return new ComponentRawNameGetter().executeCommand(name);
	}
	
	public void setComponentName(String name) throws QTasteTestFailException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().setName(name);		
	}								    

	public void pressKey(int keycode, long delay) throws QTasteTestFailException {
		new KeyPresser().executeCommand(bot, keycode, delay);
	}
	
	public void pressKey(int keycode) throws QTasteTestFailException {
		// 68 is the default delay for a keypress
		pressKey(keycode, 68);		
	}

	@Override
	public boolean exist(String pComponentName) {
		return new ExistenceChecker().executeCommand(pComponentName);
	}

	@Override
	public int getEnabledComponentCount(boolean isEnabled) {
		return new EnabledComponentCounter().executeCommand(isEnabled);
	}

	@Override
	public int countTableRows(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		return new TableRowCounter().executeCommand(COMPONENT_ENABLED_TIMEOUT, pComponentName, pColumnName, pColumnValue);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		selectInTable(pComponentName, pColumnName, pColumnValue, 0);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue, int pOccurenceIndex) throws QTasteException {
		new TableRowSelector().executeCommand(COMPONENT_ENABLED_TIMEOUT, pComponentName, pColumnName, pColumnValue, pOccurenceIndex);		
	}
	

	
	public boolean isPopupDisplayed() throws QTasteException
	{
		return new PopupChecker().executeCommand();
	}
	
	public String getPopupText() throws QTasteException
	{
		return new PopupTextGetter().executeCommand(true).get(0);
	}
	
	public String[] getAllPopupText() throws QTasteException
	{
		return new PopupTextGetter().executeCommand(false).toArray(new String[0]);
	}
	
	public void setPopupValue(String value) throws QTasteException
	{
		new PopupTextSetter().executeCommand(COMPONENT_ENABLED_TIMEOUT, value);
	}
	
	public void clickOnPopupButton(String buttonText) throws QTasteException
	{
		new PopupButtonClicker().executeCommand(COMPONENT_ENABLED_TIMEOUT, buttonText);
	}

	@Override
	public void setComponentEnabledTimeout(int pTimeOut) throws IllegalArgumentException{
		if (pTimeOut < 0)
			throw new IllegalArgumentException("Cannot set a negative timeout value. Try to set " + pTimeOut);
		
		COMPONENT_ENABLED_TIMEOUT = pTimeOut;
	}

	@Override
	public String getComponentBackgroundColor(String componentName) throws QTasteException {
		return new ComponentBackgroundColorGetter().executeCommand(componentName);
	}

	@Override
	public String getComponentForegroundColor(String componentName) throws QTasteException {
		return new ComponentForegroundColorGetter().executeCommand(componentName);
	}
	
	private static int COMPONENT_ENABLED_TIMEOUT = 10;
}
