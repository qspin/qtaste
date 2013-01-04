package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.testsuite.QTasteException;

public interface LinuxProcess extends Process {

	/**
	 * Tries to stop a process with the kill command.
	 * @throws QTasteException if the process is not running.
	 */
	void killProcess() throws QTasteException;
	/**
	 * Tries to stop a process with the kill -9 command.
	 * @throws QTasteException if the process is not running.
	 */
	void killProcessWithSigKill() throws QTasteException;
	
}
