/*
    Copyright 2007-2009 QSpin - www.qspin.be

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

package com.qspin.qtaste.testsuite;

import java.util.LinkedHashMap;

import com.qspin.qtaste.lang.DoubleWithPrecision;

/**
 * This is the interface to access the TestData associated to a TestScript
 * @author lvboque
 */
public interface TestData {

    /**
     * Return the value to which the specified key is mapped in this container
     * @param key a key in the container
     * @return the value as string to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists
     */
    public String getValue(String key) throws QTasteDataException;

    /**
     * Return the value to which the specified key is mapped in this container
     * @param key a key in the container
     * @return the value as int to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to int.
     */
    public int getIntValue(String key) throws QTasteDataException;

    /**
     * Return the value to which the specified key is mapped in this container
     * @param key a key in the container
     * @return the value as double to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to double.
     */
    public double getDoubleValue(String key) throws QTasteDataException;

    /**
     * Return the value to which the specified key is mapped in this container
     * @param key a key in the container
     * @return the value as boolean to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to boolean.
     */
    public boolean getBooleanValue(String key) throws QTasteDataException;

    /**
     * Return the content of the file to which the specified key is mapped in this container, as a byte array.
     * @param key a key in the container, starting with "FILE_"
     * @return a byte array containing the content of the file to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to boolean.
     */
    public byte[] getFileContentAsByteArray(String key) throws QTasteDataException;

    /**
     * Return the content of the file to which the specified key is mapped in this container, as a string.
     * @param key a key in the container, starting with "FILE_"
     * @return a string containing the content of the file to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to boolean.
     */
    public String getFileContentAsString(String key) throws QTasteDataException;

    /**
     * Return the value to which the specified key is mapped in this container
     * @param key a key in the container
     * @return the value as com.qspin.qtaste.lang.DoubleWithPrecision object to which the specified key is mapped in this container.
     * @throws com.qspin.qtaste.testsuite.QTasteDataException if the key doesn't exists or if the value cannot be converted to DoubleWithPrecision object.
     */
    public DoubleWithPrecision getDoubleWithPrecisionValue(String key) throws QTasteDataException;

    /**
     * Maps the specified key to the specified value in this container. Neither the key not the value can be null.     
     * @param key a key in the container
     * @param value the value
     * @throws com.qspin.qtaste.testsuite.QTasteDataException
     */
    public void setValue(String key, String value) throws QTasteDataException;

    /**
     * Removes the key and its corresponding value from this container. This method does nothing if the key is not in this container.
     * @param key the key that need to be removed
     */
    public void remove(String key);

    /**
     * Checks if this container contains a value for the specified key.
     * @param key the key to be checked
     * @return true if a value is contained for the specified key, 
     *         false otherwise
     */
    public boolean contains(String key);
    
    /**
     * Dump all the content of the container to a String
     * @return the String
     */
    public String dump();

    /**
     * Force the keys starting with "FILE_" to be loaded (or reloaded) into this container.
     * The content of a file can be retrieved using the getByteArrayValue method.
     * Normally, this method should not be called as it is done at the execution of the TestScript.     
     */
    public void loadFileIfAny();

    public LinkedHashMap<String, String> getDataHash();

    /**
     * 
     * @return diretory containing the testcase
     */
    public String getTestCaseDirectory();

    /**
     * Set the testcase directory to the directory value 
     */
    public void setTestCaseDirectory(String directory);
    
    /**
     * Get the data row id.
     * @return the data row id
     */
    public int getRowId();
    
    /**
     * Set if the data is selected or not (default is selected).
     * @param selected true if the data is selected, false otherwise
     */
    public void setSelected(boolean selected);
    
    /**
     * Check if the data is selected or not.
     * @return true if the data is selected, false otherwise
     */
    public boolean isSelected();
}
