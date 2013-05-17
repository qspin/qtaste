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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.ui.TCTreeNode;
import com.qspin.qtaste.ui.tools.FileMask;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.JTreeNode;
import com.qspin.qtaste.ui.tools.TestDataNode;
import com.qspin.qtaste.ui.tools.TristateCheckBox;
import com.qspin.qtaste.ui.treetable.TreeTableModel;
import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Log4jLoggerFactory;


/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestCampaignTreeModel extends DefaultTreeModel //AbstractTreeTableModel 
                             implements TreeTableModel {
    // Names of the columns.
    private static Logger logger = Log4jLoggerFactory.getLogger(TestCampaignTreeModel.class);
    private final String TESTUITE_DIR = "TestSuites";
    
    ArrayList<String> testbedList;
    ArrayList<TCTreeNode> testsuiteDir;
    private HashMap<String, HashMap<String, TristateCheckBox.State>> metaCampaignSelectionList;

    // Types of the columns.
    static protected Class<?>[]  cTypes = {TreeTableModel.class, String.class};

    
    public TestCampaignTreeModel(String rootName) {         
	//super(new FileNode(new File(testSuiteDir), testSuiteDir)); 
        super(new TCTreeNode(rootName,true));

        // check the available test beds
        testbedList = new ArrayList<String>();
        testsuiteDir = new ArrayList<TCTreeNode>();
        metaCampaignSelectionList = new HashMap<String, HashMap<String, TristateCheckBox.State>>();
        
        String testbedDir = StaticConfiguration.TESTBED_CONFIG_DIRECTORY;
        File fTestbedDir = new File(testbedDir);
        FileMask fileMask = new FileMask();
        fileMask.addExtension(StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION);
        File[] fTestbedList = FileUtilities.listSortedFiles(fTestbedDir,fileMask);
        for (int i = 0; i < fTestbedList.length; i++) {
            // remove the extension
            String testbedName = fTestbedList[i].getName().substring(0,
                    fTestbedList[i].getName().lastIndexOf("."));
            testbedList.add(testbedName);
        }
        
    }

    public int getColumnCount() {
        return testbedList.size()+1;
    }

    public String getColumnName(int column) {
        if (column==0) return "Test suites";
        else return testbedList.get(column-1);
    }

    public Class<?> getColumnClass(int column) {
        if (column==0) return TreeTableModel.class;
        else return Boolean.class;
    }
    
    public boolean isCellEditable(Object node, int column) {
        return true;
    }
    protected File getTestCase(Object node) {
        TCTreeNode tcNode = (TCTreeNode)node;
        
        if (tcNode.getUserObject() instanceof FileNode) {
            FileNode fileNode = ((FileNode)tcNode.getUserObject()); 
            return fileNode.getFile();       
        }
        else
        {
            return null;
        }
    }    

    public void setValueAt(Object aValue, Object node, int column)
    {
        if (column==0) return;
        TCTreeNode tcNode = (TCTreeNode)node;
        if (tcNode.getUserObject() instanceof JTreeNode) {            
            JTreeNode jTreeNode = (JTreeNode)tcNode.getUserObject();            
            this.setNodeState(jTreeNode, getColumnName(column), (TristateCheckBox.State)aValue);
        }
        else {
            this.setNodeState(tcNode.toString(), getColumnName(column), (TristateCheckBox.State)aValue);
            
        }
    }
    
    public TristateCheckBox.State getNodeState(JTreeNode node, String testbedName) {
        return getNodeState(node.getId(), testbedName);
    }

    public TristateCheckBox.State getNodeState(String nodeId, String testbedName) {
        if (metaCampaignSelectionList.containsKey(nodeId)) {
                HashMap<String, TristateCheckBox.State> currentStatus = metaCampaignSelectionList.get(nodeId);
                TristateCheckBox.State testbedState = currentStatus.get(testbedName);
                return testbedState;
        }
        else return TristateCheckBox.NOT_SELECTED;
        
    }
    
    public void setNodeState(JTreeNode node, String testbedName, TristateCheckBox.State state) {
        setNodeState(node.getId(), testbedName, state);
    }
    
    public void setNodeState(String nodeId, String testbedName, TristateCheckBox.State state) {
        if (getMetaCampaignSelectionList().containsKey(nodeId)) {
            HashMap<String, TristateCheckBox.State> currentStatus = getMetaCampaignSelectionList().get(nodeId);
            currentStatus.put(testbedName, state);
        }
        else
        {
            HashMap<String, TristateCheckBox.State> testbedSelectionList = new HashMap<String, TristateCheckBox.State>();
            for (int i=1; i<this.getColumnCount(); i++) {
                testbedSelectionList.put(this.getColumnName(i), this.getColumnName(i).equals(testbedName) ? state : TristateCheckBox.NOT_SELECTED);
            }
            getMetaCampaignSelectionList().put(nodeId, testbedSelectionList);
        }
    }
    public void updateChild(JTreeNode childNode, String testbedName) {
        // get the node associated to the treepath
        if (childNode==null) return;
        int childCount = childNode.getChildren().length;
        if (childCount > 0) {
            // get the stored object maintaining the status of all nodes
            // navigate through the children
            for (int i = 0; i < childCount; i++) {
                JTreeNode childFileNode = null;
                childFileNode = (JTreeNode) childNode.getChildren()[i];
                // retrive the status of its parent (from stored object model)
                TristateCheckBox.State parentValue = this.getNodeState(childNode, testbedName);
                if (parentValue!= TristateCheckBox.DONT_CARE) {
                    // update the object model for this child
                    setNodeState(childFileNode,testbedName, parentValue);
                    // recursive update to children of this child
                    updateChild(childFileNode, testbedName);
                }
            }
        }
        this.nodeChanged((TreeNode)this.getRoot());
        
    }
    
    public void updateRootNode(String testbedName) {
// Testsuites root node
        int countChecked=0, countUnchecked=0;
        int childCount = ((TCTreeNode)this.getRoot()).getChildCount();
        Enumeration<?> enumChildren = ((TCTreeNode)this.getRoot()).children();
        while (enumChildren.hasMoreElements()) {
            TCTreeNode childParentNode = (TCTreeNode)enumChildren.nextElement();
            JTreeNode childParentFileNode = (JTreeNode)childParentNode.getUserObject();
            TristateCheckBox.State childState = getNodeState(childParentFileNode, testbedName);
            if (childState==TristateCheckBox.SELECTED)
                countChecked++;
            if (childState==TristateCheckBox.NOT_SELECTED)
                countUnchecked++;

        }        
        TristateCheckBox.State parentState = TristateCheckBox.DONT_CARE;
        if (countUnchecked == childCount) {
            parentState = TristateCheckBox.NOT_SELECTED;
        } else if (countChecked == childCount) {
            parentState = TristateCheckBox.SELECTED;
        } else {
            parentState = TristateCheckBox.DONT_CARE;
        }
        int colIndex = this.getColumnIndex(testbedName);
        setValueAt(parentState, this.getRoot() , colIndex);
        this.nodeChanged((TreeNode)this.getRoot());
    }
    public void updateParent(JTreeNode childNode, String testbedName) {
        if (childNode==null) return;
        File childFile = childNode.getFile();
        File parentFile = childFile.getParentFile();
        if (parentFile==null) return;
        int countChecked = 0;
        int countUnchecked = 0;
        int childCount=0;
        JTreeNode parentNode=null;
        if (parentFile.getParentFile()== null)
        {
            this.updateRootNode(testbedName);
            return;
        }
        else {
            parentNode = this.getTestSuite(parentFile.getPath().replace("\\","/"));
            if (parentNode == null)
            {
                File parentParentFile = parentFile.getParentFile();
                while (parentParentFile.getParentFile()!=null) {
                    parentNode = this.getTestSuite(parentParentFile.getPath().replace("\\","/"));                    
                    if (parentNode!=null) {
                        updateParent(parentNode,testbedName);
                        break;
                    }
                    parentParentFile = parentParentFile.getParentFile();
                    
                }
                this.updateRootNode(testbedName);
                
            }
            if (parentNode==null) return;
            childCount = parentNode.getChildren().length;

            Object[] childParentNodes = (Object[])parentNode.getChildren();
            for (int i=0; i < childParentNodes.length; i++) {
                JTreeNode childParentNode = (JTreeNode)childParentNodes[i];
                TristateCheckBox.State testbedState = getNodeState(childParentNode, testbedName);
                if (testbedState == TristateCheckBox.SELECTED) {
                    countChecked++;
                }
                if (testbedState == TristateCheckBox.NOT_SELECTED) {
                    countUnchecked++;
                }
            }
        }
        TristateCheckBox.State parentState = TristateCheckBox.DONT_CARE;
        if (countUnchecked == childCount) {
            parentState = TristateCheckBox.NOT_SELECTED;
        } else if (countChecked == childCount) {
            parentState = TristateCheckBox.SELECTED;
        } else {
            parentState = TristateCheckBox.DONT_CARE;
        }
        int colIndex = this.getColumnIndex(testbedName);
        if (colIndex!=-1) {            
            if (parentNode==null) {
                setValueAt(parentState, this.getRoot() , colIndex);
            }
            else {
                setValueAt(parentState, new TCTreeNode(parentNode, true) , colIndex);
                updateParent(parentNode, testbedName);
            }
        }
        this.nodeChanged((TreeNode)this.getRoot());
            
    }
    
    public Object getValueAt(Object node, int column) {
	File file = getTestCase(node); 
	try {
	    switch(column) {
	    case 0:
                if (file!=null)
                    return file.getName();
                else
                    return "MetaCampain";
            default:
                //if (file==null) return TristateCheckBox.DONT_CARE;
                TCTreeNode tcNode = (TCTreeNode)node;
                if (tcNode.getUserObject() instanceof JTreeNode) {
                    JTreeNode jTreeNode = (JTreeNode)tcNode.getUserObject();            
                    return this.getNodeState(jTreeNode, getColumnName(column));
                }
                else
                {
                    return this.getNodeState(tcNode.toString(), getColumnName(column));
                }
	    }
	}
	catch  (SecurityException se) { }
   
	return null; 
    }

    public HashMap<String, HashMap<String, TristateCheckBox.State>> getMetaCampaignSelectionList() {
        return metaCampaignSelectionList;
    }

    public JTreeNode getTestSuite(JTreeNode parent,String directory) {
        String parentDir = parent.getId();
        directory = directory.replace("\\", "/");
        if (parentDir.equals(directory))
            return parent;
        
        Object[] childNodes = parent.getChildren();
        for (int i=0; i < childNodes.length; i++) {
            JTreeNode child = (JTreeNode)childNodes[i];
            String childDir = child.getId();
            if (childDir.equals(directory))
                return child;
            if (child.getChildren().length>0) {
                JTreeNode returnValue = getTestSuite(child, directory);
                if (returnValue!=null) return returnValue;
            }
        }
        return null;
        
    }
    public JTreeNode getTestSuite(String directory) {
        TCTreeNode rootNode = (TCTreeNode)this.getRoot();
        Enumeration<?> childNodes = rootNode.children();
        while (childNodes.hasMoreElements()){
            TCTreeNode childNode = (TCTreeNode)childNodes.nextElement();
            JTreeNode child = (JTreeNode)childNode.getUserObject();
            if (child.getId().equals(directory)) {
                return child;
            }
            JTreeNode chidChild = getTestSuite(child, directory);
            if (chidChild!=null) return chidChild;
            if (!child.getFile().getParent().equals("TestSuites")) {
                //getTestSuite
            }
        }
        return null;
        
    }
    public boolean isTestSuite(JTreeNode parent, String directory) {
        String parentDir = parent.getId();
        directory = directory.replace("\\", "/");
        if (parentDir.equals(directory))
            return true;

        Object[] childNodes = parent.getChildren();
        for (int i=0; i < childNodes.length; i++) {
            JTreeNode child = (JTreeNode)childNodes[i];
            String childDir = child.getId();
            if (childDir.equals(directory))
                return true;
            if (child.getChildren().length>0) {
                boolean returnValue = isTestSuite(child, directory);
                if (returnValue) return returnValue;
            }
        }
        return false;
        
    }
    public boolean checkIfParentAdded(File directory) {
        File parentDir = directory.getParentFile();
        if (parentDir==null) return false;
        JTreeNode parentNode = this.getTestSuite(directory.getPath().replace("\\", "/"));
        if (parentNode!=null) return true;
        // recursive through parents
        return checkIfParentAdded(parentDir);
    }
    public void removeIfChildAdded(String directory) {
        // build the childrent of this new parent
        FileNode parentNode = new FileNode(new File(directory), directory,TESTUITE_DIR);
        Object[] childNodes = parentNode.getChildren();
        for (int i=0 ; i < childNodes.length; i++) {
            JTreeNode childNode = (JTreeNode)childNodes[i];
            // now check in the already added nodes if it is already added
            // 
            TCTreeNode rootNode = (TCTreeNode)this.getRoot();
            Enumeration<?> childRootNodes  = rootNode.children();
            while (childRootNodes.hasMoreElements()) {
                TCTreeNode childRootNode = (TCTreeNode)childRootNodes.nextElement();
                JTreeNode childFileRootNode = (JTreeNode)childRootNode.getUserObject();
                if (childFileRootNode.getId().equals(childNode.getId()))
                {
                    this.removeNodeFromParent(childRootNode);
                }
            }
            if (childNode.getChildren().length>0) {
                removeIfChildAdded(childNode.getId());
            }
        }
                
    }
            
    public JTreeNode addTestSuite(String directory) {
        // check first if the directory is already added or its parent
        //logger.trace("Adding '" + directory + "'" + " directory into the test campaign");
        TCTreeNode rootNode = (TCTreeNode)this.getRoot();
        Enumeration<?> childNodes = rootNode.children();
        while (childNodes.hasMoreElements()){
            TCTreeNode childNode = (TCTreeNode)childNodes.nextElement();
            JTreeNode child = (JTreeNode)childNode.getUserObject();
            if (isTestSuite(child, directory)) {
                //logger.trace(directory + "'" + " already defined in the current test campaign, so skip it");
                return getTestSuite(directory);
            }
        }    
        
        // check if the parent dir is already added        
        File testsuiteDirFile = new File(directory);
        if (checkIfParentAdded(testsuiteDirFile)) {
            //logger.trace(directory + "'" + " already defined in another test suite within the current test campaign, so skip it");
            return null;
        }
        
        // check if child of this directory is already added, if yes, remove the previous child
        removeIfChildAdded(directory);
        FileNode childFileNode = new FileNode(testsuiteDirFile, testsuiteDirFile.getName(),TESTUITE_DIR);        
        childFileNode.setShowTestdata(true);
        TCTreeNode testsuiteNode = new TCTreeNode(childFileNode, true);
        
        // add this node to the root node
        // current list
        int currentIndex = testsuiteDir.size();
        testsuiteDir.add(testsuiteNode);
        rootNode.add(testsuiteNode);
        this.fireTreeNodesInserted(this, new Object [] {this.getRoot()}, new int[] {currentIndex}, new Object [] {testsuiteNode});

        fireAddTestSuite(testsuiteNode, childFileNode.getChildren());
        
        return childFileNode;
    }
    public void saveSelectedTestSuites(CampaignWriter writer, JTreeNode parentNode, String testbedName)  {
        // navigate through children
        Object[] children = parentNode.getChildren();
        for (int i=0; i<children.length; i++) {
            JTreeNode childFileNode = (JTreeNode)children[i];
            TristateCheckBox.State state = this.getNodeState(childFileNode, testbedName);
            if (state == TristateCheckBox.SELECTED) {
                if (childFileNode instanceof TestDataNode) {
                     for (int childIndex=0; childIndex < children.length; childIndex++) {
                         TestDataNode dataNode = (TestDataNode)children[childIndex];
                         if (getNodeState(dataNode, testbedName) == TristateCheckBox.SELECTED) {
                        	 writer.addCampaign(testbedName, parentNode.getFile().getPath().replace("\\", "/"), dataNode.getRowIndex());
                         }
                     }
                     // quit the loop because testdata already added and so children are testdata
                     break;
                }
                else
                {
                	writer.addCampaign(testbedName, childFileNode.getFile().getPath().replace("\\", "/"));
                }
            }
             if (state == TristateCheckBox.DONT_CARE) { 
                 // check its children
                 //if children are testdata add specific node
                 Object[] childChildren = childFileNode.getChildren();
                 if (childChildren.length>0) {
                     // check if it's datarow
                     JTreeNode childDataNode = (JTreeNode)childChildren[0];
                     if (childDataNode instanceof TestDataNode) {
                         for (int childIndex=0; childIndex < childChildren.length; childIndex++) {
                             TestDataNode dataNode = (TestDataNode)childChildren[childIndex];
                             if (getNodeState(dataNode, testbedName) == TristateCheckBox.SELECTED)
                             {
                            	 writer.addCampaign(testbedName, childFileNode.getFile().getPath().replace("\\", "/"), dataNode.getRowIndex());
                             }
                         }
                     }
                     else
                         saveSelectedTestSuites(writer, childFileNode, testbedName);
                     
                 }
             }
        }
    }
    
    public void save(String fileName, String campaignName) {
        //
        logger.trace("Saving the campaign " + campaignName + " into the file" + fileName);
        CampaignWriter campaignWriter = new CampaignWriter();
        TCTreeNode rootNode = (TCTreeNode) this.getRoot();
        for (int i = 1; i < this.getColumnCount(); i++) {
            String testbedName = this.getColumnName(i);
            Enumeration<?> enumRootNodeChildren = rootNode.children();
            while (enumRootNodeChildren.hasMoreElements()) {
                TCTreeNode childNode = (TCTreeNode) enumRootNodeChildren.nextElement();
                JTreeNode childFileNode = (JTreeNode) childNode.getUserObject();
                TristateCheckBox.State state = this.getNodeState(childFileNode, testbedName);
                if (state == TristateCheckBox.SELECTED) {
                	campaignWriter.addCampaign(testbedName, childFileNode.getFile().getPath().replace("\\", "/"));
                } else {
                    saveSelectedTestSuites(campaignWriter, childFileNode, testbedName);
                }
            }
        }            
    	campaignWriter.save(fileName, campaignName);
        
    }
    public int getColumnIndex(String colName) {
        for (int i=0; i<this.getColumnCount(); i++) {
            if (this.getColumnName(i).equals(colName)) return i;
        }
        return -1;
    }
    public void load(String xmlFileName) throws Exception {
        try {
            // 
            logger.trace("Loading the campaign file " + xmlFileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFileName);
            doc.getDocumentElement().normalize();
            if (!doc.getDocumentElement().getNodeName().equals("campaign")) {
                throw new Exception(xmlFileName + " is not a valid campain xml file");
            }

            String testbed = "";
            String testsuite = "";
            String rowSelector = null;

            NodeList nodeLst = doc.getElementsByTagName("run");
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node node = nodeLst.item(s);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    testbed = element.getAttribute("testbed");
                    testbed = testbed.substring(0, testbed.indexOf(".xml"));
                    
                    NodeList nodeList = element.getElementsByTagName("testsuite");                    
                    for (int testSuiteIndex=0; testSuiteIndex<nodeList.getLength();testSuiteIndex++) {
                        
                        testsuite = nodeList.item(testSuiteIndex).getAttributes().getNamedItem("directory").getNodeValue();
                        testsuite = testsuite.replace("\\", "/");
                        String dirToAdd = testsuite;
                        if (testsuite.contains("/" ))
                            dirToAdd = testsuite.split("\\/")[0]+ "/" + testsuite.split("\\/")[1];
                        JTreeNode addedNode = addTestSuite(dirToAdd);
                        for (int i=0; i<this.getColumnCount(); i++) {
                            if (this.getColumnName(i).equals(testbed)) {
                                FileNode fileNode = new FileNode(new File(testsuite), testsuite,TESTUITE_DIR);
                                // check if there is testdata or not
                                NodeList childList = nodeList.item(testSuiteIndex).getChildNodes();
                                if (childList.getLength()==0) {
                                    this.setNodeState(fileNode, testbed, TristateCheckBox.SELECTED);
                                }
                                else {
                                    boolean testDataTagFound=false;
                                    for (int c = 0; c < childList.getLength(); c++) {
                                        Node childNode = childList.item(c);
                                        if (childNode.getNodeName().equals("testdata")) {
                                            String testDataFileName = testsuite + "/TestData.csv";
                                            testDataTagFound = true;    
                                            rowSelector = childNode.getAttributes().getNamedItem("selector").getNodeValue();                        
                                            String[] rows = rowSelector.split(",");
                                            for (int rowIndex=0; rowIndex< rows.length; rowIndex++) {
                                            // update the testata nodes
                                            TestDataNode dataNode = new TestDataNode(new File(testDataFileName), testDataFileName, Integer.parseInt(rows[rowIndex]));
                                            this.setNodeState(dataNode, testbed, TristateCheckBox.SELECTED);
                                            updateParent(this.getTestSuite(addedNode, dataNode.getId()), testbed);
                                            
                                            }
                                        }
                                    }
                                    if (!testDataTagFound) {
                                        // add test suite
                                        this.setNodeState(fileNode, testbed, TristateCheckBox.SELECTED);
                                    }
                                }

                                updateChild(this.getTestSuite(addedNode, testsuite), testbed);
                                updateParent(this.getTestSuite(addedNode, testsuite), testbed);
                            }
                        }
                    }
                }
            }
        } catch (SAXException ex) {
            //
            logger.error(ex);
        } catch (IOException ex) {
            //
            logger.error(ex);
        } catch (ParserConfigurationException ex) {
            //
            logger.error(ex);
        }
    }

    public void removeTestbed(String testbedName) {
        Iterator<String> campaigns = metaCampaignSelectionList.keySet().iterator();
        while (campaigns.hasNext()) {
            String campaign = campaigns.next();
            HashMap<String,TristateCheckBox.State> selectedTestbeds= metaCampaignSelectionList.get(campaign);
            if (selectedTestbeds.containsKey(testbedName)) {
                selectedTestbeds.put(testbedName, TristateCheckBox.NOT_SELECTED);
            }
        }
        
    }
    public void removeTestSuiteFromModel(TCTreeNode node) {
        if (node.getUserObject() instanceof JTreeNode) {
            JTreeNode fileNode = (JTreeNode)node.getUserObject();
            metaCampaignSelectionList.remove(fileNode.getId());
        }
        Enumeration<?> childNodesArray = node.children();
        ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
        int[] indices = new int[node.getChildCount()];
        int index=0;
        while (childNodesArray.hasMoreElements()) {
            TCTreeNode childNode = (TCTreeNode)childNodesArray.nextElement();
            indices[index]=index;
            childNodes.add(childNode);
            index++;
            if (childNode.getUserObject() instanceof JTreeNode) {
                JTreeNode childFileNode = (JTreeNode)childNode.getUserObject();                
                metaCampaignSelectionList.remove(childFileNode.getId());            
            }
            removeTestSuiteFromModel(childNode);
        }
    }

    public void removeTestSuite(TCTreeNode node) {
        if (node.getUserObject() instanceof JTreeNode) {
            JTreeNode fileNode = (JTreeNode)node.getUserObject();
            metaCampaignSelectionList.remove(fileNode.getId());
        }
        Enumeration<?> childNodesArray = node.children();
        ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
        int[] indices = new int[node.getChildCount()];
        int index=0;
        while (childNodesArray.hasMoreElements()) {
            TCTreeNode childNode = (TCTreeNode)childNodesArray.nextElement();
            indices[index]=index;
            childNodes.add(childNode);
            index++;
            if (childNode.getUserObject() instanceof JTreeNode) {
                JTreeNode childFileNode = (JTreeNode)childNode.getUserObject();                
                metaCampaignSelectionList.remove(childFileNode.getId());            
            }
            removeTestSuiteFromModel(childNode);
        }
        node.removeAllChildren();        
        this.fireTreeNodesRemoved(this, this.getPathToRoot(node), indices, childNodes.toArray());
        
        //prevent to delete root node - 'Test Campaign'
        if (!node.isRoot()) {
            TCTreeNode parentNode = (TCTreeNode)node.getParent();
            int nodeIndex = parentNode.getIndex(node);            
            parentNode.remove(node);            
            this.fireTreeNodesRemoved(this, this.getPathToRoot(parentNode), new int[] {nodeIndex}, new TreeNode[]{node});    
        }
    }
    public void removeAll() {
        metaCampaignSelectionList.clear();
        // fire property change
        TCTreeNode rootNode = (TCTreeNode)this.getRoot();
        Enumeration<?> childNodesArray = rootNode.children();
        ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
        int[] indices = new int[rootNode.getChildCount()];
        int index=0;
        while (childNodesArray.hasMoreElements()) {
            TreeNode childNode = (TreeNode)childNodesArray.nextElement();
            indices[index]=index;
            childNodes.add(childNode);
            index++;
        }
        rootNode.removeAllChildren();
        
        this.fireTreeNodesRemoved(this, this.getPathToRoot(rootNode), indices, childNodes.toArray());
       
    }
    public void fireAddTestSuite(TCTreeNode parentNode, Object[] childNodes) {
        for (int i = 0; i < childNodes.length;i++) {
            if (childNodes[i] instanceof FileNode) {
                FileNode childFileNode = (FileNode)childNodes[i];
                childFileNode.setShowTestdata(true);
                TCTreeNode childNode = new TCTreeNode(childFileNode, true);
                parentNode.add(childNode);
                this.fireTreeNodesInserted(this, this.getPathToRoot(parentNode), new int[] {i}, new Object [] {childNode});
                if (childFileNode.getChildren()!=null) {
                    if (childFileNode.getChildren().length>0) {
                        fireAddTestSuite(childNode, childFileNode.getChildren());
                    }
                }
            }
            if (childNodes[i] instanceof TestDataNode) {
                TestDataNode childDataNode = (TestDataNode)childNodes[i];
                TCTreeNode childNode = new TCTreeNode(childDataNode, false);
                childDataNode.setShowTestdata(true);
                parentNode.add(childNode);
                this.fireTreeNodesInserted(this, this.getPathToRoot(parentNode), new int[] {i}, new Object [] {childNode});
                
            }
             
        }
    }

}
