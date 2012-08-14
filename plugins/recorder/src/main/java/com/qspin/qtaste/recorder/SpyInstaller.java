package com.qspin.qtaste.recorder;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.qspin.qtaste.tools.AbstractGUIAnalyzer;
import com.qspin.qtaste.tools.ComponentNamer;

public class SpyInstaller extends AbstractGUIAnalyzer {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Start naming thread!");
		new Thread(new ComponentNamer()).start();
		System.out.println("Start spy thread!");
		new Thread(new SpyInstaller()).start();
	}
			
	public SpyInstaller() {
		super();
	}

	@Override
	public synchronized void componentRemoved(ContainerEvent e) {
		super.componentRemoved(e);
		if(  e.getChild() != null )
		{
			for ( Spy s : e.getChild().getListeners(Spy.class) ) {
				s.removeTarget(e.getChild());
			}
		}
	}
	
	@Override
	protected boolean preProcess() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
			mWriter = new BufferedWriter(new FileWriter("spyRepport_"+ format.format(new Date().getTime()) +".xml"));
			mWriter.write("<events>\n");
			mSpy = new Spy(mWriter);
			return true;
		} catch(IOException pExc)
		{
			LOGGER.error(pExc);
			return false;
		}
	}

	@Override
	protected void processComponent(Component pComponent) {
		mSpy.addTarget(pComponent);
	}

	@Override
	protected boolean postProcess() {
		if ( mWriter != null ){
			try {
				mWriter.write("</events>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		IOUtils.closeQuietly(mWriter);
		return true;
	}

	protected BufferedWriter mWriter;
	protected Spy mSpy;
}
