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

/*
 * CSVFile.java
 *
 * Created on 11 octobre 2007, 15:34
 */
package com.qspin.qtaste.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class is responsible to build a different kind of data structures from a CSV file
 * 
 * @author lvboque
 */
public class CSVFile
{

   // private static final Logger logger = Log4jLoggerFactory.getLogger(CSVFile.class);
   private InputStream csvInputStream;
   private String name;
   private boolean alreadyParsed = false;
   private ArrayList<LinkedHashMap<String, String>> dataSet;
   private ArrayList<String> columnNames;

   /**
    * Creates a new instance of CSVFile
    */
   public CSVFile(File csvFile) throws IOException
   {
      this(new FileInputStream(csvFile), csvFile.getName());
   }

   public CSVFile(String csvFileName) throws IOException
   {
      this(new File(csvFileName));
   }

   public CSVFile(InputStream csvInputStreamFile, String name)
   {
      this.csvInputStream = csvInputStreamFile;
      this.name = name;
      dataSet = new ArrayList<LinkedHashMap<String, String>>();
      columnNames = new ArrayList<String>();      
   }

   public String getName()
   {
      return name;
   }

   /**
    * Return a list of HashMap of Name/Value from the CSV file
    * 
    * @return the list of HashMap of Name/Value read from the CSV file
    * @throws java.io.FileNotFoundException If the CSV file is not found
    * @throws java.io.IOException If an error occurs reading the CSV file
    */
   public List<LinkedHashMap<String, String>> getCSVDataSet() throws FileNotFoundException, IOException
   {
      parseCSVFile();
      return dataSet;
   }

   /**
    * Return a HashMap mapping the specified keyField with a HashMap of names/values
    * 
    * @param keyField the keyField
    * @return a HashMap the hashmap
    * @throws java.io.FileNotFoundException If the CSV file is not found
    * @throws java.io.IOException If an error occurs while reading the CSV File
    * @throws java.lang.NoSuchFieldException If the keyField is not found in the CSV file
    */
   public HashMap<String, HashMap<String, String>> getHashMapDataSet(String keyField)
         throws FileNotFoundException, IOException, NoSuchFieldException
   {
      parseCSVFile();
      HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
      for (int i = 0; i < dataSet.size(); i++)
      {
         String key = dataSet.get(i).get(keyField);
         if (key == null)
         {
            throw new NoSuchFieldException("keyField " + keyField + " is mandatory " + "at line " + i
                  + " in the csv file");
         }
         HashMap<String, String> line = dataSet.get(i);
         result.put(dataSet.get(i).get(keyField), line);
      }
      return result;
   }

   /**
    * Return a list of the column names of a CSV file
    * 
    * @return a list of the column names read from the CSV file
    * @throws java.io.FileNotFoundException If the CSV file is not found
    * @throws java.io.IOException If an error occurs while reading the CSV File
    */
   public List<String> getColumnNames() throws FileNotFoundException, IOException
   {
      parseCSVFile();
      return columnNames;
   }

   private void parseCSVFile() throws FileNotFoundException, IOException
   {
      if (!alreadyParsed)
      {
         BufferedReader csvBuffer = new BufferedReader(new InputStreamReader(csvInputStream));
         String csvLine;
         while ((csvLine = csvBuffer.readLine()) != null)
         {
            if (!csvLine.startsWith("#"))
            {
               if (csvLine.length() == 0)
               {
                  throw new IOException("CSVFile: First line cannot be an empty line");
               }
               // First line is the header with variable names
               String[] allNames = csvLine.split(";");
               for (String name : allNames)
               {
                  if (name.length() > 0)
                  {
                     columnNames.add(name);
                  }
               }

               String[] values;

               // Next lines are data
               while ((csvLine = csvBuffer.readLine()) != null)
               {
                  // Skip empty and commented out lines
                  if ((csvLine.length() != 0) && !csvLine.startsWith("#"))
                  {
                     values = csvLine.split(";", -1);

                     LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                     boolean hasNoValues = true;
                     for (int i = 0; i < allNames.length; i++)
                     {
                        if (allNames[i].length() > 0)
                        {
                           try
                           {
                              map.put(allNames[i], values[i]);
                              if (!values[i].isEmpty())
                              {
                                 hasNoValues = false;
                              }
                           }
                           catch (ArrayIndexOutOfBoundsException e)
                           {
                              // Value are missing, value is then ""
                              map.put(allNames[i], "");
                           }
                        }
                     }
                     if (!hasNoValues)
                     {
                        dataSet.add(map);
                     }
                  }
               }

            }
         }
         csvBuffer.close();
      }
      alreadyParsed = true;
   }
   
   
   
   
   //------------------------------------
   public static void main(String [ ] args) throws Exception
   {
      InputStream csvInputStream = CSVFile.class.getResourceAsStream("/TestData.csv");

      CSVFile testDataFile = new CSVFile(csvInputStream, "TestData.csv");
      List<String> columnNames = testDataFile.getColumnNames(); 

      for (int i = 0; i < columnNames.size(); i++)
      {
         System.out.println(columnNames.get(i));
      }
   }
}

