/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui;

import java.awt.Component;
import java.awt.TextComponent;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * This interface describe all the methods usable to perform actions or control on a Java GUI application.
 * This interface is called "MBean" as methods may be used remotely using JMX.
 * @author lvboque
 */
public interface JavaGUI {

   /**
    * Change the COMPONENT_ENABLED_TIMEOUT used when JavaGUI searches a fully active component.
    * @param pTimeOut the new timeout value in seconds.
    * @throws IllegalArgumentException if the timeout value is negative.
    */
   void setComponentEnabledTimeout(int pTimeOut) throws IllegalArgumentException;

   /**
     * Create a snapshot of the specified GUI componentName and save it as the specified filename in the current working directory.</br>
	  * Can be used on all {@link Component}.
     * @param componentName an identifier of the {@link Component}.
     * @param fileName the name of the image file.
     */
   void takeSnapShot(String componentName, String fileName) throws QTasteException;

   /**
     * Get the list of all the component names.
	 * @return an array of String containing all the names of the component of the GUI application.
     */
   String [] listComponents() throws QTasteException;

	/**
	  * Check if a specified component is enabled.</br>
	  * Can be used on all {@link Component}. (see {@link Component#isEnabled()})
      * @param componentName an identifier of the {@link Component}.
	  * @return <code>true</code> if the specified component is enabled.
	  */
	boolean isEnabled(String componentName) throws QTasteException;

	/**
	  * Check if a specified component (and its parent(s)) is visible.</br>
	  * Can be used on all {@link Component}. (see {@link Component#isVisible()})
      * @param componentName an identifier of the {@link Component}.
	  * @return <code>true</code> if the specified component (and all its parents) is visible.
	  */
	boolean isVisible(String componentName) throws QTasteException;

	/**
	  * Check if a specified component is editable.</br>
	  * Can be used on :
	  * <ul>
	  * <li>{@link JTextComponent} (see {@link JTextComponent#isEditable()})</li>
	  * <li>{@link JComboBox} (see {@link JComboBox#isEditable()})</li>
	  * <li>{@link JTable} (see {@link JTable#isCellEditable(int, int)})</li>
	  * <li>{@link JTree} (see {@link JTree#isEditable()})</li>
	  * </ul>
     * @param componentName an identifier of the GUI component.
	  * @return <code>true</code> if the specified component is editable.
	  */
	boolean isEditable(String componentName) throws QTasteException;

   /**
     * Click on the specified componentName.
	 * Can be used on {@link AbstractButton}. (see {@link AbstractButton#doClick()})
     * @param componentName an identifier of the {@link AbstractButton} component.
     */
   void clickOnButton(String componentName) throws QTasteException;

    /**
     * Click on the specified componentName during a specified period of time.</br>
	 * Can be used on {@link AbstractButton}. (see {@link AbstractButton#doClick(int)})
     * @param componentName an identifier of the {@link AbstractButton} component.
	 * @param pressTime an identifier of the {@link AbstractButton} component.
     */
   void clickOnButton(String componentName, int pressTime) throws QTasteException;

    /**
     * Get the text used for the specied component.</br>
	  * Can be used on :
	  * <ul>
	  * <li>{@link JTextComponent} (see {@link JTextComponent#getText()})</li>
	  * <li>{@link JLabel} (see {@link JLabel#getText()})</li>
	  * <li>{@link AbstractButton} (see {@link AbstractButton#getText()})</li>
	  * </ul>
     * @param componentName an identifier of the GUI component.
	 * @return Return the text of the specified componentName.
     */
   String getText(String componentName) throws QTasteException;

    /**
     * Set the text for the specified component.</br>
	  * Can be used on :
	  * <ul>
	  * <li>{@link TextComponent} (see {@link TextComponent#setText(String)})</li>
	  * <li>{@link JTextComponent} (see {@link JTextComponent#setText(String)})</li>
	  * </ul>
     * @param componentName an identifier of the GUI component.
	 * @param value the new value for the text.
     */
   void setText(String componentName, String value) throws QTasteException;

