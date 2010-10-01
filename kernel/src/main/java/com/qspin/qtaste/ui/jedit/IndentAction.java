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

package com.qspin.qtaste.ui.jedit;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;

import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.actions.ActionUtils;
import jsyntaxpane.actions.DefaultSyntaxAction;
import jsyntaxpane.util.JarServiceProvider;

/**
* IndentAction is used to replace Tabs with spaces. If there is selected
* text, then the lines spanning the selection will be shifted
* right by one tab-width space character.
*
* Since this is also used as an abbreviation completion action,
* Abbreviiations are processed by this event.
* 
* @author Ayman Al-Sairafi
* 
*/
@SuppressWarnings("serial")
public class IndentAction extends DefaultSyntaxAction {

public IndentAction() {
	super("insert-tab");
	}

	@Override
	public void actionPerformed(JTextComponent target, SyntaxDocument sDoc,
			int dot, ActionEvent e) {
	String selected = target.getSelectedText();
	if (selected == null) {
	// Check for abbreviations:
	Token abbrToken = sDoc.getWordAt(dot, wordsPattern);
	//Integer tabStop = ActionUtils.getTabSize(target);
	//int lineStart = sDoc.getParagraphElement(dot).getStartOffset();
	//int column = dot - lineStart;
	//int needed = tabStop - (column % tabStop);
	if (abbrvs == null || abbrToken == null) {
		target.replaceSelection("\t");
	} else {
		String abbr = abbrToken.getString(sDoc);
		target.select(abbrToken.start, abbrToken.end());
	if (abbrvs.containsKey(abbr)) {
		abbr = abbrvs.get(abbr);
	} else {
		abbr += "\t";
	}
	String[] abbrLines = abbr.split("\n");
	if (abbrLines.length > 1) {
		ActionUtils.insertLinesTemplate(target, abbrLines);
	} else {
		ActionUtils.insertSimpleTemplate(target, abbr);
	}
	}
	} else {
	String[] lines = ActionUtils.getSelectedLines(target);
	int start = target.getSelectionStart();
	StringBuilder sb = new StringBuilder();
	for (String line : lines) {
		sb.append('\t');
		sb.append(line);
		sb.append('\n');
	}
	target.replaceSelection(sb.toString());
	target.select(start, start + sb.length());
	}
	}
	private Pattern wordsPattern = Pattern.compile("\\w+");
	private Map<String, String> abbrvs;

	public void setWordRegex(String regex) {
	wordsPattern = Pattern.compile(regex);
	}

	public Pattern getWordRegex() {
	return wordsPattern;
	}

	public void setAbbreviations(String loc) {
	abbrvs = JarServiceProvider.readStringsMap(loc);
	}

	public void addAbbreviation(String abbr, String template) {
	if(abbrvs == null) {
	abbrvs = new HashMap<String, String>();
	}
	abbrvs.put(abbr, template);
	}

	public String getAbbreviation(String abbr) {
	return abbrvs == null ? null : abbrvs.get(abbr);
	}

	public Map<String, String> getAbbreviations() {
	return abbrvs;
	}
}
