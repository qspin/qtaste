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
import java.awt.Label;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class TreeDumper extends ComponentCommander {

	@Override
	String executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		String separator = data[1].toString();
		if ( c instanceof JTree )
		{
			JTree tree = (JTree) c;
			List<String> dump = new ArrayList<String>();
			dumpNode(dump, tree, tree.getModel().getRoot(), 0, "", separator);
			StringBuilder builder = new StringBuilder();
			for (String s : dump)
			{
				builder.append(s + "\n");
			}
			return builder.toString();
		} else {
			throw new QTasteTestFailException("The component \"" + data[0].toString() + "\" is not a JTree");
		}
	}

	protected void dumpNode(List<String> dump, JTree tree, Object node, int level, String prefix, String separator)
	{
		LOGGER.trace("Dump node '" + node + "' for level " + level);
		TreeModel model = tree.getModel();
		String dumpLine = prefix + getNodeText(tree, node);
		dump.add(dumpLine);
		LOGGER.trace("node '" + node + "' has " + model.getChildCount(node) + " child(ren)");
		for ( int childIndex = 0; childIndex < model.getChildCount(node); childIndex++ )
		{
			dumpNode(dump, tree, model.getChild(node, childIndex), level+1, dumpLine + separator, separator);
		}
	}
		
	private String getNodeText(JTree tree, Object node)
	{
		Component nodeComponent = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
		if (nodeComponent instanceof JLabel) {
			System.out.println("component extend JLabel");
			return ((JLabel) nodeComponent).getText();
		} else if (nodeComponent instanceof Label) {
			System.out.println("component extend TextComponent");
			return ((Label) nodeComponent).getText();
		} else {
			System.out.println("component extend something else");
			return node.toString();
		}
	}
}
