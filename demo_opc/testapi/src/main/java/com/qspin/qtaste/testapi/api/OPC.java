package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * OPC-UA is the interface of the QTaste Test API component providing means to communicate with components accessible through
 * SCADA server.
 *
 * @author nfac
 */
public interface OPC extends MultipleInstancesComponent {
    /**
     * Write a integer value to component identified by given variable name.
     *
     * @param varName component variable name
     * @param value integer value to write to
     * @throws QTasteTestFailException if the writing cannot be performed successfully
     */
	public void writeInt(String varName, int value) throws QTasteTestFailException;

	/**
	 * Write a double value to component identified by given variable name.
	 *
	 * @param varName component variable name
     * @param value double value to write to
	 * @throws QTasteTestFailException if the writing cannot be performed successfully
	 */
	public void writeDouble(String varName, double value) throws QTasteTestFailException;

	/**
	 * Write a string value to component identified by given variable name.
	 *
	 * @param varName component variable name
	 * @param value string value to write to
	 * @throws QTasteTestFailException if the writing cannot be performed successfully
	 */
	public void write(String varName, String value) throws QTasteTestFailException;

	/**
	 * Returns current value, as string, from component identified by given variable name.
	 *
	 * @param varName
	 * @param value
	 * @throws QTasteTestFailException if the reading cannot be performed successfully
	 */
	public String read(String varName) throws QTasteTestFailException;

	/**
	 * Returns current value, as integer, from component identified by given variable name.
	 *
	 * @param varName
	 * @return
	 * @throws QTasteTestFailException if the reading cannot be performed successfully
	 */
	public int readInt(String varName) throws QTasteTestFailException;

	/**
	 * Returns current value, as double, from component identified by given variable name.
	 *
	 * @param varName
	 * @return
	 * @throws QTasteTestFailException if the reading cannot be performed successfully
	 */
	public double readDouble(String varName) throws QTasteTestFailException;

	/**
	 * Subscribe to a variable that represents a component.
	 * From this point on, it will be possible to listen to changes of the associated component.
	 * Use any method checkValue to asynchronously get a value of a component.
	 *
	 * @param varName
	 * @throws QTasteTestFailException if the subscription cannot be performed successfully
	 */
	public void addVariableSubscription(final String varName) throws QTasteTestFailException;

	/**
	 * Remove a subscription for a variable that represents a component.
	 * From this point on, it will no longer be possible to listen variables changes asynchronously.
	 * Any of the method checkValue will not work.
	 *
	 * @param varName
	 * @throws QTasteTestFailException if the unsubscribing cannot be performed successfully
	 */
	public void removeVariableSubscription(final String varName) throws QTasteTestFailException;

	/**
	 * Remove all subscription made so far.
	 * From this point on, it will no longer be possible to listen variables changes asynchronously.
	 * Any of the method checkValue will not work.
	 *
	 * @param varName
	 * @throws QTasteTestFailException if unsubscribing cannot be performed successfully
	 */
	public void removeSubscriptions() throws QTasteTestFailException;

	/**
	 * Wait for the identified component reach the given integer value for a defined timeout.
	 * This method is blocking and will asynchronously listen for the component to reach the expected value.
	 * In case of timeout an exception will be thrown and the current test will fail.
	 *
	 * @param varName
	 * @param value
	 * @param timeout
	 * @throws QTasteException if timeout is reached or variable cannot be listen
	 */
	public void checkValueInt(String varName, int value, long timeout) throws QTasteException;

	/**
	 * Wait for the identified component reach the given integer value, considering the precision of the measurement,
	 *  for a defined timeout.
	 * This method is blocking and will asynchronously listen for the component to reach the expected value.
	 * In case of timeout an exception will be thrown and the current test will fail.
	 *
	 * @param varName
	 * @param value
	 * @param timeout
	 * @param precision
	 * @throws QTasteException if timeout is reached or variable cannot be listen
	 */
	public void checkValueInt(String varName, int value, long timeout, int precision) throws QTasteException;

	/**
	 * Wait for the identified component reach the given double value for a defined timeout.
	 * This method is blocking and will asynchronously listen for the component to reach the expected value.
	 * In case of timeout an exception will be thrown and the current test will fail.
	 *
	 * @param varName
	 * @param value
	 * @param timeout
	 * @throws QTasteException if timeout is reached or variable cannot be listen
	 */
	public void checkValueDouble(String varName, double value, long timeout) throws QTasteException;

	/**
	 * Wait for the identified component reach the given double value, considering the precision of the measurement,
	 *  for a defined timeout.
	 * This method is blocking and will asynchronously listen for the component to reach the expected value.
	 * In case of timeout an exception will be thrown and the current test will fail.
	 *
	 * @param varName
	 * @param value
	 * @param timeout
	 * @param precision
	 * @throws QTasteException if timeout is reached or variable cannot be listen
	 */
	public void checkValueDouble(String varName, double value, long timeout, double precision) throws QTasteException;

	/**
	 * Wait for the identified component state the given state for a defined timeout.
	 * This method is blocking and will asynchronously listen for the component to reach the expected value.
	 * In case of timeout an exception will be thrown and the current test will fail.
	 *
	 * @param varName
	 * @param value
	 * @param timeout
	 * @throws QTasteException if timeout is reached or variable cannot be listen
	 */
	public void checkValueBool(String varName, boolean value, long timeout) throws QTasteException;

}
