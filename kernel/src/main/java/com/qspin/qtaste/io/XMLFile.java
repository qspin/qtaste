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
 * CSVFile.java
 *
 * Created on 11 octobre 2007, 15:34
 */
package com.qspin.qtaste.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.qspin.qtaste.testsuite.TestRequirement;

/**
 * This class is responsible to build a different kind of data structures from an XML file
 * @author simjan
 */
public class XMLFile {

	public static final String ROOT_ELEMENT = "REQUIREMENTS_LINKS";
	public static final String REQUIREMENT_ELEMENT = "REQ";
	public static final String REQUIREMENT_ID = "id";
	public static final String DESCRIPTION_ELEMENT = "REQ_DESCRIPTION";
	public static final String SPACE_REPLACEMENT = "-_-";
    public static final ArrayList<String> COLUMN_NAMES;
    static{
    	COLUMN_NAMES = new ArrayList<String>();
    	COLUMN_NAMES.add(REQUIREMENT_ID);
    	COLUMN_NAMES.add(DESCRIPTION_ELEMENT);
    }
    //private static final Logger logger = Log4jLoggerFactory.getLogger(XMLFile.class);
    private File xmlFile;
    private boolean alreadyParsed = false;
    private List<TestRequirement> dataSet;

    /**
     * Creates a new instance of XMLFile
     */
    public XMLFile(File xmlFile) {
        this.xmlFile = xmlFile;
        dataSet = new ArrayList<TestRequirement>();
    }

    public XMLFile(String xmlFileName) {
        this(new File(xmlFileName));
    }

    public String getName() {
        return xmlFile.getName();
    }

    /**
     * Return a list of HashMap of Name/Value from the XML file
     * @return the list of HashMap of Name/Value read from the XML file
     * @throws java.io.IOException If an error occurs reading the XML file
     */
    public List<TestRequirement> getXMLDataSet() throws IOException, SAXException, ParserConfigurationException {
        parseXMLFile();
        return dataSet;
    }

    /**
     * Return a list of the column names of a XML file
     * @return a list of the column names read from the XML file
     * @throws java.io.FileNotFoundException If the XML file is not found
     * @throws java.io.IOException If an error occurs while reading the XML File
     */
    public List<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    private void parseXMLFile() throws IOException, SAXException, ParserConfigurationException {
        if (!alreadyParsed) {
        	XMLHandler xmlParser = new XMLHandler();
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = fabrique.newSAXParser();
			parseur.parse(xmlFile, xmlParser);
			dataSet = xmlParser.getDecodedRequirement();
        }
        alreadyParsed = true;
    }
}
