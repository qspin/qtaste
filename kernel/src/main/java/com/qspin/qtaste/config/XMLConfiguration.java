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

package com.qspin.qtaste.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

@SuppressWarnings("serial")
public abstract class XMLConfiguration extends org.apache.commons.configuration.XMLConfiguration {                    
  
	public XMLConfiguration() {               
        super();            
    }
                
	public XMLConfiguration(String file) throws ConfigurationException {               
        super(file);            
    }
                
    /**
     * Fetches the specified property. This task is delegated to the associated
     * expression engine.
     *
     * @param key the key to be looked up
     * @return the found value
     */    
    @Override
    public List<Object> getProperty(String key)
    {
        List<?> nodes = fetchNodeList(key);

        if (nodes.size() == 0)
        {
            return null;
        }
        else
        {
            List<Object> list = new ArrayList<Object>();
            for (Iterator<?> it = nodes.iterator(); it.hasNext();)
            {
                ConfigurationNode node = (ConfigurationNode) it.next();
                if (node.getValue() != null)
                {                                              
                    list.add(node.getValue());
                }
            }

            if (list.size() < 1)
            {
                return null;
            }
            else
            {
                return list;
            }
        }
    }      
}




