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

package com.qspin.qtaste.opcua.testapi.api;

import java.math.BigInteger;
import java.util.Calendar;

import org.opcfoundation.ua.builtintypes.DateTime;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * OPC-UA is the interface of the QTaste Test API component providing means to communicate with components accessible through
 * SCADA server.
 *
 * @author nfac
 */
public interface OPCUA extends MultipleInstancesComponent {
	/**
     * Write a boolean value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeBoolean(String varName, int value) throws QTasteTestFailException; 
	
	/**
     * Write an unsigned 8-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeByte(String varName, int value) throws QTasteTestFailException;
	
	/**
     * Write an unsigned 16-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeUInt16(String varName, int value) throws QTasteTestFailException;
    
    /**
     * Write an unsigned 32-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeUInt32(String varName, long value) throws QTasteTestFailException;
    
    /**
     * Write an unsigned 64-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeUInt64(String varName, long value) throws QTasteTestFailException;
    
    /**
     * Write an unsigned 64-bits value to a component identified by given variable name.
     * Use this method with the value as a string for values higher than 2^63.
     *
     * @param varName component variable name
     * @param value value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeUInt64(String varName, String value) throws QTasteTestFailException;
    
    /**
     * Write a signed 8-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value byte value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeSByte(String varName, int value) throws QTasteTestFailException;
	
	/**
     * Write a signed 16-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value short value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeInt16(String varName, int value) throws QTasteTestFailException;
    
    /**
     * Write a signed 32-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value integer value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeInt32(String varName, int value) throws QTasteTestFailException;
    
    /**
     * Write a signed 64-bits value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value long value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeInt64(String varName, long value) throws QTasteTestFailException;

    /**
     * Write a float value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value float value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeFloat(String varName, float value) throws QTasteTestFailException;
    
    /**
     * Write a double value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value double value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeDouble(String varName, double value) throws QTasteTestFailException;
    
    /**
     * Write a date & time value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value DateTime value to write to. (Unit: number of 100 nanosecond intervals since January 1, 1601 UTC)
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeDateTime(String varName, long value) throws QTasteTestFailException;

    /**
     * Write a string value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value string value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeString(String varName, String value) throws QTasteTestFailException;
    
    /**
     * Write a byte string value to a component identified by given variable name.
     *
     * @param varName component variable name
     * @param value byte string value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
    public void writeByteString(String varName, String value) throws QTasteTestFailException;
    
    //////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a boolean value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as boolean
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public boolean readBoolean(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current unsigned 8-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as unsigned byte
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public int readByte(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current unsigned 16-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as an unsigned Int16
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public int readUInt16(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current unsigned 32-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as an unsigned Int32
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public long readUInt32(String varName) throws QTasteTestFailException;

    /**
     * Returns the current unsigned 64-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as an unsigned Int64
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public long readUInt64(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current signed 8-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as signed byte
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public int readSByte(String varName) throws QTasteTestFailException;
    
    /**
     * Returns current signed 16-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as short (signed Int16)
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public int readInt16(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current 32-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as integer (signed Int32)
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public int readInt32(String varName) throws QTasteTestFailException;

    /**
     * Returns the current 64-bits value from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as long  (signed Int64)
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public long readInt64(String varName) throws QTasteTestFailException;  

    /**
     * Returns the current value, as float, from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as float
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public float readFloat(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current value, as double, from a component identified by given variable name.
     *
     * @param varName component variable name
     * @return current value as double
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public double readDouble(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current value, as long, from a component identified by given variable name. 
     * Unit: number of 100 nanosecond intervals since January 1, 1601 UTC
     *
     * @param varName component variable name
     * @return current value as double
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public long readDateTime(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current value, as String, from a component identified by given variable name. 
     *
     * @param varName component variable name
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public String readString(String varName) throws QTasteTestFailException;
    
    /**
     * Returns the current value, as string, from a component identified by given variable name. 
     *
     * @param varName component variable name
     * @throws QTasteTestFailException if the reading cannot be performed successfully
     */
    public String readByteString(String varName) throws QTasteTestFailException;

    
    
