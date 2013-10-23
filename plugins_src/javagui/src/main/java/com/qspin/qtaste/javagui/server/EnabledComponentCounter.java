package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * Component asker responsible to count the number of GUI component that are enabled/disabled.
 * 
 * @author simjan
 *
 */
class EnabledComponentCounter extends ComponentCommander {

	/**
	 * @param data a string representing the boolean enabled state.
	 * @return the number of GUI component with the given enabled state.
	 */
	@Override
	Integer executeCommand(Object... data) {
		int counter = 0;
		List<Container> superContainers = new ArrayList<Container>();
		for( Frame f : Frame.getFrames() )
		{
			if (!superContainers.contains(f))
			{
				superContainers.add(f);
			}
		}
		for( Window w : Window.getWindows() )
		{
			if (!superContainers.contains(w))
			{
				superContainers.add(w);
			}
		}
		for( Window w : Window.getOwnerlessWindows() )
		{
			if (!superContainers.contains(w))
			{
				superContainers.add(w);
			}
		}
		
		boolean isEnable = Boolean.parseBoolean(data[0].toString());
		for ( Container c : superContainers )
		{
			counter += getEnabledComponentCount(isEnable, c);
		}
		return counter;
	}
	
	protected int getEnabledComponentCount(boolean isEnabled, Container c)
	{
		int counter = 0;
		if ( c.isEnabled() == isEnabled )
		{
			counter ++;
		}
		for (int i=0; i<c.getComponentCount(); i++)
		{
			Component child = c.getComponent(i);
			if ( c instanceof Container )
			{
				counter += getEnabledComponentCount(isEnabled, (Container)child);
			}
			else
			{
				counter += child.isEnabled() == isEnabled? 1 : 0;
			}
		}
		return counter;
	}
}
