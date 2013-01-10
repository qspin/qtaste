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

/**
 * A ResultSetXMLConverter is able to create a XML Document from a JDBC ResultSet object
 * @author lvboque
 */
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ResetSetXMLConverter class enables to convert a JDBC ResultSet objects into an XML Documents
 * The format of the generated XML document will be as follow:
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
public class ResultSetXMLConverter {

    public static String getField(Document doc, String fieldName) throws Exception {
        String fieldValues="";
        NodeList nl = doc.getElementsByTagName("Row");
        for (int i=0; i< nl.getLength(); i++) {
            Node rowNode = nl.item(i);
            NodeList colNodes = rowNode.getChildNodes();            
            for (int j = 0; j < colNodes.getLength(); j++) {
                Node colNode = colNodes.item(j);
                String columnName = colNode.getNodeName();
                // get its value
                Node valueNode = colNode.getFirstChild();
                if (columnName.equals(fieldName) && (valueNode!=null)) {
                    String columnValue = valueNode.getNodeValue();
                    if (columnValue==null) continue;
                    if (fieldValues.length()>0)
                        fieldValues +=  ";" + columnValue;
                    else
                        fieldValues += columnValue;                        
                }
            }
        }
        return fieldValues;
    }
    /**
     * Return an XML document containing the specified ResultSet.
     * @param rs The specified ResultSet
     * @return the XML document
     * @throws java.lang.Exception 
     */
    public static Document getResultSetAsXML(ResultSet rs, String tableName, String keyName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element results = doc.createElement("Results");
        doc.appendChild(results);
       

        ResultSetMetaData rsmd = rs.getMetaData();
        // add the name of the table
        Element tableNameElement = doc.createElement("table");
        tableNameElement.setAttribute("name", tableName);
        tableNameElement.setAttribute("key", keyName);
        results.appendChild(tableNameElement);        

        
        int colCount = rsmd.getColumnCount();

        while (rs.next()) {
            Element row = doc.createElement("Row");
            tableNameElement.appendChild(row);
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(i);
                // If the value is null, don't put it in the XML Document
                if (value != null) {
                    String colType = rsmd.getColumnTypeName(i);
                    Element node = doc.createElement(columnName);
                    if (colType.equals("bytea")) {
                        String byteaStr="";
                        byte[] imgBytes = rs.getBytes(i);
                        for (int byteIndex = 0; byteIndex < imgBytes.length; byteIndex++) {
                            if (byteIndex!=0)
                                byteaStr= byteaStr + ";" + Byte.toString(imgBytes[byteIndex]);
                            else
                                byteaStr= Byte.toString(imgBytes[byteIndex]);
                        }
                        node.appendChild(doc.createTextNode(byteaStr));
                    }
                    else
                    {
                        node.appendChild(doc.createTextNode(value.toString()));
                    }
                    row.appendChild(node);
                }
            }
        }
        return doc;
    }
    /**
     * Return an XML document containing the specified ResultSet.
     * @param rs The specified ResultSet
     * @return the XML document
     * @throws java.lang.Exception 
     */
    public static Document getResultSetAsXML(ResultSet rs, String tableName) throws Exception {
        return getResultSetAsXML(rs, tableName, "");
    }
  
    /**
     * Return the XMLDocument formatted as a String
     * @param doc the XML Document
     * @return A String representation of the XML Document
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws javax.xml.transform.TransformerException
     */
    public static String getDocumentAsXmlString(Document doc)
            throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        // we want to pretty format the XML output
        // note : this is broken in jdk1.5 beta!
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //
        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        return sw.toString();
    }
}

