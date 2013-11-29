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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface of methods exposed via JMX by QTaste simulators.
 * 
 * @author David Ergo
 */
public interface SimulatorMBean {

    /**
     * Sets the simulator script file.
     * @param scriptFileName file name of a Python script
     * @throws java.io.FileNotFoundException if script file was not found
     * @throws java.io.IOException if an error occurred while reading script file
     * @throws java.lang.Exception if an error occurred while evaluating script
     */
    void setScriptFile(String scriptFileName) throws FileNotFoundException, IOException, Exception;

    /**
     * Sets the simulator script.
     * @param scriptContent Python script content
     * @throws java.lang.Exception if an error occurred while evaluating script
     */
    void setScript(String scriptContent) throws Exception;

    /**
     * Initializes the simulator by invoking the initialize() method of the Python simulator instance.
     * <p>
     * Note that this is not necessary after setting a simulator script because it should be done in the constructor of the Python simulator.
     * @throws java.lang.Exception if an error occurred while invoking the initialize() method of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    void initialize() throws Exception;

    /** 
     * Sets the value of a Python simulator instance member variable.
     * @param variable name of the member variable
     * @param value value to set the variable to
     * @throws java.lang.Exception if an error occurred while setting the value of the variable of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    void setVariable(String variable, Object value) throws Exception;

    /**
     * Gets the value of a Python simulator instance member variable.
     * @param variable name of the member variable
     * @return value of the variable
     * @throws java.lang.Exception if an error occurred while getting the value of the variable of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    Object getVariable(String variable) throws Exception;
    
    /**
     * Invokes a method of the Python simulator instance with no arguments.
     * @param method name of the method to call
     * @return the return value of the method call or null if none
     * @throws java.lang.Exception if an error occurred while invoking the method of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    Object invoke(String method) throws Exception;
    
    /**
     * Invokes a method of the Python simulator instance with one argument.
     * @param method name of the method to call
     * @param argument argument value
     * @return the return value of the method call or null if none
     * @throws java.lang.Exception if an error occurred while invoking the method of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    Object invoke(String method, Object argument) throws Exception;
    
    /**
     * Invokes a method of the Python simulator instance.
     * @param method name of the method to call
     * @param arguments array of arguments
     * @return the return value of the method call or null if none
     * @throws java.lang.Exception if an error occurred while invoking the method of the Python simulator instance
     *                             or if there is no Python simulator instance
     */
    Object invoke(String method, Object[] arguments) throws Exception;
}
