package com.qspin.qtaste.util.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class representing a service.
 * @author simjan
 *
 */
public abstract class Service implements Runnable {

	private List<ServiceListener> m_listeners;
	private boolean m_hasToRun;
	
	/**
	 * Returns the service's name.
	 * @return the service's name.
	 */
	public abstract String getName();
	/**
	 * Method executed on each service iteration.
	 */
	protected abstract void process();
	
	/**
	 * Constructor.
	 */
	public Service()
	{
		m_listeners = new ArrayList<ServiceListener>();
		m_hasToRun = false;
	}
	
	/**
	 * Adds the service listener to this service.
	 * @param listener the listener to add
	 */
	public synchronized void addServiceListener(ServiceListener listener)
	{
		if ( !m_listeners.contains(listener) )
		{
			m_listeners.add(listener);
		}
	}

	/**
	 * Removes the service listener from this service.
	 * @param listener the listener to remove
	 */
	public synchronized void removeServiceListener(ServiceListener listener)
	{
		if ( m_listeners.contains(listener) )
		{
			m_listeners.remove(listener);
		}
	}
	
	/**
	 * If the service is not running, starts a thread to execute the service.
	 */
	public void startService()
	{
		if ( isRunning() )
			return;
		
		new Thread(this, getName()).start();
	}

	/**
	 * If the service is running, request the service to stop.
	 */
	public void stopService()
	{
		if ( !isRunning() )
			return;
		
		m_hasToRun = false;
	}
	
	public boolean isRunning() {
		return m_hasToRun;
	}

	@Override
	public void run()
	{
		try
		{
			m_hasToRun = true;
			for (ServiceListener listener : Collections.unmodifiableList(m_listeners))
			{
				listener.serviceStarted(this);
			}
		
			while(m_hasToRun)
			{
				process();
			}
		}
		finally
		{
			m_hasToRun = false;
			for (ServiceListener listener : Collections.unmodifiableList(m_listeners))
			{
				listener.serviceStopped(this);
			}
		}
	}
	
	public interface ServiceListener
	{
		void serviceStarted(Service s);
		void serviceStopped(Service s);
	}
	
}
