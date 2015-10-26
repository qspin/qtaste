/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui.server;

import java.awt.Component;
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
	
			File file = new File(mData[0].toString());
			file.createNewFile();
			ImageIO.write(myImage, "jpg", file);				 			
		}
		catch (Exception e) {
			throw new QTasteTestFailException("Error saving snapshot " + mData[0].toString() + ":", e);
		}	
	}

	@Override	
	protected boolean isAccessible(Component c) {
		return true;
	}

	@Override
	protected void prepareActions() throws QTasteTestFailException {}
}
