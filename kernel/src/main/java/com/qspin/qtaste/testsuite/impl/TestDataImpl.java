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

/*
 * TestDataImpl.java
 *
 * Created on 11 octobre 2007, 15:26
 */
package com.qspin.qtaste.testsuite.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.apache.commons.configuration.ConversionException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.lang.DoubleWithPrecision;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author lvboque
 */
public class TestDataImpl implements TestData, Serializable {

    private static final long serialVersionUID = 5468593730307413915L;
    private static Logger logger = Log4jLoggerFactory.getLogger(TestDataImpl.class);
    protected LinkedHashMap<String, String> hash;
    protected HashMap<String, Object> hashFiles;
    protected int rowId;
    protected boolean isSelected = true;
    protected String testCaseDirectory;

    /**
     * Creates a new instance of TestDataImpl
     */
    // TODO: Check if pairs is not more appropriate?
    public TestDataImpl(int rowId, LinkedHashMap<String, String> map) {
        super();
        this.rowId = rowId;
        this.hash = map;
        this.hashFiles = new HashMap<String, Object>();
    }

    @Override
	public LinkedHashMap<String, String> getDataHash() {
        return hash;
    }

    @Override
	public String getValue(String key) throws QTasteDataException {
        if (!hash.containsKey(key)) {
            if (key.equals("INSTANCE_ID")) {
                try {
                    return TestBedConfiguration.getInstance().getDefaultInstanceId();
                } catch (NoSuchElementException ex) {
                    throw new QTasteDataException("Default instance_id is not defined in TestBed configuration!");
                } catch (ConversionException ex) {
                    throw new QTasteDataException("Default instance_id is not valid in TestBed configuration!");
                }
            } else {
                throw new QTasteDataException("TestData doesn't contain value for data " + key);
            }
        }

        String value = hash.get(key);
        if (key.startsWith("FILE_")) {
            File f = new File(value);
            if (!f.isAbsolute()) {
                value = this.getTestCaseDirectory() + File.separator + value;
            }
        }
        return value;
    }

    @Override
	public int getIntValue(String key) throws QTasteDataException {
        try {
            return Integer.parseInt(getValue(key));
        } catch (NumberFormatException e) {
            throw new QTasteDataException(e.toString() + " while parsing integer data " + key, e);
        }
    }

    @Override
	public double getDoubleValue(String key) throws QTasteDataException {
        try {
            return Double.parseDouble(getValue(key));
        } catch (NumberFormatException e) {
            throw new QTasteDataException(e.toString() + " while parsing double data " + key, e);
        }
    }

    @Override
	public boolean getBooleanValue(String key) throws QTasteDataException {
        String value = getValue(key);
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            // try to use integer value: 0=false, 1=true
            try {
                int intValue = Integer.parseInt(value);
                if (intValue == 1) {
                    return true;
                } else if (intValue == 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
            }

            throw new QTasteDataException("Error while parsing boolean data " + key + " for input string: \"" + value + "\"");
        }
    }

    @Override
	public DoubleWithPrecision getDoubleWithPrecisionValue(String key) throws QTasteDataException {
        String value = getValue(key);
        try {
            return new DoubleWithPrecision(value);
        } catch (NumberFormatException e) {
            throw new QTasteDataException("Error while parsing DoubleWithPrecision data " + key + " for input string: \"" + value + "\"", e);
        }
    }

    @Override
	public byte[] getFileContentAsByteArray(String key) throws QTasteDataException {
        getValue(key); // to check if data exists
        if (hashFiles.containsKey(key)) {
            byte[] array = (byte[]) hashFiles.get(key);
            return array;
        } else {
            throw new QTasteDataException("Test data " + key + " has no byte array value");
        }
    }

    @Override
	public String getFileContentAsString(String key) throws QTasteDataException {
        return new String(getFileContentAsByteArray(key));
    }

    @Override
	public String getFileContentAsString(String key, String encoding) throws QTasteDataException {
    	try {
    		return new String(getFileContentAsByteArray(key), encoding);
    	} catch (UnsupportedEncodingException e) {
    		throw new QTasteDataException("Unsupported encoding: " + encoding);
    	}
    }

    @Override
	public void setValue(String key, String value) throws QTasteDataException {
        if (key.startsWith("FILE_")) {
            loadFile(key, value);
        }
        hash.put(key, value);
    }

    @Override
	public void remove(String key) {
        hash.remove(key);
        if (key.startsWith("FILE_")) {
            hashFiles.remove(key);
        }
    }

    @Override
	public boolean contains(String key) {
        return hash.containsKey(key);
    }

    @Override
	public String dump() {
        TreeSet<String> sortedKeys = new TreeSet<String>(hash.keySet());
        String result = new String("{");
        Iterator<String> iKey = sortedKeys.iterator();
        for (int i = 0; i < sortedKeys.size(); i++) {
            if (i > 0) {
                result += ", ";
            }
            String key = iKey.next();
            result += key + "=" + hash.get(key);
        }
        result += "}";
        return result;
    }

    @Override
	public void loadFileIfAny() {
        // Load files defined in testdata if any
        Iterator<String> i = hash.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (key.startsWith("FILE_")) {
                try {
                    loadFile(key, hash.get(key));
                } catch (QTasteDataException e) {
                    logger.error("An error happened while loading the file " + hash.get(key) + ": " + e.getMessage());
                }
            }
        }
    }

    private void loadFile(String key, String filename) throws QTasteDataException {
        File f = new File(filename);
        if (!f.isAbsolute()) {
            f = new File(this.getTestCaseDirectory() + File.separator + filename);
        }

        try {
        	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        	byte[] buffer = new byte[(int) f.length()];

            bis.read(buffer);
            bis.close();
            logger.debug("Loaded file: " + f.getPath() + " size:" + buffer.length);
            hashFiles.put(key, buffer);
        } catch (IOException e) {
            throw new QTasteDataException(e.getMessage());
        }
    }

    @Override
	public void setTestCaseDirectory(String directory) {
        testCaseDirectory = directory;
    }

    @Override
	public String getTestCaseDirectory() {
        return testCaseDirectory;
    }

    @Override
	public int getRowId() {
        return rowId;
    }

    @Override
	public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
	public boolean isSelected() {
        return isSelected;
    }
}