   /**
    * Select the specified tab for the tabbed pane.</br>
	 * Can be used on {@link JTabbedPane}. (see {@link JTabbedPane#setSelectedIndex(int)})
    * @param tabbedPaneComponentName the {@link JTabbedPane} component name
    * @param tabIndex the tab index (first at 0).
    */
   void selectTab(String tabbedPaneComponentName, int tabIndex) throws QTasteException;

   /**
    * Select the specified tab for the tabbed pane.</br>
	 * Can be used on {@link JTabbedPane}. (see {@link JTabbedPane#setSelectedIndex(int)})
    * @param tabbedPaneComponentName the {@link JTabbedPane} component name
    * @param tabTitle the tab title.
    */
   void selectTabTitled(String tabbedPaneComponentName, String tabTitle) throws QTasteException;

   /**
    * Select the specified tab for the tabbed pane.</br>
	 * Can be used on {@link JTabbedPane}. (see {@link JTabbedPane#setSelectedIndex(int)})
    * @param tabbedPaneComponentName the {@link JTabbedPane} component name
    * @param tabComponentId the tab component Id Name.
    */
   void selectTabId(String tabbedPaneComponentName, String tabComponentId) throws QTasteException;

   /**
    * Set the selection state to the specified component.</br>
	* Can be used on {@link AbstractButton}. (see {@link AbstractButton#setSelected(boolean)})
    * @param componentName an identifier of the {@link AbstractButton} component.
	* @param value the new selection state.
    */
   void selectComponent(String componentName, boolean value) throws QTasteException;

   /**
    * Select the value for the specified component.</br>
	  * Can be used on :
	  * <ul>
	  * <li>{@link AbstractButton} (see {@link #selectComponent(String, boolean)})</li>
	  * <li>{@link JComboBox} (see {@link JComboBox#setSelectedIndex(int)})</li>
	  * <li>{@link JList} (see {@link JList#setSelectedIndex(int)})</li>
	  * <li>{@link JSpinner} (see {@link JSpinner#setValue(Object)})</li>
	  * <li>{@link JSlider} (see {@link JSlider#setValue(int)})</li>
	  * </ul>
    * @param componentName an identifier of the GUI component.
	* @param value the value to select.
    */
   void selectValue(String componentName, String value) throws QTasteException;

  /**
    * Return the currently selected value for the specified component.</br>
    * Can be used on :
    * <ul>
    * <li>{@link AbstractButton} (see {@link #selectComponent(String, boolean)})</li>
    * <li>{@link JComboBox} (see {@link JComboBox#setSelectedIndex(int)})</li>
    * <li>{@link JList} (see {@link JList#setSelectedIndex(int)})</li>
    * <li>{@link JSpinner} (see {@link JSpinner#setValue(Object)})</li>
    * <li>{@link JSlider} (see {@link JSlider#setValue(int)})</li>
    * </ul>
    * @param componentName an identifier of the component.
    * @return the currenlty selected value
    */
   String getSelectedValue(String componentName) throws QTasteException;


   /**
    * Select the index for the specified component.</br>
	  * Can be used on :
	  * <ul>
	  * <li>{@link JComboBox} (see {@link JComboBox#setSelectedIndex(int)})</li>
	  * <li>{@link JList} (see {@link JList#setSelectedIndex(int)})</li>
	  * </ul>
    * @param componentName an identifier of the GUI component.
	* @param index the index to select.
    */
   void selectIndex(String componentName, int index) throws QTasteException;

   /**
    * Select the node for the specified JTree.</br>
    * In this method, node names in the node path are simple strings.</br>
    * If you need more flexibility, see {@link selectNodeRe} which supports regular expressions.</br>
    * Can be used on {@link JTree}. (see {@link JTree#setSelectionPaths(javax.swing.tree.TreePath[])})
    * @param componentName an identifier of the JTree component.
    * @param nodePath the path to the node to select. This is a string composed by node names, separated by a separator.
    * @param nodePathSeparator the separator used in the node path to separate node path elements.
    * @throws QTasteException
    */
   void selectNode(String componentName, String nodePath, String nodePathSeparator) throws QTasteException;

