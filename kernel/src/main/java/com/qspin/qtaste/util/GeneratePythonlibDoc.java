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

package com.qspin.qtaste.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.qspin.qtaste.config.StaticConfiguration;

/**
 * This class is responsible for generating an XML document containing all the documentation of the python script included in a pythonlib directory.
 * @author simjan
 */
public class GeneratePythonlibDoc {

        private static final Logger LOGGER = Logger.getLogger(GeneratePythonlibDoc.class);
	
	private final static File ROOT_SCRIPT_DIRECTORY = new File(StaticConfiguration.DEFAULT_TESTSUITES_DIR);

	/**
	 * File filter which accept only directories.
	 */
	private final static FileFilter DIRECTORY_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}
	};
	/**
	 * File filter which accept only file with the .py extention (case insensitive).
	 */
	private final static FileFilter PYTHON_SCRIPT_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isFile() && f.getName().toLowerCase().endsWith(".py");
		}
	};
	
	/**
	 * Flag to specify if the generation has already be done (at least) one time during this VM execution.
	 */
	private static boolean ALREADY_RUN = false;
	/**
	 * Flag to avoid multiple asynchronous generations (in the same time).
	 */
	private static boolean IS_RUNNING = false;
	
	/**
	 * @return <code>true</code> if the generation has already be done (at least) one time during this VM execution.
	 */
	public static boolean hasAlreadyRunOneTime()
	{
		return ALREADY_RUN;
	}
	
	/**
	 * Generates the documentation of scripts located in a pythonlib directory in a separated thread.
	 */
	public static synchronized void generateAsynchronously()
	{
		if ( IS_RUNNING )
			return;
		
		new Thread(new Runnable() {
			public void run() {
				generate();
			}
		}).start();
	}

	/**
	 * Generates the documentation of scripts located in a pythonlib directory.
	 */
    public static synchronized void generate() {
        LOGGER.debug("Generating documentation of test documentation included in pythonlib directories.");
        try
		{
        	IS_RUNNING = true;
			List<File> pythonLibDirectories = findPythonLibDirectories(ROOT_SCRIPT_DIRECTORY);

			List<File> pythonScriptFiles = findPythonScripts(pythonLibDirectories);
			for (File script : pythonScriptFiles)
			{
				if (hasToGenerateDocumentation(script))
				{
					GenerateTestStepsModulesDoc.generate(script.getAbsolutePath());
				}
			}
			ALREADY_RUN = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
        finally
        {
        	IS_RUNNING = false;
        }
    }

    public static void main(String[] args) {
		// Log4j Configuration
		PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");

		if (args.length == 0) {
            generate();
        }
    }
    
    /**
     * Searches (recursively) all directories named "pythonlib" contained in the directory.
     * @param parentDirectory The directory to scan
     * @return A list containing all directories named pythonlib.
     */
    private static List<File> findPythonLibDirectories(File parentDirectory)
	{
		List<File> foundDirectories = new ArrayList<File>();
		if (parentDirectory != null && parentDirectory.exists())
		{
			for (File dir : parentDirectory.listFiles(DIRECTORY_FILE_FILTER))
			{
				if ( dir.getName().equals("pythonlib") )
					foundDirectories.add(dir);
				else
					foundDirectories.addAll(findPythonLibDirectories(dir));
			}
		}
		return foundDirectories;
	}
	
    /**
     * Searches for all python script files contains in the directories. 
     * @param pythonLibDirectories A list of directories to scan.
     * @return A list of all found files.
     */
	private static List<File> findPythonScripts(List<File> pythonLibDirectories)
	{
		List<File> scripts = new ArrayList<File>();
		for ( File dir : pythonLibDirectories )
		{
			if ( dir.exists() )
			{
				scripts.addAll(Arrays.asList(dir.listFiles(PYTHON_SCRIPT_FILE_FILTER)));
			}
		}
		return scripts;
	}
	
	/**
	 * Checks if the documentation related to the script is outdated.<br/>
	 * The documentation is outdated for a script <b><i>scriptName</i>.py</b> if:
	 * <ul>
	 * <li>the documentation file <b><i>scriptName</i>-steps-doc.xml</b> doesn't exist
	 * <li>the documentation file last modification date is older than the script last modification date.
	 * </ul>
	 * @param script The script to check.
	 * @return <code>true</code>if the script exist or if the documentation is outdated.
	 */
	private static boolean hasToGenerateDocumentation(File script)
	{
		if ( script.exists() )
		{
			String scriptDocFileName = script.getName().substring(0, script.getName().length()-3);
			scriptDocFileName += "-steps-doc.xml";
			File scriptDocFile = new File(script.getParentFile(), scriptDocFileName);
			if (!scriptDocFile.exists())
				return true;
			
			return scriptDocFile.lastModified() <= script.lastModified(); 
		}
		return false;
	}
}
