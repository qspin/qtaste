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
 * NamesValuesList.java
 *
 * Created on 18 mars 2008, 14:07
 */

package com.qspin.qtaste.util;

import java.io.Serializable;

/**
 * @author der
 */
public class NameValue<N, V> implements Serializable {
    static final long serialVersionUID = 7411230514645455527L;
    public N name;
    public V value;

    public NameValue(N name, V value) {
        this.name = name;
        this.value = value;
    }
}
