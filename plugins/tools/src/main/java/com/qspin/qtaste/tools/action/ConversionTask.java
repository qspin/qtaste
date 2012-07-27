package com.qspin.qtaste.tools.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.factory.python.PythonEventFactory;
import com.qspin.qtaste.tools.model.EventManager;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.EventComparator;

public class ConversionTask implements Runnable {

	public ConversionTask()
	{
		mListener = new ArrayList<ProgressListener>();
	}
	
	public void setAcceptedComponentName(List<Object> pComponentName)
	{
		mAcceptedComponentName = pComponentName == null ? new ArrayList<Object>() : pComponentName;
	}
	
	public void setAcceptedEventType(List<Object> pEventTypes)
	{
		mAcceptedEventType = pEventTypes == null ? new ArrayList<Object>() : pEventTypes;
	}
	
	public void setOutputDirectory(String pOutputDirectory) throws IOException
	{
		mOutputDirectory = new File(pOutputDirectory);
		if( !mOutputDirectory.mkdirs() && !mOutputDirectory.exists() )
		{
			throw new IOException("Unable to create the output directory");
		}
	}
	
	@Override
	public void run() {
		File outputFile = new File(mOutputDirectory, OUTPUT_PYTHON_FILE_NAME);
		FileWriter fw = null;
		BufferedWriter writer = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		try
		{
			LOGGER.info("Conversion start");
			fireProgress(-1, "Retreives concerned event(s)");
			List<Event> events = retrieveEvents();
			LOGGER.info("Conversion of " + events.size() + " of " + EventManager.getInstance().getEvents().size() + " event(s)");
			if ( !events.isEmpty() ) 
			{
				fw = new FileWriter(outputFile, false);
				writer = new BufferedWriter(fw);
				isr = new InputStreamReader(ConversionTask.class.getResourceAsStream(PYTHON_TEMPLATE_PATH));
				reader = new BufferedReader(isr);
				writeHeader(reader, writer);
				for ( int i=0; i< events.size(); ++i)
				{
					writer.write( PythonEventFactory.createEvent(events.get(i)));
					writer.newLine();
				}
				writeFooter(reader, writer);
			}
			
		}
		catch (IOException pExc)
		{
			LOGGER.error(pExc);
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(fw);
			LOGGER.info("Conversion complete");
			fireProgress(1, "Conversion stop");
		}
	}
	
	public void addProgressListener(ProgressListener pListener)
	{
		mListener.add(pListener);
	}
	
	public void removeProgressListener(ProgressListener pListener)
	{
		mListener.remove(pListener);
	}
	
	protected void fireProgress(double pProgress, String pMessage)
	{
		for (ProgressListener listener : mListener)
		{
			listener.progressReceived(pProgress, pMessage);
		}
	}
	
	protected List<Event> retrieveEvents()
	{
		List<Event> concernedEvent = new ArrayList<Event>();
		for ( Object componentName : mAcceptedComponentName )
		{
			concernedEvent.addAll(EventManager.getInstance().getEventsForComponent(componentName.toString()));
		}
		int i=0;
		while (i<concernedEvent.size())
		{
			if ( mAcceptedEventType.contains(concernedEvent.get(i).getType()) )
			{
				++i;
			} else {
				concernedEvent.remove(i);
			}
		}
		Collections.sort(concernedEvent, new EventComparator());
		return concernedEvent;
	}
	
	protected void writeHeader(BufferedReader pReader, BufferedWriter pWriter) throws IOException
	{
		String line = pReader.readLine();
		while ( line != null && !line.trim().equals("pass") )
		{
			pWriter.write(line);
			pWriter.newLine();
			line = pReader.readLine();
		}
	}
	
	protected void writeFooter(BufferedReader pReader, BufferedWriter pWriter) throws IOException
	{
		String line = pReader.readLine();
		while ( line != null )
		{
			pWriter.write(line);
			pWriter.newLine();
			line = pReader.readLine();
		}
	}

	private List<ProgressListener> mListener;
	private List<Object> mAcceptedComponentName;
	private List<Object> mAcceptedEventType;
	private File mOutputDirectory;
	
	private static final Logger LOGGER = Logger.getLogger(ConversionTask.class);
	private static final String OUTPUT_PYTHON_FILE_NAME = "TestScript.py";
	private static final String PYTHON_TEMPLATE_PATH = "/" + OUTPUT_PYTHON_FILE_NAME;
}
