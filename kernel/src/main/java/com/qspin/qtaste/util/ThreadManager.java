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

package com.qspin.qtaste.util;

import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.testsuite.TestScript.TaskThread;

/**
 *
 * @author vdubois
 */
public class ThreadManager {
        public static void stopThread(ThreadGroup group,int level) {
            // Get threads in `group'
            int numThreads = group.activeCount();
            Thread[] threads = new Thread[numThreads * 2];
            numThreads = group.enumerate(threads, false);

            // Enumerate each thread in `group'
            for (int i = 0; i < numThreads; i++) {
                // Get thread
                Thread thread = threads[i];
                if (thread instanceof TaskThread)
                {
                    TaskThread taskThread = (TaskThread)thread;
                    taskThread.abort("Test aborted by the user", TestResult.Status.NOT_AVAILABLE, true);
                }

            }
        }        

}
