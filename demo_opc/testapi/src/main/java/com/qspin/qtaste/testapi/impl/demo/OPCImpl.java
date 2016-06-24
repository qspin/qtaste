package com.qspin.qtaste.testapi.impl.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.prosysopc.ua.MonitoredItemBase;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.Subscription;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.MonitoringMode;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.datacollection.collection.Cache;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.testapi.api.OPC;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class OPCImpl implements OPC {

    private String m_instance;
    private UaClient m_client;
    private String opcUrl;
    private String opcPrefix;
    Lock m_lock;
    Condition m_newDataValue;
    private Map<String, Subscription> m_subscriptions;

    private enum VariableType {
        STRING_TYPE,
        INT_TYPE,
        DOUBLE_TYPE,
        BOOL_TYPE
    }

    ;

    public OPCImpl(String instanceID) throws QTasteException {
        m_instance = instanceID;
        m_subscriptions = new HashMap<String, Subscription>();
        opcUrl = TestBedConfiguration.getInstance().getMIString(m_instance, "OPC", "opc_url");
        opcPrefix = TestBedConfiguration.getInstance().getMIString(m_instance, "OPC", "opc_prefix");
        initialize();
    }

    @Override
    public void initialize() throws QTasteException {
        try {
            m_lock = new ReentrantLock();
            m_newDataValue = m_lock.newCondition();
            m_client = new UaClient(opcUrl);
            m_client.setSecurityMode(SecurityMode.NONE);
            m_client.connect();
        } catch (Exception e) {
            throw new QTasteException("Unable to initialize the connection to the OPC!", e);
        }
    }

    @Override
    public void terminate() throws QTasteException {
        if (m_client != null && m_client.isConnected()) {
            removeSubscriptions();
            m_subscriptions.clear();
            m_client.disconnect();
        }
    }

    @Override
    public String getInstanceId() {
        return m_instance;
    }

    @Override
    public void writeInt(String varName, int value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(1, opcPrefix + varName);
            m_client.writeValue(node, value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }

    @Override
    public void writeDouble(String varName, double value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(1, opcPrefix + varName);
            m_client.writeValue(node, value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }

    @Override
    public void write(String varName, String value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(1, opcPrefix + varName);
            m_client.writeValue(node, value.toString());
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }

    @Override
    public int readInt(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(1, opcPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

    @Override
    public double readDouble(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(1, opcPrefix + varName);
        try {
            return m_client.readValue(node).getValue().doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

    @Override
    public String read(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(1, opcPrefix + varName);
        try {
            return m_client.readValue(node).getValue().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

    private Subscription getSubscription(String varName) {
        if (!m_subscriptions.containsKey(varName)) {
            m_subscriptions.put(varName, new Subscription());
        }
        return m_subscriptions.get(varName);
    }

    @Override
    public void addVariableSubscription(final String varName) throws QTasteTestFailException {
        try {
            //initialize Data Cache with variable to subscribe
            CacheImpl.getInstance().dataReceived(System.currentTimeMillis(), "OPC", "OPC Probe", "OPC." + varName, null, null,
                  "OPC");
            //register to get temperature update
            String realProperty = opcPrefix + varName;
            NodeId node = new NodeId(1, realProperty);
            Subscription mySub = getSubscription(varName);
            MonitoredDataItem item = new MonitoredDataItem(node, Attributes.Value, MonitoringMode.Reporting);
            item.addChangeListener(new MonitoredDataItemListener() {
                @Override
                public void onDataChange(MonitoredDataItem arg0, DataValue arg1, DataValue newValue) {
                    if (newValue == null) {
                        return;
                    }
                    m_lock.lock();
                    try {
                        CacheImpl.getInstance().dataReceived(System.currentTimeMillis(), "OPC", "OPC Probe", "OPC." + varName,
                              newValue.getValue(), null, "OPC");
                        m_newDataValue.signalAll();
                    } finally {
                        try {
                            m_lock.unlock();
                        } catch (IllegalMonitorStateException e) {
                            // Case where test TaskThread is stop due to timeout
                        }
                    }
                }
            });
            mySub.addItem(item);
            m_client.addSubscription(mySub);
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot subscribe to variable OPC." + varName);
        }
    }

    @Override
    public void removeVariableSubscription(final String varName) throws QTasteTestFailException {
        try {
            Subscription mySub = getSubscription(varName);
            for (MonitoredItemBase n : mySub.getItems()) {
                if (n instanceof MonitoredDataItem) {
                    MonitoredDataItem m = ((MonitoredDataItem) n);
                    m.setDataChangeListener(null);
                }
            }
            m_client.removeSubscription(mySub);
            m_subscriptions.remove(varName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Failed to unregister from the OPC");
        }
    }

    @Override
    public void removeSubscriptions() throws QTasteTestFailException {
        for (String subName : m_subscriptions.keySet()) {
            removeVariableSubscription(subName);
        }
    }

    private void checkValueAsync(String varName, Object expectedValue, VariableType type, long timeout, Object precision)
          throws QTasteException {
        boolean expectedValueNotReach = true;
        m_lock.lock();
        try {
            long nanosTimeout = TimeUnit.SECONDS.toNanos(timeout);
            Cache cache = CacheImpl.getInstance();
            String currentStringValue = "";
            while (expectedValueNotReach) {
                Variant currentValue = (Variant) cache.getLast("OPC." + varName).getValue();
                if (currentValue != null && !currentValue.toString().isEmpty()) {
                    currentStringValue = currentValue.toString();
                    switch (type) {
                        case STRING_TYPE: {
                            expectedValueNotReach = !currentStringValue.equals(expectedValue.toString());
                            break;
                        }
                        case INT_TYPE: {
                            int valueInt = ((Double) Double.parseDouble(currentStringValue)).intValue();
                            int expectedValueInt = ((Integer) Integer.parseInt(expectedValue.toString())).intValue();
                            int precisionInt = ((Integer) Integer.parseInt(precision.toString())).intValue();
                            expectedValueNotReach = !((valueInt >= (expectedValueInt - precisionInt)) && (valueInt <= (
                                  expectedValueInt + precisionInt)));
                            break;
                        }
                        case DOUBLE_TYPE: {
                            double valueDouble = ((Double) Double.parseDouble(currentStringValue)).doubleValue();
                            double expectedValueDouble = ((Double) Double.parseDouble(expectedValue.toString())).doubleValue();
                            double precisionDouble = ((Double) Double.parseDouble(precision.toString())).doubleValue();
                            expectedValueNotReach = !((valueDouble >= (expectedValueDouble - precisionDouble)) && (valueDouble
                                  <= (expectedValueDouble + precisionDouble)));
                            break;
                        }
                        case BOOL_TYPE: {
                            boolean valueBool = currentStringValue.equalsIgnoreCase("true");
                            boolean expectedBool = expectedValue.toString().equalsIgnoreCase("true");
                            expectedValueNotReach = (valueBool != expectedBool);
                            break;
                        }
                        default:
                            break;
                    }
                }

                if (expectedValueNotReach) {
                    // Wait for variable update
                    nanosTimeout = m_newDataValue.awaitNanos(nanosTimeout);
                    if (nanosTimeout <= 0) {
                        break; // timeout reached
                    }
                }
            }
            if (expectedValueNotReach) {
                throw new QTasteTestFailException("Failed to get the expected value of " + expectedValue.toString() +
                      " for variable OPC." + varName + " but got " + (currentStringValue.isEmpty() ? "[]" : currentStringValue));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable OPC." + varName);
        } finally {
            try {
                m_lock.unlock();
            } catch (IllegalMonitorStateException e) {
                // Case where test TaskThread is stop due to timeout
            }
        }
    }

    @Override
    public void checkValueInt(String varName, int value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.INT_TYPE, timeout, 0);
    }

    @Override
    public void checkValueInt(String varName, int value, long timeout, int precision) throws QTasteException {
        checkValueAsync(varName, value, VariableType.INT_TYPE, timeout, precision);
    }

    @Override
    public void checkValueDouble(String varName, double value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.DOUBLE_TYPE, timeout, 0);
    }

    @Override
    public void checkValueDouble(String varName, double value, long timeout, double precision) throws QTasteException {
        checkValueAsync(varName, value, VariableType.DOUBLE_TYPE, timeout, precision);
    }

    @Override
    public void checkValueBool(String varName, boolean value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.BOOL_TYPE, timeout, 0);
    }

}
