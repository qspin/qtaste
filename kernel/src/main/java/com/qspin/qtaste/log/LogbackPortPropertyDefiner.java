package com.qspin.qtaste.log;

import ch.qos.logback.core.PropertyDefinerBase;
import com.qspin.qtaste.config.TestEngineConfiguration;

public class LogbackPortPropertyDefiner extends PropertyDefinerBase
{
   public String getPropertyValue()
   {
      TestEngineConfiguration config = TestEngineConfiguration.getInstance();
      return config.getString("logback_server.port");
   }
}
