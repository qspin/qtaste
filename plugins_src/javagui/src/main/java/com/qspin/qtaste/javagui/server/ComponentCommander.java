package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * 
 * Class to perform an action on a Component.
 * @author simjan
 *
 */
abstract class ComponentCommander {

	/**
	 * Executes the a command on a component.
	 * @param data an array of Object
	 * @return true if the command is successfully performed.
	 * @throws QTasteException
	 */
	abstract Object executeCommand(Object... data) throws QTasteException;
	
	/** used for logging. */
	protected static final Logger LOGGER = Logger.getLogger(ComponentCommander.class);
	
	/**
	 * Retrieve the GUI component base on its name.
	 * @param name the GUI component's name.
	 * @return the found GUI component.
	 * @throws QTasteTestFailException If no GUI component is found.
	 */
	protected Component getComponentByName(String name) throws QTasteException {
		mFoundComponent = null;
		mFindWithEqual = false;
		LOGGER.debug("try to find a component with the name : " + name);
		// TODO: Think about several component having the same names!
		for (Window window: Window.getWindows()) {
			if (mFindWithEqual) {
				break;
			}
			if ( !checkName(name, window) || !mFindWithEqual ) {
				LOGGER.debug("parse window");
				lookForComponent(name, window.getComponents());
			}
		}
		if ( mFoundComponent != null )
		{
			mFoundComponent.requestFocus();
			Component parent = mFoundComponent.getParent();
			//active the parent
			while ( parent != null && !(parent instanceof Window) )
			{
				parent = parent.getParent();
			}
			if ( parent != null )
				((Window)parent).toFront();
			
			return mFoundComponent;
		}
		throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
	}

	/**
	 * Browses recursively the components in order to find components with the name.
	 * @param name the component's name.
	 * @param components components to browse.
	 * @return the first component with the name.
	 */
	protected Component lookForComponent(String name, Component[] components) {
		for (int i = 0; i < components.length && !mFindWithEqual; i++) {
			//String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
			Component c = components[i];
			checkName(name, c);
			if ( !mFindWithEqual )
			{
				if (c instanceof Container) {
					Component result = lookForComponent(name, ((Container) c).getComponents());
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}
	
	protected boolean checkName(String name, Component c)
	{
		if (c != null && c.getName() != null)
		{
			if ( c.getName().contains(name) ) {
				mFoundComponent = c;
				if ( c.getName().equals(name) )
				{
					mFindWithEqual = true;
					LOGGER.debug("Component:" + name + " is found!");
				} else {
					LOGGER.debug("Component:" + name + " is (maybe) found! (component's name : " + mFoundComponent.getName() + ")");
				}
				return true;
			}
		}
		return false;
	}
	
	protected boolean mFindWithEqual;
	protected Component mFoundComponent;

	/**
	 * Finds all popups. A Component is a popup if it's a JDialog, modal and not resizable.
	 * @return the list of all found popups.
	 */
	protected static List<JDialog> findPopups()
	{
		//find all popups
		List<JDialog> popupFound = new ArrayList<JDialog>();
		for (Window window: Window.getWindows()) {
//			LOGGER.debug("parse window - type : " + window.getClass());
			if ( isAPopup(window) )
			{
				//it's maybe a popup... a popup is modal and not resizable and containt a JOptionPane component.
				JDialog dialog = (JDialog) window;
				LOGGER.info("Find a popup with the title '" + dialog.getTitle() +"'.");
				popupFound.add(dialog);
			}
		}
		return popupFound;
	}
	
	protected static boolean hasTheFocus(Component c)
	{
		if ( c != null )
		{
			if ( c.isFocusOwner() )
				return true;
			else if ( c instanceof Container )
			{
				for ( int i = 0; i < ((Container)c).getComponentCount(); i++ )
				{
					if ( hasTheFocus(((Container)c).getComponent(i)) )
						return true;
				}
			}
		}
		return false;
	}
	
	protected static boolean isAPopup(Component c)
	{
		if ( c == null )
		{
			LOGGER.trace( "The given component is null!");
			return false;
		}
		if ( !(c instanceof JDialog) )
		{
			LOGGER.trace( "The given component is not a JDialog!");
			return false;
		}
		JDialog dialog = (JDialog)c;

		if ( !dialog.isShowing() ) 
		{
			LOGGER.info("The given component is not displayed!");
			return false;
		}
		if ( !dialog.isModal() ) 
		{
			LOGGER.info("The given component is not modal!");
			return false;
		}
		if ( dialog.isResizable() ) 
		{
			LOGGER.info("The given component is rezisable!");
			return false;
		}
		if ( getJOptionPane(dialog) == null ) 
		{
			LOGGER.info("The given component does not contain any JOptionPane!");
			return false;
		}
		return true;
	}
	
	protected static JOptionPane getJOptionPane(Component c)
	{
		if ( c instanceof JOptionPane )
			return (JOptionPane)c;
		else if (c instanceof Container )
		{
			for ( Component comp :  ((Container)c).getComponents() )
			{
				JOptionPane jop = getJOptionPane(comp); 
				if ( jop != null )
					return jop;
			}
		}
		return null;		
	}
	
	/**
	 * Try to active the window containing the component.
	 * @param c the component contained in the window to active.
	 * @return <code>true</code> only if the parent window is active at the end of the activation process.
	 */
	protected boolean setComponentFrameVisible(Component c)
	{
		Component parent = c;
		//active the parent window
		while ( !(parent instanceof Window) )
		{
			parent = parent.getParent();
		}
			    
		if ( !((Window)parent).isActive() )
		{
			LOGGER.trace("try to active the window of '" + c.getName() + "' cause its window is not active");
			JFrame newFrame = new JFrame();
			newFrame.pack();
			newFrame.setVisible(true);
			newFrame.toFront();
	    	newFrame.setVisible(false);
	    	newFrame.dispose();
			((Window)parent).toFront();
			try {
				LOGGER.trace("current focused window : " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow());
				((Window)parent).requestFocus();
				Thread.sleep(1000);
				LOGGER.trace("focused window after request: " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean parentActiveState = !((Window)parent).isActive();
			LOGGER.trace("parent active state ? " + parentActiveState );
			if (!parentActiveState)
			{
				LOGGER.warn("The window activation process failed!!!");
				return false;
			} else {
				LOGGER.trace("The window activation process is completed!!!");
			}
		} else {
			LOGGER.trace("the parent window of '" + c.getName() + "' is already active");
		}
		return true;
	}
}
