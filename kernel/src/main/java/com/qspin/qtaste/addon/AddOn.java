package com.qspin.qtaste.addon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * 
 * @author simjan
 *
 */
public abstract class AddOn {

	/** Constructor. */
	public AddOn(AddOnMetadata pMetaData)
	{
		mLoaded = false;
		mListeners = new ArrayList<PropertyChangeListener>();
		mMetaData = pMetaData;
	}
	
	/**
	 * Returns the add-on's identifier.
	 * @return The add-on's identifier.
	 */
	public String getAddOnId()
	{
		return mMetaData.getName();
	}
	
	/**
	 * Loads and plugs the add-on in QTaste.
	 * @return <code>true</code> if the add-on is successfully loaded.
	 * @throws AddOnException
	 */
	public abstract boolean loadAddOn() throws AddOnException;
	/**
	 * 
	 * Unloads the add-on in QTaste.
	 * @return <code>true</code> if the add-on is successfully unloaded.
	 * @throws AddOnException
	 */
	public abstract boolean unloadAddOn() throws AddOnException;
	
	/**
	 * Specifies if the add-on provide some configuration GUI.
	 * @return <code>true</code> if the add-on provide some configuration GUI.
	 */
	public abstract boolean hasConfiguration();
	
	/**
	 * Provides the add-on's configuration GUI.
	 * @return the add-on's configuration GUI. If {@link #hasConfiguration()} returns <code>true</code>, it cannot be <code>null</code>.
	 */
	public abstract JPanel getConfigurationPane();
	
	/**
	 * Registers a listener to be notified when a add-on's property is updated.
	 * @param pListener The listener to register.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pListener)
	{
		mListeners.add(pListener);
	}
	/**
	 * Unregisters a listener to be no more notified when a add-on's property is updated.
	 * @param pListener The listener to register.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pListener)
	{
		mListeners.remove(pListener);
	}
	
	/**
	 * Returns the add-on's loading state.
	 * @return <code>true</code> if the add-on is loaded.
	 */
	public boolean isAddOnLoaded()
	{
		return mMetaData.getStatus().equals(AddOnMetadata.LOAD);
	}
	
	/**
	 * Changes the add-on loading state. Fires a property change event ({@link #PROPERTY_LOAD_STATE}).
	 * @param isLoaded The new add-on loading state.
	 */
	protected void setAddOnLoaded(boolean isLoaded)
	{
		String oldState = mMetaData.getStatus();
		mMetaData.setStatus(AddOnMetadata.LOAD);
		firePropertyChangedEvent(new PropertyChangeEvent(this, PROPERTY_LOAD_STATE, oldState, mMetaData.getStatus()));
	}
	
	/**
	 * Notifies the event to all registered listeners. 
	 * @param pEvent The event to send.
	 */
	protected void firePropertyChangedEvent(PropertyChangeEvent pEvent)
	{
		for ( PropertyChangeListener listener : mListeners )
		{
			listener.propertyChange(pEvent);
		}
	}
	
	/** Flag to specify if the add-on is already loaded. */
	protected boolean mLoaded;
	protected AddOnMetadata mMetaData;
	
	/** List of all registered listeners who are notified when an add-on's property is updated. */
	protected List<PropertyChangeListener> mListeners;
	/** Property name for the add-on loading state flag. */
	public static final String PROPERTY_LOAD_STATE = "PROPERTY_LOAD_STATE";
}
