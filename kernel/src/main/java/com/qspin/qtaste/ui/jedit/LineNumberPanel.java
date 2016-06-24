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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.ui.jedit;

/**
 * @author vdubois
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import de.sciss.syntaxpane.SyntaxDocument;
import de.sciss.syntaxpane.actions.ActionUtils;
import de.sciss.syntaxpane.components.SyntaxComponent;
import de.sciss.syntaxpane.util.Configuration;

import com.qspin.qtaste.debug.BreakPointScript;
import com.qspin.qtaste.debug.Breakpoint;
import com.qspin.qtaste.event.TestScriptBreakpointEvent;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * LineRuleis used to number the lines in the EdiorPane
 *
 * @author Ayman Al-Sairafi
 */
@SuppressWarnings("serial")
public class LineNumberPanel extends JComponent implements SyntaxComponent, PropertyChangeListener, DocumentListener,
      CaretListener {

    private final static int MARGIN = 10;
    public static final String PROPERTY_BACKGROUND = "LineNumbers.Background";
    public static final String PROPERTY_FOREGROUND = "LineNumbers.Foreground";
    public static final String PROPERTY_LEFT_MARGIN = "LineNumbers.LeftMargin";
    public static final String PROPERTY_RIGHT_MARGIN = "LineNumbers.RightMargin";
    public static final int DEFAULT_R_MARGIN = 5;
    public static final int DEFAULT_L_MARGIN = 5;
    private JEditorPane pane;
    private String format;
    private int lineCount = -1;
    private int r_margin = DEFAULT_R_MARGIN;
    private int l_margin = DEFAULT_L_MARGIN;
    private int charHeight;
    private int charWidth;
    private TestScriptBreakpointEvent lastBreakEvent;
    private Color breakpointReachedColor = new Color(189, 230, 170);
    private Status status;

    public LineNumberPanel() {
        lastBreakEvent = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setFont(pane.getFont());
        Rectangle clip = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        g.setColor(getForeground());
        int lh = charHeight;
        int end = clip.y + clip.height + lh;
        int lineNum = clip.y / lh + 1;
        // round the start to a multiple of lh
        for (int y = (clip.y / lh) * lh + lh; y <= end; y += lh) {
            NonWrappingTextPane textPane = (NonWrappingTextPane) pane;
            HashMap<Integer, Boolean> breakpoints = textPane.getBreakpointScript().getBreakpoints();
            if (breakpoints.containsKey(lineNum) && breakpoints.get(lineNum)) {
                Color penColor = g.getColor();
                g.setColor(BreakPointScript.BreakpointColor);
                g.fillRect(MARGIN + 5, y - charHeight + 5, 12, charHeight / 2 + 4);
                g.setColor(Color.white);
                g.fillRect(MARGIN + 9, y - charHeight + 9, 4, charHeight / 2 - 4);
                g.setColor(penColor);
            } else {
                int currentLine = getLineAtCaret(pane) + 1;
                Font boldFont = g.getFont().deriveFont(Font.BOLD);
                if (currentLine == lineNum) {
                    g.setFont(boldFont);
                }
                String text = String.format(format, lineNum);
                g.drawString(text, l_margin, y);
                g.setFont(pane.getFont());
            }
            lineNum++;
            if (lineNum > lineCount) {
                break;
            }
            drawBreakpointHeader(g, y, lineNum - 1);
        }
    }

    private static int getLineAtCaret(JTextComponent component) {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();

        return root.getElementIndex(caretPosition);
    }

    /**
     * Update the size of the line numbers based on the length of the document
     */
    private void updateSize() {
        int newLineCount = ActionUtils.getLineCount(pane);
        if (newLineCount == lineCount) {
            return;
        }
        lineCount = newLineCount;
        int h = (int) pane.getPreferredSize().getHeight();
        int d = (int) Math.log10(lineCount) + 1;
        if (d < 1) {
            d = 1;
        }
        int w = d * charWidth + r_margin + l_margin;
        format = "%" + d + "d";
        setPreferredSize(new Dimension(w, h));
        getParent().doLayout();
    }

    /**
     * Get the JscrollPane that contains an editor pane, or null if none.
     *
     * @param editorPane an editor pane
     * @return the JscrollPane that contains the editor pane, or null if none
     */
    public JScrollPane getScrollPane(JTextComponent editorPane) {
        Container p = editorPane.getParent();
        while (p != null) {
            if (p instanceof JScrollPane) {
                return (JScrollPane) p;
            }
            p = p.getParent();
        }
        return null;
    }

    @Override
    public void config(Configuration config) {
        r_margin = config.getInteger(PROPERTY_RIGHT_MARGIN, DEFAULT_R_MARGIN);
        l_margin = config.getInteger(PROPERTY_LEFT_MARGIN, DEFAULT_L_MARGIN);
        Color foreground = config.getColor(PROPERTY_FOREGROUND, Color.BLACK);
        setForeground(foreground);
        Color back = config.getColor(PROPERTY_BACKGROUND, Color.WHITE);
        setBackground(back);
    }

    @Override
    public void install(JEditorPane editor) {
        this.pane = editor;
        FontMetrics fontMetrics = pane.getFontMetrics(pane.getFont());
        charHeight = fontMetrics.getHeight();
        charWidth = fontMetrics.charWidth('0');
        editor.addPropertyChangeListener(this);
        JScrollPane sp = getScrollPane(pane);
        if (sp == null) {
            Log4jLoggerFactory.getLogger(getClass()).warn(
                  "JEditorPane is not enclosed in JScrollPane, no LineNumbers will be displayed");
        } else {
            sp.setRowHeaderView(this);
            this.pane.getDocument().addDocumentListener(this);
            this.pane.addCaretListener(this);
            updateSize();
            this.addMouseListener(new LineNumberMouseListener(this));
        }

        status = Status.INSTALLING;
    }

    @Override
    public void deinstall(JEditorPane editor) {
        status = Status.DEINSTALLING;
        JScrollPane sp = getScrollPane(editor);
        if (sp != null) {
            editor.getDocument().removeDocumentListener(this);
            sp.setRowHeaderView(null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals("document")) {
            if (evt.getOldValue() instanceof SyntaxDocument) {
                SyntaxDocument syntaxDocument = (SyntaxDocument) evt.getOldValue();
                syntaxDocument.removeDocumentListener(this);
            }
            if (evt.getNewValue() instanceof SyntaxDocument && status.equals(Status.INSTALLING)) {
                SyntaxDocument syntaxDocument = (SyntaxDocument) evt.getNewValue();
                syntaxDocument.addDocumentListener(this);
                updateSize();
            }
        } else if (evt.getPropertyName().equals("font")) {
            FontMetrics fontMetrics = pane.getFontMetrics(pane.getFont());
            charHeight = fontMetrics.getHeight();
            charWidth = fontMetrics.charWidth('0');
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        reValidateBreakpoint();
        updateSize();
    }

    private void reValidateBreakpoint() {
        // check if breakpoint are still valid
        boolean valid = true;
        NonWrappingTextPane textPane = (NonWrappingTextPane) pane;
        HashMap<Integer, Boolean> breakpoints = textPane.getBreakpointScript().getBreakpoints();
        Iterator<Integer> it = breakpoints.keySet().iterator();
        while (it.hasNext()) {
            int line = it.next();
            Element lineElement = pane.getDocument().getRootElements()[0].getElement(line - 1);
            if (lineElement == null) {
                valid = false;
            } else {
                Document doc = pane.getDocument();
                if (doc instanceof SyntaxDocument) {
                    SyntaxDocument pythonDoc = (SyntaxDocument) doc;

                    String text = pythonDoc.getUncommentedText(lineElement.getStartOffset(), lineElement.getEndOffset());
                    // validate the selected line
                    if (text.replaceAll("[\t\n ]", "").length() == 0) {
                        valid = false;
                    }
                }
            }
        }
        if (!valid) {
            textPane.getBreakpointScript().removeAllBreakpoints();
            lastBreakEvent = null;
            //processChangedBreakpoint();
        }
        repaint();

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        reValidateBreakpoint();
        updateSize();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateSize();
    }

    public int getLeftMargin() {
        return l_margin;
    }

    public void setLeftMargin(int l_margin) {
        this.l_margin = l_margin;
    }

    public int getRightMargin() {
        return r_margin;
    }

    public void setRightMargin(int r_margin) {
        this.r_margin = r_margin;
    }

    public void update(TestScriptBreakpointEvent event) {
        lastBreakEvent = event;
        repaint();
        //update();
    }

    private void drawBreakpointHeader(Graphics g, int y, int line) {
        if (lastBreakEvent != null && lastBreakEvent.getAction() == TestScriptBreakpointEvent.Action.BREAK) {
            // high light the breakpoint line
            Object extraData = lastBreakEvent.getExtraData();
            if (extraData instanceof Breakpoint && pane instanceof NonWrappingTextPane) {
                NonWrappingTextPane textPane = (NonWrappingTextPane) pane;
                Breakpoint breakpoint = (Breakpoint) extraData;
                Document doc = pane.getDocument();
                if (doc instanceof SyntaxDocument) {
                    String fileName = textPane.getFileName();
                    int ScriptLineNumber = breakpoint.getLineIndex();
                    if ((line == ScriptLineNumber) && fileName.equals(breakpoint.getFileName())) {
                        // draw a sign to signals that break has been reached
                        Color penColor = g.getColor();
                        g.setColor(breakpointReachedColor);
                        //g.fillRect(25,y-5,35,fontHeight-10);
                        Polygon p = new Polygon();
                        int width = getWidth();
                        p.addPoint(width - 13, y - 4);
                        p.addPoint(width - 8, y - 4);
                        p.addPoint(width - 8, y - 8);
                        p.addPoint(width - 2, y - 2);
                        p.addPoint(width - 8, y + 4);
                        p.addPoint(width - 8, y);
                        p.addPoint(width - 13, y);
                        g.fillPolygon(p);
                        g.setColor(Color.BLACK);
                        g.drawPolygon(p);
                        g.setColor(penColor);
                    }
                }
            }
        }
    }

    class LineNumberMouseListener extends MouseAdapter {

        private LineNumberPanel panel;

        public LineNumberMouseListener(LineNumberPanel lnp) {
            panel = lnp;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
            // put breakpoint to the selected line (or remove if already done)

            JEditorPane jtp = panel.pane;
            if (jtp instanceof NonWrappingTextPane) {
                NonWrappingTextPane nonWrappingTextPane = (NonWrappingTextPane) jtp;
                int lineIndex = nonWrappingTextPane.yToLine(e.getY());

                Document doc = jtp.getDocument();
                if (doc instanceof SyntaxDocument) {
                    SyntaxDocument pythonDoc = (SyntaxDocument) doc;
                    Element lineElement = pythonDoc.getDefaultRootElement().getElement(lineIndex + 1);
                    String text = pythonDoc.getUncommentedText(lineElement.getStartOffset(), lineElement.getEndOffset());
                    // validate the selected line 
                    if (text.replaceAll("[\t\n ]", "").length() == 0) {
                        return;
                    }

                    nonWrappingTextPane.getBreakpointScript().toggleBreakpoint(lineIndex + 2);
                    repaint();
                }
                //revalidate();
                //panel.update();
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }
}
