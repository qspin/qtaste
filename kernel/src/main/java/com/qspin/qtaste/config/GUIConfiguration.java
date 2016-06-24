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
 * This class is responsible for providing GUI configuration parameters
 *
 * @author lvboque
 */
@SuppressWarnings("serial")
public class GUIConfiguration extends XMLConfiguration {

    private static Logger logger = Log4jLoggerFactory.getLogger(GUIConfiguration.class);
    private static GUIConfiguration instance;
    private static final String CONF_FILE = StaticConfiguration.CONFIG_DIRECTORY + "/gui.xml";

    /**
     * Constructs an instance of GUIConfiguration.
     *
     * @throws ConfigurationException
     */
    private GUIConfiguration() throws ConfigurationException {
        setFileName(CONF_FILE);
        try {
            load();
        } catch (ConfigurationException e) {
            logger.debug("Couldn't load GUI configuration file " + CONF_FILE + " - a new one will be created");
            setRootElementName("gui_configuration");
            save();
        }
    }

    synchronized public static GUIConfiguration getInstance() {
        if (instance == null) {
            try {
                instance = new GUIConfiguration();
            } catch (ConfigurationException e) {
                logger.error("Couldn't create GUIConfiguration");
                return null;
            }
        }
        return instance;
    }
}
