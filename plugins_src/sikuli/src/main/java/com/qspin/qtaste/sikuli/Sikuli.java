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

package com.qspin.qtaste.sikuli;

import java.util.List;

import com.qspin.qtaste.testsuite.QTasteException;

/**
 * This interface describes all the methods usable to perform actions or control
 * on a Sikuli application.
 */
public interface Sikuli {

    /**
     * Creates a snapshot of the screen and saves it with the specified filename
     * prefix in the directory.
     *
     * @param directory the path to the parent directory.
     * @param fileNamePrefix the prefix name of the image file.
     */
    void takeSnapShot(String directory, String fileNamePrefix) throws QTasteException;

    /**
     * Searches on all screens for an occurrence of the image and returns it.
     * @param fileName The image to find.
     * @return an {@link Area} instance.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    Area find(String fileName) throws QTasteException;

    /**
     * Searches on all screens for all occurrences of the image and returns them.
     * @param fileName The image to find.
     * @return a list of {@link Area} instances.
     * @throws QTasteException if the file doesn't exist or if no occurrence is found.
     */
    List<Area> findAll(String fileName) throws QTasteException;
    
    /**
     * Checks if the image is displayed on the screen or not.
     *
     * @param fileName The image file path.
     * @return <code>true</code> if the image is displayed, otherwise, returns <code>false</code>.
     * @throws QTasteException If the image cannot be loaded.
     */
    boolean exists(String fileName) throws QTasteException;

    /**
     * Waits a region matching the image is displayed on the screen.
     *
     * @param fileName the image file name.
     * @throws QTasteException If no region match within a default time out.
     */
    void wait(String fileName) throws QTasteException;

    /**
     * Waits a region matching the image is displayed on the screen within the
     * timeout.
     *
     * @param fileName the image file name.
     * @param timeout the timeout expressed in seconds.
     * @throws QTasteException If no region match within the time out.
     */
    void wait(String fileName, double timeout) throws QTasteException;

    /**
     * Waits no more region match the image is displayed on the screen.
     *
     * @param fileName the image file name.
     * @throws QTasteException If there are at least one region matching the image within a default time out.
     */
    void waitVanish(String fileName) throws QTasteException;

    /**
     * Waits no more region match the image is displayed on the screen.
     *
     * @param fileName the image file name.
     * @param timeout the timeout expressed in seconds.
     * @throws QTasteException If there are at least one region matching the image within the time out.
     */
    void waitVanish(String fileName, double timeout) throws QTasteException;

    /**
     * Puts the mouse hover the region identified by the image.
     *
     * @param fileName the image file path.
     * @throws QTasteException
     */
    void hover(String fileName) throws QTasteException;

    /**
     * Executes a Drag'nDrop command from the first image to the second one.
     *
     * @param targetFileName the DnD origin image file path.
     * @param destinationFileName the DnD destination image file path.
     * @throws QTasteException
     */
    void dragDrop(String targetFileName, String destinationFileName) throws QTasteException;

    /**
     * Executes a Drag'nDrop command from the first image to the second one, but sikuli is waiting the delay before
     * release the mouse button.
     * @param targetFileName the DnD origin image file path.
     * @param destinationFileName the DnD destination image file path.
     * @param delayBeforeDrop the delay to wait before release the mouse button, in Second.
     * @throws QTasteException
     */
    void dragDrop(String targetFileName, String destinationFileName, int delayBeforeDrop) throws QTasteException;



    /**
     * Executes a simple right click on the region identified by the picture.
     *
     * @param fileName the image file path.
     * @throws QTasteException
     */
    void rightClick(String fileName) throws QTasteException;

    /**
     * Executes a simple left click on the region identified by the picture.
     *
     * @param fileName the image file path.
     * @throws QTasteException
     */
    void click(String fileName) throws QTasteException;

    /**
     * Executes a double left click on the region identified by the picture.
     *
     * @param fileName the image file path.
     * @throws QTasteException
     */
    void doubleClick(String fileName) throws QTasteException;

    /**
     * Clicks on the region identified by the image and type the text.
     *
     * @param fileName the image file name.
     * @param value the new value for the text.
     */
    void type(String fileName, String value) throws QTasteException;

    /**
     * Types the text within the focused component.
     *
     * @param value the new value for the text.
     */
    void type(String value) throws QTasteException;

    /**
     * Clicks on the region identified by the image and uses the "paste" command to put the text.
     *
     * @param fileName the image file name.
     * @param value the new value for the text.
     */
    void paste(String fileName, String value) throws QTasteException;

    /**
     * Uses the "paste" command to put the text within the focused component.
     *
     * @param value the new value for the text.
     */
    void paste(String value) throws QTasteException;

    /**
     * Opens the Sikuli test script directory and executes the script.
     *
     * @param ScriptPath The Sikuli test directory path.
     * @throws QTasteException if the script cannot be executed or if the execution failed.
     */
    void openAndRunScript(String ScriptPath) throws QTasteException;

    /**
     * Set The default minimum similarity of find operations.
     * While using a Region.find() operation, if only an image file is provided, Sikuli searches the region using a default minimum similarity of 0.7.
     *
     * @param level The similarity level value (0 <= value <= 1).
     * @throws QTasteException if an error occurs while setting the similarity level.
     */
    void setSimilarity(double level) throws QTasteException;

    /**
     * Create a PNG screen capture at specified position x, y and with size w, h saved under the specified filename
     * @param x the x position
     * @param y the y position
     * @param w the width
     * @param h the height
     * @param filename the captured png file
     */

    void capture(int x,int y, int w, int h, String filename) throws QTasteException;

    /**
     * Create a PNG screen capture at specified position x, y and with size w, h saved under the specified filename
     * @param x the x position
     * @param y the y position
     * @param w the width
     * @param h the height
     * @param directory the directory of the png file
     * @param name the name of the captured png file
     */
    void capture(int x,int y, int w, int h, String directory, String name) throws QTasteException;
}