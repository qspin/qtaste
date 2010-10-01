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

package com.qspin.qtaste.ui.testcasebuilder;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.impl.TestDataImpl;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestDesignDataPanel  extends JPanel{
    static private Logger logger = Log4jLoggerFactory.getLogger(TestDesignDataPanel.class);
    //private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);
    protected JTextArea mTestDesignDataPane = new JTextArea();
    private TestData mData = new TestDataImpl(1, new LinkedHashMap<String, String>());
    
    public TestDesignDataPanel() {
        super (new BorderLayout());
    }
    
    public JTextArea getDataTextArea()
    {
        return mTestDesignDataPane;
    }
   public void init()
   {
    
        JPanel metaDataPanel = new JPanel();        
        // now create the editor
        mTestDesignDataPane.getCaret().setSelectionVisible(true);

        this.add(metaDataPanel, BorderLayout.NORTH);
        this.add(mTestDesignDataPane);
        setName("Test data");

   }
   public void updateTestData(TestData data) {
       // overwrite already existing value and add new ones
       Iterator<String> it = data.getDataHash().keySet().iterator();
        while (it.hasNext()) {
            try {
                String key = it.next();
                mData.setValue(key, data.getValue(key));
            } catch (QTasteDataException ex) {
                logger.error(ex);
            }
        }
       TestDataBuilder dataBuilder =  new TestDataBuilder(mData);
       this.mTestDesignDataPane.setText(dataBuilder.buildCsvString());
        
        
   }

}
