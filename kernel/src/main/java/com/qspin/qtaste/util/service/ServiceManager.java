package com.qspin.qtaste.util.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.service.Service.ServiceListener;

/**
 * Class used for the service management.
 * Class implemented with the Singleton pattern. 
 * @author simjan
 */

public final class ServiceManager implements ServiceListener{

	private static ServiceManager INSTANCE;
	/**
	 * Returns the unique instance of the manager. If none exists, create one.
	 * @return the unique instance of the manager.
	 */
	public static synchronized ServiceManager getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new ServiceManager();
		}
		return INSTANCE;
	}
	protected ServiceManager()
	{
		m_registeredServices = new ArrayList<Service>();
	}
	
	protected final static Logger logger = Logger.getLogger(ServiceManager.class);
	private List<Service> m_registeredServices;

	/**
	 * Adds the service to the manager (if needed) and starts it.
	 * @param s the service to register and to start.
	 */
	public synchronized void registerAndStartService(Service s)
	{
		if ( !m_registeredServices.contains(s) )
		{
			m_registeredServices.add(s);
			s.addServiceListener(this);
		}
		s.startService();
	}
	
	/**
	 * Removes the service from the manager.
	 * @param The service to remove.
	 */
	public synchronized void unregisterService(Service s)
	{
		if ( m_registeredServices.contains(s) )
		{
			m_registeredServices.remove(s);
			s.removeServiceListener(this);
		}
	}
	
	/**
	 * Requests the running registered service to stop.
	 * @param s the service to stop.
	 */
	public synchronized void stopService(Service s)
	{
		if ( !m_registeredServices.contains(s) && s.isRunning() )
		{
			s.stopService();
		}
	}
	
	/**
	 * Requests all registered services to stop.
	 */
	public synchronized void stopAllRegisteredServices()
	{
		for ( Service s : m_registeredServices )
		{
			try
			{
				stopService(s);
			}
			catch(Exception ex)
			{
				logger.error("Error while stopping the service '" + s.getName() + "' : " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Checks if the service is registered in the ServiceManager.
	 * @param s the service to check
	 * @return <code>true</code> if the service is registered in the ServiceManager.
	 */
	public synchronized boolean isServiceRegistered(Service s)
	{
		return m_registeredServices.contains(s);
	}
	
	@Override
	public void serviceStarted(Service s) {
		logger.info("Service '" + s.getName() + "' has started");
	}
	
	@Override
	public void serviceStopped(Service s) {
		logger.info("Service '" + s.getName() + "' has stopped");
	}
}
