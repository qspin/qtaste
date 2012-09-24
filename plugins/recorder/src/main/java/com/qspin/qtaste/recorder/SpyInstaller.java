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
		new Thread(new ComponentNamer()).start();
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
