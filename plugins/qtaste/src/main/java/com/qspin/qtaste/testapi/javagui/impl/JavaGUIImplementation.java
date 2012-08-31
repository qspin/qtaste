package com.qspin.qtaste.testapi.javagui.impl;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.javagui.JavaGUIImpl;
import com.qspin.qtaste.testapi.javagui.api.JavaGUI;
import com.qspin.qtaste.testsuite.QTasteException;

public class JavaGUIImplementation extends JavaGUIImpl implements JavaGUI
{

   public JavaGUIImplementation(String instanceId) throws Exception
   {
      super(TestBedConfiguration.getInstance().getMIString(instanceId, "JavaGUI", "jmx_url"), instanceId);
   }
   
   @Override
   public void initialize() throws QTasteException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void terminate() throws QTasteException
   {
      // TODO Auto-generated method stub
      
   }

}
