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

package com.qspin.qtaste.ui.testcampaign;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.campaign.Campaign;
import com.qspin.qtaste.kernel.campaign.CampaignManager;
import com.qspin.qtaste.ui.MainPanel;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.treetable.JTreeTable;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.ThreadManager;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestCampaignMainPanel extends JPanel {

    private MainPanel parent;
    private JTreeTable treeTable;
    private JComboBox metaCampaignComboBox = new JComboBox();
    private JButton addNewMetaCampaignButton = new JButton("Add");
    private JButton saveMetaCampaignButton = new JButton("Save");
    private JButton runMetaCampaignButton = new JButton("Run");
    private boolean isExecuting = false;
    private CampaignExecutionThread testExecutionHandler = null;
    private MetaCampaignFile selectedCampaign = null;
    private static Logger logger = Log4jLoggerFactory.getLogger(TestCampaignMainPanel.class);
    private final List<ActionListener> campaignActionListeners = Collections.synchronizedList(new LinkedList<ActionListener>());
    public static final int RUN_ID = 0;
    public static final String STARTED_CMD = "STARTED";
    public static final String STOPPED_CMD = "STOPPED";
    private static final String LAST_SELECTED_CAMPAIGN_PROPERTY = "last_selected_campaign";

    public TestCampaignMainPanel(MainPanel parent) {
        super(new BorderLayout());
        this.parent = parent;
        genUI();
    }

    public void genUI() {
        TestCampaignTreeModel model = new TestCampaignTreeModel("Test Campaign");
        treeTable = new JTreeTable(model);

        FormLayout layout = new FormLayout("6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px, pref, 6px:grow", "6px, fill:pref, 6px");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        int colIndex = 2;

        JLabel label = new JLabel("Campaign:");
        saveMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/save_32"));
        saveMetaCampaignButton.setToolTipText("Save campaign");
        saveMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (selectedCampaign == null) {
                    logger.warn("No Campaign created");
                    return;
                }
                treeTable.save(selectedCampaign.getFileName(), selectedCampaign.getCampaignName());
            }
        });
        addNewMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/add"));
        addNewMetaCampaignButton.setToolTipText("Define a new campaign");
        addNewMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String newCampaign = JOptionPane.showInputDialog(null,
                        "New campaign creation:",
                        "Campaign name:", JOptionPane.QUESTION_MESSAGE);
                if (newCampaign != null && newCampaign.length() > 0) {
                    int index = addTestCampaign(newCampaign);
                    metaCampaignComboBox.setSelectedIndex(index);
                    MetaCampaignFile currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
                    selectedCampaign = currentSelectedCampaign;
                    if (selectedCampaign != null) {
                    	treeTable.save(selectedCampaign.getFileName(), selectedCampaign.getCampaignName());
                    }
                    metaCampaignComboBox.validate();
                    metaCampaignComboBox.repaint();
                }
            }
        });

        // get last selected campaign
        GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
        String lastSelectedCampaign = guiConfiguration.getString(LAST_SELECTED_CAMPAIGN_PROPERTY);

        // add campaigns found in the list
        MetaCampaignFile[] campaigns = populateCampaignList();
        builder.add(label, cc.xy(colIndex, 2));
        colIndex+=2;
        builder.add(metaCampaignComboBox, cc.xy(colIndex, 2));
        colIndex+=2;

        // add test campaign mouse listener, for the Rename and Remove actions
        TestcampaignMouseListener testcampaignMouseListener = new TestcampaignMouseListener();
        java.awt.Component[] mTestcampaignListComponents = metaCampaignComboBox.getComponents();
        for (int i = 0; i < mTestcampaignListComponents.length; i++) {
        	mTestcampaignListComponents[i].addMouseListener(testcampaignMouseListener);
        }

        metaCampaignComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MetaCampaignFile currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
                if (currentSelectedCampaign != selectedCampaign) {
                    selectedCampaign = currentSelectedCampaign;
	                if (selectedCampaign != null) {
	                    treeTable.removeAll();
	                    if (new File(selectedCampaign.getFileName()).exists()) {
	                        treeTable.load(selectedCampaign.getFileName());
	                    }

	                    GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
	                    guiConfiguration.setProperty(LAST_SELECTED_CAMPAIGN_PROPERTY, selectedCampaign.getCampaignName());
	                    try {
	                        guiConfiguration.save();
	                    } catch (ConfigurationException ex) {
	                        logger.error("Error while saving GUI configuration: " + ex.getMessage(), ex);
	                    }
	                } else {
	                	treeTable.removeAll();
	                }
                }
            }
        });

    	boolean setLastSelectedCampaign = false;
        if (lastSelectedCampaign != null) {
            // select last selected campaign
            for (int i = 0; i < campaigns.length; i++) {
                if (campaigns[i].getCampaignName().equals(lastSelectedCampaign)) {
                    metaCampaignComboBox.setSelectedIndex(i);
                    setLastSelectedCampaign = true;
                    break;
                }
            }
        }
        if (!setLastSelectedCampaign && metaCampaignComboBox.getItemCount() > 0) {
            metaCampaignComboBox.setSelectedIndex(0);
        }

        runMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/running_32"));
        runMetaCampaignButton.setToolTipText("Run campaign");
        runMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (selectedCampaign == null) {
                        logger.warn("No Campaign created");
                        return;
                    }

                    // first save the current campaign if needed
                    if (treeTable.hasChanged()) {
                        treeTable.save(selectedCampaign.getFileName(), selectedCampaign.getCampaignName());
                    }

                    // set SUT version
                    TestBedConfiguration.setSUTVersion(parent.getSUTVersion());

                    testExecutionHandler = new CampaignExecutionThread(selectedCampaign.getFileName());
                    Thread t = new Thread(testExecutionHandler);
                    t.start();
                // set the window to test result
                // TO DO
                } catch (Exception ex) {
                    //
                    logger.error(ex.getMessage(), ex);
                }
            }
        });

        builder.add(addNewMetaCampaignButton, cc.xy(colIndex, 2));
        colIndex+=2;
        builder.add(saveMetaCampaignButton, cc.xy(colIndex, 2));
        colIndex+=2;
        builder.add(runMetaCampaignButton, cc.xy(colIndex, 2));
        colIndex+=2;

        JScrollPane sp = new JScrollPane(treeTable);
        this.add(builder.getPanel(), BorderLayout.NORTH);
        this.add(sp);
    }

    public void setExecuteButtonsEnabled(boolean enabled) {
    	runMetaCampaignButton.setEnabled(enabled);
    }

    private MetaCampaignFile[] populateCampaignList() {
    	// clear the list
    	metaCampaignComboBox.removeAllItems();
        MetaCampaignFile[] campaigns = MetaCampaignFile.getExistingCampaigns();
        for (int i = 0; i < campaigns.length; i++) {
            metaCampaignComboBox.addItem(campaigns[i]);
        }
        return campaigns;

	}

	public JTreeTable getTreeTable() {
        return treeTable;
    }

    public CampaignExecutionThread getExecutionThread() {
        return testExecutionHandler;
    }

    public class UpdateButtons implements Runnable {

        public void run() {
            runMetaCampaignButton.setEnabled(!isExecuting);
        }
    }

    public void addTestCampaignActionListener(ActionListener listener) {
        campaignActionListeners.add(listener);
    }

    public void removeTestCampaignActionListener(ActionListener listener) {
        campaignActionListeners.remove(listener);
    }

    /**
     * Add a test campaign to the test campaigns combobox.
     *
     * @param campaignName test campaign name
     * @return index of the added test campaign in the combobox or -1 if not added
     */
    public int addTestCampaign(String campaignName) {
      // add the campaign in the list, keeping sorted order
        int index = 0;
        while (index < metaCampaignComboBox.getItemCount()) {
            String elementCampaignName = ((MetaCampaignFile)metaCampaignComboBox.getItemAt(index)).getCampaignName();
            int comparison = campaignName.compareToIgnoreCase(elementCampaignName);
            if (comparison < 0) {
                // insert new campaign at this index
                break;
            } else if (comparison == 0) {
                // error: a campaign with the same name already exists
                JOptionPane.showMessageDialog(null, "A test campaign named '" + elementCampaignName + "' already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                index = -1;
                break;
            }
            index++;
        }
        if (index >= 0) {
            MetaCampaignFile newItem = new MetaCampaignFile(campaignName);
            metaCampaignComboBox.insertItemAt(newItem, index);
        }
        return index;
    }

    public class GenerateDocumentActionListener extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            MetaCampaignFile selectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
            try {
                StringWriter output = new StringWriter();
                PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
                interp.setOut(output);
                interp.setErr(output);
                interp.cleanup();
                String args = "import sys;sys.argv[1:]= [r'" + selectedCampaign.getFileName() + "']";
                interp.exec(args);
                interp.exec("__name__ = '__main__'");
                String s =   "execfile(r'tools/TestProcedureDoc/generateTestCampaignDoc.py')";
                interp.exec(s);
                interp.cleanup();
                interp = null;
                File campaingFile = new File(selectedCampaign.getFileName());
                if (campaingFile.exists()) {
                	File resultsFile = new File(campaingFile.getParentFile().getCanonicalPath() + "/" + selectedCampaign.getCampaignName() + "-doc.html");
                	if (resultsFile.exists()) {
	                	if (Desktop.isDesktopSupported()) {
	                		Desktop.getDesktop().open(resultsFile);
	                	} else {
	                		logger.error("Feature not supported by this platform");
	                	}
                	}
                	else {
                		JOptionPane.showMessageDialog(null,
                				"Error during generation of TPO file",
                				"Error", JOptionPane.ERROR_MESSAGE);

                	}
                }

            } catch (PyException ex) {
                logger.error(ex.getMessage(), ex);
        		JOptionPane.showMessageDialog(null,
        				"Error during generation of TPO file\n" + ex.getMessage(),
        				"Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
        		JOptionPane.showMessageDialog(null,
        				"Error during generation of TPO file\n" + ex.getMessage(),
        				"Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public class CampaignExecutionThread implements Runnable {

        private String xmlFileName;

        public CampaignExecutionThread(final String xmlFileName) {
            this.xmlFileName = xmlFileName;
        }

        public void stop() {
        	CampaignManager campaignManager = CampaignManager.getInstance();
        	campaignManager.stopByUser(); // Flag to terminate all loaded test suites
            ThreadGroup root = Thread.currentThread().getThreadGroup();
            ThreadManager.stopThread(root, 0);
        }

        public void run() {
            // listen to events
            SwingUtilities.invokeLater(new UpdateButtons());
            isExecuting = true;

            try {
                synchronized (campaignActionListeners) {
                    Iterator<ActionListener> it = campaignActionListeners.iterator();
                    ActionListener al;
                    while (it.hasNext()) {
                        al = it.next();
                        //
                        al.actionPerformed(new ActionEvent(TestCampaignMainPanel.this, RUN_ID, STARTED_CMD));
                    }
                }
                CampaignManager campaignManager = CampaignManager.getInstance();
                Campaign campaign = campaignManager.readFile(xmlFileName);
                campaignManager.execute(campaign);
            } catch (Exception e) {
                logger.fatal(e.getMessage(), e);
            } finally {
                isExecuting = false;
                SwingUtilities.invokeLater(new UpdateButtons());
                testExecutionHandler = null;
                synchronized (campaignActionListeners) {
                    Iterator<ActionListener> it = campaignActionListeners.iterator();
                    ActionListener al;
                    while (it.hasNext()) {
                        al = it.next();
                        //
                        al.actionPerformed(new ActionEvent(TestCampaignMainPanel.this, RUN_ID, STOPPED_CMD));
                    }
                }
            }
        }
    }

    public class TestcampaignMouseListener extends MouseAdapter {

        private void evaluatePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // display the context dialog
                JPopupMenu menu = new JPopupMenu();
                menu.add(new RenameCampaignAction());
                menu.add(new RemoveCampaignAction());

                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), metaCampaignComboBox);
                menu.show(metaCampaignComboBox, pt.x, pt.y);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            evaluatePopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            evaluatePopup(e);
        }

		class RenameCampaignAction extends AbstractAction {

            public RenameCampaignAction() {
                super("Rename Campaign");
            }

            public void actionPerformed(ActionEvent e) {
            	MetaCampaignFile currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
            	String input = JOptionPane.showInputDialog(null,
                        "Give the new name of the test " + currentSelectedCampaign.getCampaignName(),
                        currentSelectedCampaign.getCampaignName()
                        );
                if (input==null) return;

                boolean result = currentSelectedCampaign.renameFile(input);
                if (!result) {
                	logger.error("Impossible to rename " + currentSelectedCampaign.getFileName() + " to " + input);
                	return;
                }
                metaCampaignComboBox.validate();
                metaCampaignComboBox.repaint();
            }
        }

		class RemoveCampaignAction extends AbstractAction {

            public RemoveCampaignAction() {
                super("Remove Campaign");
            }

            public void actionPerformed(ActionEvent e) {
            	MetaCampaignFile currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();

            	if (JOptionPane.showConfirmDialog(null, "Are you sure to remove the campaign '" + currentSelectedCampaign.getCampaignName() + "'",
            			"Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) ==
                        JOptionPane.OK_OPTION)
                {
            		// delete file
            		currentSelectedCampaign.removeFile();
            		// remove from combo box
            		metaCampaignComboBox.removeItemAt(metaCampaignComboBox.getSelectedIndex());
            		if (metaCampaignComboBox.getItemCount() > 0) {
                        metaCampaignComboBox.setSelectedIndex(0);
                        currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
                        selectedCampaign = currentSelectedCampaign;
                    } else {
                    	selectedCampaign =  null;
                    	metaCampaignComboBox.setSelectedIndex(-1);
                    }

                }
                metaCampaignComboBox.validate();
                metaCampaignComboBox.repaint();
            }
        }
    }

}
