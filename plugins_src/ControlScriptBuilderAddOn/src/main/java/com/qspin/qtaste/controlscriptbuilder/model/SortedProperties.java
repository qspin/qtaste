package com.qspin.qtaste.controlscriptbuilder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("serial")
final class SortedProperties extends Properties {

	@SuppressWarnings({ "unchecked", "rawtypes" })
   public synchronized Enumeration keys()
   {
      Enumeration keysEnum = super.keys();
      List<String> keyList = new ArrayList<String>();
      while (keysEnum.hasMoreElements())
      {
         keyList.add(keysEnum.nextElement().toString());
      }
      Collections.sort(keyList);
      return Collections.enumeration(keyList);
   }
}
