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

package com.qspin.qtaste.datacollection.pusher.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.qspin.qtaste.datacollection.Data;
import com.qspin.qtaste.datacollection.collection.CacheImpl;
import com.qspin.qtaste.datacollection.collection.DataReceivedListener;
import com.qspin.qtaste.io.ObjectFile;
import com.qspin.qtaste.util.HashtableLinkedList;

/**
 * This class is responsible to broadcast Data object into the Cache respecting the timing of a previously
 * recorded sequence of data.
 * @author lvb
 */
public class CachePusherImpl extends AbstractPusher {
    //private static Logger logger = Log4jLoggerFactory.getLogger(CachePusherImpl.class);
    private ArrayList<Data> array;
    
     @SuppressWarnings("unchecked")
    public CachePusherImpl(File file) throws Exception {
        super((HashtableLinkedList<String,Data>) new ObjectFile(file.toString()).load());
    }
    
    public CachePusherImpl(HashtableLinkedList<String,Data> data) throws Exception {
        super(data);
        this.array = new ArrayList<Data>();
    }

    public void prepare(String name, Data data) {
        array.add(data);
    }

    public void publish() {
        //logger.info("publish");  
        Iterator<Data> i = array.iterator();
        Data data = null;
        DataReceivedListener listener = CacheImpl.getInstance();
        while (i.hasNext()) {
            data = i.next();
            listener.dataReceived(data.getTimestamp(), data.getSender(), data.getDest(), data.getName(), data.getValue(), data.getSource(), data.getType());
            //logger.debug("Publishing " + data.getName() + " timestamp " + data.getTimestamp());       
        }
        array.clear();
     }
}
