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

import java.io.File;
import java.io.IOException;

/**
 * Class containing static configuration, like static paths and filenames.
 * 
 * @author David Ergo
 */
public class StaticConfiguration {

	public static final String QTASTE_ROOT = getQTasteRoot();
	public static final String JYTHON_HOME = QTASTE_ROOT + "/tools/jython/lib";
	public static final String JYTHON_LIB = JYTHON_HOME + "/Lib";
	public static final String TEST_SCRIPT_FILENAME = "TestScript.py";
	public static final String TEST_DATA_FILENAME = "TestData.csv";
	public static final String TEST_REQUIREMENTS_FILENAME = "Req.xml";
	public static final String TEST_SCRIPT_DOC_TOOLS_DIR = QTASTE_ROOT + "/tools/TestScriptDoc";
	public static final String TEST_SCRIPT_DOC_XML_FILENAME = "TestScript-doc.xml";
	public static final String TEST_SCRIPT_DOC_HTML_FILENAME = "TestScript-doc.html";
	public static final String TEST_SUITE_DOC_HTML_FILENAME = "TestSuite-doc.html";
	public static final String TEST_API_DOC_DIR = "testapi/target/TestAPI-doc";
	public static final String CONFIG_DIRECTORY = QTASTE_ROOT + "/conf";
	public static final String QTASTE_USER_MANUAL_FILE = QTASTE_ROOT + "/doc/html/qtaste_user_manual.html";
	public static final String QTASTE_RELEASE_NOTES_FILE = QTASTE_ROOT + "/doc/html/qtaste_release_notes.html";
	public static final String TESTBED_CONFIG_DIRECTORY = "Testbeds";
	public static final String CONTROL_SCRIPTS_DIRECTORY = TESTBED_CONFIG_DIRECTORY + "/ControlScripts";
	public static final String TESTBED_CONFIG_FILE_EXTENSION = "xml";
	public static final String LAST_SELECTED_TESTBED_PROPERTY = "last_selected_testbed";
	public static final String DEFAULT_TESTSUITES_DIR = "TestSuites";
	public static final String CAMPAIGN_DIRECTORY = "TestCampaigns";
	public static final String CAMPAIGN_FILE_EXTENSION = "xml";
	public static final String FORMATTER_DIR = QTASTE_ROOT + "/tools/TestScriptDoc";
	public static final String ADDITIONNAL_JYTHON_LIB;
	public static final String PLUGINS_HOME = QTASTE_ROOT + "/plugins";
	static {
		//JYTHON_LIB initialization
		String path = "";
		if (System.getenv("QTASTE_JYTHON_LIB") != null) {
			path = System.getenv("QTASTE_JYTHON_LIB");
		}
		ADDITIONNAL_JYTHON_LIB = path;
	}

	/**
	 * Get QTaste root directory from QTASTE_ROOT environment variable.
	 * 
	 * @return the QTaste root directory
	 */
	private static String getQTasteRoot() {
		String qtasteRoot = System.getenv("QTASTE_ROOT");
		if (qtasteRoot == null) {
			System.err.println("QTASTE_ROOT environment variable is not defined");
			System.exit(1);
		}
		try {
			qtasteRoot = new File(qtasteRoot).getCanonicalPath();
		} catch (IOException e) {
			System.err.println("QTASTE_ROOT environment variable is invalid (" + qtasteRoot + ")");
			System.exit(1);
		}
		return qtasteRoot;
	}
}
