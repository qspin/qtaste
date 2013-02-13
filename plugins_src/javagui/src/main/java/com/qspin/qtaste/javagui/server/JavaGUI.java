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

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Robot;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.text.ParseException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.tcom.jmx.impl.JMXAgent;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 *  JavaGUI is a java agent started with the same VM as the java GUI application.
 *  It implements all the JavaGUIMBean services using JMX.
 * @author lvboque
 */
public class JavaGUI extends JMXAgent implements JavaGUIMBean,
		KeyEventDispatcher {

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

	private Component getComponentByName(String name) throws QTasteTestFailException {
		// TODO: Think about several component having the same names!
		Window[] windows = Frame.getWindows();
		Component foundComponent = null;
		for (int w = 0; w < windows.length; w++) {
			Window window = windows[w];
			if (window.getName().equals(name)) {
				return window;
			}
			Component c = lookForComponent(name, window.getComponents());
			if (c != null) {
				if (!c.isEnabled()) {
					throw new QTasteTestFailException("The component \"" + name + "\" is not enabled.");
				}				
				c.requestFocus();
				foundComponent = c;				
			}
		}
		if ( foundComponent != null )
		{
			return foundComponent;
		}
		throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
	}	

	private Component lookForComponent(String name, Component[] components) {
		for (int i = 0; i < components.length; i++) {
			//String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
			Component c = components[i];
			if (c != null && c.getName() != null && c.getName().contains(name)) {
				System.out.println("Component:" + name + " is found!");
				return c;
			} else {
				if (c instanceof Container) {
					Component result = lookForComponent(name,
							((Container) c).getComponents());
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

		
	/*
	 * public boolean clickOnButton(String name) { Component c =
	 * getComponentByName(name); if (c == null) { return false; } if (c
	 * instanceof AbstractButton) { ((AbstractButton) c).doClick(); } return
	 * true; }
	 */

	public String[] listComponents() throws QTasteTestFailException {
		ArrayList<String> list = new ArrayList<String>();

		Frame[] frames = Frame.getFrames();
		for (int f = 0; f < frames.length; f++) {
			Frame frame = frames[f];
			if (frame.getName() != null) {
				list.add(frame.getName());
			}
		}
		Window[] windows = Frame.getWindows();
		for (int w = 0; w < windows.length; w++) {
			Window window = windows[w];
			if (window.getName() != null) {
				list.add(window.getName());
			}
			list.addAll(browseComponent(window.getComponents()));
		}
		String[] result = (String[]) list.toArray(new String[0]);
		return result;
	}
	

	private ArrayList<String> browseComponent(Component[] components) {
		ArrayList<String> list = new ArrayList<String>();
		for (int c = 0; c < components.length; c++) {			
			String componentName = components[c].getName();
			// System.out.println("browsing " + components[c].toString());
			// System.out.println("name=" + componentName);
			if (componentName != null) {
				//System.out.println("Component:" + componentName + " is found!");
				//if (!componentName.startsWith("null."))
					list.add(componentName);					
			}
			if (components[c] instanceof Container) {
				list.addAll(browseComponent(((Container) components[c])
						.getComponents()));
			}
			if (components[c] instanceof JPopupMenu) {
				System.out.println("detected JPopupMenu !!!!");
				JPopupMenu m = (JPopupMenu)components[c];
				list.addAll(browseComponent(m.getComponents()));
			}
		}
		return list;
	}

	public boolean keyPressedOnComponent(String componentName, int vkEvent)  throws QTasteTestFailException {
		Component c = getComponentByName(componentName);
		if (c == null) {
			return false;
		}

		System.out.println("Location on screen:" + c.getLocationOnScreen());
		KeyEvent event = new KeyEvent(c, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, vkEvent);
		try {

			java.lang.reflect.Field f = AWTEvent.class
					.getDeclaredField("focusManagerIsDispatching");
			f.setAccessible(true);
			f.set(event, Boolean.TRUE);
			c.dispatchEvent(event);
		} catch (Exception exc) {
			System.out.println("Exception sending event" + exc);
			return false;
		}

		return true;
	}

	
	public boolean clickOnButton(String componentName) throws QTasteTestFailException {
		return clickOnButton(componentName, 68);
	}

	public boolean clickOnButton(final String componentName, final int pressTime) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);		
		if (!c.isVisible())
			throw new QTasteTestFailException("Button " + componentName + " is not visible!");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					AbstractButton btn = (AbstractButton) c;					
					btn.doClick(pressTime);
				} catch (Exception exc) {
					System.out.println("Exception sending event" + exc);
		//			return false;
				}
			}
		});
		return true;
	}

	public boolean isEnabled(String componentName) throws QTasteTestFailException {
		Component c = getComponentByName(componentName);
		return c==null?false:c.isEnabled();
	}

	public String getButtonText(String componentName) throws QTasteTestFailException {
		AbstractButton c = (AbstractButton) getComponentByName(componentName);
		return c.getText();
	}

	public void takeSnapShot(final String componentName, final String fileName) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {									
					Dimension size = c.getSize();
					BufferedImage myImage = new BufferedImage(size.width, size.height,
							BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = myImage.createGraphics();
					c.paint(g2);
			
					File file = new File(fileName);
					file.createNewFile();
					System.out.println("creating empty file");
					ImageIO.write(myImage, "jpg", file);				 			
			}
			catch (Exception e) {
				System.out.println("Error saving snapshot " + fileName + ":" + e);
			}	
			}
		});
	}

	public String getText(String componentName) throws QTasteTestFailException {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JLabel) {
				return ((JLabel) c).getText();
			} else if (c instanceof JTextComponent) {
				return ((JTextComponent) c).getText();
			}
		}
		return null;
	}
				

	// TODO: boolean returns is useless and confusing!
	public boolean setText(final String componentName, final String value) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
										
				// Support for AWT
				if (c instanceof TextField) {					
					TextComponent t = (TextComponent) c;
					t.setText(value);
					forceToLooseFocus(c);						
				}
				
				// Support for Swing		
				if (c instanceof JTextComponent) {			
					System.out.println("Swing case");
					JTextComponent t = (JTextComponent) c;
					t.setText(value);
					t.getParent().requestFocus();			
				}
				
				if (c instanceof JTextComponent) {
				    if ( c instanceof JFormattedTextField ){
						try {
							JFormattedTextField field = ((JFormattedTextField)c);
							field.requestFocus();
							field.setText(value);
							//launch an exception for invalid input
							field.commitEdit();
							//lose focus to format the value
							forceToLooseFocus(c);
							} catch (ParseException e) {
								// Invalid value in field
								//return false;
								//TODO: Handle the case of invalid values
							}
						}
				    else {
				    	((JTextComponent) c).setText(value);    	
				    	((JTextComponent)c).requestFocus();		    	
					}				
				    //return true;
				}
				//throw new QTasteTestFailException("JavaGUI cannot setText for such component " + c.getClass().getName());
			}
		});
		return true;
	}
	
	
	private void forceToLooseFocus(Component c) {
		Container parent= c.getParent();
		while ( parent != null && !parent.isFocusable() )
		{
			parent.getParent();
		}
		if ( parent != null ) {
			parent.requestFocus();			
		}
	}

	public boolean selectComponent(final String componentName, final boolean value) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);
		if (c != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {												
					if (c instanceof JCheckBox) {
						((JCheckBox) c).setSelected(value);
					} else if (c instanceof JRadioButton) {
						((JRadioButton) c).setSelected(value);
					}			
				}
			});
		}
		return false;	
	}			

	public boolean selectValue(final String componentName, final String value) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JCheckBox || c instanceof JRadioButton) {
				return selectComponent(componentName, Boolean.parseBoolean(value));
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {																					
					if (c instanceof JComboBox) {
						JComboBox combo = (JComboBox) c;
						System.out.println("LVB4:" + combo.getItemCount());
						for (int i = 0; i < combo.getItemCount(); i++) {							//
							// Use a startsWith instead of equals() as toString() can return more than the value
							System.out.println("LVB4:" + combo.getItemAt(i).toString());
							if ((combo.getItemAt(i)).toString().startsWith(value)) {
								combo.setSelectedIndex(i);
								return;
							}
						}						
					}
					if (c instanceof JList) {
						JList list = (JList) c;
						for (int i = 0; i < list.getModel().getSize(); i++) {
							if (list.getModel().getElementAt(i).toString()
									.equals(value)) {
								list.setSelectedIndex(i);
								return;
							}
						}
						// TODO: Value not found! Send exception?
					}
					if (c instanceof JSpinner ) {
						JSpinner spinner = (JSpinner) c;
						try {
							spinner.getModel().setValue(Double.parseDouble(value));
						} catch(Exception pExc) {
							JOptionPane.showMessageDialog(null, pExc.getStackTrace() );
							return;
						}
						return;
					}					
					if (c instanceof JSlider ) {
						JSlider slider = (JSlider) c;
						slider.getModel().setValue(Integer.parseInt(value));
						return;
					} 
					else {
						System.out.println("component '" + c.getName() +"' ("+c.getClass()+") found but unused" );
					}
				}
			});
		}
		return false;
	}

	public boolean selectIndex(final String componentName, final int index) throws QTasteTestFailException {
		final Component c = getComponentByName(componentName);
		if (c != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {																					
					if (c instanceof JComboBox) {
						JComboBox combo = (JComboBox) c;
						if (combo.getItemCount() > index) {
							combo.setSelectedIndex(index);
							return;
						}
					}
					if (c instanceof JList) {
						JList list = (JList) c;
						if (list.getModel().getSize() > index) {
							list.setSelectedIndex(index);						
						}
					}
				}
			});
		}
		return false;
	}

	private void getAllKeys() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

	}

	public static void premain(String agentArgs, Instrumentation inst) {
		new JavaGUI();
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		System.out.println("JavaGUI received: " + e);
		return true;
	}

	@Override
	public boolean selectNode(String componentName, String nodeName,
			String nodeSeparator) throws QTasteTestFailException {
		String[] nodeNames = nodeName.split(nodeSeparator);
		Component c = getComponentByName(componentName);
		JTree tree = (JTree) c;
		if (c != null && c instanceof JTree && nodeNames.length > 0) {
			TreeModel model = tree.getModel();
			Object node = model.getRoot();
			Object[] path = new Object[nodeNames.length];
			Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
			String value = null;
			System.out.println("component is " + nodeComponent);
			if ( nodeComponent instanceof JLabel )
			{
				System.out.println("component extend JLabel");
				value = ((JLabel)nodeComponent).getText();
			} else if ( nodeComponent instanceof Label )
			{
				System.out.println("component extend TextComponent");
				value = ((Label)nodeComponent).getText();
			} else {
				System.out.println("component extend something else");
				value = node.toString();
			}
			System.out.println("compare node (" + value + ") with root (" + nodeNames[0] + ")");
			if (value.equals(nodeNames[0])) {
				path[0] = node;

				for (int i = 1; i < nodeNames.length; i++) {
					for (int childIndex = 0; childIndex < model.getChildCount(node); childIndex++) {
						Object child = model.getChild(node, childIndex);
						nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, child, true, false, true, i, false);
						value = null;
						if ( nodeComponent instanceof JLabel )
						{
							System.out.println("component extend JLabel");
							value = ((JLabel)nodeComponent).getText();
						} else if ( nodeComponent instanceof Label )
						{
							System.out.println("component extend TextComponent");
							value = ((Label)nodeComponent).getText();
						} else {
							System.out.println("component extend something else");
							value = child.toString();
						}
						System.out.println("compare node (" + value + ") with value (" + nodeNames[i] + ")");
						if (value.equals(nodeNames[i])) {
							node = child;
							path[i] = node;
							break;
						}
					}
					if (path[i] == null) {
						return false;
					}
				}
				((JTree) c).setSelectionPath(new TreePath(path));
				((JTree) c).expandPath(new TreePath(path));
				((JTree) c).setExpandsSelectedPaths(true);

				return true;
			}
		}
		return false;
	}
	// Todo: getColor, awt?

	@Override
	public boolean selectTab(String tabbedPaneComponentName, int tabIndex) throws QTasteTestFailException {
		Component c = getComponentByName(tabbedPaneComponentName);
		if (c != null && c instanceof JTabbedPane) {
			((JTabbedPane)c).setSelectedIndex(tabIndex);
			return tabIndex == ((JTabbedPane)c).getSelectedIndex();
		}
		return false;
	}
	
	public String whoAmI() throws QTasteTestFailException {		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}		
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getName();	
	}
	
	public String whereAmI() throws QTasteTestFailException {		
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
		if (bot == null)
			throw new QTasteTestFailException("JavaGUI cannot pressKey if java.awt.Robot is not available!");
		bot.keyPress(keycode);
		try {			
			Thread.sleep(delay);
		}
		catch (InterruptedException e) { 
			e.printStackTrace();
		}	
		bot.keyRelease(keycode);
	}
	
	public void pressKey(int keycode) throws QTasteTestFailException {
		// 68 is the default delay for a keypress
		pressKey(keycode, 68);		
	}
	
}
