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
		killProcessWithSignal(-1);
	}

	@Override
	public void killProcessWithSignal(int pSignal) throws QTasteException {
		if (getStatus() != ProcessStatus.RUNNING)
			throw new QTasteTestFailException("Unable to stop a non running process.");
		try
		{
			String command = "kill ";
			if (pSignal > 0)
				command += "-" + pSignal + " ";
			command +=  getPid();
			LOGGER.trace("Kill the process " + getInstanceId() + " with the command : " + command);
			Runtime.getRuntime().exec(command);
			Thread.sleep(1000);
			LOGGER.trace("Process " + getInstanceId() + " status : " + getStatus());
			if ( getStatus() != ProcessStatus.STOPPED )
				throw new QTasteTestFailException("The process " + getInstanceId() + " is still running.");
		}
		catch (IOException pException)
		{
			LOGGER.error("Unable to kill the process " + getInstanceId() + " : " + pException.getMessage(), pException);
		} catch (InterruptedException pException) {
			LOGGER.error("Unable to kill the process " + getInstanceId() + " : " + pException.getMessage(), pException);
		}
	}

}
