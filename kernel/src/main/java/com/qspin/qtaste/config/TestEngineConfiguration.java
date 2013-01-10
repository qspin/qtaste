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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * This class is responsible for providing TestEngine parameters
 * @author lvboque
 */
@SuppressWarnings("serial")
public class TestEngineConfiguration extends XMLConfiguration {

	private static Logger logger = Log4jLoggerFactory.getLogger(TestEngineConfiguration.class);
    private static TestEngineConfiguration instance;    
    private static final String DEFAULT_CONF_FILE = StaticConfiguration.CONFIG_DIRECTORY + "/engine.xml";
    private static String confFile = DEFAULT_CONF_FILE;

    
    private TestEngineConfiguration() throws ConfigurationException {        
            super(confFile);        
    }
    
    /**
     * Get an instance of the TestEngineConfiguration. By default, it uses the default configuration file.
     * setConfFile method can be used to specified another configuration file.
     * @return The TestEngineConfiguration.
     */
    synchronized public static TestEngineConfiguration getInstance() {
        try {
            if (instance == null) {            
                instance = new TestEngineConfiguration();
            }
            return instance;
        }
        catch (ConfigurationException e) {
            logger.fatal("Cannot load configuration", e);
        }
        return null;
    }
    
     /**
     * Set the configuration file to the specified confFile.
     * @param confFile The configuration file.
     */
    public static void setConfigFile(String confFile) {
        TestEngineConfiguration.confFile = confFile;
        instance = null;
    }
}
