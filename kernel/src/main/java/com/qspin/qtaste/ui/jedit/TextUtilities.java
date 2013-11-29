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


/**
 * @author vdubois
 */
public class TextUtilities
{
	/**
	 * Locates the start of the word at the specified position.
	 * @param text the text
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 */
	public static int findWordStart(String text, int pos, String noWordSep)
	{
		return findWordStart(text, pos, noWordSep, true, false);
	}

	/**
	 * Locates the start of the word at the specified position.
	 * @param text the text
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 * @param joinNonWordChars Treat consecutive non-alphanumeric
	 * characters as one word
	 */
	public static int findWordStart(String text, int pos, String noWordSep,
		boolean joinNonWordChars)
	{
		return findWordStart(text,pos,noWordSep,joinNonWordChars,false);
	}

	/**
	 * Locates the start of the word at the specified position.
	 * @param line the line
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 * @param joinNonWordChars Treat consecutive non-alphanumeric
	 * characters as one word
	 * @param eatWhitespace Include whitespace at start of word
	 */
	public static int findWordStart(String line, int pos, String noWordSep,
		boolean joinNonWordChars, boolean eatWhitespace)
	{
		char ch = line.charAt(pos);

		if(noWordSep == null)
			noWordSep = "";

		int type;
		if(Character.isWhitespace(ch))
			type = WHITESPACE;
		else if(Character.isLetterOrDigit(ch)
			|| noWordSep.indexOf(ch) != -1)
			type = WORD_CHAR;
		else
			type = SYMBOL;

		for(int i = pos; i >= 0; i--)
		{
			ch = line.charAt(i);
			switch(type)
			{
			case WHITESPACE:
				// only select other whitespace in this case
				if(Character.isWhitespace(ch))
					break;
				// word char or symbol; stop
				else
					return i + 1; //}}}
			//{{{ Word character...
			case WORD_CHAR:
				// word char; keep going
				if(Character.isLetterOrDigit(ch) ||
					noWordSep.indexOf(ch) != -1)
				{
					break;
				}
				// whitespace; include in word if eating
				else if(Character.isWhitespace(ch)
					&& eatWhitespace)
				{
					type = WHITESPACE;
					break;
				}
				else
					return i + 1;
			case SYMBOL:
				if(!joinNonWordChars && pos != i)
					return i + 1;

				// whitespace; include in word if eating
				if(Character.isWhitespace(ch))
				{
					if(eatWhitespace)
					{
						type = WHITESPACE;
						break;
					}
					else
						return i + 1;
				}
				else if(Character.isLetterOrDigit(ch) ||
					noWordSep.indexOf(ch) != -1)
				{
					return i + 1;
				}
				else
				{
					break;
				}
			}
		}

		return 0;
	}

	/**
	 * Locates the end of the word at the specified position.
	 * @param line The text
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 */
	public static int findWordEnd(String line, int pos, String noWordSep)
	{
		return findWordEnd(line, pos, noWordSep, true);
	}

	/**
	 * Locates the end of the word at the specified position.
	 * @param line The text
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 * @param joinNonWordChars Treat consecutive non-alphanumeric
	 * characters as one word
	 * @since jEdit 4.1pre2
	 */
	public static int findWordEnd(String line, int pos, String noWordSep,
		boolean joinNonWordChars)
	{
		return findWordEnd(line,pos,noWordSep,joinNonWordChars,false);
	}

