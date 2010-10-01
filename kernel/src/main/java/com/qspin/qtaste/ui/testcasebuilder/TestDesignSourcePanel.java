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

package com.qspin.qtaste.ui.testcasebuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestDesignSourcePanel extends JPanel implements DropTargetListener
 {
    //static private Logger logger = Log4jLoggerFactory.getLogger(TestDesignSourcePanel.class);
    //private static final Font NORMAL_FONT = new Font("Dialog", Font.PLAIN, 12);
    protected JTextArea mTestDesignSourcePane = new JTextArea();
    
    public TestDesignSourcePanel() {
        super (new BorderLayout());
    }
    
    public JTextArea getSourceTextArea()
    {
        return mTestDesignSourcePane;
    }
   public void init()
   {
    
        JPanel metaDataPanel = new JPanel();        
        // now create the editor
        mTestDesignSourcePane.getCaret().setSelectionVisible(true);

        this.add(metaDataPanel, BorderLayout.NORTH);
        this.add(mTestDesignSourcePane);
        setName("Test script");
   }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public synchronized void drop(DropTargetDropEvent dtde) {
        try
        {
            Transferable tr = dtde.getTransferable();
            if (tr.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String data = (String)tr.getTransferData(DataFlavor.stringFlavor);
                // add the required data if any
                String pythonParameters = getRequiredData(data);
                String pythonInvoke = "testAPI." + data + "("; 
                pythonInvoke+=pythonParameters;
                pythonInvoke+=")";
                mTestDesignSourcePane.append(pythonInvoke+ "\n");
                dtde.getDropTargetContext().dropComplete(true);
            }
            else {
                System.err.println("Drop rejected");
                dtde.rejectDrop();
            }
        }
        catch (Exception e)
        {
                System.err.println(e.getMessage());
        }
    }

    private String getRequiredData(String data)
    {
        //TODO: to be implemented
        System.err.println("TestDesignSourcePanel.getRequiredData() to be implemented");
        return new String();
        
        
    }
    public void dragGestureRecognized(DragGestureEvent dge) {
    }
    
}

