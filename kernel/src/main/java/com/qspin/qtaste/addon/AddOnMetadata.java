package com.qspin.qtaste.addon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

public final class AddOnMetadata {

    public static AddOnMetadata createAddOnMetadata(File pJar) {
        AddOnMetadata am = new AddOnMetadata();
        am.setJarName(pJar.getName());
        URL manifestURL;
        try {
            manifestURL = new URL("jar:file:" + pJar.getAbsolutePath() + "!/META-INF/MANIFEST.MF");
            LOGGER.debug("Add-on jar url : " + manifestURL);
            Manifest manifest = new Manifest(manifestURL.openStream());
            Attributes attributes = manifest.getMainAttributes();
            am.setName(attributes.getValue(ADDON_NAME_ATTRIBUTE));
            am.setVersion(attributes.getValue(ADDON_VERSION_ATTRIBUTE));
            am.setDescription(attributes.getValue(ADDON_DESCRIPTION_ATTRIBUTE));
            am.setMainClass(attributes.getValue(ADDON_MAIN_CLASS_ATTRIBUTE));

            if (am.getMainClass() != null) {
                Class<?> addOnClass = Class.forName(am.getMainClass());
                if (AddOn.class.isAssignableFrom(addOnClass)) {
                    return am;
                }
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Couldn't create jar manifest URL for reading version information: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Couldn't read jar manifest for reading addon metadata information: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Couldn't initialize the referenced class: " + e.getMessage());
        }
        LOGGER.warn("jar " + am.getJarName() + " is not an add-on. The jar will be ignored");
        return null;
    }

    protected AddOnMetadata() {
        mListener = new ArrayList<PropertyChangeListener>();
        setStatus(NONE);
    }

    public void addPropertyChangeListener(PropertyChangeListener pListener) {
        mListener.add(pListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener pListener) {
        mListener.remove(pListener);
    }

    protected void firePropertyChangeEvent(PropertyChangeEvent pEvent) {
        for (PropertyChangeListener listener : mListener) {
            listener.propertyChange(pEvent);
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getJarName() {
        return mJarName;
    }

    public void setJarName(String jarName) {
        mJarName = jarName;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        LOGGER.trace("Change status from " + mStatus + " to " + status + " for the addon " + getName());
        PropertyChangeEvent evt = new PropertyChangeEvent(this, STATUS_PROPERTY_ID, mStatus, status);
        mStatus = status;
        firePropertyChangeEvent(evt);
    }

    public String getMainClass() {
        return mMainClass;
    }

    public void setMainClass(String pMainClass) {
        LOGGER.trace("Change add-on main class from '" + mMainClass + "' to '" + pMainClass + "' for (" + getName() + ")");
        mMainClass = pMainClass;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        LOGGER.trace("Change add-on description from '" + mDescription + "' to '" + description + "' for (" + getName() + ")");
        mDescription = description;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public String toString() {
        return getName() + " (" + getStatus() + ")";
    }

    private String mName;
    private String mDescription;
    private String mVersion;
    private String mJarName;
    private String mMainClass;
    private String mStatus;
    private boolean mActive;
    private List<PropertyChangeListener> mListener;

    static final String LOAD = "LOAD";
    static final String ERROR = "ERROR";
    static final String NONE = "N/A";

    static final String STATUS_PROPERTY_ID = "add-on metadata - status";

    private static final Name ADDON_MAIN_CLASS_ATTRIBUTE = new Attributes.Name("addonMainClass");
    private static final Name ADDON_NAME_ATTRIBUTE = new Attributes.Name("addonName");
    private static final Name ADDON_VERSION_ATTRIBUTE = new Attributes.Name("addonVersion");
    private static final Name ADDON_DESCRIPTION_ATTRIBUTE = new Attributes.Name("addonDescription");
    private static final Logger LOGGER = Logger.getLogger(AddOnMetadata.class);
}
