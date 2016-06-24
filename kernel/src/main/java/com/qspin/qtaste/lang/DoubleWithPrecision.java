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

package com.qspin.qtaste.lang;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * A DoubleWithPrecision object represents a Double with a given precision.
 *
 * @author Laurent Vanboquestal
 */
public class DoubleWithPrecision implements Serializable, Comparable<Object> {
    private static final long serialVersionUID = -2641351048813045103L;
    private static final Logger LOGGER = Log4jLoggerFactory.getLogger(DoubleWithPrecision.class);
    private Double mValue;
    private Double mPrecision;

    /**
     * Constructs a new instance of DoubleWithPrecision from given value and precision.
     *
     * @param value the value
     * @param precision the precision
     */
    public DoubleWithPrecision(Double value, Double precision) {
        this.mValue = value;
        this.mPrecision = precision;
    }

    /**
     * Constructs a new instance of DoubleWithPrecision from its string representation.
     *
     * @param s the string representation using the format "<CODE>double(precision_double)</CODE>",
     * e.g. "<CODE>1.3(0.001)</CODE>", or simply a double which means precision is 0.0, e.g. "<CODE>1.3</CODE>"
     * @throws java.lang.NumberFormatException if the string does not contain a parsable DoubleWithPrecision
     */
    public DoubleWithPrecision(String s) throws NumberFormatException {
        // format is: "double(precision_double)" or "double"
        int posOpenParen = s.indexOf("(");
        if (posOpenParen >= 0) {
            int posCloseParen = s.indexOf(")");
            if (posCloseParen <= posOpenParen + 1) {
                throw new NumberFormatException("String doesn't match \"double(precision_double)\" format");
            }
            mValue = Double.parseDouble(s.substring(0, posOpenParen));
            mPrecision = Double.parseDouble(s.substring(posOpenParen + 1, posCloseParen));
        } else {
            mValue = Double.parseDouble(s);
            mPrecision = 0.0;
        }
    }

    /**
     * Returns the value of this DoubleWithPrecision object.
     *
     * @return the value of this object
     */
    public Double getValue() {
        return mValue;
    }

    /**
     * Returns the precision of this DoubleWithPrecision object.
     *
     * @return the precision of this object
     */
    public Double getPrecision() {
        return mPrecision;
    }

    /**
     * Compares this DoubleWithPrecision object to the specified object.
     *
     * @param obj the object to compare with this object, null or of type Double, Integer or DoubleWithPrecision.
     * @return <code>true</code> if the argument is not null and is a Double or Integer object representing a value between (value
     * - precision) and (value + precision) or
     * a DoubleWithPrecision object whoose value is between (value - (precision + obj_precision)) and (value + (precision +
     * obj_precision)),
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Double || obj instanceof Integer) {
            Double d;
            if (obj instanceof Double) {
                d = (Double) obj;
            } else {
                d = ((Integer) obj).doubleValue();
            }
            return ((d >= (mValue - mPrecision)) && (d <= (mValue + mPrecision)));
        }

        if (obj instanceof DoubleWithPrecision) {
            DoubleWithPrecision dwp = (DoubleWithPrecision) obj;
            return ((dwp.mValue >= (mValue - (mPrecision + dwp.mPrecision))) && (dwp.mValue <= (mValue + (mPrecision
                  + dwp.mPrecision))));
        }
        LOGGER.warn("Comparison between a DoubleWithPrecision and a " + obj.getClass().getName() + " is not supported");
        return false;
    }

    /**
     * Returns a hash code for this DoubleWithPrecision object.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.mValue != null ? this.mValue.hashCode() : 0);
        hash = 97 * hash + (this.mPrecision != null ? this.mPrecision.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a string representation of this DoubleWithPrecision object.
     *
     * @return a String representation of this object using "<CODE>value(precision)</CODE>" format
     */
    @Override
    public String toString() {
        return "" + mValue + "(" + mPrecision + ")";
    }

    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }
        if (o instanceof DoubleWithPrecision) {
            if (this.mValue < ((DoubleWithPrecision) o).mValue) {
                return -1;
            }
            // this.mValue is bigger than o.mValue
            return 1;
        }
        if (o instanceof Double) {
            if (this.mValue < (Double) o) {
                return -1;
            }
            // this.mValue is bigger than o.mValue
            return 1;
        }
        throw new ClassCastException("Cannot compare " + this.getClass().getName() + " to " + o.getClass().getName());
    }
}
