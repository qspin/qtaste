package com.qspin.qtaste.testapi.impl.demo;

import java.io.IOException;

import com.qspin.qtaste.testapi.api.LinuxProcess;
import com.qspin.qtaste.testapi.api.ProcessStatus;
import com.qspin.qtaste.testapi.impl.generic.ProcessImpl;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.util.OS;

public class LinuxProcessImpl extends ProcessImpl implements LinuxProcess {

	public LinuxProcessImpl(String pInstanceId) throws QTasteException {
		super(pInstanceId);
		if ( OS.getType() != OS.Type.LINUX )
			throw new QTasteTestFailException("Cannot create a Linux process on a non Linux operating system.");
	}

	@Override
	public void killProcess() throws QTasteException {
		if (getStatus() != ProcessStatus.RUNNING)
			throw new QTasteTestFailException("Unable to stop a non running process.");
		try
		{
			Runtime.getRuntime().exec("kill " + getPid());
			Thread.sleep(1000);
			if ( searchPid() != -1 )
				throw new QTasteTestFailException("The process is still running.");
		}
		catch (IOException pException)
		{
			LOGGER.error("Unable to kill the process :" + pException.getMessage(), pException);
		} catch (InterruptedException pException) {
			LOGGER.error("Unable to kill the process with a sigkill : " + pException.getMessage(), pException);
		}
	}

	@Override
	public void killProcessWithSigKill() throws QTasteException {
		if (getStatus() != ProcessStatus.RUNNING)
			throw new QTasteTestFailException("Unable to stop a non running process.");
		try
		{
			Runtime.getRuntime().exec("kill -9 " + getPid());
			Thread.sleep(1000);
			if ( searchPid() != -1 )
				throw new QTasteTestFailException("The process is still running.");
		}
		catch (IOException pException)
		{
			LOGGER.error("Unable to kill the process with a sigkill : " + pException.getMessage(), pException);
		} catch (InterruptedException pException) {
			LOGGER.error("Unable to kill the process with a sigkill : " + pException.getMessage(), pException);
		}
	}

}
