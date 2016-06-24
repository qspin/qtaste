package com.qspin.qtaste.testapi.api;

import java.util.List;
import java.util.Map;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 * This test APi is responsible to check the start/stop sequence of a process.
 * It offers the possibility to retrieve the process's outputs and process's exit status.
 *
 * @author simjan
 */
public interface Process extends MultipleInstancesComponent {

    /**
     * Starts the previously initialized process.
     *
     * @throws QTasteException if no "ready to run" process exist.
     * @see #getStatus()
     * @see ProcessStatus
     */
    void start() throws QTasteException;

    /**
     * Initialized a process with the given parameters.
     *
     * @param pEnvUpdate A map filled with the environment variable to update. Can be <code>null</code>.
     * @param pWorkingDirectory The process working directory path.
     * @param pProcessArguments Process's arguments.
     * @throws QTasteException if a process is already defined.
     */
    void initialize(Map<String, String> pEnvUpdate, String pWorkingDirectory, String... pProcessArguments) throws QTasteException;

    /**
     * Defined the maximal number of line saved for the standard output.
     * Negative value: no limit; 0: save nothing; positive value: the new limit.
     *
     * @param pLimit The number of line to save.
     */
    void setStdOutLimit(int pLimit);

    /**
     * Defined the maximal number of line saved for the standard error output.
     * Negative value: no limit; 0: save nothing; positive value: the new limit.
     *
     * @param pLimit The number of line to save.
     */
    void setStdErrLimit(int pLimit);

    /**
     * Returns the status of the process. If no process are specified, return {@link ProcessStatus#UNDEFINED}.
     *
     * @return The status of the process. If no process are specified, return {@link ProcessStatus#UNDEFINED}.
     * @throws QTasteException
     */
    ProcessStatus getStatus() throws QTasteException;

    /**
     * Stops the running process.
     *
     * @throws QTasteException if no running process exist.
     */
    void stop() throws QTasteException;

    /**
     * Returns the exit code of the process.
     *
     * @return the processs's exit code.
     * @throws QTasteException if the process has not ended.
     */
    int getExitCode() throws QTasteException;

    /**
     * Returns the process's standard outputs.
     *
     * @return the process's logs.
     * @throws QTasteException If the process is not running or stopped.
     */
    List<String> getStdOut() throws QTasteException;

    /**
     * Returns the process's standard error outputs.
     *
     * @return the process's logs.
     * @throws QTasteException If the process is not running or stopped.
     */
    List<String> getStdErr() throws QTasteException;
}