   /**
    * Select the node for the specified JTree.</br>
    * In this method, node names in the node path are regular expressions. </br>
    * Be careful if you use some special regex characters in the node path separator.</br>
    * Can be used on {@link JTree}. (see {@link JTree#setSelectionPaths(javax.swing.tree.TreePath[])})
    * @param componentName an identifier of the JTree component.
    * @param nodePath the path to the node to select. This is a string composed by node names, separated by a separator.
    * 		 	 	  Here, node path elements are regular expressions.
    * @param nodePathSeparator the separator used in the node path to separate node path elements. 
    * 						   It is processed as a regular expression.
    * @throws QTasteException
    */
   void selectNodeRe(String componentName, String nodePath, String nodePathSeparator) throws QTasteException;

   /**
    * Clear the JTree selection.
    * @param componentName an identifier of the JTree component.
    * @throws QTasteException
    */
   void clearNodeSelection(String componentName) throws QTasteException;
   
  /**
    * Return the currently selected node for the specified JTree.</br>
    * Can be used on {@link JTree}.
    * @param componentName an identifier of the JTree component.
    * @param nodeSeparator the node separator used in the value parameter.
    */
   String getSelectedNode(String componentName, String nodeSeparator) throws QTasteException;

   /**
    * Parse a {@link JTree} component and create a String with the node content.</br>
	* Can be used on {@link JTree}.
    * @param treeComponentName the {@link JTree} component's name.
    * @param separator the string value that will separate node.
    * @return a String with the node contents.
    * @throws QTasteException
    */
   String dumpTreeContent(String treeComponentName, String separator) throws QTasteException;

   /**
    * Return the content of the list identified by the specified componentName.</br>
    * Can be used on {@link JComboBox} or  {@link JList}.
    * @param componentName the specified component name.
    * @return a String [] containing the values of the list.
    * @throws QTasteTestFailException
    */
   String [] getListContent(String componentName) throws QTasteException;

   /**
    * Return the name of the {@link Component} that have the focus.</br>
	* Can be used on all {@link Component}. (see {@link Component#getName()})
    * @return the name of the component
    * @throws QTasteTestFailException
    */
   String whoAmI() throws QTasteException;

   /**
    * Return the full name of the {@link Component} identified by the ID.</br>
	* Can be used on all {@link Component}. (see {@link Component#getName()()})
    * @param name the {@link Component} ID.
    * @return the name of the component
    * @throws QTasteTestFailException
    */
   String getRawName(String name) throws QTasteException;

   /**
    * Return an array with the full name of all popups.
    * @return an array with the full name of all popups
    * @throws QTasteTestFailException
    */
   String[] getPopupRawNames() throws QTasteException;

   /**
    * Return the full name of the active popup.
    * @return the full name of the active popup.
    * @throws QTasteTestFailException
    */
   String getPopupRawName() throws QTasteException;

   /**
    * Set the name of the GUI component that have the focus with the specified name.
    * @throws QTasteTestFailException
    */
   @Deprecated
   void setComponentName(String name) throws QTasteException;

   /**
    * Send the specified key code to the application.
    * @param keycode key code of the key sent to the application.
    * @throws QTasteTestFailException If some internal errors occurs.
    */
   void pressKey(int keycode) throws QTasteException;

   /**
    * Send the specified key code to the application.
    * @param keycode key code of the key sent to the application.
    * @param delay delay for the button pressed in milliseconds.
    * @throws QTasteTestFailException If some internal errors occurs.
    */
   void pressKey(int keycode, long delay) throws QTasteException;

   /**
    * Checks if a component with the name exist or not.</br>
	* Can be used on all {@link Component}.
    * @param pComponentName The {@link Component}'s name.
    * @return <code>true</code> if the component exist.
    */
   boolean exist(String pComponentName);

