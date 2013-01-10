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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.qspin.qtaste.recorder.tray.RecorderTray;
import com.qspin.qtaste.tools.AbstractGUIAnalyzer;
import com.qspin.qtaste.tools.ComponentNamer;
import com.qspin.qtaste.tools.filter.Filter;
import com.qspin.qtaste.tools.filter.FilterXmlHandler;

public class SpyInstaller extends AbstractGUIAnalyzer {

	public static void premain(String agentArgs, Instrumentation inst) {
		new Thread(ComponentNamer.getInstance()).start();
		if (agentArgs == null)
		{
			new Thread(new SpyInstaller()).start();
		} else {
			new Thread(new SpyInstaller(agentArgs)).start();
		}
	}
	
	public SpyInstaller() {
		this(null);
	}

	public SpyInstaller(String pXmlFilterDefinitionPath) {
		super();
		mFilter = new ArrayList<RecorderFilter>();
		if ( pXmlFilterDefinitionPath != null )
		{
			FilterXmlHandler gestionnaire = new FilterXmlHandler();
			try {
				SAXParserFactory fabrique = SAXParserFactory.newInstance();
				SAXParser parseur = fabrique.newSAXParser();
				parseur.parse(pXmlFilterDefinitionPath, gestionnaire);
				for (Filter f : gestionnaire.getDecodedFilters() )
				{
					mFilter.add(new RecorderFilter(f));
				}
			} catch (IOException pExc) {
				LOGGER.error(pExc);
			} catch (SAXException pExc) {
				LOGGER.error(pExc);
			} catch (ParserConfigurationException pExc) {
				LOGGER.error(pExc);
			}
		}
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
			mSpy = new Spy(mWriter, mFilter);
			new RecorderTray(mSpy);
			mSpy.setActive(true);
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

	protected List<RecorderFilter> mFilter;
	protected BufferedWriter mWriter;
	protected Spy mSpy;
}
