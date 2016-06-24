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

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * The ResourceManager is responsible to load and give access to resources. The resources are images, sounds,
 * fonts and bundles.
 *
 * The ResourceManager knows how many languages are supported for the frontend.
 *
 * The ResourceManager knows the colors used for the QSpinTheme.
 */
public final class ResourceManager {
    protected static final Logger LOGGER = Logger.getLogger(ResourceManager.class);

    public static ResourceManager getInstance() {
        return mInstance;
    }

    public Clip getSound(String pName) {
        if (mSounds.containsKey(pName)) {
            return mSounds.get(pName);
        }
        return loadSound(pName);
    }

    public ImageIcon getImageIcon(String pName) {
        if (mImageIcons.containsKey(pName)) {
            return mImageIcons.get(pName);
        }
        return loadImageIcon(pName);
    }

    public void freeImageIcon(String pName) {
        mImageIcons.remove(pName);
    }

    public Color getLightColor() {
        if (mWarningColorScheme) {
            return mLightRed;
        }
        return mLightBlue;
    }

    public Color getNormalColor() {
        if (mWarningColorScheme) {
            return Color.RED;
        }
        return mBlue;
    }

    public Color getDarkColor() {
        if (mWarningColorScheme) {
            return mDarkRed;
        }
        return mDarkBlue;
    }

    public Color getLightBlue() {
        return mLightBlue;
    }

    public Color getNormalBlue() {
        return mBlue;
    }

    public Color getDarkBlue() {
        return mDarkBlue;
    }

    public Color getBackColor() {
        return mLightGray;
    }

    public Color getBackgroundColor() {
        return mBackground;
    }

    public Color getSecondaryColor() {
        return mOrange;
    }

    public Locale getLocale() {
        return mLocale;
    }

    public ResourceBundle getBundle(String pBundleName) {
        if (mBundles.containsKey(pBundleName)) {
            return mBundles.get(pBundleName);
        }
        return loadBundle(pBundleName);
    }

    public ResourceBundle forceBundle(String pBundleName) {
        return loadBundle(pBundleName);
    }

    public int getNumberOfLanguages() {
        return mSupportedLanguages.length;
    }

    public String getSupportedLanguageId(int pIndex) {
        return mSupportedLanguages[pIndex];
    }

    public void setLanguage(int pIndex) {
        if (pIndex == 0) {
            mLocale = new Locale("en", "US");
        } else if (pIndex == 1) {
            mLocale = new Locale("fr", "FR");
        } else {
            mLocale = new Locale("ch", "ZH");
        }
        mBundles.clear();
        LOGGER.info("Language set to " + mLocale + ".");
    }

    public void setWarningColorScheme(boolean pEnable) {
        mWarningColorScheme = pEnable;
    }

    public void setStandardFont(Font pFont) {
        final float largesize = 14.0f;
        final float largersize = 20.0f;
        final float smallsize = 3.0f;
        final float bigsize = 24.0f;
        final float biggestsize = 36.0f;

        mStandardFont = pFont;
        mLargeFont = pFont.deriveFont(Font.BOLD, largesize);
        mLargerFont = pFont.deriveFont(Font.BOLD, largersize);
        mStandardFontLight = pFont.deriveFont(Font.PLAIN);
        mStandardFontBold = pFont.deriveFont(Font.BOLD);
        mSmallFont = pFont.deriveFont(smallsize);
        mBigFont = pFont.deriveFont(Font.BOLD, bigsize);
        mBiggestFont = pFont.deriveFont(Font.BOLD, biggestsize);
        mBiggestFontLight = pFont.deriveFont(Font.PLAIN, biggestsize);
    }

    public Font getSmallFont() {
        return mSmallFont;
    }

    public Font getStandardFont() {
        return mStandardFont;
    }

    public Font getStandardFontBold() {
        return mStandardFontBold;
    }

    public Font getLargeFont() {
        return mLargeFont;
    }

    public Font getLargerFont() {
        return mLargerFont;
    }

    public Font getStandardFontLight() {
        return mStandardFontLight;
    }

    public Font getBigFont() {
        return mBigFont;
    }

    public Font getBiggestFont() {
        return mBiggestFont;
    }

    public Font getBiggestFontLight() {
        return mBiggestFontLight;
    }

    private ResourceBundle loadBundle(String pName) {
        ResourceBundle bundle = ResourceBundle.getBundle("res/" + pName, getLocale());
        mBundles.put(pName, bundle);
        return bundle;
    }

