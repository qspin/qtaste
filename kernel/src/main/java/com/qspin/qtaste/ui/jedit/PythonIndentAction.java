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

import javax.swing.text.JTextComponent;

import de.sciss.syntaxpane.SyntaxDocument;
import de.sciss.syntaxpane.Token;
import de.sciss.syntaxpane.TokenType;
import de.sciss.syntaxpane.actions.ActionUtils;
import de.sciss.syntaxpane.actions.DefaultSyntaxAction;

import org.apache.log4j.Logger;

import com.qspin.qtaste.util.Log4jLoggerFactory;

@SuppressWarnings("serial")
public class PythonIndentAction extends DefaultSyntaxAction {
	protected static Logger logger = Log4jLoggerFactory.getLogger(PythonIndentAction.class);
	

	/**
	* This class should be mapped to VK_ENTER. It performs proper indentation
	* for Java Type languages and automatically inserts "*" in multi-line comments
	* Initial Code contributed by ser... AT mail.ru
	*
	* @author Ayman Al-Sairafi
	*/
	public PythonIndentAction() {
		super("PYTHON_INDENT");
	}

	/**
	* Return a string with number of spaces equal to the tab-stop of the TextComponent
	* @param target
	* @return
	*/
	public static String getTab(JTextComponent target) {
		return "\t";
	}
	
    /**      * Get the indentation of a line of text.
     *   This is the subString from
     *         beginning of line to the first non-space char      
     * @param line the line of text      
     * @return indentation of line.      
     */
     public static String getIndent(String line) {
    	 if (line == null || line.length() == 0) {
    		 return "";
    	 }
    	 int i = 0;
    	 while (i < line.length() && line.charAt(i) == '\t') 
    	 {             
    		 i++;         
    		 }         
    	 return line.substring(0, i);
     } 	
	/**
	* {@inheritDoc}
	* @param e 
	*/
	@Override
	public void actionPerformed(JTextComponent target, SyntaxDocument sDoc,
			int dot, ActionEvent e) {
	int pos = target.getCaretPosition();
	int start = sDoc.getParagraphElement(pos).getStartOffset();
	String line = ActionUtils.getLine(target);
	String lineToPos = line.substring(0, pos - start);
	String prefix = getIndent(line);
	Token t = sDoc.getTokenAt(pos);
	if (TokenType.isComment(t)) {
		if (line.trim().endsWith("*/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		} else if (line.trim().startsWith("*")) {
			prefix += "# ";
		} else if (line.trim().startsWith("##")) {
			prefix += "#";
		}
	} else if (lineToPos.trim().endsWith(":")) {
		// check if current line is not comment		
		prefix += getTab(target);
		} else {
			
			String noComment = sDoc.getUncommentedText(start, pos);
			// skip EOL comments
			if (noComment.trim().endsWith(":")) {
				prefix += getTab(target);
			}
			if ((lineToPos.length()>0) && (line.equals(prefix))) {
				if (prefix.length()>0)			
					prefix = prefix.substring(1);
			}
			if ((lineToPos.length()==0)) {
				if (prefix.length()>0)			
					prefix="";
			}
		}
		target.replaceSelection("\n" + prefix);
	}	
}