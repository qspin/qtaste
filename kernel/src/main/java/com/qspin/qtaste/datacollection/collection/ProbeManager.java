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

package com.qspin.qtaste.datacollection.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * The Probe manager is responsible for the starting of probes depending on the Testbed configuration.
 *
 * @author lvb
 */
public class ProbeManager {

    private static Logger logger = Log4jLoggerFactory.getLogger(ProbeManager.class);
    private static ProbeManager instance = null;
    private ArrayList<Probe> probes = null;
    private int numberUsers = 0;

    private ProbeManager() {
        probes = new ArrayList<>();
    }

    public synchronized static ProbeManager getInstance() {
        if (instance == null) {
            instance = new ProbeManager();
        }
        return instance;
    }

    public synchronized void start() {
        try {
            logger.info("Starting ProbeManager:");
            if (numberUsers == 0) {
                TestBedConfiguration config = TestBedConfiguration.getInstance();
                List<Object> l = config.getList("probe_manager.probe");
                if (l != null) {
                    for (Object o : l) {
                        String probeName = (String) o;
                        logger.info("Starting probe:" + probeName);
                        try {
                            Class<?> probeClass = Class.forName(probeName);
                            Probe probe = (Probe) probeClass.newInstance();
                            probes.add(probe);
                            probe.start();
                        } catch (Exception e) {
                            logger.fatal("Cannot start probe " + probeName, e);
                        }
                    }
                }

            }
            numberUsers++;
        } catch (Exception e) {
            logger.fatal("ProbeManager cannot start some Probes", e);
        }
    }

    public synchronized void stop() {
        numberUsers--;
        if (numberUsers == 0) {
            for (Probe probe : probes) {
                logger.info("Stopping probe:" + probe);
                probe.stop();
            }
            probes.clear();
        }
    }
}
