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

	public String[] listComponents() throws QTasteTestFailException {
		return new ComponentLister().executeCommand();
	}

	public boolean clickOnButton(String componentName) throws QTasteTestFailException {
		return clickOnButton(componentName, 68);
	}

	public boolean clickOnButton(final String componentName, final int pressTime) throws QTasteTestFailException {
		return new ButtonClicker().executeCommand(componentName, pressTime);
	}

	public boolean isEnabled(String componentName) throws QTasteTestFailException {
		return new EnabledStateGetter().executeCommand(componentName);
	}

	public void takeSnapShot(final String componentName, final String fileName) throws QTasteTestFailException {
		new Snapshotter().executeCommand(componentName, fileName);
	}

	public String getText(String componentName) throws QTasteTestFailException {
		return new TextGetter().executeCommand(componentName);
	}
				
	// TODO: boolean returns is useless and confusing!
	public boolean setText(final String componentName, final String value) throws QTasteTestFailException {
		return new TextSetter().executeCommand(componentName, value);
	}	
	
	public boolean selectComponent(final String componentName, final boolean value) throws QTasteTestFailException {
		return new ComponentSelector().executeCommand(componentName, value);
	}			

	public boolean selectValue(final String componentName, final String value) throws QTasteTestFailException {
		return new ValueSelector().executeCommand(componentName, value);
	}

	public boolean selectIndex(final String componentName, final int index) throws QTasteTestFailException {
		return new IndexSelector().executeCommand(componentName, index);
	}

	@Override
	public boolean selectNode(String componentName, String nodeName, String nodeSeparator) throws QTasteTestFailException {
		return new TreeNodeSelector().executeCommand(componentName, nodeName, nodeSeparator);
	}
	// Todo: getColor, awt?

	@Override
	public boolean selectTab(String tabbedPaneComponentName, int tabIndex) throws QTasteTestFailException {
		return new TabSelector().executeCommand(tabbedPaneComponentName, tabIndex);
	}
	
	public String whoAmI() throws QTasteTestFailException {		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}		
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getName();	
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
		return new TableRowCounter().executeCommand(pComponentName, pColumnName, pColumnValue);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue) throws QTasteException {
		selectInTable(pComponentName, pColumnName, pColumnValue, 0);
	}

	@Override
	public void selectInTable(String pComponentName, String pColumnName, String pColumnValue, int pOccurenceIndex) throws QTasteException {
		new TableRowSelector().executeCommand(pComponentName, pColumnName, pColumnValue, pOccurenceIndex);		
	}
	
}
