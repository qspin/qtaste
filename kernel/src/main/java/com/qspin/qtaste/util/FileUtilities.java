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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import com.qspin.qtaste.ui.tools.FileMask;

/**
 *
 * @author vdubois
 */
public class FileUtilities {

	private static Logger logger = Log4jLoggerFactory.getLogger(FileUtilities.class);
	    
	public static File[] listSortedFiles(File directory, FileMask fileMask) {
		File[] fList = directory.listFiles(fileMask);
		if (fList==null) return fList;
		// sort testbed by alphabetic order (ignoring case)
		Arrays.sort(fList, new Comparator<File>() {
		
		    public int compare(File o1, File o2) {
		        return o1.getName().compareToIgnoreCase(o2.getName());
		    }
		});
		return  fList;
	}
	
	public static File[] listSortedFiles(File directory) {
		return listSortedFiles(directory, null);
	}
	
	public static File[] listSortedFiles(File directory, FileFilter filter) {
		File[] fList = directory.listFiles(filter);
		if (fList==null) return fList;
		// sort testbed by alphabetic order (ignoring case)
		Arrays.sort(fList, new Comparator<File>() {
		
		    public int compare(File o1, File o2) {
		        return o1.getName().compareToIgnoreCase(o2.getName());
		    }
		});
		return  fList;
	}
	
	public static void copy(String sourceName, String destName) {
	    try {
	        File sourceFile = new File(sourceName);
	        File destFile = new File(destName);
	        // create directories if any
	        destFile.getParentFile().mkdirs();
	        FileUtilities.copy(sourceFile, destFile);
	    } catch (IOException ex) {
	        logger.error("Impossible to copy source file '" +sourceName + "' to '" + destName + "'");
	    }
	}
	
	/** Fast & simple file copy. 
	 * @param source source file
	 * @param dest destination file or directory  
	 */
	public static void copy(File source, File dest) throws IOException {
		if (dest.isDirectory()) {
			dest = new File(dest + File.separator + source.getName());
		}
		
		FileChannel in = null, out = null;
	    try {          
	         in = new FileInputStream(source).getChannel();
	         out = new FileOutputStream(dest).getChannel();
	 
	         long size = in.size();
	         MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
	 
	         out.write(buf);
	    } finally {
	         if (in != null)  in.close();
	         if (out != null) out.close();
	    }
	}

	public static void copyFiles(String sourceDir, String destDir) throws IOException {
		copyFiles(sourceDir, destDir, null);
	}

	public static void copyFiles(String sourceDir, String destDir, FileMask fileMask) throws IOException {
	    try {
	        File sourceDirFile = new File(sourceDir);
	        File destDirFile = new File(destDir);
	        // create directories if any
	        destDirFile.mkdirs();
	        FileUtilities.copyFiles(sourceDirFile, destDirFile, fileMask);
	    } catch (IOException ex) {
	        logger.error("Impossible to copy files from '" + sourceDir + "' to '" + destDir + "'");
	    }
	}

	public static void copyFiles(File sourceDir, File destDir) throws IOException {
		copyFiles(sourceDir, destDir, null);
	}

	public static void copyFiles(File sourceDir, File destDir, FileMask fileMask) throws IOException {
		File[] sourceFiles = sourceDir.listFiles(fileMask);
		if (sourceFiles != null) {
			for (File sourceFile: sourceFiles) {
				copy(sourceFile, destDir);
			}
		}
	}

	/**
	 * Reads file content.
	 * @param filename name of the file to read
	 * @return string containing the file content
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static String readFileContent(String filename) throws FileNotFoundException, IOException {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    StringBuffer content = new StringBuffer();
	    String line;
	    final String eol = System.getProperty("line.separator");
	    while ((line = reader.readLine()) != null) {
	        content.append(line);
	        content.append(eol);
	    }
	    return content.toString();
	}
	
	public static void copyResourceFile(Class<?> clazz, String sourceResourceFileName, String destFileName) throws URISyntaxException, IOException {
    	InputStream srcFileStream = clazz.getResourceAsStream(sourceResourceFileName);
        if (srcFileStream == null) {
        	throw new IOException("Resource file " + sourceResourceFileName + " not found");
        }

        File destFile = new File(destFileName);

		if (destFile.isDirectory()) {
			destFile = new File(destFile + File.separator + sourceResourceFileName.substring(sourceResourceFileName.lastIndexOf('/') + 1));
		}
		
		FileChannel out = null;
	    try {
	         out = new FileOutputStream(destFile).getChannel();
	         
	         byte[] buf = new byte [srcFileStream.available()];
	         while (srcFileStream.available() > 0) {
	        	 int size = srcFileStream.read(buf);
		         out.write(ByteBuffer.wrap(buf, 0, size));
	         }
	    } finally {
	         srcFileStream.close();
	         if (out != null) out.close();
	    }
	}
	
	public static void copyResourceFiles(Class<?> clazz, String sourceResourceDirName, String destDirName) throws URISyntaxException, IOException {
        // create directories if any
        File destDirFile = new File(destDirName);
        destDirFile.mkdirs();

        String[] resourceFileNames = listResourceFiles(clazz, sourceResourceDirName);
		for (String resourceFileName: resourceFileNames) {
			resourceFileName = sourceResourceDirName + "/" + resourceFileName;
			copyResourceFile(clazz, resourceFileName, destDirName);
		}		
	}

	/**
	 * List directory files in a resource folder. Not recursive.
	 * Works for regular files and also JARs.
	 * 
	 * @param clazz Any java class that lives in the same place as the resources you want.
	 * @param resourceDirName resource folder path.
	 * @return the full path name of each folder file, not the full paths.
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static String[] listResourceFiles(Class<?> clazz, String resourceDirName) throws URISyntaxException, IOException {
		if (!resourceDirName.endsWith("/")) {
			resourceDirName = resourceDirName + "/";
		}
	    URL dirURL = clazz.getResource(resourceDirName);
	    if (dirURL == null) {
	    	throw new IOException("Resource directory " + resourceDirName + " not found");
	    }
	    if (dirURL.getProtocol().equals("file")) {
	    	File[] entries = new File(dirURL.toURI()).listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.isFile();
				}});
	    	String[] fileNames = new String[entries.length];
	    	for (int i=0; i < entries.length; i++) {
	    		fileNames[i] = entries[i].toString();
	    	}
	    	return fileNames;
	    } 

	    if (dirURL.getProtocol().equals("jar")) {
	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
	        JarFile jar = new JarFile(jarPath);
	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
	        List<String> result = new ArrayList<String>();
	        String relativeResourceDirName = resourceDirName.startsWith("/") ? resourceDirName.substring(1) : resourceDirName;
	        while (entries.hasMoreElements()) {
	        	String name = entries.nextElement().getName();
	        	if (name.startsWith(relativeResourceDirName) && !name.equals(relativeResourceDirName)) { //filter according to the path
	        		String entry = name.substring(relativeResourceDirName.length());
	        		int checkSubdir = entry.indexOf("/");
	        		if (checkSubdir < 0) {
	        			// not a subdirectory
		        		result.add(entry);
	        		}
	        	}
	        }
	        return result.toArray(new String[result.size()]);
	      } 
	        
	      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	  }
}
