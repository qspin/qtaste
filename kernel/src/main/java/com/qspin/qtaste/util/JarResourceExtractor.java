package com.qspin.qtaste.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.log4j.Logger;
import org.jfree.io.IOUtils;

/**
 * Utility class to extract a file stored in a jar file into a specific folder.
 * 
 */
public final class JarResourceExtractor {

	/**
	 * Extracts the file from the jar to the target directory.
	 * 
	 * @param pResourcePath
	 *            the file to extract.
	 * @param pTargetDirectory
	 *            the directory where to extract the file.
	 * 
	 * @return The extracted file or <code>null</code> if a problem occurs.
	 */
	public static File extractResource(String pResourcePath, String pTargetDirectory) throws IOException {
		if (!createDirectory(pTargetDirectory)) {
			throw new IOException("The '" + pTargetDirectory + "' directory cannot be created.");
		}
		
		InputStream is = getThisClass().getResourceAsStream(pResourcePath);
		if (is == null) {
			return null;
		}
		byte[] sourceMD5 = MD5Generator.getMessageDigestFromResource(pResourcePath);
		is.close();
		
		String resourceName = new File(getThisClass().getResource(pResourcePath).getFile()).getName();
		File fileName = new File(pTargetDirectory, resourceName);
		byte[] targetMD5 = new byte[0];
		if (fileName.exists()) {
			targetMD5 = MD5Generator.getMessageDigestFromFile(fileName.getAbsolutePath());
		}
		
		if ( !MessageDigest.isEqual(sourceMD5,targetMD5) ) {
			LOGGER.debug("Extraction of " + resourceName);
			fileName.delete();
			
			FileOutputStream os = new FileOutputStream(fileName);
			is = getThisClass().getResourceAsStream(pResourcePath);
			IOUtils.getInstance().copyStreams(is, os);
			os.flush();
			os.close();
			is.close();
		} else {
			LOGGER.debug( resourceName + " is already extracted");
		}
		return fileName;
	}

	/**
	 * Creates the directory if it doesn't exist.
	 * 
	 * @param pDirectoryPath
	 *            the directory to create.
	 * @return <code>true</code> if the directory exists or has been
	 *         successfully created.
	 */
	public final static boolean createDirectory(String pDirectoryPath) {
		File directory = new File(pDirectoryPath);
		if (!directory.exists()) {
			return directory.mkdirs();
		}
		return true;
	}

	private static final Class<JarResourceExtractor> getThisClass() {
		return JarResourceExtractor.class;
	}

	/** used for logging. */
	private static final Logger LOGGER = Logger.getLogger(JarResourceExtractor.class);

}
