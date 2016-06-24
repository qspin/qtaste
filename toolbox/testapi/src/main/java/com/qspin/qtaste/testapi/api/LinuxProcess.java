package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.testsuite.QTasteException;

public interface LinuxProcess extends Process {

    /**
     * Tries to stop a process with the kill command.
     *
     * @throws QTasteException if the process is not running.
     */
    void killProcess() throws QTasteException;

    /**
     * Tries to stop a process with the kill -9 command.
     *
     * @param pSignal the numeric value of the signal. For example, 9 for a sigKill.
     * @throws QTasteException if the process is not running.
     */
    void killProcessWithSignal(int pSignal) throws QTasteException;

    /**
     * Returns the process's identifier.
     *
     * @return the process's identifier.
     * @throws QTasteException If the process is not running.
     */
    int getPid() throws QTasteException;

}