    private ImageIcon loadImageIcon(String pName) {
        java.net.URL imageurl = ResourceManager.class.getResource("/res/" + pName + ".png");
        if (imageurl != null) {
            ImageIcon icon = new ImageIcon(imageurl);
            if (icon.getIconHeight() > 0) {
                mImageIcons.put(pName, icon);
            } else {
                LOGGER.error("Unable to load icon " + pName + ".");
            }
            return icon;
        }
        return null;
    }

   /*
   private Mixer getMixer()
   {
      Info[] mixerInfo = AudioSystem.getMixerInfo();
      int i = 0;
      boolean present = false;

      while(i < mixerInfo.length && !present)
      {
         if(mixerInfo[i].getName().contains("Java"))
         {
            present = true;
         }
         else
         {
            ++i;
         }
      }

      if(present)
      {
         return AudioSystem.getMixer(mixerInfo[i]);
      }
      else
      {
         return null;
      }
   }

   private Clip getClip(Mixer pMixer, AudioInputStream pStream)
   {
      DataLine.Info lineInfo = new DataLine.Info(Clip.class, pStream.getFormat());
      try
      {
         return (Clip)AudioSystem.getLine(lineInfo);
      }
      catch (LineUnavailableException e)
      {
         return null;
      }
   }
   */

    private Clip loadSound(String pName) {
        try {
            java.net.URL soundurl = ResourceManager.class.getResource("/res/" + pName + ".wav");
            AudioInputStream s = AudioSystem.getAudioInputStream(soundurl);
            Clip snd = AudioSystem.getClip();
            snd.open(s);
            mSounds.put(pName, snd);
            return snd;
        } catch (Exception e) {
            LOGGER.error("Unable to load sound " + pName + ".");
            return null;
        }
    }

    private ResourceManager() {
        Font f = new Font("Dialog", Font.PLAIN, 12);
        setStandardFont(f);

        try {
            mUnitBundle = getBundle("Units");
        } catch (MissingResourceException e) {
            LOGGER.warn("No Units resource bundle found.");
        }

        try {
            mFormatBundle = getBundle("Format");
        } catch (MissingResourceException e) {
            LOGGER.warn("No Format resource bundle found.");
        }
    }

    public String getUnit(String pKey) {
        return mUnitBundle.getString(pKey);
    }

    public DecimalFormat getFormat(String pKey) {
        String fmt;

        try {
            fmt = mFormatBundle.getString(pKey);
        } catch (Exception e) {
            fmt = "0.00";
        }

        try {
            DecimalFormat dec = (DecimalFormat) NumberFormat.getNumberInstance();
            dec.applyPattern(fmt);
            return dec;
        } catch (ClassCastException e) {
            return new DecimalFormat(fmt);
        }
    }

    private static ResourceManager mInstance = new ResourceManager();
    private ResourceBundle mUnitBundle;
    private ResourceBundle mFormatBundle;
    private Font mSmallFont;
    private Font mStandardFont;
    private Font mStandardFontLight;
    private Font mLargeFont;
    private Font mLargerFont;
    private Font mBigFont;
    private Font mBiggestFont;
    private Font mBiggestFontLight;
    private Font mStandardFontBold;
    private Locale mLocale = new Locale("en", "US");
    private HashMap<String, ImageIcon> mImageIcons = new HashMap<String, ImageIcon>();
    private HashMap<String, ResourceBundle> mBundles = new HashMap<String, ResourceBundle>();
    private HashMap<String, Clip> mSounds = new HashMap<String, Clip>();
    private boolean mWarningColorScheme = false;
    private final Color mLightBlue = new Color(0.78f, 0.67f, 0.87f);
    private final Color mBlue = new Color(0.38f, 0.12f, 0.69f);
    private final Color mDarkBlue = new Color(0.19f, 0.06f, 0.345f);
    private final Color mLightRed = new Color(255, 65, 65);
    private final Color mDarkRed = new Color(157, 0, 0);
    private final Color mLightGray = new Color(215, 215, 215);
    private final Color mOrange = new Color(241, 171, 0);
    private final Color mBackground = new Color(250, 250, 250);
    private String[] mSupportedLanguages = {"english", "french", "chinese"};

    public static final int ENGLISH = 0;
    public static final int FRENCH = 1;
    public static final int CHINESE = 2;
}
