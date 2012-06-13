package com.qspin.qtaste.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class JarResourceExtractorTest extends TestCase {

	public void testExtractResource() {
		if ( JarResourceExtractor.createDirectory(TEMP_DIR_NAME) ) {
			try {
				assertNull("extraction of a nonexistant file succeed !", JarResourceExtractor.extractResource("noFileFound", TEMP_DIR_NAME));
				File extracted = JarResourceExtractor.extractResource("/JarResourceExtractor/emptyFile", TEMP_DIR_NAME);
				assertNotNull("extraction of an existent file failed", extracted.length());
				assertTrue("The empty file is not empty!!", extracted.length() == 0 );
				extracted = JarResourceExtractor.extractResource("/JarResourceExtractor/updatedFile/emptyFile", TEMP_DIR_NAME);
				assertNotNull("extraction of an existent file failed", extracted);
				assertTrue("The file has not been updated!!", extracted.length() > 0 );
				File target = new File(TEMP_DIR_NAME);
				extracted.delete();
				target.delete();
				System.out.println(target.exists());
			} catch (IOException pExc ) {
				fail("IOException : " + pExc.getMessage());
			}
		}
	}

	public void testCreateDirectory() {
		boolean result = JarResourceExtractor.createDirectory(TEMP_DIR_NAME);
		File target = new File(TEMP_DIR_NAME);
		assertEquals(result, target.exists());
		if (target.exists() ) {
			target.delete();
		}
	}
	
	private static final String TEMP_DIR_NAME = "temp";

}