	/**
	 * Locates the end of the word at the specified position.
	 * @param line The text
	 * @param pos The position
	 * @param noWordSep Characters that are non-alphanumeric, but
	 * should be treated as word characters anyway
	 * @param joinNonWordChars Treat consecutive non-alphanumeric
	 * characters as one word
	 * @param eatWhitespace Include whitespace at end of word
	 * @since jEdit 4.2pre5
	 */
	public static int findWordEnd(String line, int pos, String noWordSep,
		boolean joinNonWordChars, boolean eatWhitespace)
	{
		if(pos != 0)
			pos--;

		char ch = line.charAt(pos);

		if(noWordSep == null)
			noWordSep = "";

		int type;
		if(Character.isWhitespace(ch))
			type = WHITESPACE;
		else if(Character.isLetterOrDigit(ch)
			|| noWordSep.indexOf(ch) != -1)
			type = WORD_CHAR;
		else
			type = SYMBOL;
		//}}}

		for(int i = pos; i < line.length(); i++)
		{
			ch = line.charAt(i);
			switch(type)
			{
			case WHITESPACE:
				// only select other whitespace in this case
				if(Character.isWhitespace(ch))
					break;
				else
					return i;
			case WORD_CHAR:
				if(Character.isLetterOrDigit(ch) ||
					noWordSep.indexOf(ch) != -1)
				{
					break;
				}
				// whitespace; include in word if eating
				else if(Character.isWhitespace(ch)
					&& eatWhitespace)
				{
					type = WHITESPACE;
					break;
				}
				else
					return i; //}}}
			case SYMBOL:
				if(!joinNonWordChars && i != pos)
					return i;

				// if we see whitespace, set flag.
				if(Character.isWhitespace(ch))
				{
					if(eatWhitespace)
					{
						type = WHITESPACE;
						break;
					}
					else
						return i;
				}
				else if(Character.isLetterOrDigit(ch) ||
					noWordSep.indexOf(ch) != -1)
				{
					return i;
				}
				else
				{
					break;
				}
			}
		}

		return line.length();
	}

	/**
	 * Converts tabs to consecutive spaces in the specified string.
	 * @param in The string
	 * @param tabSize The tab size
	 */
	public static String tabsToSpaces(String in, int tabSize)
	{
		StringBuffer buf = new StringBuffer();
		int width = 0;
		for(int i = 0; i < in.length(); i++)
		{
			switch(in.charAt(i))
			{
			case '\t':
				int count = tabSize - (width % tabSize);
				width += count;
				while(--count >= 0)
					buf.append(' ');
				break;
			case '\n':
				width = 0;
				buf.append(in.charAt(i));
				break;
			default:
				width++;
				buf.append(in.charAt(i));
				break;
                        }
                }
                return buf.toString();
	}


	//getStringCase() method
	public static final int MIXED = 0;
	public static final int LOWER_CASE = 1;
	public static final int UPPER_CASE = 2;
	public static final int TITLE_CASE = 3;

	/**
	 * Returns if the specified string is all upper case, all lower case,
	 * or title case (first letter upper case, rest lower case).
	 * @param str The string
	 * @since jEdit 4.0pre1
	 */
	public static int getStringCase(String str)
	{
		if(str.length() == 0)
			return MIXED;

		int state = -1;

		char ch = str.charAt(0);
		if(Character.isLetter(ch))
		{
			if(Character.isUpperCase(ch))
				state = UPPER_CASE;
			else
				state = LOWER_CASE;
		}

		for(int i = 1; i < str.length(); i++)
		{
			ch = str.charAt(i);
			if(!Character.isLetter(ch))
				continue;

			switch(state)
			{
			case UPPER_CASE:
				if(Character.isLowerCase(ch))
				{
					if(i == 1)
						state = TITLE_CASE;
					else
						return MIXED;
				}
				break;
			case LOWER_CASE:
			case TITLE_CASE:
				if(Character.isUpperCase(ch))
					return MIXED;
				break;
			}
		}

		return state;
	}

	/**
	 * Converts the specified string to title case, by capitalizing the
	 * first letter.
	 * @param str The string
	 * @since jEdit 4.0pre1
	 */
	public static String toTitleCase(String str)
	{
		if(str.length() == 0)
			return str;
		else
		{
			return Character.toUpperCase(str.charAt(0))
				+ str.substring(1).toLowerCase();
		}
	}

	private static final int WHITESPACE = 0;
	private static final int WORD_CHAR = 1;
	private static final int SYMBOL = 2;

}
