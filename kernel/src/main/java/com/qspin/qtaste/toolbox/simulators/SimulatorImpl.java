/*
    Copyright 2007-2009 QSpin - www.qspin.be

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

package com.qspin.qtaste.toolbox.simulators;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.PythonHelper;

/**
 * Base class for QTaste simulators.
 * 
 * @author David Ergo
 */
public class SimulatorImpl implements SimulatorMBean {

    private static final Logger LOGGER = Log4jLoggerFactory.getLogger(SimulatorImpl.class);
    private Timer mTimer = new Timer("QTaste Simulator Thread"); // timer for task scheduling
    private PythonInterpreter mInterpreter; // Python interpreter
    private PyObject mPySimulator; // internal Python simulator instance
    /**
     * Internal simulator instance, which may be the default java implementation or the instance created in the Python script.
     */
    protected Object mSimulator;

    
    /**
     * Constructs a new instance.
     */
    public SimulatorImpl() {
        // initialize Python interpreter
        Properties properties = new Properties();
        properties.setProperty("python.home", StaticConfiguration.JYTHON_HOME);
        properties.setProperty("python.path", StaticConfiguration.JYTHON_LIB);
        PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});

        // initialize the shutdown hook to terminate application properly
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread ());

        if (OS.getType() == OS.Type.WINDOWS)
        {
           // create hidden window to handle WM_CLOSE messages on Windows to react to taskkill
           JFrame frame = new JFrame();
           frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
           frame.pack();
        }
    }
    
    /**
     * Shuts down properly. 
     * This method is typically called from a shutdown hook to terminate the simulator properly.
     * <p>
     * Cancels the timer thread used for task scheduling.
     * Overloading implementations should call this method AFTER doing its own work.
     */
    protected void shutdown() {
        LOGGER.info("Shutting down SimulatorImpl");
        LOGGER.info("Cancelling timer thread");
        mTimer.cancel();
        mTimer = null;
        mSimulator = mPySimulator = null;
        mInterpreter = null;
    }

    /**
     * Sets the internal simulator instance (Python or default java implementation).
     * This method is called from the default java implementation or from within the Python script.
     * 
     * @param simulator the simulator instance.
     */
    public void setSimulator(Object simulator) {
        this.mSimulator = simulator;
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     * 
     * @param task Python object callable without arguments, to schedule
     * @param delay delay in seconds before task is to be executed
     */
    public void scheduleTask(PyObject task, double delay) {
        mTimer.schedule(new PythonCallTimerTask(task), Math.round(delay * 1000));
    }

    /**
     * Schedules the specified task for repeated <i>fixed-delay execution</i>, beginning after the specified delay. 
     * Subsequent executions take place at approximately regular intervals separated by the specified period.
     * <p>
     * See {@link Timer} documentation for more information.
     * 
     * @param task Python object callable without arguments, to schedule
     * @param delay delay in seconds before task is to be executed
     * @param period time in seconds between successive task executions
     */
    public void scheduleTask(PyObject task, double delay, double period) {
        mTimer.schedule(new PythonCallTimerTask(task), Math.round(delay * 1000), Math.round(period * 1000));
    }

    /**
     * Schedules the specified task for repeated <i>fixed-rate execution</i>, beginning after the specified delay. 
     * Subsequent executions take place at approximately regular intervals, separated by the specified period. 
     * <p>
     * See {@link Timer} documentation for more information.
     * 
     * @param task Python object callable without arguments, to schedule
     * @param delay delay in seconds before task is to be executed
     * @param period time in seconds between successive task executions
     */
    public void scheduleTaskAtFixedRate(PyObject task, double delay, double period) {
        mTimer.scheduleAtFixedRate(new PythonCallTimerTask(task), Math.round(delay * 1000), Math.round(period * 1000));
    }

    public void setScriptFile(String filename) throws FileNotFoundException, IOException, Exception {
        File f = new File(filename);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        byte[] buffer = new byte[(int) f.length()];
        bis.read(buffer);
        bis.close();
        String script = new String(buffer);
        setScript(script);
    }

    public void setScript(String script) throws Exception {
        script = script.replace("\r\n", "\n");
        script = script.replace("\\n", "\n");

        mTimer.cancel();
        mTimer = new Timer("Simulator thread");
        mSimulator = mPySimulator = null;

        mInterpreter = new PythonInterpreter();
        mInterpreter.set("connector", this);
        mInterpreter.exec("import sys; sys.path.append(r'" + StaticConfiguration.JYTHON_LIB + "')");
        try {
            mInterpreter.exec(script);
            if (mSimulator != null) {
                mInterpreter.set("__simulator__", mSimulator);
                mPySimulator = mInterpreter.get("__simulator__");
                LOGGER.info("Simulator script loaded");
            } else {
                LOGGER.error("Python script simulator instance has not been set");
                mInterpreter = null;
            }
        } catch (PyException e) {
            mSimulator = mPySimulator = null;
            mInterpreter = null;
            logAndThrowException("Error while evaluating simulator script", e);
        }
    }

    public void initialize() throws Exception {
        if (mInterpreter == null) {
            logAndThrowException("Python simulator instance not loaded");
        }
        try {
            mInterpreter.exec("__simulator__.initialize()");
            LOGGER.info("Called Python simulator instance initialize() method successfully");
        } catch (PyException e) {
            logAndThrowException("Error while calling initialize() method of Python simulator instance", e);
        }
    }

    public void setVariable(String name, Object value) throws Exception {
        if (mPySimulator == null) {
            logAndThrowException("Python simulator instance not loaded");
        }
        try {
            PyObject pyValue = (value instanceof PyObject) ? (PyObject) value : Py.java2py(value);
            mPySimulator.__setattr__(new PyString(name), pyValue);
            return;
        } catch (PyException e) {
            logAndThrowException("Error while setting value of " + name + " variable of Python simulator instance", e);
        }
    }

    public Object getVariable(String name) throws Exception {
        if (mPySimulator == null) {
            logAndThrowException("Python simulator instance not loaded");
        }
        try {
            return mPySimulator.__getattr__(new PyString(name));
        } catch (PyException e) {
            logAndThrowException("Error while getting value of " + name + " variable of Python simulator instance", e);
        }
        return null; // not executed
    }

    public Object invoke(String method) throws Exception {
        return invoke(method, new Object[0]);
    }

    public Object invoke(String method, Object argument) throws Exception {
        Object[] arguments = {argument};
        return invoke(method, arguments);
    }

    public Object invoke(String method, Object[] arguments) throws Exception {
        if (mPySimulator == null) {
            logAndThrowException("Python simulator instance not loaded");
        }
        try {
            PyObject pyMethod = mPySimulator.__getattr__(new PyString(method));
            return pyMethod._jcall(arguments);
        } catch (PyException e) {
            logAndThrowException("Error while invoking " + method + " method of Python simulator instance", e);
        }
        return null; // not executed
    }

    /**
     * Logs a message with Log4j using info level.
     * @param message message to log
     */
    public void log(String message) {
        LOGGER.info(message);
    }

    /**
     * Logs a message with Log4j using given level.
     * @param message message to log
     * @param level log level
     */
    public void log(String message, Level level) {
        LOGGER.log(level, message);
    }

    /**
     * Logs message and throws Exception.
     * @param message message
     * @throws java.lang.Exception Exception built with given message
     */
    private static void logAndThrowException(String message) throws Exception {
        LOGGER.error(message);
        throw new Exception(message);
    }

    /**
     * Logs message and exception and throws Exception.
     * @param message message
     * @param e PyException
     * @throws java.lang.Exception Exception built with 'message + ":\n" + message_of_e'
     */
    private static void logAndThrowException(String message, PyException e) throws Exception {
        LOGGER.error(message, e);
        throw new Exception(message + ":\n" + PythonHelper.getMessage(e));
    }

    /**
     * TimerTask for calling a callable Python object by a timer.
     */
    private class PythonCallTimerTask extends TimerTask {

        PyObject task;

        PythonCallTimerTask(PyObject task) {
            this.task = task;
        }

        public void run() {
            try {
                task.__call__();
            } catch (Exception e) {
                LOGGER.error("Error while executing scheduled task", e);
            }
        }
    }
    
    /**
     * Shutdown hook thread.
     * Shuts down calls the shutdown method.
     */
    private class ShutdownHookThread extends Thread {
        @Override
        public void run() {
            shutdown();
        }
    }
}
