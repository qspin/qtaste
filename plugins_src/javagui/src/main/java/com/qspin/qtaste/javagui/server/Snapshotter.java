package com.qspin.qtaste.javagui.server;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class Snapshotter extends UpdateComponentCommander {

	public void run() {
		try {

			Dimension size = component.getSize();
			BufferedImage myImage = new BufferedImage(size.width, size.height, 
													  BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = myImage.createGraphics();
			component.paint(g2);
	
			File file = new File(mData[1].toString());
			file.createNewFile();
			System.out.println("creating empty file");
			ImageIO.write(myImage, "jpg", file);				 			
		}
		catch (Exception e) {
			mError = new QTasteTestFailException("Error saving snapshot " + mData[1].toString() + ":", e);
		}	
	}
}
