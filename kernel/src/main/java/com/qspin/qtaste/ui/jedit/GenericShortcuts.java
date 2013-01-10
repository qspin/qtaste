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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import com.qspin.qtaste.ui.TestCasePane;

/**
 *
 * @author vdubois
 */
public class GenericShortcuts {

    private TestCasePane m_TCPane;
    public GenericShortcuts(TestCasePane tcPane){
        m_TCPane = tcPane;
        init();
    }
    
    public void init() {
        
        Keymap parent = m_TCPane.getActiveTextPane().getKeymap();
        Keymap newmap = JTextComponent.addKeymap("KeymapExampleMap", parent);

        KeyStroke ctrlSkey = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
        newmap.addActionForKeyStroke(ctrlSkey, new SaveAction());
        //KeyStroke ctrlZkey = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK);
        //newmap.addActionForKeyStroke(ctrlZkey, new UndoAction());

        m_TCPane.getActiveTextPane().setKeymap(newmap);
         
    }

    protected class SaveAction implements Action {
            public Object getValue(String key) {
                return null;
            }
            public void putValue(String key, Object value) {
            }
            public void setEnabled(boolean b) {
            }
            public boolean isEnabled() {
                return true;
            }
            public void addPropertyChangeListener(PropertyChangeListener listener) {
            }
            public void removePropertyChangeListener(PropertyChangeListener listener) {
            }
            public void actionPerformed(ActionEvent e) {
               m_TCPane.saveActiveDocument(); 
                    
            }

        }
}
