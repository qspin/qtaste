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

package com.qspin.qtaste.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

/**
 * Class for getting version informations from the manifest contained in the jar containing the derived class object.
 */
public class Version {

    private static Logger logger = Log4jLoggerFactory.getLogger(Version.class);
    private Attributes attributes;

    /**
     * Constructs the Version object for the specified jar
     * @param jarFileName file name of the jar containing the manifest
     */
    public Version(String jarFileName) {
        URL manifestURL;
        try {
            jarFileName = new File(jarFileName).getCanonicalPath().toString();
            manifestURL = new URL("jar:file:/" + jarFileName.replace('\\', '/') + "!/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(manifestURL.openStream());
            attributes = manifest.getMainAttributes();
        } catch (MalformedURLException e) {
            logger.error("Couldn't create jar manifest URL for reading version information: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Couldn't read jar manifest for reading version information: " + e.getMessage());
        }
    }

    /** 
     * Constructs the Version object for the jar containing the derived class.
     */
    protected Version() {
        String classContainer = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        if (classContainer.endsWith(".jar")) {
            URL manifestURL;
            try {
                manifestURL = new URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(manifestURL.openStream());
                attributes = manifest.getMainAttributes();
            } catch (MalformedURLException e) {
                logger.error("Couldn't create jar manifest URL for reading version information: " + e.getMessage());
            } catch (IOException e) {
                logger.error("Couldn't read jar manifest for reading version information: " + e.getMessage());
            }
        } else {
            logger.warn("Package not running from jar: " + classContainer);
        }
    }

    /**
     * Gets version string.
     * @return the version string, stored in the "Implementation-Version" attribute of the manifest
     */
    public String getVersion() {
        return getManifestAttributeValue(new Attributes.Name("Kernel-Implementation-Version"));
    }

    /**
     * Gets full version string, is equivalent to getVersion.
     * @return the full version string
     */
    public String getFullVersion() {
        return getVersion();
    }

    /**
     * Gets the value of an attribute of the manifest.
     * @param attributeName the name of the attribute
     * @return the value of the attribute, or "undefined" if the attribute couldn't be read
     */
    protected String getManifestAttributeValue(Attributes.Name attributeName) {
        try {
            String value = attributes.getValue(attributeName);
            return value != null ? value : "undefined";
        } catch (NullPointerException e) {
            return "undefined";
        } catch (IllegalArgumentException e) {
            logger.error("Invalid attribute name when reading jar manifest for reading version information: " + e.getMessage());
            return "undefined";
        }
    }
}
