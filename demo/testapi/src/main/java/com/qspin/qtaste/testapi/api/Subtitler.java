package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.SingletonComponent;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 * The subtitler displays information on the bottom of the screen.
 *
 * @author simjan
 */
public interface Subtitler extends SingletonComponent {

    /**
     * Displays the subtitle for maximum 3 seconds. <br>
     * Same as setSubtitle(subtitle, 3)
     *
     * @param subtitle The message to display.
     */
    void setSubtitle(String subtitle) throws QTasteException;

    /**
     * Displays the subtitle for maximum the specified number of seconds.
     * Only one message is displayed in the same time. If a message is already displayed, it is replaced by the new one and the
     * displayed timer reset to the given value.
     *
     * @param subtitle The message to display.
     * @param displayTimeInSecond The maximum display time.
     */
    void setSubtitle(String subtitle, double displayTimeInSecond) throws QTasteException;
}
