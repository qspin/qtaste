package com.qspin.qtaste.structureanalyzer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import com.qspin.qtaste.tools.ComponentNamer;

/**
 * Analyzes the GUI component structure.
 */
public class Analyzer {
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Start naming thread!");
		new Thread(new ComponentNamer()).start();
		System.out.println("Start analyzer thread!");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					System.out.println("analyze thread start");
					Thread.sleep(20000);
					System.out.println("thread start analyse");
					new Analyzer().analyse();				
					System.out.println("thread stop analyse");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	protected Analyzer()
	{
		
	}
	
	protected final void analyse()
	{
		try
		{
			prepareWriter();
			System.out.println("number of windows : " + Frame.getWindows().length);
			for ( int i=0; i<Frame.getWindows().length; ++i )
			{ 
				Window windows = Frame.getWindows()[i];
				analyzeComponent(windows, 0);
			}
			mWriter.write("</body></html>");
			mWriter.flush();
			mWriter.close();
		} catch (IOException pExc) {
			pExc.printStackTrace();
		} finally {
			
		}
	}

	protected void analyzeComponent(Component pComponent, int pLevel) throws IOException
	{
		Class<?> componentClass = pComponent.getClass();
		String componentName = componentClass.getName().replace(componentClass.getPackage().getName() +".", "");
		componentName = componentName.replace('$', '_');
		writeComponent("<class>" + componentClass.getName() + "</class>", pLevel);
		writeComponent("<name>" + pComponent.getName() + "</name>", pLevel);
		if ( pComponent instanceof Container && ((Container)pComponent).getComponentCount() > 0 )
		{
			writeComponent("<childs>", pLevel);
			for ( int i=0; i<((Container)pComponent).getComponentCount(); ++i )
			{
				writeComponent("<child>", pLevel+1);
				Component c = ((Container)pComponent).getComponent(i);
				analyzeComponent(c, pLevel+2);
				writeComponent("</child>", pLevel+1);
			}
			writeComponent("</childs>", pLevel);
		}
	}

	protected void prepareWriter() throws IOException {
		mWriter = new BufferedWriter( new FileWriter("struct.xml"));
		mWriter.write("<html><body>");
		mWriter.newLine();
	}

	protected void writeComponent(String pText, int pLevel) throws IOException {
		StringBuilder builder = new StringBuilder();
		for ( int i=0; i<pLevel; ++i){
			builder.append("   ");
		}
		builder.append(pText);
		mWriter.write( builder.toString());
		mWriter.newLine();
	}
	
	protected BufferedWriter mWriter;
}
