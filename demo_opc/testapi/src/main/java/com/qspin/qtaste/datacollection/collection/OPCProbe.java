package com.qspin.qtaste.datacollection.collection;

import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.Subscription;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.MonitoringMode;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.qspin.qtaste.config.TestBedConfiguration;

public class OPCProbe extends AbstractProbe {

    private UaClient client;

    public OPCProbe() throws Exception {
        super();
        client = new UaClient(TestBedConfiguration.getInstance().getMIString("Neptis", "OPC", "opc_url"));

        client.setSecurityMode(SecurityMode.NONE);
        client.connect();

        //this.addVariableSubscription("SUP_RO_INT_AI_OVEN_1_TEMP", "Temperature");

        //register to get temperature update
        /*String realProperty = "[AB1]Global.SUP_RO_INT_AI_OVEN_1_TEMP";
        NodeId node = new NodeId(1, realProperty);
		Subscription mySub = new Subscription();
		MonitoredDataItem item = new MonitoredDataItem(node, Attributes.Value, MonitoringMode.Reporting);
		item.addChangeListener(new MonitoredDataItemListener() {
			@Override
			public void onDataChange(MonitoredDataItem arg0, DataValue arg1, DataValue newValue) {
				if ( newValue == null || !newValue.getValue().isNumber() )
					return;

				CacheImpl.getInstance().dataReceived(System.currentTimeMillis(),
													 "OPC",
													 "OPC Probe",
													 "OPC.Temperature",
													 newValue.getValue().doubleValue() / 10.,
													 null,
													 "OPC");

			}
		});
		mySub.addItem(item);
		client.addSubscription(mySub);*/
    }

    @Override
    public void serverLoop() throws Exception {
        // TODO Auto-generated method stub

    }

    public void addVariableSubscription(String nodeVarName, final String cacheVarName) throws Exception {
        //register to get temperature update
        String realProperty = "[AB1]Global." + nodeVarName;
        NodeId node = new NodeId(1, realProperty);
        Subscription mySub = new Subscription();
        MonitoredDataItem item = new MonitoredDataItem(node, Attributes.Value, MonitoringMode.Reporting);
        item.addChangeListener(new MonitoredDataItemListener() {
            @Override
            public void onDataChange(MonitoredDataItem arg0, DataValue arg1, DataValue newValue) {
                if (newValue == null) {
                    return;
                }

                CacheImpl.getInstance().dataReceived(System.currentTimeMillis(), "OPC", "OPC Probe", "OPC." + cacheVarName,
                      newValue.getValue().toString(), null, "OPC");
                CacheImpl.getInstance().notifyAll();
            }
        });
        mySub.addItem(item);
        client.addSubscription(mySub);
    }

}
