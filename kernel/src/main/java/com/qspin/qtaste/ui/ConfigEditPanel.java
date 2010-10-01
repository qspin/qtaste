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

package com.qspin.qtaste.ui;

import com.qspin.qtaste.ui.panel.TitlePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.ui.tools.GridBagLineAdder;
import com.qspin.qtaste.ui.widget.InfoCbBox;
import com.qspin.qtaste.ui.widget.InfoCbBox.Info;
import com.qspin.qtaste.ui.widget.InfoCbBox.Info.Level;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class ConfigEditPanel  extends JDialog {

        static private Logger logger = Log4jLoggerFactory.getLogger(ConfigEditPanel.class);
        private InfoCbBox mInfoBox;
        private JPanel mDetailsPanel;
        private JScrollPane mDetailsScrollPane;
        private Properties mSelectedProperties;         

    public ConfigEditPanel(Window owner) {
        super(owner, "QTaste Configuration", ModalityType.APPLICATION_MODAL);
        TitlePanel mainConfigPanel = new TitlePanel("QTaste Configuration", "main/qspin", false);
        this.setLayout(new GridBagLayout());
        GridBagLineAdder adder = new GridBagLineAdder(this);
        mSelectedProperties = new Properties();
        
        
        // add the Site selection panel
        JPanel siteSelectionPanel = new JPanel(new BorderLayout());
        
        mInfoBox = new InfoCbBox();
        this.initSiteCb(mInfoBox);
        mInfoBox.addActionListener(new CbActionListener(this));
        siteSelectionPanel.add(mInfoBox);
        
        
        adder.addToEnd(mainConfigPanel);
        adder.addToEnd(siteSelectionPanel);
        adder.addSeparator("Details");
        
        mDetailsPanel = new JPanel(new GridLayout(1,4, 10,10));
        mDetailsScrollPane = new JScrollPane(mDetailsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mDetailsScrollPane.setPreferredSize(new Dimension(500,600)); 
        //mDetailsPanel.setLayout(new GridLayout(1,4, 10,10));
        //jsp.getViewport().add(mDetailsPanel);

        adder.addToEnd(mDetailsScrollPane);
        //this.setPreferredSize(new Dimension(600,800));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    public void fillSiteDetails(File siteFile)
    {
        try {
            // the file must be selected through CongigManager
            mSelectedProperties.load(new FileInputStream(siteFile.getAbsoluteFile().getCanonicalPath()));
            
            GridLayout gridLayout = (GridLayout) mDetailsPanel.getLayout();
            gridLayout.setRows(mSelectedProperties.size());
            gridLayout.setColumns(4);
            mDetailsPanel.removeAll();
            
            Iterator it = mSelectedProperties.entrySet().iterator();
            //mDetailsPanel.setLayout(new GridLayout(cfg.getConfig().getKeysSize(), 2));
            // clear the objects
            while (it.hasNext()) {
                Entry<String, String> keyhash = (Entry<String, String>) it.next();
                String key = keyhash.getKey();
                // add objects into the panel
                //String key = it.next();
                //Iterator<String> keyIt = o.keySet().iterator();
                //String key = keyIt.next();
                JLabel label = new JLabel(key);
                JTextField tf = new JTextField();
                tf.setText(keyhash.getValue());
                mDetailsPanel.add(label);
                mDetailsPanel.add(tf);
            }
            mDetailsPanel.revalidate();
            mDetailsScrollPane.revalidate();
            //this.pack();
        } catch (FileNotFoundException ex) {
            logger.error(ex);
        }
         catch (IOException ex) {
            logger.error(ex);
        }
                
    }
    public void initSiteCb(InfoCbBox cb) {
        // get the properties files in the configuration directory
        String confDirectory = "conf";
        File confDirectoryFile = new File(confDirectory);
        File[] configFiles = FileUtilities.listSortedFiles(confDirectoryFile, new FileMask("properties", "QTaste configuration file"));
        for (File configFile : configFiles)
        {
            if (!configFile.isDirectory())  {
                
            
            ConfigInfo fileInfo = new ConfigInfo(configFile.getName(), configFile );
            cb.addInfo(fileInfo);
            }
        }
    }

    private class CbActionListener implements ActionListener {
        
        private ConfigEditPanel mPanel;
        public CbActionListener(ConfigEditPanel panel)
        {
            mPanel = panel;
        }

        public void actionPerformed(ActionEvent e)  {
            InfoCbBox info = (InfoCbBox)e.getSource();
            mPanel.fillSiteDetails(((ConfigInfo)info.getItemAt(info.getSelectedIndex())).mFile);
        }
    }
    private class ConfigInfo implements Info {

        private String mSiteName;
        private File mFile;
        public ConfigInfo(String siteName, File file) {
            mSiteName = siteName;
            mFile = file;
        }
        public Level getLevel() {
            return Level.INFO;
        }

        public String getMessage() {
            return mSiteName;
        }
        
    }
}
