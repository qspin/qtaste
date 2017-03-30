/* This file is derived from the javadoc API provided in the Prosys OPC-UA Java SDK Client/Server evaluation package (release 2.2.6-708)
 * As seen as, for licensing reasons, QTaste may not redistribute the Prosys OPC-UA Java SDK client jar file, the needed classes and methods are mocked-up to allow to build QTaste and all the testAPIs without requiring the Prosys OPC-UA Java SDK client jar file at build time.
 * None of those mock-up classes are present in the QTaste jars file.  
 * 
 * For using the QTaste OPC-UA testAPI, you'll need to contact Prosys (www.prosysopc.com) to obtain the OPC-UA Java SDK client jar file and add a dependency to this jar file in the pom.xml of your testapi folder.
 */

package com.prosysopc.ua.client;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.StatusCode;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UaApplication;
import com.qspin.qtaste.datacollection.Data;

public class UaClient extends UaApplication {

	public UaClient(String opcUrl) {
		// TODO Auto-generated constructor stub
	}

	public void setSecurityMode(SecurityMode securityMode) throws ServerConnectionException {
		// TODO Auto-generated method stub
	}

	public void connect() throws ServiceException, ConnectException, SessionActivationException, InvalidServerEndpointException {
		// TODO Auto-generated method stub
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public void disconnect() {
		// TODO Auto-generated method stub
	}

	public boolean writeValue(NodeId node, Object value) throws ServiceException, StatusException {
		// TODO Auto-generated method stub
		return false;
	}

	public DataValue readValue(NodeId node) throws ServiceException, StatusException {
		// TODO Auto-generated method stub
		return null;
	}

	public Subscription addSubscription(Subscription mySub) throws ServiceException, StatusException {
		// TODO Auto-generated method stub
		return null;
	}

	public StatusCode removeSubscription(Subscription mySub) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}