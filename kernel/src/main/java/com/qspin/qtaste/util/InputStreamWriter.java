package com.qspin.qtaste.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Redirect an input stream to an output Stream.
 */
public final class InputStreamWriter extends Thread {
    /**
     * Constructor.
     *
     * @param pLogPrefix a log prefix
     * @param pInput an input stream
     */
    public InputStreamWriter(String pLogPrefix, InputStream pInput) {
        mInput = pInput;
        mLogPrefix = pLogPrefix;
        mOutput = Collections.synchronizedList(new ArrayList<String>());
        mLimit = 1000;
    }

    public void run() {
        try {
            LOGGER.debug("Stream redirection start");
            BufferedReader br = new BufferedReader(new InputStreamReader(mInput));
            String line;
            while ((line = br.readLine()) != null) {
                if (mLimit != 0) {
                    mOutput.add(line);
                }
                if (mLimit > 0) {
                    while (mOutput.size() > mLimit) {
                        mOutput.remove(0);
                    }
                }
                LOGGER.debug(mLogPrefix + " => " + line);
                mLastLine = line;
            }
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            LOGGER.debug("Stream redirection stop");
        }
    }

    public void setBufferLimit(int pLimit) {
        mLimit = pLimit;
        if (mLimit == 0) {
            mOutput.clear();
        } else if (mLimit > 0) {
            while (mOutput.size() > mLimit) {
                mOutput.remove(0);
            }
        }
    }

    /**
     * Returns last line read from the input stream.
     *
     * @return last line read from the input stream.
     */
    public String getLastLineRead() {
        return mLastLine;
    }

    public List<String> getLogs() {
        return mOutput;
    }

    /**
     * Prefix that will appear in the logger.
     */
    private String mLogPrefix;
    /**
     * Last line read from the input stream.
     */
    private String mLastLine = "";
    /**
     * The redirected input stream.
     */
    private final InputStream mInput;
    /**
     * buffer limit
     */
    private int mLimit;
    /**
     * The output stream. Can be null.
     */
    private final List<String> mOutput;
    /**
     * Used for logging.
     */
    private static final Logger LOGGER = Logger.getLogger(InputStreamWriter.class);
}