package com.qspin.qtaste.util;

/**
 * Structure containing a value and a timestamp.
 */
public class TimedValue {

    public Object value;
    public long timestamp;

    public TimedValue(Object value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }
}
