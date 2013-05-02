package com.qspin.qtaste.javagui.server;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class Snapshotter extends UpdateComponentCommander {

	public void doActionsInSwingThread() throws QTasteTestFailException{
		try {

			Dimension size = component.getSize();
			BufferedImage myImage = new BufferedImage(size.width, size.height, 
													  BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = myImage.createGraphics();
			component.paint(g2);
	
			File file = new File(mData[2].toString());
			file.createNewFile();
			System.out.println("creating empty file");
			ImageIO.write(myImage, "jpg", file);				 			
		}
		catch (Exception e) {
			throw new QTasteTestFailException("Error saving snapshot " + mData[2].toString() + ":", e);
		}	
	}

	@Override
	protected void prepareActions() throws QTasteTestFailException {}
}
