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

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.TextAction;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.syntaxkits.PythonSyntaxKit;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.debug.BreakPointScript;
import com.qspin.qtaste.testsuite.impl.JythonTestScript;
import com.qspin.qtaste.ui.TestCasePane;
import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.FileSearch;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class NonWrappingTextPane extends JEditorPane /*JTextPane*/ {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestCasePane.class);
    private TestCasePane mTcPane = null;
    public boolean isTestScript;
    private String fileName;
    private boolean isModified = false;
    private BreakPointScript mBreakpointScript;
    private long loadDateAndTime;

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean value) {
        boolean wasAlreadyModified = isModified;
        isModified = value;
        if (wasAlreadyModified != isModified) {
            firePropertyChange("isModified", wasAlreadyModified, isModified);
        }
    }

    public void installAdditionalPopup() {
        JPopupMenu popup = this.getComponentPopupMenu();
        if (popup != null) {
            popup.add(new AddNewStep());
        }

    }

    public NonWrappingTextPane(boolean testscript) {
        super();
        PythonSyntaxKit.initKit();

        loadDateAndTime = System.currentTimeMillis();
        isTestScript = testscript;


        //setEditorKit(new HighlightKit());

        this.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                // check if the file has been modified outside the editor
                File file = new File(fileName);
                // check date and time
                long lastFileModifiedDate = file.lastModified();
                if (loadDateAndTime < lastFileModifiedDate) {
                    loadDateAndTime = lastFileModifiedDate;
                    if (JOptionPane.showConfirmDialog(null, "File has been modified outside this editor.\n Do you want to reload it?'",
                            "Update confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (mTcPane == null) {
                            return;
                        }
                        mTcPane.loadTestCaseSource(file, true, isTestScript, true);
                    }
                }
            }

            public void focusLost(FocusEvent e) {
            }
        });


        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    // check the word selected
                    Document doc = getDocument();
                    if (doc instanceof SyntaxDocument) {
                        try {
                            String selectedText = getSelectedText();
                            if (selectedText == null) {
                                return;
                            }
                            SyntaxDocument pythonDoc = (SyntaxDocument) doc;
                            // now check if the corresponding line, import exist
                            int lineIndex = yToLine(e.getY());
                            int startIndex = pythonDoc.getDefaultRootElement().getElement(lineIndex + 1).getStartOffset();
                            int endIndex = pythonDoc.getDefaultRootElement().getElement(lineIndex + 1).getEndOffset();
                            String text = pythonDoc.getText(startIndex, endIndex - startIndex);
                            if (selectedText.equals("importTestScript")) {
                                int parenPos = text.indexOf("(");
                                if (parenPos > 0) {
                                    // retrieve the script name
                                    String quoteStr = text.substring(parenPos + 1, parenPos + 2);
                                    int startQuotePos = text.indexOf(quoteStr);
                                    int endQuotePos = text.lastIndexOf(quoteStr);
                                    if (startQuotePos > 0 && endQuotePos > 0 && startQuotePos != endQuotePos) {
                                        String testScriptName =
                                                text.substring(startQuotePos + 1, endQuotePos);
                                        // search the script from testscript file
                                        File testScriptFile = new File(getFileName());
                                        try {
                                            String testScriptFileDir = testScriptFile.getParentFile().getParentFile().getCanonicalPath();
                                            String importedTestScript = testScriptFileDir + File.separator + testScriptName + File.separator + "TestScript.py";
                                            mTcPane.loadTestCaseSource(new File(importedTestScript), true, false);
                                        } catch (IOException e1) {
                                            // TODO Auto-generated catch block
                                        }
                                    }
                                }
                            }

                            if ((text.contains("import ")) || (text.indexOf("from " + selectedText) >= 0)) {
                                // the selected text is then maybe an existing python lib
                                // path to python lib
                                FileSearch fileSearch = new FileSearch();
                                List<String> pythonLibPath = JythonTestScript.getAdditionalPythonPath(new File(fileName));
                                // replace the "." characters to "/"

                                selectedText = selectedText.replace(".", File.separator);
                                if (mTcPane != null) {
                                    fileSearch.addSearchPath(new File(getFileName()).getParent());
                                    fileSearch.addSearchPath(mTcPane.getCurrentSelectedTestsuite());
                                    FileNode selectedNode = mTcPane.getCurrentSelectedFileNode();
                                    if (selectedNode != null) {
                                        if (selectedNode.isTestcaseDir()) {
                                            fileSearch.addSearchPath(selectedNode.getFile().getParent());
                                        }
                                    }
                                    for (String dir : pythonLibPath) {
                                        fileSearch.addSearchPath(dir);
                                    }
                                    fileSearch.addSearchPath(StaticConfiguration.JYTHON_LIB);
                                    fileSearch.addSearchPath(StaticConfiguration.ADDITIONNAL_JYTHON_LIB);
                                    String importFileName =
                                            fileSearch.getFirstFileFound(selectedText + ".py");
                                    if (importFileName != null) // now open the docfile
                                    {
                                        // load the file
                                        mTcPane.loadTestCaseSource(new File(importFileName), true, false);
                                    }
                                }
                            }
                        } catch (BadLocationException ex) {
                        }
                    }
                }
            }
        });



        TextAction selectWordAction = new TextAction("Select Word") {

            public void actionPerformed(ActionEvent evt) {
                int pos = getCaretPosition();

                try {
                    // Find start of word from caret
                    int start = TextUtilities.findWordStart(getDocument().getText(0, getDocument().getLength()), pos, "_");

                    // Check if start precedes whitespace
                    if (start < getDocument().getLength()
                            && Character.isWhitespace(getDocument().getText(start, 1).charAt(0))) {
                        // Check if caret is at end of word
                        if (pos > 0
                                && !Character.isWhitespace(getDocument().getText(pos - 1, 1).charAt(0))) {
                            // Start searching before the caret
                            start = TextUtilities.findWordStart(getDocument().getText(0, getDocument().getLength()), pos - 1, "_");
                        } else {
                            // Caret is not adjacent to a word
                            start = -1;
                        }
                    }
                    if (start != -1) {
                        // Find end of word from start.
                        int end = TextUtilities.findWordEnd(getDocument().getText(0, getDocument().getLength()), start + 1, "_");

                        // Set selection
                        select(start, end);
                    }
                } catch (BadLocationException e) {
                }
            }
        };
        getActionMap().put("select-word", selectWordAction);


    }
    // Override getScrollableTracksViewportWidth
    // to preserve the full width of the text

    @Override
    public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();
        ComponentUI myui = getUI();

        return parent != null ? (myui.getPreferredSize(this).width <= parent.getSize().width) : true;
    }

    public void init(String contentType) {
        setContentType(contentType);
        DefaultSyntaxKit kit = (DefaultSyntaxKit) getEditorKit();
        kit.deinstallComponent(this, "jsyntaxpane.components.LineNumbersRuler");
        kit.installComponent(this, "com.qspin.qtaste.ui.jedit.LineNumberPanel");
        installAdditionalPopup();
        setFont(new Font("monospaced", Font.PLAIN, 12));
        setTabs(4);
        ActionMap actions = this.getActionMap();
        // remove the default behaviour
        actions.remove("indent");
        // install the new one (with same name to keep the inputpmap
        actions.put("indent", new com.qspin.qtaste.ui.jedit.IndentAction());

        // install Specific indentation for Python on ENTER key
        PythonIndentAction newAction = new PythonIndentAction();
        KeyStroke ks = KeyStroke.getKeyStroke("ENTER");
        newAction.putValue(Action.ACCELERATOR_KEY, ks);
        this.getInputMap().put(ks, "PYTHON_INDENT");
        actions.put("PYTHON_INDENT", newAction);

        // add a document filter to replace tabs by 4 spaces when some text is added or replaced
        // in the document
        if (isTestScript) {
	        Document document = getDocument();
	        
	        if (document instanceof AbstractDocument) {
	        	((AbstractDocument)document).setDocumentFilter(new IndentationDocumentFilter());
	        }
        }
    }

    public void init() {
        init("text/python");
    }

    public void clearUndos() {
        SyntaxDocument doc = (SyntaxDocument) this.getDocument();
        doc.clearUndos();
    }

    public void setTabs(int charactersPerTab) {
        FontMetrics fm = getFontMetrics(getFont());
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * charactersPerTab;

        TabStop[] tabs = new TabStop[10];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
    }

    /**
     * Converts a y co-ordinate to a line index.
     * @param y The y co-ordinate
     */
    public int yToLine(int y) {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        int height = fm.getHeight();
        Document doc = this.getDocument();
        int length = doc.getLength();
        Element map = doc.getDefaultRootElement();
        int startLine = map.getElementIndex(0);
        int endline = map.getElementIndex(length);

        return Math.max(0, Math.min(endline - 1,
                y / height + startLine)) - 1;
    }

    public LineNumberPanel getLineNumberPanel() {
        Component comp = getParent().getParent();
        if (comp instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) comp;
            Component viewHeader = sp.getRowHeader().getView();
            if (viewHeader instanceof LineNumberPanel) {
                return (LineNumberPanel) viewHeader;
            }
        }
        return null;
    }

    public LineNumberPanel getDefaultLineNumberPanel() {
        Component comp = getParent().getParent();
        if (comp instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) comp;
            Component viewHeader = sp.getRowHeader().getView();
            if (viewHeader instanceof LineNumberPanel) {
                return (LineNumberPanel) viewHeader;
            }
        }
        return null;
    }

    public void selectLine(int lineNumber) {
        SyntaxDocument pythonDoc = (SyntaxDocument) getDocument();
        javax.swing.text.Element lineElement = pythonDoc.getDefaultRootElement().getElement(lineNumber - 1);
        int startIndex = lineElement.getStartOffset();
        int endIndex = lineElement.getEndOffset() - 1;
        requestFocusInWindow();
        select(startIndex, endIndex);
    }

    public void setTestCasePane(TestCasePane tcPane) {
        mTcPane = tcPane;
    }

    public String getFileName() {
        return fileName;
    }

    public void removeAllBreakpoints() {
        this.mBreakpointScript.removeAllBreakpoints();
    }

    public BreakPointScript getBreakpointScript() {
        return mBreakpointScript;
    }

    public void setFileName(String fileName) {
        mBreakpointScript = new BreakPointScript(this.getDocument());
        mBreakpointScript.setFileName(fileName);
        this.fileName = fileName;
    }

    public void addDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                if (e.getDocument() instanceof SyntaxDocument) {
                    setModified(true);
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (e.getDocument() instanceof SyntaxDocument) {
                    setModified(true);
                }
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    public void save() {
        BufferedWriter output = null;
        try {
            File file = new File(getFileName());
            //output = new BufferedWriter(new FileWriter(file)); //TODO Remove loc
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            output.append(getText());
            output.close();
            loadDateAndTime = file.lastModified();
        } catch (IOException ex) {
            logger.fatal("Cannot save file", ex);
            JOptionPane.showMessageDialog(
            		null,
            		"Error during the file saving :\n" + ex.getMessage() + "\nSee the log for more information",
            		"Cannot save the file",
            		JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                output.close();
                setModified(false);
            } catch (IOException ex) {
                logger.fatal("Cannot save file", ex);
                JOptionPane.showMessageDialog(
                		null,
                		"Error during the file saving :\n" + ex.getMessage() + "\nSee the log for more information",
                		"Cannot save the file",
                		JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public class AddNewStep extends AbstractAction {

        public AddNewStep() {
            super("Add new step");
        }

        public void actionPerformed(ActionEvent e) {
            try {
                // ask for the stepName
                String input = null;
                input = JOptionPane.showInputDialog(null,
                        "Give the name of the step",
                        "Name of the step",
                        JOptionPane.QUESTION_MESSAGE);
                if (input != null) {
                    // get the selected table lines
                    Document doc = getDocument();
                    String NewStepTemplate =
                            "def " + input + "():\n"
                            + "	\"\"\"\n"
                            + "	@step	   Description of the actions done for this step\n"
                            + "	@expected  Description of the expected result\n"
                            + "	\"\"\"\n"
                            + "	pass\n";
                    doc.insertString(NonWrappingTextPane.this.getCaretPosition(), NewStepTemplate, null);

                    String CallStepTemplate = "doStep(" + input + ")\n";
                    doc.insertString(doc.getLength(), CallStepTemplate, null);
                }
            } catch (BadLocationException ex) {
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    class SaveFile extends AbstractAction {

        public SaveFile() {
            super("Save");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	if (NonWrappingTextPane.this.isModified()) {
        		NonWrappingTextPane.this.save();
        	}
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
    /**
     * Document filter to replace tabs with spaces when the document is edited.
     */
    class IndentationDocumentFilter extends DocumentFilter {

        @Override
    	public void	insertString(DocumentFilter.FilterBypass fb, int offset, String text, 
    			AttributeSet attrs) throws BadLocationException {
    		super.insertString(fb, offset, text.replaceAll("\t", StaticConfiguration.PYTHON_INDENT_STRING), attrs);
    	}
    	
        @Override
    	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, 
    			AttributeSet attrs) throws BadLocationException {
    		String newText = text;

    		if (text != null) {
    			newText = text.replaceAll("\t", StaticConfiguration.PYTHON_INDENT_STRING);
    		}

    		super.replace(fb, offset, length, newText, attrs);
    	}
    }
}
