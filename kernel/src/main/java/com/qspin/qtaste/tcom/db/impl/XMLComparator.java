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

package com.qspin.qtaste.tcom.db.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLComparator class enables to compare XML Documents structured as described bellow
 * <p><pre>
 *  &lt;?xml version="1.0" encoding="ISO-8859-1"?&gt;
 * 
 *     &lt;Results&gt;    	
 *          &lt;Row&gt;
 *              ...
 *          &lt;/Row&gt;
 * 
 *          &lt;Row&gt;
 *              ...
 *          &lt;/Row&gt;
 *     &lt;/Results&gt;
 * </pre>
 * <br> 
 * @author lvboque
 */
public class XMLComparator {
    private List<String> errorsList = new ArrayList<String>();
   
    private static Node setParameterValues(Node node, HashMap<String, String> parameters) {
        String nodeValue=null;
        Node valueNode= node.getFirstChild();
        if (valueNode!=null)
            nodeValue = valueNode.getNodeValue();
        if (nodeValue !=null) {
            Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> parameter = it.next();
                nodeValue = nodeValue.replace("$"+parameter.getKey(), parameter.getValue());
                valueNode.setNodeValue(nodeValue);
            }
        }
        NodeList nl = node.getChildNodes();
        for (int i =0; i<nl.getLength();i++) {
            Node childNode = nl.item(i);
            childNode = setParameterValues(childNode, parameters);
        }
        return node;
    }
    public static org.w3c.dom.Document setParameterValues(org.w3c.dom.Document doc, HashMap<String, String> parameters) {
        // traverse de doc
        org.w3c.dom.Element rootElement = doc.getDocumentElement();
        // retrieve row by row
        Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
        HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();

        while (it.hasNext()) {
            Entry<String, String> parameter = it.next();
            String parameterValue = parameter.getValue();
            String[] values = parameterValue.split(";");
            parameterMap.put(parameter.getKey(), values);
        }        
        NodeList nl = rootElement.getElementsByTagName("Row");
        for (int i =0; i<nl.getLength();i++) {
            Node node = nl.item(i);
            HashMap<String, String> rowParameters = new HashMap<String, String>();
            Iterator<Entry<String, String[]>> rowIt = parameterMap.entrySet().iterator();
            while (rowIt.hasNext()) {
                Entry<String, String[]> rowParameter = rowIt.next();
                rowParameters.put(rowParameter.getKey(), rowParameter.getValue()[i]);
            }
            node = setParameterValues(node, rowParameters);
        }
        return doc;
    }
    public org.w3c.dom.Document getDocumentFromFile(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    /**
     * Create a structure to store all the rows and key/value for the specified document
     * @param doc the specified Document
     * @return a List containing all the rows and the key/value pairs
     */
    private List<HashMap<String, String>> createRowsHashMap(Document doc, String tableName) {

        List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

        NodeList nl = doc.getElementsByTagName("Row");

        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            // check if the rows belongs to the table name
            Node tableNodeNameAttr = n.getParentNode().getAttributes().getNamedItem("name");
            String tableNameAttr = tableNodeNameAttr.getNodeValue();
            if (!tableNameAttr.equals(tableName)) continue;
            
            if (n.hasChildNodes()) {
                NodeList child = n.getChildNodes();
                HashMap<String, String> hash = new HashMap<String, String>();
                result.add(hash);
                for (int j = 0; j < child.getLength(); j++) {
                    Node childNode = child.item(j);

                    if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    //System.out.println("childNode is :" + childNode.getNodeName() + " with value " + childNode.getTextContent());
                    hash.put(childNode.getNodeName(), childNode.getTextContent());
                }
            }
        }
        return result;
    }

    /**
     * Check that all the rows and elements defined in the doc1 are included in doc2
     * @param doc1 The source document
     * @param doc2 The checked document
     * @return True if all the elements defined in the doc1 exists in the doc2
     */
    public boolean compare(Document doc1, Document doc2, String tableName) {
        NodeList nl = doc1.getElementsByTagName("Row");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            // check if the rows belongs to the table name
            Node tableNodeNameAttr = n.getParentNode().getAttributes().getNamedItem("name");
            String tableNameAttr = tableNodeNameAttr.getNodeValue();
            if (!tableNameAttr.equals(tableName)) continue;
            Node tableNodeKeyAttr = n.getParentNode().getAttributes().getNamedItem("key");
            String tableKeyAttr = tableNodeKeyAttr.getNodeValue();
            String[] primaryKeyFields = new String[]{tableKeyAttr};
            return compare(doc1, doc2, tableName, primaryKeyFields);
            
        }
        return true; // table is not to be compared (not found)
    }
    /**
     * Check that all the rows and elements defined in the doc1 are included in doc2
     * @param doc1 The source document
     * @param doc2 The checked document
     * @param primaryKeyFields A String array containing all the field names composing the primary key
     * @return True if all the elements defined in the doc1 exists in the doc2
     */
    public boolean compare(Document doc1, Document doc2, String tableName, String[] primaryKeyFields) {
        // Remove errors of previous calls
        errorsList.clear();
        // Iterator all the Row

        List<HashMap<String, String>> doc1Hash = createRowsHashMap(doc1, tableName);
        List<HashMap<String, String>> doc2Hash = createRowsHashMap(doc2, tableName);

        for (Iterator<HashMap<String, String>> row1 = doc1Hash.iterator(); row1.hasNext();) {
            HashMap<String, String> hash1 = row1.next();
            //System.out.println(hash1.toString());

            boolean found = false;
            for (Iterator<HashMap<String, String>> row2 = doc2Hash.iterator(); row2.hasNext();) {
                HashMap<String, String> hash2 = row2.next();
                int i = 0;

                while (i < primaryKeyFields.length) {
                    String key = primaryKeyFields[i];

                    if (!hash2.containsKey(key)) {
                        // Error the primary key field is not present!                        
                        errorsList.add("Error the primary key field is not present!");
                    }

                    String value1 = hash1.get(key);
                    String value2 = hash2.get(key);

                    if (value1.equals(value2)) {
                        //System.out.println("One part is found!");
                    } else {
                        //System.out.println("not the row");
                        break;
                    }
                    i++;
                }
                found = (i == primaryKeyFields.length);

                if (found) {
                    //System.out.println("The primary key is found!");
                    // The matching row is found, check the content of all fields                    
                    Iterator<String> it = hash1.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        String value1 = hash1.get(key);
                        String value2 = hash2.get(key);
                        if (!value1.equals(value2)) {
                            errorsList.add("Expected to get " + value1 + " for the field " + key + " but got " + value2 + " for row" + getPrimaryKeysExtraDetails(hash1, primaryKeyFields));
                        }
                    }
                    break;
                }
            }
            // The row with the primary key is not found
            if (!found) {               
                errorsList.add("Cannot find row " + getPrimaryKeysExtraDetails(hash1, primaryKeyFields));
            }
        }
        return (errorsList.size() == 0);
    }

    private String getPrimaryKeysExtraDetails(HashMap<String, String> hash, String[] primaryKeyFields) {
        String message = " with primary key";        
        for (int i = 0; i < primaryKeyFields.length; i++) {            
            message += " " + primaryKeyFields[i] + "=" + hash.get(primaryKeyFields[i]);            
        }
        return message;
    }

    /**
     * Check that all the key/value pair contained in hash1 exists in hash2
     * @param hash1 The hash containing key/value
     * @param hash2 The hash containing key/value 
     * @return True only if all the key/value contained in hash1 are present in hash2
     */
    /*
    private boolean isIncluding(HashMap<String, String> hash1, HashMap<String, String> hash2) {
        Set<Map.Entry<String, String>> list = hash1.entrySet();
        Iterator<Map.Entry<String, String>> i = list.iterator();

        boolean allVariablesMatch = true;

        while (i.hasNext()) {
            Map.Entry<String, String> entry = i.next();
            String expectedKey = entry.getKey();
            String expectedValue = entry.getValue();
            if (hash2.containsKey(expectedKey)) {
                String actualValue = hash2.get(expectedKey);
                if (!actualValue.equals(expectedValue)) {
                    allVariablesMatch = false;
                }
            } else {
                allVariablesMatch = false;
            }
        }
        return allVariablesMatch;
    }
    */

    /**
     * Return an Iterator of String containing all the differences detected while comparing the documents
     * @return Iterator<String> an iterator of String describing all the differences
     */
    public Iterator<String> getErrorsList() {
        return errorsList.iterator();
    }
}