   /**
    * Counts the number of components that have the enabled state.</br>
	* Can be used on all {@link Component}. (see {@link Component#isEnabled()})
    * @param isEnabled <code>false</code> if the disabled components have to be counted.
    * @return The number of components that have the enabled state.
    */
   int getEnabledComponentCount(boolean isEnabled);

   /**
    * Counts the number of rows that have the value in the column.
	* Can be used on {@link JTable}.
    * @param pComponentName The {@link JTable}'s name.
    * @param pColumnName The column's name.
    * @param pColumnValue The value.
    * @return The number of rows that have the value in the specified column.
    */
   int countTableRows(String pComponentName, String pColumnName, String pColumnValue)throws QTasteException;
   /**
    * Select the row with the first occurrence of the value for the specified column in the {@link JTable}.
	* Can be used on {@link JTable}.
    * @param pComponentName The {@link JTable}'s name.
    * @param pColumnName The column's name.
    * @param pColumnValue The value.
    */
   void selectInTable(String pComponentName, String pColumnName, String pColumnValue)throws QTasteException;
   /**
    * Select the row with the X occurrence of the value for the specified column in the {@link JTable}.
	* Can be used on {@link JTable}.
    * @param pComponentName The {@link JTable}'s name.
    * @param pColumnName The column's name.
    * @param pColumnValue The value.
    * @param pOccurenceIndex The occurrence index to select.
    */
   void selectInTable(String pComponentName, String pColumnName, String pColumnValue, int pOccurenceIndex)throws QTasteException;

   /**
    * Checks if there is at least one popup displayed.
    * @return <code>true</code> if there is at least one popup.
    */
   boolean isPopupDisplayed() throws QTasteException;

   /**
    * Retrieves the text (message) of the active popup.</br>
    * @see JOptionPane#getMessage()
    * @return the text (message) of the active popup.
    */
   String getPopupText() throws QTasteException;

   /**component
    * Retrieves all popup texts.</br>
    * @see JOptionPane#getMessage()
    * @return all popup texts.
    */
   String[] getAllPopupText() throws QTasteException;

   /**
    * inserts a value in the active popup field.
    * @param value the value to insert.
    * @see JOptionPane#showInputDialog(Object)
    */
   void setPopupValue(String value) throws QTasteException;

   /**
    * Clicks on the button with the text in the active popup.
    * @param buttonText the button text.
    */
   void clickOnPopupButton(String buttonText) throws QTasteException;

   /**
    * Searches the component identified by the name and returns the component's background color.</br>
	* Can be used on all {@link Component}. (see {@link Component#getBackground()})
    * @param componentName the {@link Component}'s name.
    * @return the found component's background color with the RGB color format expressed in hexadecimal.
    */
   String getComponentBackgroundColor(String componentName) throws QTasteException;

   /**
    * Retrieve the location of the component on the screen. </br>
	* Can be used on all {@link Component}. (see {@link Component#getLocationOnScreen()})
    * @param componentName the {@link Component}'s name.
    * @return the location in pixel. (0, 0) is the upper left corner of the screen.
    */
   double[] getComponentLocation(String componentName) throws QTasteException;

   /**
    * Searches the component identified by the name and returns the component's foreground color.</br>
	* Can be used on {@link JTextComponent}. (see {@link JTextComponent#getForeground()})
    * @param componentName the {@link JTextComponent}'s name.
    * @return the found component's foreground color with the RGB color format expressed in hexadecimal.
    */
   String getComponentForegroundColor(String componentName) throws QTasteException;

   /**
    * Selects a file with a {@link JFileChooser}. Finds the file chooser with its name, set the file path in the text
    * field and click on the button with the specified text.
    * @param fileChooserComponentName The JFileChooser's name.
    * @param filepath The path to the file to select.
    * @throws QTasteException If No file JFilechooser with the specified name is found; If the text field is not found; If no button with the text exist.
    */
   void selectFileThroughFileChooser(String fileChooserComponentName, String filepath) throws QTasteException;

   /**
    * Analyze the structure of a java application and save it in the specified filename in the current working directory.
    * @param fileName the name of the image file.
    */
    void analyzeStructure(String fileName) throws QTasteException;
}
