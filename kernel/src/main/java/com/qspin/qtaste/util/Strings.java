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

package com.qspin.qtaste.util;

import java.util.Collection;

/**
 * Strings utilities.
 * 
 * @author David Ergo
 */
public class Strings {

    /**
     * Joins strings or objects string representations using given separator.
     * 
     * @param objects an array of objects to convert to string and join
     * @param separator a string to use a separator
     * @return string containing strings joined using separator
     */
    public static String join(Object[] objects, String separator) {
        String result = null;

        if (objects.length > 0) {
            result = objects[0].toString();
            for (int i = 1; i < objects.length; i++) {
                result += separator + objects[i].toString();
            }
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Joins strings or objects string representations using given separator.
     * 
     * @param objects a collection of objects to convert to string and join
     * @param separator a string to use a separator
     * @return string containing strings joined using separator
     */
    public static String join(Collection<?> objects, String separator) {
        String result = null;
        
        if (objects.size() > 0) {
            for (Object object : objects) {
                if (result == null) {
                    result = object.toString();
                } else {
                    result += separator + object.toString();
                }
            }
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Converts a string to a null-terminated fixed size byte array.
     * 
     * @param s string to convert
     * @param length size of the byte array to return
     * @return byte array of specified length containing the string s null-terminated
     */
    public static byte[] toNullTerminatedFixedSizeByteArray(String s, int length) {
        if (s.length() >= length) {
            s = s.substring(0, length - 1);
        }
        while (s.length() < length) {
            s += '\0';
        }
        return s.getBytes();
    }

    /**
     * Converts a null-terminated byte array into a string.
     * 
     * @param array byte array containing a null-terminated string
     * @return string from array
     */
    public static String fromNullTerminatedByteArray(byte[] array) {
        int stringSize = array.length;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                stringSize = i;
                break;
            }
        }

        return new String(array, 0, stringSize);
    }
}
