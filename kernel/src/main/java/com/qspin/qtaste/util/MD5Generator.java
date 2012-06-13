package com.qspin.qtaste.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Utility class to compute a MD5 checksum.
 *
 */
public final class MD5Generator {

	/**
	 * Compute the MD5 checksum for resource at the path. 
	 * @param pResourcePath
	 * @return the compute checksum
	 * @see Class#getResourceAsStream(String)
	 * @see MessageDigest#digest()
	 * @throws IOException
	 */
	public static byte[] getMessageDigestFromResource(String pResourcePath)
			throws IOException {
		InputStream is = MD5Generator.class.getResourceAsStream(pResourcePath);
		byte[] result = getMessageDigestFromStream(is);
		is.close();
		return result;
	}
	
	/**
	 * Compute the MD5 checksum for file at the path. 
	 * @param pFilePath
	 * @return the compute checksum
	 * @see FileInputStream(String)
	 * @see MessageDigest#digest()
	 * @throws IOException
	 */
	public static byte[] getMessageDigestFromFile(String pFilePath)
			throws IOException {
		InputStream is = new FileInputStream(pFilePath);
		byte[] result = getMessageDigestFromStream(is);
		is.close();
		return result;
	}

	
	/**
	 * Compute the MD5 checksum for input stream.
	 * @param pStream
	 * @return the compute checksum
	 * @see MessageDigest#digest()
	 * @throws IOException
	 */
	public static byte[] getMessageDigestFromStream(InputStream pStream)
			throws IOException {
		try {
			MessageDigest msg = MessageDigest.getInstance("MD5");
			int numRead;
			byte[] buffer = new byte[1024];
			do {
				numRead = pStream.read(buffer);
				if (numRead > 0) {
					msg.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			return msg.digest();
		} catch (NoSuchAlgorithmException pExc) {
			LOGGER.warn("Cannot generate MD5 checksum...");
		}
		return new byte[0];
	}

	/** used for logging. */
	private static final Logger LOGGER = Logger.getLogger(MD5Generator.class);
}