    /**
     * Subscribe to a variable that represents a component.
     * From this point on, it will be possible to listen to changes of the associated component.
     * Use any method checkValue to asynchronously get a value of a component.
     *
     * @param varName component variable name
     * @throws QTasteTestFailException if the subscription cannot be performed successfully
     */
    public void addVariableSubscription(final String varName) throws QTasteTestFailException;

    /**
     * Remove a subscription for a variable that represents a component.
     * From this point on, it will no longer be possible to listen variables changes asynchronously.
     * Any of the method checkValue will not work.
     *
     * @param varName component variable name
     * @throws QTasteTestFailException if the unsubscribing cannot be performed successfully
     */
    public void removeVariableSubscription(final String varName) throws QTasteTestFailException;

    /**
     * Remove all subscription made so far.
     * From this point on, it will no longer be possible to listen variables changes asynchronously.
     * Any of the method checkValue will not work.
     *
     * @throws QTasteTestFailException if unsubscribing cannot be performed successfully
     */
    public void removeSubscriptions() throws QTasteTestFailException;
  

    /**
     * Wait for the identified component reach the given byte value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 8-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueByte(String varName, int value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given byte value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 8-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueByte(String varName, int value, long timeout, int precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given short value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 16-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt16(String varName, int value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given short value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 16-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt16(String varName, int value, long timeout, int precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given integer value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 32-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt32(String varName, long value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given integer value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 32-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt32(String varName, long value, long timeout, long precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given long value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 64-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt64(String varName, long value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given long value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 64-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt64(String varName, long value, long timeout, long precision) throws QTasteException;

    /**
     * Wait for the identified component reach the given long value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected unsigned 64-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt64(String varName, String value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given long value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     * Use this method with the value as a string for values higher than 2^63.
     *
     * @param varName component variable name
     * @param value expected unsigned 64-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueUInt64(String varName, String value, long timeout, String precision) throws QTasteException;

    
    /**
     * Wait for the identified component reach the given byte value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     * Use this method with the value as a string for values higher than 2^63.
     *
     * @param varName component variable name
     * @param value expected signed 8-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueSByte(String varName, int value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given byte value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 8-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueSByte(String varName, int value, long timeout, int precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given short value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 16-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt16(String varName, int value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given short value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 16-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt16(String varName, int value, long timeout, int precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given integer value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 32-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt32(String varName, int value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given integer value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 32-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt32(String varName, int value, long timeout, int precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given long value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 64-bits value
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt64(String varName, long value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given long value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected signed 64-bits value
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueInt64(String varName, long value, long timeout, long precision) throws QTasteException;
        
    /**
     * Wait for the identified component reach the given float value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as float
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueFloat(String varName, float value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given double value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as float
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueFloat(String varName, float value, long timeout, float precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given double value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as double
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueDouble(String varName, double value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given double value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as double
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueDouble(String varName, double value, long timeout, double precision) throws QTasteException;
    
    /**
     * Wait for the identified component reach the given date & time value for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as long
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueDateTime(String varName, long value, long timeout) throws QTasteException;

    /**
     * Wait for the identified component reach the given double value, considering the precision of the measurement,
     * for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as long
     * @param timeout maximum wait duration
     * @param precision precision of the measurement
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueDateTime(String varName, long value, long timeout, long precision) throws QTasteException;

    /**
     * Wait for the identified component state the given state for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as boolean
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueBoolean(String varName, boolean value, long timeout) throws QTasteException;
        
    /**
     * Wait for the identified component state the given state for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as string
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueString(String varName, String value, long timeout) throws QTasteException;
    
    /**
     * Wait for the identified component state the given state for a defined timeout.
     * This method is blocking and will asynchronously listen for the component to reach the expected value.
     * In case of timeout an exception will be thrown and the current test will fail.
     *
     * @param varName component variable name
     * @param value expected value as an hexadecimal byte string
     * @param timeout maximum wait duration
     * @throws QTasteException if timeout is reached or variable cannot be listen
     */
    public void checkValueByteString(String varName, String value, long timeout) throws QTasteException;
    



}

