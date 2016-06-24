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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

//import org.apache.log4j.Logger;

/**
 * This class contains methods to start a command in a new process and wait for the process to terminate.
 *
 * @author lvboque
 */
public class Exec {

    //protected static Logger logger = Log4jLoggerFactory.getLogger(Exec.class);
    private volatile Process process;

    /**
     * Executes the a command specified in parameter.
     *
     * @param cmd a specified system command
     * @return the cancel value of the process. By convention, 0 indicates normal termination.
     */
    public int exec(String cmd) throws IOException, InterruptedException {
        return exec(cmd, null);
    }

    public int exec(String[] cmd) throws IOException, InterruptedException {
        return exec(cmd, null);
    }

    /**
     * Executes the a command specified in parameter.
     *
     * @param cmd a specified system command
     * @param env the environment variables map to use or null to inherit process environment
     * @return the cancel value of the process. By convention, 0 indicates normal termination
     */
    public int exec(String cmd, Map<String, String> env) throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, null);
    }

    public int exec(String[] cmd, Map<String, String> env) throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, null);
    }

    /**
     * Executes the a command specified in parameter.
     *
     * @param cmd a specified system command
     * @param env the environment variables map to use or null to inherit process environment
     * @param output the ByteArrayOutputStream will get a copy of the output and error streams of the subprocess
     * @return the cancel value of the process. By convention, 0 indicates normal termination
     */
    public int exec(String cmd, Map<String, String> env, ByteArrayOutputStream output) throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, output, null);
    }

    public int exec(String[] cmd, Map<String, String> env, ByteArrayOutputStream output)
          throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, output, null);
    }

    /**
     * Executes the a command specified in parameter.
     *
     * @param cmd a specified system command
     * @param env the environment variables map to use or null to inherit process environment
     * @param out the OutputStream will gets the output stream of the subprocess
     * @param err the OutputStream will gets the error stream of the subprocess
     * @param output the ByteArrayOutputStream will get a copy of the output and error streams of the subprocess
     * @return the cancel value of the process. By convention, 0 indicates normal termination
     */
    public int exec(String cmd, Map<String, String> env, OutputStream out, OutputStream err, ByteArrayOutputStream output)
          throws IOException, InterruptedException {
        return exec(cmd, env, out, err, output, null);
    }

    public int exec(String[] cmd, Map<String, String> env, OutputStream out, OutputStream err, ByteArrayOutputStream output)
          throws IOException, InterruptedException {
        return exec(cmd, env, out, err, output, null);
    }

    /**
     * Executes the a command specified in parameter.
     *
     * @param cmd a specified system command
     * @param env the environment variables map to use or null to inherit process environment
     * @param dir the working directory of the subprocess, or null if the subprocess should inherit the working directory of the
     * current process
     * @return the cancel value of the process. By convention, 0 indicates normal termination
     */
    public int exec(String cmd, Map<String, String> env, File dir) throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, null, dir);
    }

    public int exec(String[] cmd, Map<String, String> env, File dir) throws IOException, InterruptedException {
        return exec(cmd, env, System.out, System.err, null, dir);
    }

    /**
     * Executes the a command specified in the specified directory.
     *
     * @param cmd a specified system command
     * @param env the environment variables map to use or null to inherit process environment
     * @param stdout an OutputStream for stdout
     * @param stderr an OutputStream for stderr
     * @param output the ByteArrayOutputStream will get a copy of the output and error streams of the subprocess
     * @param dir the working directory of the subprocess, or null if the subprocess should inherit the working directory of the
     * current process
     * @return the cancel value of the process. By convention, 0 indicates normal termination
     */
    public int exec(String cmd, Map<String, String> env, OutputStream stdout, OutputStream stderr, ByteArrayOutputStream
          output, File dir)
          throws IOException, InterruptedException {
        //logger.debug("Executing '" + cmd + "'");
        if (output == null) {
            output = new ByteArrayOutputStream();
        }
        try {
            String[] envp;
            if (env != null) {
                envp = new String[env.size()];
                int i = 0;
                for (Map.Entry<String, String> envMapEntry : env.entrySet()) {
                    envp[i++] = envMapEntry.getKey() + "=" + envMapEntry.getValue();
                }
            } else {
                envp = null;
            }

            process = Runtime.getRuntime().exec(cmd, envp, dir);
            process.getOutputStream().close();
            MyReader t1 = new MyReader(process.getInputStream(), stdout, output);
            MyReader t2 = new MyReader(process.getErrorStream(), stderr, output);
            t1.start();
            t2.start();
            int exitCode = process.waitFor();
            process = null;
            t1.cancel();
            t2.cancel();
            t1.join();
            t2.join();
            //if (output.size() > 0) {
            //    logger.debug("Executed command output:\n" + output.toString());
            //}
            return exitCode;
        } finally {
            process = null;
        }
    }

    public int exec(String[] cmd, Map<String, String> env, OutputStream stdout, OutputStream stderr, ByteArrayOutputStream
          output, File dir)
          throws IOException, InterruptedException {
        //logger.debug("Executing ['" + Strings.join(cmd, "', '") + "']");
        if (output == null) {
            output = new ByteArrayOutputStream();
        }
        try {
            String[] envp;
            if (env != null) {
                envp = new String[env.size()];
                int i = 0;
                for (Map.Entry<String, String> envMapEntry : env.entrySet()) {
                    envp[i++] = envMapEntry.getKey() + "=" + envMapEntry.getValue();
                }
            } else {
                envp = null;
            }

            process = Runtime.getRuntime().exec(cmd, envp, dir);
            process.getOutputStream().close();
            MyReader t1 = new MyReader(process.getInputStream(), stdout, output);
            MyReader t2 = new MyReader(process.getErrorStream(), stderr, output);
            t1.start();
            t2.start();
            int exitCode = process.waitFor();
            process = null;
            t1.cancel();
            t2.cancel();
            t1.join();
            t2.join();
            //if (output.size() > 0) {
            //    logger.debug("Executed command output:\n" + output.toString());
            //}
            return exitCode;
        } finally {
            process = null;
        }
    }

    /**
     * Kills the executed process.
     */
    public void kill() {
        if (process != null) {
            process.destroy();
            process = null;
        }
    }

    class MyReader extends Thread {

        BufferedInputStream in;
        OutputStream out;
        final ByteArrayOutputStream outResult;
        boolean cancelled;
        byte[] buffer;

        MyReader(InputStream in, OutputStream out, ByteArrayOutputStream outResult) {
            this.in = new BufferedInputStream(in);
            this.out = out;
            this.outResult = outResult;
            this.cancelled = false;
            this.buffer = new byte[256];
        }

        @Override
        public void run() {
            try {
                while (!cancelled) {
                    int numberBytesTransferred = transferAvailableBytes();
                    if (numberBytesTransferred == 0) {
                        Thread.sleep(100);
                    } else if (numberBytesTransferred < 0) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                // try to transfer remaining bytes
                transferAvailableBytes();
            }
        }

        public void cancel() {
            cancelled = true;
        }

        /**
         * Transfers available bytes from in to out and return number of bytes transferred.
         *
         * @return number of bytes transferred or -1 if error
         */
        private int transferAvailableBytes() {
            try {
                int numberBytesTransferred = 0;
                int numberBytesAvailable = in.available();
                while (numberBytesAvailable > 0) {
                    int numberBytesRead = in.read(buffer, 0, Math.min(numberBytesAvailable, buffer.length));
                    if (out != null) {
                        out.write(buffer, 0, numberBytesRead);
                    }
                    synchronized (outResult) {
                        outResult.write(buffer, 0, numberBytesRead);
                    }
                    numberBytesTransferred += numberBytesRead;
                    numberBytesAvailable = in.available();
                }
                return numberBytesAvailable >= 0 ? numberBytesTransferred : -1;
            } catch (IOException e) {
                return -1;
            }
        }
    }
}
