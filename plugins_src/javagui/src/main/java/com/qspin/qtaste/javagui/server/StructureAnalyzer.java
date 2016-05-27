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

package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * @author pguermo
 */
final class StructureAnalyzer extends ComponentCommander {

	/**
	* Analyze the structure of a java application and save it in the specified filename in the current working directory.
	* @return null
	*/
	@Override
	Object executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		try {
			prepareWriter(data[0].toString());
			for ( int i=0; i<Frame.getWindows().length; ++i )
			{
				Window windows = Frame.getWindows()[i];
				analyzeComponent(windows, 1);
			}
			mWriter.write("</root>");
			mWriter.flush();
			mWriter.close();
		} catch (IOException e) {
			throw new QTasteTestFailException("Error saving to file" + data[0].toString() + ":", e);
		}

		return null;
	}

	protected void analyzeComponent(Component pComponent, int pLevel) throws IOException
	{
		Class<?> componentClass = pComponent.getClass();
		String componentText = null;

		String componentClassName = componentClass.getName();
		if ("javax.swing.JButton".equals(componentClassName))
		{
			componentText = ((javax.swing.JButton) pComponent).getText();
		}
		else if ("javax.swing.JToggleButton".equals(componentClassName))
		{
			componentText = ((javax.swing.JToggleButton) pComponent).getText();
		}
		else if ("javax.swing.JRadioButton".equals(componentClassName))
		{
			componentText = ((javax.swing.JRadioButton) pComponent).getText();
		}
		else if ("javax.swing.JLabel".equals(componentClassName))
		{
			componentText = ((javax.swing.JLabel) pComponent).getText();
		}
		else if ("javax.swing.JTextField".equals(componentClassName))
		{
			componentText = ((javax.swing.JTextField) pComponent).getText();
		}
		else if ("javax.swing.JFormattedTextField".equals(componentClassName))
		{
			componentText = ((javax.swing.JFormattedTextField) pComponent).getText();
		}
		else if ("javax.swing.JTextArea".equals(componentClassName))
		{
			componentText = ((javax.swing.JTextArea) pComponent).getText();
		}

		if ( pComponent instanceof Container && ((Container)pComponent).getComponentCount() > 0 )
		{
			writeComponent("<component class=\"" + componentClassName + "\" name=\""+ pComponent.getName() + "\""
					+ ((componentText != null && componentText.equals(""))?"":" text=\"" + componentText + "\"")
					+ ">", pLevel);
			for ( int i=0; i<((Container)pComponent).getComponentCount(); ++i )
			{
				Component c = ((Container)pComponent).getComponent(i);
				analyzeComponent(c, pLevel+1);
			}
			writeComponent("</component>", pLevel);
		}
		else
		{
			writeComponent("<component class=\"" + componentClassName + "\" name=\""+ pComponent.getName() + "\""
					+ ((componentText != null && componentText.equals(""))?"":" text=\"" + componentText + "\"")
					+ "></component>", pLevel);
		}
	}

	protected void prepareWriter(String fileName) throws IOException {
		if (fileName.equals("")) {
			fileName = "struct.xml";
		}
		mWriter = new BufferedWriter( new FileWriter(fileName));
		mWriter.write("<?xml version=\"1.0\"?>");
		mWriter.newLine();
		mWriter.write("<root>");
		mWriter.newLine();
	}

	protected void writeComponent(String pText, int pLevel) throws IOException {
		StringBuilder builder = new StringBuilder();
		for ( int i=0; i<pLevel; ++i){
			builder.append("   ");
		}
		builder.append(pText);
		mWriter.write(builder.toString());
		mWriter.newLine();
	}

	protected BufferedWriter mWriter;
	private static Logger LOGGER = Logger.getLogger(StructureAnalyzer.class);

}