package com.qspin.qtaste.recorder;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SpyInstaller implements Runnable, ContainerListener {

	public static void premain(String agentArgs, Instrumentation inst) {
		new Thread(new SpyInstaller()).start();
	}
			
	@Override
	public void run() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("spyRepport.xml"));
			writer.write("<events>\n");
			while (true) 
			{
				if (mSpy == null ) {
					mSpy = new Spy(writer);
				}
				deploy();
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SpyInstaller() {
		mSpiedWindowList = new ArrayList<Window>();
	}
	
	public void deploy()
	{
		for ( int i=0; i<Frame.getWindows().length; ++i )
		{ 
			Window windows = Frame.getWindows()[i];
			if ( !mSpiedWindowList.contains(windows) )
			{
				if (windows.getName() == null || windows.getName().equalsIgnoreCase("null")) {
					windows.setName("window_"+i);
				}
				deployOnComponent(windows);
				LOGGER.info("New window spied");
				mSpiedWindowList.add(windows);
			}
		}
	}
	
	protected void deployOnComponent(Component pComponent)
	{
		if ( pComponent instanceof Container && ((Container)pComponent).getComponentCount() > 0 )
		{
			((Container)pComponent).addContainerListener(this);
			for ( int i=0; i<((Container)pComponent).getComponentCount(); ++i )
			{
				Component c = ((Container)pComponent).getComponent(i);
				if (c.getName() == null || c.getName().equalsIgnoreCase("null") ) {
					c.setName(pComponent.getName() + "_child"+i);
				}
				deployOnComponent(c);
			}
		}
		mSpy.addTarget(pComponent); 
	}
	@Override
	public synchronized void componentAdded(ContainerEvent e) {
		deployOnComponent(e.getComponent());
	}

	@Override
	public synchronized void componentRemoved(ContainerEvent e) {
		for ( Spy s : e.getComponent().getListeners(Spy.class) ) {
			s.removeTarget(e.getComponent());
		}
		if ( e.getComponent() instanceof Container )
		{
			((Container)e.getComponent()).removeContainerListener(this);
		}
	}

	protected List<Window> mSpiedWindowList;
	protected Spy mSpy;
	protected static final Logger LOGGER = Logger.getLogger(SpyInstaller.class);
}
