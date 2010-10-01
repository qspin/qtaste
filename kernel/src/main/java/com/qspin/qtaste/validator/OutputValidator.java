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

package com.qspin.qtaste.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.util.Strings;

public class OutputValidator extends Validator {

    private File reference;
    private StringBuffer buf;
    private String extraDetails;
    private static final String IGNORE_TAG = "###IGNORE_THIS###";
    private static final String IGNORE_VALUE = ".*?";
    private static final String LINE_SEPARATOR = "\n";

    public static void check(File reference, StringBuffer buf) throws QTasteTestFailException {
        check(reference, buf, null);
    }

    public static void check(File reference, StringBuffer buf, String failMessagePrefix) throws QTasteTestFailException {
        OutputValidator validator = new OutputValidator(reference, buf);
        validator.validate(failMessagePrefix);
    }

    private OutputValidator(File reference, StringBuffer buf) {
        this.reference = reference;
        this.buf = buf;
        this.extraDetails = new String("No detail available!");
    }

    private String readLine(StringBuffer buf, int pos) {
        int p = buf.indexOf(LINE_SEPARATOR, pos);
        if (p < 0) {
            return null;
        }
        return buf.substring(pos, p);
    }

    protected boolean validate() {
        try {
            BufferedReader ref = new BufferedReader(new FileReader(reference));
            int s = 0;
            //int e=0;
            int lineCounter = 1;
            String refLine = null;
            String gotLine = null;
            do {
                refLine = ref.readLine();
                if (refLine != null) {
                    gotLine = readLine(buf, s);
                    if (gotLine == null) {
                        this.extraDetails = "Output size (" + lineCounter + " lines) is smaller than the one expected!";
                        return false;
                    }
                    //					System.out.println("" + lineCounter + ":" + refLine);
//					System.out.println("" + lineCounter + ":" + gotLine);
                    //System.out.print("got line is: " + lineCounter + gotLine + "\n");
                    String[] lineParts = refLine.split(IGNORE_TAG);
                    for (int i=0; i < lineParts.length; i++) {
                        lineParts[i] = Pattern.quote(lineParts[i]);
                    }
                    String patternedLine = Strings.join(lineParts, IGNORE_VALUE);
                    if (!gotLine.matches(patternedLine)) {
                        //System.out.println("NO MATCH! with " + patternedLine);
                        this.extraDetails = new String("Error at line " + lineCounter + ": expecting " + patternedLine + " but got " + gotLine);
                        return false;
                    }
                    // Get the next String but skip the file.separator

                    //System.out.println("gotstring.length:" + gotLine.length());				
                    //s = e + LINE_SEPARATOR.length();
                    s += (gotLine.length() + 1);
                    lineCounter++;
                }
            } while (refLine != null);
            gotLine = readLine(buf, s);
            if (gotLine != null) {
                this.extraDetails = "Output size (" + lineCounter + " lines) is bigger than the one expected!";
                return false;
            }
        } catch (Exception e) {
            this.extraDetails = e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected String getExtraDetails() {
        return this.extraDetails;
    }
}
