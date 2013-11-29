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

package com.qspin.qtaste.datacollection;

import java.io.Serializable;

/**
 * A Data object is the structure used by the datacollection package
 * It consists of a key/value pair containg some extra data like source of data, timestamp
 * @author lvboque
 */

public class Data implements Serializable {

	private static final long serialVersionUID = -5501674332646234262L;

	public enum DataSource {
        OTHER,
        OPC,
        JMX
    
    }; 
    
    private long timestamp;
    private String sender;
    private String dest;
    private String name;
    private Object value;
    private Object type;
    private DataSource source;
    
    /**
     * Create a new instance of Data
     * @param timestamp representing the time when the data has been received
     * @param name of the data (key)
     * @param value is an object (or value) mapped to the name (key)
     * @param source identifier for the provider of the data
     * @param type identifier of the type of data (may change depending on the source/provider)
     */
    public Data(long timestamp, String sender, String dest, String name, Object value, DataSource source, Object type) {
        this.timestamp = timestamp;
        this.name = name;
        this.value = value;
        this.type = type;
        this.sender = sender;
        this.dest = dest;
        this.source = source;
    }
    
    /**
     * Return the type of Data depending on the DataSource
     * @return an object identifying the type of data 
     */
    public Object getType() {
        return this.type;
    }

    /**
     * Return the sender of Data
     * @return an string identifying the sender of the data
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Return the destination of Data
     * @return an string identifying the destination of the data
     */
    public String getDest() {
        return this.dest;
    }
    
    
    /**
     * Return a timestamp representing the time when the data has been received
     * @return timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }
    
   
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Return an Object representing the value mapped to the name/key
     * @return the value mapped to the name/key
     */
    public Object getValue() {
        return this.value;
    }
    
    /**
     * Return the name identifying the data
     * @return the identifier of the data
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Return the identifier of the provider of the data
     * @return the identifier of the provider
     */
    public DataSource getSource() {
        return this.source;
    }
    
    /**
     * Display a string representation of this object to stdout
     */
    public void dump() {
        System.out.println("" + 
                          timestamp + "," +
                           name + "," +
                           value + "," +
                           type
                );
    }
}
