package com.qspin.qtaste.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class Log4jAppender extends AppenderBase
{

   protected void append(Object pObject)
   {
      if (pObject instanceof LoggingEventVO)
      {
         LoggingEventVO logEvent = (LoggingEventVO) pObject;
         Level level = logEvent.getLevel();
         String message = logEvent.getMessage();
         String loggerName = logEvent.getLoggerName();
         String application = logEvent.getLoggerContextVO().getName();
         if (application == null) {
            application = "";
         }
         IThrowableProxy iThrowableProxy = logEvent.getThrowableProxy();

         Exception exception = null;
         if (iThrowableProxy != null)
         {
            String className = iThrowableProxy.getClassName();
            try
            {
               StackTraceElementProxy[] stackTraceProxy = iThrowableProxy.getStackTraceElementProxyArray();
               int stackTraceSize = stackTraceProxy.length;
               StackTraceElement[] stackTrace = new StackTraceElement[stackTraceSize];
               for (int i = 0; i < stackTraceSize; i++) {
                  stackTrace[i] = stackTraceProxy[i].getStackTraceElement();
               }

               Constructor constuctor = Class.forName(className).getConstructor(String.class);
               exception = (Exception) constuctor.newInstance(message);
               exception.setStackTrace(stackTrace);
            }
            catch (InstantiationException pE)
            {
               pE.printStackTrace();
            }
            catch (InvocationTargetException pE)
            {
               pE.printStackTrace();
            }
            catch (NoSuchMethodException pE)
            {
               pE.printStackTrace();
            }
            catch (IllegalAccessException pE)
            {
               pE.printStackTrace();
            }
            catch (ClassNotFoundException pE)
            {
               pE.printStackTrace();
            }

         }

         Logger logger = Log4jLoggerFactory.getLogger(loggerName);

         if (application != null) {
            Hashtable mdc = MDC.getContext();
            MDC.put("application", application);
         }

         if (level == Level.ERROR) {
            logger.error(message, exception);
         } else if (level == Level.DEBUG.WARN) {
            logger.warn(message, exception);
         } else if (level == Level.INFO) {
            logger.info(message, exception);
         } else if (level == Level.DEBUG) {
            logger.debug(message, exception);
         } else if (level == Level.TRACE) {
            logger.trace(message, exception);
         }
      }
   }
}