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

package com.qspin.qtaste.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * This class is responsible for providing test bed configuration parameters
 * @author lvboque
 */
@SuppressWarnings("serial")
public class TestBedConfiguration extends XMLConfiguration {

	private static Logger logger = Log4jLoggerFactory.getLogger(TestBedConfiguration.class);
    private static TestBedConfiguration instance;
    private static String configFile;
    private static List<ConfigurationChangeHandler> configurationChangeHandlers = new ArrayList<ConfigurationChangeHandler>();
    private static long lastModifiedTime;
    private static String sutVersion;

    private TestBedConfiguration() throws ConfigurationException {
        super(configFile);
        lastModifiedTime = new File(configFile).lastModified();
        logger.info("Loaded testbed configuration file " + configFile);
    }

    /**
     * Get an instance of the TestBedConfiguration.
     * setConfFile method can be used to specified another configuration file.
     * @return The TestBedConfiguration or null if not configuration file has been set.
     */
    synchronized public static TestBedConfiguration getInstance() {
        try {
            if (instance == null) {
            	if (configFile == null) {
            		return null;
            	} else {
            		instance = new TestBedConfiguration();
            	}
            }
            return instance;
        } catch (ConfigurationException e) {
            logger.fatal("Cannot load configuration", e);
            return null;
        }
    }

    /**
     * Set the configuration file to the specified file.
     * @param file The configuration file.
     */
    public static void setConfigFile(String file) {
        if (!file.equals(configFile)) {
            configFile = file;
            instance = null;
            onConfigurationChange();
        }
    }

    /**
     * Register an handler with callback method which will be called when configuration has changed.
     * @param handler
     */
    public static void registerConfigurationChangeHandler(ConfigurationChangeHandler handler) {
        if (!configurationChangeHandlers.contains(handler)) {
            configurationChangeHandlers.add(handler);
        }
    }

    /**
     * Unregister an handler with callback method which will be called when configuration has changed.
     * @param handler
     */
    public static void unregisterConfigurationChangeHandler(ConfigurationChangeHandler handler) {
        configurationChangeHandlers.remove(handler);
    }

    /**
     * Reload configuration file if it has been modified since loading.
     */
    public static void reloadConfigFileIfModified() {
        long newLastModifiedTime = new File(configFile).lastModified();
        if (newLastModifiedTime != lastModifiedTime) {
            logger.info("Testbed configuration file " + configFile + " has been modified and will be reloaded");
            instance = null;
            onConfigurationChange();
        }
    }

    /**
     * Check if testbed has a control script.
     * @return true if testbed has a control script, false otherwise
     */
    public boolean hasControlScript() {
    	return containsKey("control_script");
    }
    
    /**
     * Return the file name of the control script or null if none.
     * @return the file name of the control script or null if none.
     */
    public String getControlScriptFileName() {
        String scriptFilename = getString("control_script");
        if (scriptFilename != null && !new File(scriptFilename).isAbsolute() && !scriptFilename.startsWith(StaticConfiguration.CONTROL_SCRIPTS_DIRECTORY)) {
        	scriptFilename = StaticConfiguration.CONTROL_SCRIPTS_DIRECTORY + "/" + scriptFilename;
        }
        return scriptFilename;
    }
    
    /**
     * Return the arguments of the control script or null if none.
     * @return the arguments of the control script or null if none.
     */
    public String getControlScriptArguments() {
        return getString("control_script.arguments");
    }
    
    /**
     * Return the id of the default instance
     * @return id of the default instance
     * @throws java.util.NoSuchElementException
     *             if the configuration key is not available.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a int value.    
     */
    public String getDefaultInstanceId() {
        return getString("multiple_instances_components[@default]");
    }

