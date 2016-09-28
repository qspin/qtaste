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

package com.qspin.qtaste.sikuli.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Image;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Runner;
import org.sikuli.script.Screen;

import com.qspin.qtaste.sikuli.Area;
import com.qspin.qtaste.tcom.jmx.impl.JMXAgent;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Sikuli is a java agent started with the same VM as the Sikuli application.
 * It implements all the SikuliMBean services using JMX.
 */
public class Sikuli extends JMXAgent implements SikuliMBean {

    /**
     * Default timeout value expressed in seconds.
     */
    public final static double DEFAULT_TIMEOUT = 3;

    private boolean mPreviousScriptFailed;

    public Sikuli() {
        mPreviousScriptFailed = false;
        init();
        // These settings are required to enable OCR but not setted by default by sikuli
        Settings.OcrTextSearch = true;
        Settings.OcrTextRead = true;
    }

    private Image loadImageFromPath(String path) throws QTasteTestFailException {
        try {
            return new Image(ImageIO.read(new File(path)));
        } catch (Exception ex) {
            throw new QTasteTestFailException("Cannot load the image file locate in " + path + " : " + ex.getMessage(), ex);
        }
    }

    private Region getRegion(String imageFilePath) throws QTasteTestFailException {
        try {
            return Screen.all().find(loadImageFromPath(imageFilePath));
        } catch (FindFailed ex) {
            throw new QTasteTestFailException("Cannot find the image on screen : " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean exists(String fileName) throws QTasteException {
        //out of the try catch to ensure the error doesn't come from test data
        loadImageFromPath(fileName);
        try {
            return getRegion(fileName) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void takeSnapShot(String directory, String fileName) throws QTasteException {
        Screen.all().capture().save(directory, fileName);
    }

    @Override
    public void wait(String fileName) throws QTasteException {
        wait(fileName, DEFAULT_TIMEOUT);
    }

    @Override
    public void wait(String fileName, double timeout) throws QTasteException {
        try {
            Screen.all().wait(loadImageFromPath(fileName), timeout);
        } catch (FindFailed ex) {
            throw new QTasteTestFailException("Cannot find the image on screen : " + ex.getMessage(), ex);
        }
    }

    @Override
    public void waitVanish(String fileName, double timeout) throws QTasteException {
        if (!Screen.all().waitVanish(loadImageFromPath(fileName), timeout)) {
            throw new QTasteTestFailException("The target is still displayed");
        }
    }

    @Override
    public void waitVanish(String fileName) throws QTasteException {
        waitVanish(fileName, DEFAULT_TIMEOUT);
    }

    @Override
    public void hover(String fileName) throws QTasteException {
        if (getRegion(fileName).hover() != 1)
			throw new QTasteTestFailException("Cannot move the pointer on the location (image: " + fileName + ")");
    }

    @Override
    public void dragDrop(String targetFileName, String destinationFileName) throws QTasteException {
        try {
            Screen.all().dragDrop(loadImageFromPath(targetFileName), loadImageFromPath(destinationFileName));
        } catch (FindFailed ex) {
            throw new QTasteTestFailException("Cannot execute the Drag And Drop command : " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public Area find(String fileName) throws QTasteException
    {
    	return new Area(getRegion(fileName));
    }
    
    @Override
    public List<Area> findAll(String fileName) throws QTasteException
    {
    	try
    	{
	    	List<Area> areas = new ArrayList<Area>();
	    	Iterator<Match> it = Screen.all().findAll(loadImageFromPath(fileName));
	    	while(it.hasNext())
	    	{
	    		Match m = it.next();
	    		areas.add(new Area(m));
	    	}
	    	return areas;
    	}
    	catch(Exception ex)
    	{
    		throw new QTasteException(ex.getMessage(), ex);
    	}
    	
    }

    @Override
    public void rightClick(String fileName) throws QTasteException {
        getRegion(fileName).rightClick();
    }

    @Override
    public void click(String fileName) throws QTasteException {
        getRegion(fileName).click();
    }

    @Override
    public void doubleClick(String fileName) throws QTasteException {
        getRegion(fileName).doubleClick();
    }

    @Override
    public void type(String fileName, String value) throws QTasteException {
        try {
            Screen.all().type(loadImageFromPath(fileName), value);
        } catch (FindFailed ex) {
            throw new QTasteTestFailException("Cannot execute the type command : " + ex.getMessage(), ex);
        }
    }

    @Override
    public void type(String value) throws QTasteException {
        Screen.all().type(value);
    }

    @Override
    public void paste(String fileName, String value) throws QTasteException {
        try {
            Screen.all().paste(loadImageFromPath(fileName), value);
        } catch (FindFailed ex) {
            throw new QTasteTestFailException("Cannot execute the type command : " + ex.getMessage(), ex);
        }
    }

    @Override
    public void paste(String value) throws QTasteException {
        Screen.all().paste(value);
    }

    @Override
    public void openAndRunScript(String scriptPath) throws QTasteException {
        if (mPreviousScriptFailed) {
            throw new QTasteTestFailException(
                  "Cannot execute another script after a script failure, the sikuli module have to be restarted!");
        }

        int retCode;
        if ((retCode = Runner.runScripts(new String[] {"-r", scriptPath})) != 0) {
            mPreviousScriptFailed = true;
            throw new QTasteTestFailException("Execution failed... (return code : " + retCode + ")");
        }
    }

    @Override
    public void setSimilarity(double level) throws QTasteException {
        if (level < 0.0 || level > 1.0) {
            throw new QTasteTestFailException("Invalid similarity level provided (" + level + "). Expected value between 0 and 1!");
        }
        Settings.MinSimilarity = level;
    }


}
