package com.qspin.qtaste.util.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.util.GenerateTestScriptDoc;

/**
 * Service class used to generate the python script documentation for pythonlib directories content.
 * Two process iterations are separated by 10seconds.
 * @author simjan
 *
 */
public final class PythonLibDocGeneratorService extends Service {

	public final static PythonLibDocGeneratorService INSTANCE = new PythonLibDocGeneratorService();
	
	private final static File ROOT_SCRIPT_DIRECTORY = new File(StaticConfiguration.DEFAULT_TESTSUITES_DIR);
	
	private PythonLibDocGeneratorService()
	{
		super();
	}

	private final static FileFilter DIRECTORY_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}
	};
	private final static FileFilter PYTHON_SCRIPT_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isFile() && f.getName().endsWith(".py");
		}
	};
	private final static Logger logger = Logger.getLogger(PythonLibDocGeneratorService.class);
	
	@Override
	public String getName() {
		return "Python Lib Documentation Service Generation";
	}
	
	@Override
	protected void process()
	{
		try
		{
			List<File> pythonLibDirectories = findPythonLibDirectories(ROOT_SCRIPT_DIRECTORY);
			logger.trace(pythonLibDirectories.size() + " directory(ies) found");

			List<File> pythonScriptFiles = findPythonScripts(pythonLibDirectories);
			logger.trace(pythonScriptFiles.size() + " script(s) found");
			for (File script : pythonScriptFiles)
			{
				if (hasToGenerateDocumentation(script))
				{
					logger.trace(script.getName() + " documentation has to be updated!");
					GenerateTestScriptDoc.generate(script.getAbsolutePath());
				}
			}
			Thread.sleep(10*1000);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private List<File> findPythonLibDirectories(File parentDirectory)
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
	
	private List<File> findPythonScripts(List<File> pythonLibDirectories)
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
	
	private boolean hasToGenerateDocumentation(File script)
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
