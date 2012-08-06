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
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SpyInstaller implements Runnable, ContainerListener {

	public static void premain(String agentArgs, Instrumentation inst) {
		new Thread(new SpyInstaller()).start();
	}
			
	@Override
	public void run() {
		mWriter = null;
		try {
			mWriter = new BufferedWriter(new FileWriter("spyRepport_"+ new Date().getTime() +".xml"));
			mWriter.write("<events>\n");
			while (true) 
			{
				if (mSpy == null ) {
					mSpy = new Spy(mWriter);
				}
				deploy();
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if ( mWriter != null ){
				try {
					mWriter.write("</events>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			IOUtils.closeQuietly(mWriter);
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

	protected BufferedWriter mWriter;
	protected List<Window> mSpiedWindowList;
	protected Spy mSpy;
}
