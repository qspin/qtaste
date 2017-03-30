/* This file is derived from the javadoc API provided in the Prosys OPC-UA Java SDK Client/Server evaluation package (release 2.2.6-708)
 * As seen as, for licensing reasons, QTaste may not redistribute the Prosys OPC-UA Java SDK client jar file, the needed classes and methods are mocked-up to allow to build QTaste and all the testAPIs without requiring the Prosys OPC-UA Java SDK client jar file at build time.
 * None of those mock-up classes are present in the QTaste jars file.  
 * 
 * For using the QTaste OPC-UA testAPI, you'll need to contact Prosys (www.prosysopc.com) to obtain the OPC-UA Java SDK client jar file and add a dependency to this jar file in the pom.xml of your testapi folder.
 */

package com.prosysopc.ua;

import org.opcfoundation.ua.builtintypes.UnsignedInteger;

import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredItem;

public class SubscriptionBase implements Comparable<SubscriptionBase> {
		
	public MonitoredItemBase[] getItems() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int compareTo(SubscriptionBase o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void addItem(MonitoredItemBase item, UnsignedInteger itemId) {
		// TODO Auto-generated method stub
	}

}