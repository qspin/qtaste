/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.opcua.testapi.impl;

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
import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.builtintypes.UnsignedLong;
import org.opcfoundation.ua.builtintypes.UnsignedShort;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.MonitoringMode;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.datacollection.collection.Cache;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.opcua.testapi.api.OPCUA;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class OPCUAImpl implements OPCUA {

    private String m_instance;
    private UaClient m_client;
    private String opcuaUrl;
    private String opcuaPrefix;
    private int opcuaNamespaceIdx;
    Lock m_lock;
    Condition m_newDataValue;
    private Map<String, Subscription> m_subscriptions;

    private enum VariableType {
        STRING_TYPE,
        BYTESTRING_TYPE,
        SBYTE_TYPE,
        BYTE_TYPE,
        INT16_TYPE,
        UINT16_TYPE,
        INT32_TYPE,
        UINT32_TYPE,
        INT64_TYPE,
        UINT64_TYPE,
        FLOAT_TYPE,
        DOUBLE_TYPE,
        DATETIME_TYPE,
        BOOLEAN_TYPE
    };

    public OPCUAImpl(String instanceID) throws QTasteException {
        m_instance = instanceID;
        m_subscriptions = new HashMap<String, Subscription>();
        opcuaUrl = TestBedConfiguration.getInstance().getMIString(m_instance, "OPCUA", "opcua_url");
        opcuaPrefix = TestBedConfiguration.getInstance().getMIString(m_instance, "OPCUA", "opcua_prefix");
        opcuaNamespaceIdx = Integer.parseInt(TestBedConfiguration.getInstance().getMIString(m_instance, "OPCUA", "opcua_namespace"));
        initialize();
    }

    @Override
    public void initialize() throws QTasteException {
        try {
            m_lock = new ReentrantLock();
            m_newDataValue = m_lock.newCondition();
            m_client = new UaClient(opcuaUrl);
            m_client.setSecurityMode(SecurityMode.NONE);
            m_client.connect();
        } catch (Exception e) {
            throw new QTasteException("Unable to initialize the connection to the OPCUA!", e);
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
	public void writeBoolean(String varName, int value) throws QTasteTestFailException {
    	try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, (value != 0));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
    
    @Override
	public void writeByte(String varName, int value) throws QTasteTestFailException {
    	try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, UnsignedByte.valueOf(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
    
    @Override
	public void writeSByte(String varName, int value) throws QTasteTestFailException {
    	try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, (byte) value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}

	@Override
	public void writeUInt16(String varName, int value) throws QTasteTestFailException {
		try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, UnsignedShort.valueOf(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
	
	@Override
	public void writeInt16(String varName, int value) throws QTasteTestFailException {
		try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, (short) value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
	
	
    @Override
    public void writeUInt32(String varName, long value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, UnsignedInteger.valueOf(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }
    
    @Override
    public void writeInt32(String varName, int value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, (int) value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }

	@Override
	public void writeUInt64(String varName, long value) throws QTasteTestFailException {
		try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, UnsignedLong.valueOf(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
	
	@Override
	public void writeUInt64(String varName, String value) throws QTasteTestFailException {
		try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, UnsignedLong.parseUnsignedLong(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
	
	@Override
	public void writeInt64(String varName, long value) throws QTasteTestFailException {
		
		try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, (long) value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
	}
	
    @Override
    public void writeFloat(String varName, float value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }

    @Override
    public void writeDouble(String varName, double value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, value);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }
    
    @Override
    public void writeDateTime(String varName, long value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, new DateTime(value));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }
    
    @Override
    public void writeString(String varName, String value) throws QTasteTestFailException {
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, value.toString());
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }
    
    @Override
    public void writeByteString(String varName, String value) throws QTasteTestFailException {
    	
    	byte[] bytes = new byte[value.length()];
		
		for (int i=0; i<value.length(); i++) {
    		bytes[i]=(byte) value.charAt(i);
    	}
    	
        try {
            NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
            m_client.writeValue(node, bytes);
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot send the value " + value + " for variable " + varName, ex);
        }
    }
    
    @Override
	public boolean readBoolean(String varName) throws QTasteTestFailException {
    	NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue() != 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
    
    @Override
	public int readByte(String varName) throws QTasteTestFailException {
    	NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
    
    @Override
	public int readSByte(String varName) throws QTasteTestFailException {
    	NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().byteValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}

	@Override
	public int readInt16(String varName) throws QTasteTestFailException {
		NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
	
	@Override
	public int readUInt16(String varName) throws QTasteTestFailException {
		NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
	
	@Override
    public int readInt32(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

	@Override
    public long readUInt32(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

	@Override
	public long readInt64(String varName) throws QTasteTestFailException {
		NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
	
	@Override
	public long readUInt64(String varName) throws QTasteTestFailException {
		NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
	}
	
    @Override
    public float readFloat(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

    @Override
    public double readDouble(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }
    
    @Override
    public long readDateTime(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
        	DateTime dateTime = m_client.readValue(node).getValue().asClass(org.opcfoundation.ua.builtintypes.DateTime.class, null); 
            if (dateTime != null) {
            	return dateTime.getValue();
            } else {
            	throw new QTasteTestFailException("Cannot convert DateTime value");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }

    @Override
    public String readString(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
            return m_client.readValue(node).getValue().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable " + varName);
        }
    }
    
    @Override
    public String readByteString(String varName) throws QTasteTestFailException {
        NodeId node = new NodeId(opcuaNamespaceIdx, opcuaPrefix + varName);
        try {
        	byte[] byteArray = (byte[]) m_client.readValue(node).getValue().getValue();
        	
        	String string = "";
    		for (int i=0; i<byteArray.length; i++) {
    			string += (char) byteArray[i];
    		}
    		
            return string;
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
        	
            CacheImpl.getInstance().dataReceived(System.currentTimeMillis(), "OPCUA", "OPCUA Probe", "OPCUA." + m_instance + "." + opcuaPrefix + varName, 
            		null, null, "OPCUA");

            String realProperty = opcuaPrefix + varName;
            NodeId node = new NodeId(opcuaNamespaceIdx, realProperty);
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
                    	CacheImpl.getInstance().dataReceived(System.currentTimeMillis(), "OPCUA", "OPCUA Probe", "OPCUA." + m_instance + "." + opcuaPrefix + varName,
                              newValue.getValue(), null, "OPCUA");
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
            throw new QTasteTestFailException("Cannot subscribe to variable OPCUA." + varName);
        }
    }

    @Override
    public void removeVariableSubscription(final String varName) throws QTasteTestFailException {
        try {
            Subscription mySub = getSubscription("OPCUA." + m_instance + "." + opcuaPrefix + varName);
            for (MonitoredItemBase n : mySub.getItems()) {
                if (n instanceof MonitoredDataItem) {
                    MonitoredDataItem m = ((MonitoredDataItem) n);
                    m.setDataChangeListener(null);
                }
            }
            m_client.removeSubscription(mySub);
            m_subscriptions.remove("OPCUA." + m_instance + "." + opcuaPrefix + varName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Failed to unregister from the OPCUA");
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
                Variant currentValue = (Variant) cache.getLast("OPCUA." + m_instance + "." + opcuaPrefix + varName).getValue();
                if (currentValue != null && !currentValue.toString().isEmpty()) {
                    currentStringValue = currentValue.toString();
                    switch (type) {
                    	case STRING_TYPE: {
                    		expectedValueNotReach = !currentStringValue.equals(expectedValue.toString());
                            break;
                        }
                    	case BYTESTRING_TYPE: {
                    		byte[] byteArray = (byte[]) currentValue.getValue();
                        	String valueStr = "";
                    		for (int i=0; i<byteArray.length; i++) {
                    			valueStr += String.format("%02x", byteArray[i] & 0xff);
                    		}
                    		expectedValueNotReach = !valueStr.equalsIgnoreCase(expectedValue.toString());
                            break;
                        }
                        case SBYTE_TYPE: 
                        case BYTE_TYPE: 
                        case INT16_TYPE: 
                        case UINT16_TYPE: 
                        case INT32_TYPE: {
                            int valueInt = ((Double) Double.parseDouble(currentStringValue)).intValue();
                            int expectedValueInt = ((Integer) Integer.parseInt(expectedValue.toString())).intValue();
                            int precisionInt = ((Integer) Integer.parseInt(precision.toString())).intValue();
                            expectedValueNotReach = !((valueInt >= (expectedValueInt - precisionInt)) && (valueInt <= (
                                  expectedValueInt + precisionInt)));
                            break;
                        }
                        case UINT32_TYPE: 
                        case INT64_TYPE: {
                            long valueLong = ((Long) Long.parseLong(currentStringValue)).longValue();
                            long expectedValueLong = ((Long) Long.parseLong(expectedValue.toString())).longValue();
                            long precisionLong = ((Long) Long.parseLong(precision.toString())).longValue();
                            expectedValueNotReach = !((valueLong >= (expectedValueLong - precisionLong)) && (valueLong <= (
                                  expectedValueLong + precisionLong)));
                            break;
                        }
                        case UINT64_TYPE: {
                        	UnsignedLong valueLong = ((UnsignedLong) UnsignedLong.parseUnsignedLong(currentStringValue));
                            UnsignedLong expectedValueLong = ((UnsignedLong) UnsignedLong.parseUnsignedLong(expectedValue.toString()));
                            UnsignedLong precisionLong = ((UnsignedLong) UnsignedLong.parseUnsignedLong(precision.toString()));
                            expectedValueNotReach = !( (valueLong.longValue() >= (expectedValueLong.longValue() - precisionLong.longValue()))) && 
                            		                   (valueLong.longValue() <= ((expectedValueLong.longValue() + precisionLong.longValue())) );
                            break;
                        }
                        case FLOAT_TYPE: 
                        case DOUBLE_TYPE: {
                            double valueDouble = ((Double) Double.parseDouble(currentStringValue)).doubleValue();
                            double expectedValueDouble = ((Double) Double.parseDouble(expectedValue.toString())).doubleValue();
                            double precisionDouble = ((Double) Double.parseDouble(precision.toString())).doubleValue();
                            expectedValueNotReach = !((valueDouble >= (expectedValueDouble - precisionDouble)) && (valueDouble
                                  <= (expectedValueDouble + precisionDouble)));
                            break;
                        }
                        case DATETIME_TYPE: {
                            double valueDateTime = ((Long) Long.parseLong(currentStringValue)).longValue();
                            double expectedValueDateTime = ((Long) Long.parseLong(expectedValue.toString())).longValue();
                            double precisionDateTime = ((Long) Long.parseLong(precision.toString())).longValue();
                            expectedValueNotReach = !((valueDateTime >= (expectedValueDateTime - precisionDateTime)) && (valueDateTime
                                  <= (expectedValueDateTime + precisionDateTime)));
                            break;
                        }
                        case BOOLEAN_TYPE: {
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
                      " for variable OPCUA." + varName + " but got " + (currentStringValue.isEmpty() ? "[]" : currentStringValue));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new QTasteTestFailException("Cannot read variable OPCUA." + varName);
        } finally {
            try {
                m_lock.unlock();
            } catch (IllegalMonitorStateException e) {
                // Case where test TaskThread is stop due to timeout
            }
        }
    }
    
    @Override
	public void checkValueSByte(String varName, int value, long timeout) throws QTasteException {
    	checkValueAsync(varName, value, VariableType.SBYTE_TYPE, timeout, 0);
	}

	@Override
	public void checkValueSByte(String varName, int value, long timeout, int precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.SBYTE_TYPE, timeout, precision);
	}
    
    @Override
	public void checkValueByte(String varName, int value, long timeout) throws QTasteException {
    	checkValueAsync(varName, value, VariableType.BYTE_TYPE, timeout, 0);
	}

	@Override
	public void checkValueByte(String varName, int value, long timeout, int precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.BYTE_TYPE, timeout, precision);
	}

	@Override
	public void checkValueInt16(String varName, int value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.INT16_TYPE, timeout, 0);
	}

	@Override
	public void checkValueInt16(String varName, int value, long timeout, int precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.INT16_TYPE, timeout, precision);
	}
	
	@Override
	public void checkValueUInt16(String varName, int value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.UINT16_TYPE, timeout, 0);
	}

	@Override
	public void checkValueUInt16(String varName, int value, long timeout, int precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.UINT16_TYPE, timeout, precision);
	}
	
	@Override
    public void checkValueInt32(String varName, int value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.INT32_TYPE, timeout, 0);
    }

    @Override
    public void checkValueInt32(String varName, int value, long timeout, int precision) throws QTasteException {
        checkValueAsync(varName, value, VariableType.INT32_TYPE, timeout, precision);
    }
    
	@Override
    public void checkValueUInt32(String varName, long value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.UINT32_TYPE, timeout, 0);
    }

    @Override
    public void checkValueUInt32(String varName, long value, long timeout, long precision) throws QTasteException {
        checkValueAsync(varName, value, VariableType.UINT32_TYPE, timeout, precision);
    }

	@Override
	public void checkValueInt64(String varName, long value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.INT64_TYPE, timeout, 0);
	}

	@Override
	public void checkValueInt64(String varName, long value, long timeout, long precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.INT64_TYPE, timeout, precision);
	}
	
	@Override
	public void checkValueUInt64(String varName, long value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.UINT64_TYPE, timeout, 0);
	}

	@Override
	public void checkValueUInt64(String varName, long value, long timeout, long precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.UINT64_TYPE, timeout, precision);
	}
	
	@Override
	public void checkValueUInt64(String varName, String value, long timeout) throws QTasteException {
		checkValueAsync(varName, UnsignedLong.parseUnsignedLong(value), VariableType.UINT64_TYPE, timeout, 0);
	}

	@Override
	public void checkValueUInt64(String varName, String value, long timeout, String precision) throws QTasteException {
		checkValueAsync(varName, UnsignedLong.parseUnsignedLong(value), VariableType.UINT64_TYPE, timeout, UnsignedLong.parseUnsignedLong(precision));
	}
	
    @Override
    public void checkValueFloat(String varName, float value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.DOUBLE_TYPE, timeout, 0);
    }

    @Override
    public void checkValueFloat(String varName, float value, long timeout, float precision) throws QTasteException {
        checkValueAsync(varName, value, VariableType.DOUBLE_TYPE, timeout, precision);
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
	public void checkValueDateTime(String varName, long value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.DATETIME_TYPE, timeout, 0);
	}

	@Override
	public void checkValueDateTime(String varName, long value, long timeout, long precision) throws QTasteException {
		checkValueAsync(varName, value, VariableType.DATETIME_TYPE, timeout, precision);
	}
	
	@Override
    public void checkValueBoolean(String varName, boolean value, long timeout) throws QTasteException {
        checkValueAsync(varName, value, VariableType.BOOLEAN_TYPE, timeout, 0);
    }

	@Override
	public void checkValueString(String varName, String value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.STRING_TYPE, timeout, 0);
	}

	@Override
	public void checkValueByteString(String varName, String value, long timeout) throws QTasteException {
		checkValueAsync(varName, value, VariableType.BYTESTRING_TYPE, timeout, 0);
	}
}
