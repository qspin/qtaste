package com.qspin.qtaste.testapi.impl.generic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testapi.api.Process;
import com.qspin.qtaste.testapi.api.ProcessStatus;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.util.InputStreamWriter;

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
	}
	
	

	@Override
	public void initialize(Map<String, String> pEnvUpdate, String workingDirectory, String... pProcessArguments) throws QTasteException {
		 mBuilder = new ProcessBuilder(Arrays.asList(pProcessArguments));
		 mBuilder.directory(new File(workingDirectory));
		 if ( pEnvUpdate != null )
		 {
			 for ( String key : pEnvUpdate.keySet() )
			 {
				 mBuilder.environment().put(key, pEnvUpdate.get(key));
			 }
		 }
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
	
	protected static final Logger LOGGER = Logger.getLogger(ProcessImpl.class);
}