    /**
     * Return the index corresponding to the instance id specified as parameter
     * @param instanceId the instance id
     * @param component The component name.
     * @return the index of the instance id, -1 if the instance is not found
     */
    public int getMIIndex(String instanceId, String component) {
        for (int i = 0; i <= instance.getMaxIndex("multiple_instances_components." + component); i++) {
            String id = instance.getString("multiple_instances_components." + component + "(" + i + ") [@id]");            
            if (id == null)
                return -1;
            if (id.equals(instanceId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the String associated with the given configuration key for the specified instanceId
     * @param instanceId The instance Id id
     * @param component The component name.
     * @param key The Configuration key
     * @return The value of the parameter identified by the key or null if the key is not present    
     */
    public String getMIString(String instanceId, String component, String key) {
        int index = getMIIndex(instanceId, component);
        return instance.getString("multiple_instances_components." + component + "(" + index + ")." + key);
    }

    /**
     * Get the String associated with the given configuration key for the specified instanceId
     * @param instanceId The instance Id id
     * @param component The component name.
     * @param key The Configuration key
     * @param defaultValue The default value if the configuration key is not available
     * @return The value of the parameter identified by the key or defaultValue if the key is not present
     */
    public String getMIString(String instanceId, String component, String key, String defaultValue) {
        int index = getMIIndex(instanceId, component);
        return instance.getString("multiple_instances_components." + component + "(" + index + ")." + key, defaultValue);
    }

    /**
     * Get a int associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @return The value of the parameter identified by the key.
     * @throws java.util.NoSuchElementException
     *             if the configuration key is not available.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a int value.    
     */
    public int getMIInt(String instanceId, String component, String key) throws NoSuchElementException, ConversionException {
        int index = getMIIndex(instanceId, component);
        return instance.getInt("multiple_instances_components." + component + "(" + index + ")." + key);
    }

    /**
     * Get a int associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @param defaultValue The default value if the configuration key is not available
     * @return The value of the parameter identified by the key or defaultValue if the key is not present
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a int value.    
     */
    public int getMIInt(String instanceId, String component, String key, int defaultValue) throws ConversionException {
        int index = getMIIndex(instanceId, component);
        return instance.getInt("multiple_instances_components." + component + "(" + index + ")." + key, defaultValue);
    }

    /**
     * Get a boolean associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @return The value of the parameter identified by the key.
     * @throws java.util.NoSuchElementException
     *             if the configuration key is not available.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a boolean value.    
     */
    public boolean getMIBoolean(String instanceId, String component, String key) throws NoSuchElementException, ConversionException {
        int index = getMIIndex(instanceId, component);
        return instance.getBoolean("multiple_instances_components." + component +" (" + index + ")." + key);
    }

    /**
     * Get a boolean associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @param defaultValue The default value if the configuration key is not available
     * @return The value of the parameter identified by the key or defaultValue if the key is not present
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a boolean value.    
     */
    public boolean getMIBoolean(String instanceId, String component, String key, boolean defaultValue) throws ConversionException {
        int index = getMIIndex(instanceId, component);
        return instance.getBoolean("multiple_instances_components." + component + "(" + index + ")." + key, defaultValue);
    }

    /**
     * Get a short associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @return The value of the parameter identified by the key.
     * @throws java.util.NoSuchElementException
     *             if the configuration key is not available.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a short value.    
     */
    public short getMIShort(String instanceId, String component, String key) throws NoSuchElementException, ConversionException {
        int index = getMIIndex(instanceId, component);
        return instance.getShort("multiple_instances_components." + component + "(" + index + ")." + key);
    }

    /**
     * Get a short associated with the given configuration key for the specified instanceId.
     * @param instanceId The instance Id id.
     * @param component The component name.
     * @param key The Configuration key.
     * @param defaultValue The default value if the configuration key is not available
     * @return The value of the parameter identified by the key or defaultValue if the key is not present
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value associated to the configuration key is convertible into a short value.    
     */
    public short getMIShort(String instanceId, String component, String key, short defaultValue) throws ConversionException {
        int index = getMIIndex(instanceId, component);
         return instance.getShort("multiple_instances_components." + component + "(" + index + ")." + key, defaultValue);
    }

    /**
     * Call configuration change handlers.
     */
    protected static void onConfigurationChange() {
        for (ConfigurationChangeHandler handler : configurationChangeHandlers) {
        	try {
        		handler.onConfigurationChange();
        	} catch (Exception pExc)
        	{
        		logger.error("An error occured during the testbed configuration change event management:" + pExc.getMessage(), pExc);
        	}
        }
    }

    public interface ConfigurationChangeHandler {
        /*
         * Testbed configuration change handler
         */

        /*
         * method called when the testbed configuration has changed
         */
        void onConfigurationChange();
    }


     /**
     * Set the SUT version used by this testbed.
     * @param sutVersion The sut version.
     */
    public static void setSUTVersion(String sutVersion) {
        TestBedConfiguration.sutVersion = sutVersion;
    }

    /**
     * Get the SUT version used by this testbed
     * @return The SUT version
     */
    public static String getSUTVersion() {
     if (sutVersion == null)
         return "undefined";
     return sutVersion;
    }
}
