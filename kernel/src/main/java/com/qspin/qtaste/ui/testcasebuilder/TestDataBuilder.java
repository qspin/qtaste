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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.ui.testcasebuilder;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
public class TestDataBuilder {
    static private Logger logger = Log4jLoggerFactory.getLogger(TestDataBuilder.class);
    private TestData mData;
    
    public TestDataBuilder(TestData data) {
        mData = data;
    }
    public String buildPythonString() {
        
        Iterator<String> it = mData.getDataHash().keySet().iterator();
        String returnString= "";
        while (it.hasNext()) {
            try {
                String key = it.next();
                //returnString += "'" + key + "':";
                String value = mData.getValue(key);
                returnString += "'" + value + "'";
                if (it.hasNext()) returnString +=",";
            } catch (QTasteDataException ex) {
                logger.error(ex);
            }
        }
        return returnString;
    }
            
    public String buildCsvString() {
        Iterator<String> it = mData.getDataHash().keySet().iterator();
        String returnString= "";
        // define the column name
        while (it.hasNext()) {
            String key = it.next();
            returnString += key;
            if (it.hasNext()) returnString +=";";
        }
        returnString+= "\n";
        // define the values name
        Iterator<String> values = mData.getDataHash().values().iterator();
        while (values.hasNext()) {
            String value = values.next();
            returnString += value;
            if (values.hasNext()) returnString +=";";
        }
        return returnString; 
    }
}
