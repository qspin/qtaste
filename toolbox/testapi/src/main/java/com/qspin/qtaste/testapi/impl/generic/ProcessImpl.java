package com.qspin.qtaste.testapi.impl.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testapi.api.Process;
import com.qspin.qtaste.testapi.api.ProcessStatus;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.util.InputStreamWriter;
import com.qspin.qtaste.util.OS;

public class ProcessImpl implements Process {

	public ProcessImpl(String pInstanceId) throws QTasteException
	{
		mInstanceId = pInstanceId;
		initialize();
	}
	
	@Override
	public String getInstanceId() {
		return mInstanceId;
	}

	@Override
	public void initialize() throws QTasteException {
		mCurrentProcess = null;
		mStatus = ProcessStatus.UNDEFINED;
	}

	@Override
	public void terminate() throws QTasteException {
		if ( getStatus() == ProcessStatus.RUNNING )
		{
			stop();
		}
	}

	@Override
	public void start() throws QTasteException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					mCurrentProcess = mBuilder.start();
					mStatus = ProcessStatus.RUNNING;
					mStdLogs = new InputStreamWriter(mCurrentProcess.getInputStream());
					new Thread(mStdLogs).start();
					mErrLogs = new InputStreamWriter(mCurrentProcess.getErrorStream());
					new Thread(mErrLogs).start();
					mReturnCode = mCurrentProcess.waitFor();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
				}
				finally
				{
					mStatus = ProcessStatus.STOPPED;
				}
			}
		}).start();
		mPid = searchPid();
	}
	
	/**
	 * Searches the process'identifier of the current process. If none found, return -1.
	 * <br/> Only available for Unix process.
	 * @return the process'identifier or -1 if none found.
	 */
	protected int searchPid()
	{
		if ( OS.getType() != OS.Type.LINUX )
		{
			LOGGER.warn("Unable to retreive the process pid on a non unix system.");
			return -1;
		}
		
		List<String> lines = new ArrayList<String>();
		
		//rebuild the process command
		String cmd = "";
		for (int i=0; i<mParameters.length; i++)
		{
			if ( i > 0 )
				cmd += " ";
			cmd += mParameters[i];
		}
		
		//use ps command to list all process and filter on the process command
		try
		{
			java.lang.Process myProcess = Runtime.getRuntime().exec( "ps -eo pid,command" );  
            BufferedReader stdout = new BufferedReader( new InputStreamReader( myProcess.getInputStream() ) ) ;  
            String line = stdout.readLine();
            while ( line != null )  
            {
            	if (line.contains(cmd) && !lines.contains(line))
            	{
					lines.add(line);
            	}
            	line = stdout.readLine();
            }  
            myProcess.waitFor() ;  
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		//get the last process and retrieve the pid (first value of the line)
		if ( lines.size() >= 1 )
		{
			String line = lines.get(lines.size()-1).trim();
			for (String part : line.split(" "))
			{
				System.out.println(part);
			}
			return Integer.parseInt(line.split(" ")[0]);
		}
		else
		{
			LOGGER.warn("unable to find the process pid");
			return -1;
		}
	}

	@Override
	public void initialize(String... pProcessArguments) throws QTasteException {
		 mBuilder = new ProcessBuilder(Arrays.asList(pProcessArguments));
		 mParameters = pProcessArguments;
		 mStatus = ProcessStatus.READY_TO_START;
	}

	@Override
	public void setStdOutLimit(int pLimit) {
		mStdLogs.setBufferLimit(pLimit);
	}

	@Override
	public void setStdErrLimit(int pLimit) {
		mErrLogs.setBufferLimit(pLimit);
	}

	@Override
	public ProcessStatus getStatus() throws QTasteException {
		return mStatus;
	}

	@Override
	public void stop() throws QTasteException {
		if (getStatus() != ProcessStatus.RUNNING)
		{
			throw new QTasteException("Invalide state. Cannot stop a non running process.");
		}
		mCurrentProcess.destroy();
	}

	@Override
	public int getPid() throws QTasteException {
		if (getStatus() != ProcessStatus.RUNNING)
		{
			throw new QTasteException("Invalide state. Cannot retrieve tha pid of a non running process.");
		}
		return mPid;
	}

	@Override
	public int getExitCode() throws QTasteException {
		if ( getStatus() != ProcessStatus.STOPPED )
		{
			throw new QTasteException("Invalide state. The process is not terminated.");
		}
		return mReturnCode;
	}

	@Override
	public List<String> getStdOut() throws QTasteException {
		if ( getStatus() != ProcessStatus.RUNNING && getStatus() != ProcessStatus.STOPPED )
		{
			throw new QTasteException("Invalide state. The process is not yet started.");
		}
		return mStdLogs.getLogs();
	}

	@Override
	public List<String> getStdErr() throws QTasteException {
		if ( getStatus() != ProcessStatus.RUNNING && getStatus() != ProcessStatus.STOPPED )
		{
			throw new QTasteException("Invalide state. The process is not yet started.");
		}
		return mErrLogs.getLogs();
	}
	
	protected String mInstanceId;
	protected ProcessBuilder mBuilder;
	protected java.lang.Process mCurrentProcess;
	protected String[] mParameters;
	protected ProcessStatus mStatus;
	protected InputStreamWriter mStdLogs;
	protected InputStreamWriter mErrLogs;
	protected int mReturnCode;
	protected int mPid;
	
	protected static final Logger LOGGER = Logger.getLogger(ProcessImpl.class);
}
