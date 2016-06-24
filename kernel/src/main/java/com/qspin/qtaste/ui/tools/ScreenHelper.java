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

package com.qspin.qtaste.ui.tools;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * The ScreenHelper class knows how many screens are available in the configuration. All the windows of the
 * front end can be redirected on a specific screen. Currently the windows are displayed on the larger
 * available screen in a multiple screens configuration.
 */
public class ScreenHelper {
    private java.util.List<Rectangle> mCoords = new ArrayList<Rectangle>();
    private int mLargerScreen;
    private int mCurrentScreen;
    private Dimension mForcedResolution;

    public ScreenHelper() {
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Rectangle rect = new Rectangle();
        for (int i = 0; i < devices.length; ++i) {
            GraphicsConfiguration gc = devices[i].getDefaultConfiguration();
            new Frame(gc);
            Rectangle currentrect = gc.getBounds();
            mCoords.add(currentrect);
            if (currentrect.width > rect.width) {
                rect = currentrect;
                mLargerScreen = i;
            }
        }
        mCurrentScreen = mLargerScreen;
    }

    public Rectangle getCurrentScreenCoords() {
        if (mForcedResolution == null) {
            return getScreenCoords(mCurrentScreen);
        }
        Rectangle rect = getScreenCoords(mCurrentScreen);
        rect.width = mForcedResolution.width;
        rect.height = mForcedResolution.height;
        return rect;
    }

    public int getSecondaryScreenIndex() {
        return 1 - mCurrentScreen;
    }

    public Rectangle getLargerScreenCoords() {
        return mCoords.get(mLargerScreen);
    }

    public int getNumberOfScreens() {
        return mCoords.size();
    }

    public Rectangle getScreenCoords(int pIndex) {
        return mCoords.get(pIndex);
    }

    public void setCurrentScreen(int pIndex) {
        mCurrentScreen = pIndex;
    }

    public void setForcedResolution(Dimension pDim) {
        mForcedResolution = pDim;
    }
}
