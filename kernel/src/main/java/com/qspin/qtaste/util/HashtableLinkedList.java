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

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author lvboque
 */
public class HashtableLinkedList<N, V> implements Serializable {

    static final long serialVersionUID = 2363181376252177998L;
    private transient Hashtable<N, LinkedList<V>> hash;
    private LinkedList<NameValue<N, V>> order;
    private long clearHistoryTimestamp;
    //private static Logger logger = Log4jLoggerFactory.getLogger(HashtableLinkedList.class);

    /**
     * Creates a new instance of HashtableLinkedList
     */
    public HashtableLinkedList() {
        hash = new Hashtable<>();
        order = new LinkedList<>();
        clearHistoryTimestamp = System.currentTimeMillis();
    }

    public synchronized void put(N name, V value) {
        putInHash(name, value);
        order.add(new NameValue<>(name, value));
    }

    public synchronized boolean remove(N name, V value) {
        LinkedList<V> list = hash.get(name);
        return list != null && list.remove(value);
    }

    /**
     * Warning: you may get a ConcurrentModificationException when using the
     * ListIterator if this object is modified concurrently
     *
     * @param name a name
     * @return a list iterator to the list mapped to name
     */
    public synchronized ListIterator<V> get(N name) {
        LinkedList<V> list = hash.get(name);
        if (list != null) {
            return list.listIterator(0);
        } else {
            return null;
        }
    }

    public synchronized V getLast(N name) {
        LinkedList<V> list = hash.get(name);
        if (list != null) {
            return list.getLast();
        } else {
            return null;
        }
    }

    public synchronized Enumeration<N> keys() {
        return hash.keys();
    }

    public synchronized int size() {
        return hash.size();
    }

    /**
     * Warning: you may get a ConcurrentModificationException when using the
     * ListIterator if this object is modified concurrently
     *
     * @return a list iterator to the list of insertions sorted by time
     */
    public synchronized ListIterator<NameValue<N, V>> getByInsertionTime() {
        return order.listIterator(0);
    }

    public synchronized void clear() {
        hash.clear();
        order.clear();
        clearHistoryTimestamp = System.currentTimeMillis();
    }

    public synchronized void clearHistory() {
        // remove all values but last from hash
        Enumeration<N> e = hash.keys();
        while (e.hasMoreElements()) {
            N name = e.nextElement();
            LinkedList<V> l = hash.get(name);
            if (l.size() > 1) {
                V last = l.getLast();
                l.clear();
                l.add(last);
            }
        }

        // remove all values but last from order
        Iterator<NameValue<N, V>> i = order.iterator();
        while (i.hasNext()) {
            NameValue<N, V> nameValue = i.next();
            LinkedList<V> l = hash.get(nameValue.name);
            if (!l.contains(nameValue.value)) {
                i.remove();
            }
        }

        clearHistoryTimestamp = System.currentTimeMillis();
    }

    public synchronized long getClearHistoryTimestamp() {
        return clearHistoryTimestamp;
    }

    private void putInHash(N name, V value) {
        LinkedList<V> list = hash.get(name);
        if (list != null) {
            list.add(value);
        } else {
            list = new LinkedList<>();
            list.add(value);
            hash.put(name, list);
        }
    }

    // write order linked-list but not hash hashtable (transient)
    // difference with default method is that this one is synchronized
    private synchronized void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    // read order linked-list but not hash hashtable (transient)
    // and rebuild hash hashtable
    private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // rebuild hash hashtable
        hash = new Hashtable<>();
        for (NameValue<N, V> nameValue : order) {
            putInHash(nameValue.name, nameValue.value);
        }
    }
}
