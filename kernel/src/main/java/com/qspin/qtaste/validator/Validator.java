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

import com.qspin.qtaste.testsuite.QTasteTestFailException;

public abstract class Validator {

    protected abstract boolean validate();

    protected abstract String getExtraDetails();

    public void validate(String failMessagePrefix) throws QTasteTestFailException {
        if (!validate()) {
            throw new QTasteTestFailException(getExceptionMessage(failMessagePrefix));
        }
    }

    public void waitForValidation(long maxTime_ms) throws QTasteTestFailException {
        waitForValidation(maxTime_ms, 100, null);
    }

    public void waitForValidation(long maxTime_ms, long checkIntervalTime_ms, String failMessagePrefix)
          throws QTasteTestFailException {
        try {
            long beginTime_ms = System.currentTimeMillis(); // begin time
            long elapsedTime_ms; // total elapsed time
            do {
                if (validate()) {
                    return;
                }
                elapsedTime_ms = System.currentTimeMillis() - beginTime_ms;
                long remainingTime_ms = maxTime_ms - elapsedTime_ms;
                if (remainingTime_ms <= 0) {
                    throw new QTasteTestFailException(getExceptionMessage(failMessagePrefix));
                } else {
                    Thread.sleep(Math.min(checkIntervalTime_ms, remainingTime_ms));
                }
            }
            while (true);
        } catch (InterruptedException ex) {
            throw new QTasteTestFailException("Thread has been interrupted while waiting in validation");
        }
    }

    protected String getExceptionMessage(String failMessagePrefix) {
        String message;
        if (failMessagePrefix != null) {
            message = failMessagePrefix + ": " + getExtraDetails();
        } else {
            message = getExtraDetails();
        }
        return message;
    }
}
