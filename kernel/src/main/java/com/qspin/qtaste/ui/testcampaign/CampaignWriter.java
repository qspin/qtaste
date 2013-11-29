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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.util.Log4jLoggerFactory;


public class CampaignWriter {
	
    private static Logger logger = Log4jLoggerFactory.getLogger(CampaignWriter.class);
    private HashMap<String, ArrayList<Testcase>> m_campaign;
	
    public CampaignWriter() {
    	m_campaign = new HashMap<String, ArrayList<Testcase>>();
    }
    public void addCampaign(String testbed, String testcaseDirectory, int rowId) {
    	if (m_campaign.containsKey(testbed)) {
    		ArrayList<Testcase> testcases = m_campaign.get(testbed);
            Iterator<Testcase> testcaseIt = testcases.iterator();
            while (testcaseIt.hasNext()) {
            	Testcase testcase = testcaseIt.next();
            	if (testcaseDirectory.equals(testcase.getDirectory())) {
            		if (testcase.rowId!=null) {
            			if (testcase.getRowid().contains(rowId)) return; // already added
            			testcase.getRowid().add(rowId);
            			return;
            		}
            		else {
            			// set all rows by settings the array to null
            			testcase.setRowId(new ArrayList<Integer>());
            			testcase.getRowid().add(rowId);
            			return;
            		}
            	}
            }
            // add the testcase
            Testcase testcase = new Testcase(testcaseDirectory, new ArrayList<Integer>());
            testcase.getRowid().add(rowId);
            testcases.add(testcase);
            return;
    	}
    	else
    	{
            ArrayList<Testcase> testcases = new ArrayList<Testcase>();
            Testcase testcase = new Testcase(testcaseDirectory, new ArrayList<Integer>());
            testcase.getRowid().add(rowId);
            testcases.add(testcase);
            m_campaign.put(testbed,  testcases);
    	}
    	
    }
    public void addCampaign(String testbed, String testcaseDirectory) {
    	if (m_campaign.containsKey(testbed)) {
    		ArrayList<Testcase> testcases = m_campaign.get(testbed);
            Iterator<Testcase> testcaseIt = testcases.iterator();
            while (testcaseIt.hasNext()) {
            	Testcase testcase = testcaseIt.next();
            	if (testcaseDirectory.equals(testcase.getDirectory())) {
            		if (testcase.rowId==null) // all rows
            			return;
            		else
            			// set all rows by settings the array to null
            			testcase.rowId =null;
            		return;
            	}
            }
            // add the testcase
            Testcase testcase = new Testcase(testcaseDirectory, null);
            testcases.add(testcase);
    	}
    	else
    	{
            Testcase testcase = new Testcase(testcaseDirectory, null);
            ArrayList<Testcase> testcases = new ArrayList<Testcase>();
            testcases.add(testcase);
            m_campaign.put(testbed,  testcases);
    	}
    }
    	
	public void save(String fileName, String campaignName) {
        logger.trace("Saving the campaign " + campaignName + " into the file" + fileName);
        java.io.FileWriter fw = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element campaign = doc.createElement("campaign");
            campaign.setAttribute("name", campaignName);
            doc.appendChild(campaign);
            
            Iterator<String> testbeds = m_campaign.keySet().iterator();
            // sort the iterator
            ArrayList<String> testbedList = new ArrayList<String>();
            while (testbeds.hasNext()) {
            	String testbedName = testbeds.next();
            	testbedList.add(testbedName);
            }
            Collections.sort(testbedList);
            testbeds = testbedList.iterator();
            while (testbeds.hasNext()) {
            	String testbedName = testbeds.next();
            	String testbedFileName = testbedName + "." + StaticConfiguration.CAMPAIGN_FILE_EXTENSION;
            
                Element run = doc.createElement("run");
                run.setAttribute("testbed", testbedFileName);
                ArrayList<Testcase> testcases = m_campaign.get(testbedName);
                Iterator<Testcase> testcaseIt = testcases.iterator();
                while (testcaseIt.hasNext()) {
                	Testcase testcase = testcaseIt.next();
                    Element testsuite = doc.createElement("testsuite");
                    testsuite.setAttribute("directory", testcase.getDirectory());
                    // check if rows of testdata must be specified
                    if (testcase.getRowid()!=null) {
                        Element testdata = doc.createElement("testdata");
                        String selector = "";              
                        Iterator<Integer> rowIt = testcase.getRowid().iterator();
                        while (rowIt.hasNext()) {
                        	Integer rowId = rowIt.next();
                            if (selector.length() >= 1) selector += ",";
                            selector += rowId.toString();
                        }
                        testdata.setAttribute("selector", selector);
                        testsuite.appendChild(testdata);
                    }
                    
                    run.appendChild(testsuite);
                }
                if (run.hasChildNodes()) {
                    campaign.appendChild(run);
                }
            }
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            fw = new java.io.FileWriter(fileName);
            StreamResult sr = new StreamResult(fw);
            transformer.transform(domSource, sr);
        } catch (TransformerConfigurationException ex) {
            //
            logger.error(ex);
        } catch (TransformerException ex) {
            //
            logger.error(ex);
        } catch (IOException ex) {
            //
            logger.error(ex);
        } catch (ParserConfigurationException ex) {
            //
            logger.error(ex);
        }

        finally {
            try {
            	if (fw !=null)
            		fw.close();
            } catch (IOException ex) {
                //
                logger.error(ex);
            }
        }
    }
		
	public class Testcase {
		private ArrayList<Integer> rowId;
		private String directory;
		
		public Testcase(String directory, ArrayList<Integer> rowId) {
			this.directory = directory;
			this.rowId = rowId;
		}
		
		public ArrayList<Integer> getRowid() {
			return rowId;
		}
		public String getDirectory() {
			return directory;
		}
		public void setRowId(ArrayList<Integer> rows) {
			rowId = rows;
		}
	}

}
