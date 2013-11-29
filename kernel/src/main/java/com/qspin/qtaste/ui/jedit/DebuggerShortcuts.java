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
public class DebuggerShortcuts {

    private TestCasePane m_TCPane;
    public DebuggerShortcuts(TestCasePane tcPane){
        m_TCPane = tcPane;
        init();
    }
    
    public void init() {
        
        Keymap parent = m_TCPane.getActiveTextPane().getKeymap();
        Keymap newmap = JTextComponent.addKeymap("KeymapExampleMap", parent);

        KeyStroke shiftF5key = KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_MASK);
        newmap.addActionForKeyStroke(shiftF5key, new ShiftF5Action());

        KeyStroke F5key = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
        newmap.addActionForKeyStroke(F5key, new F5Action());
        
        KeyStroke F6Key = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
        newmap.addActionForKeyStroke(F6Key, new F6Action());

        KeyStroke F8Key = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);
        newmap.addActionForKeyStroke(F8Key, new ShiftF5Action());

        KeyStroke CTRLF2Key = KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
        newmap.addActionForKeyStroke(CTRLF2Key, new CTRLF2Action());
        m_TCPane.getActiveTextPane().setKeymap(newmap);
         
    }

    protected class F5Action implements Action {
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
            	if (m_TCPane.isExecuting)
                 m_TCPane.continueStepInto();
                    
            }

        }
    
    protected class CTRLF2Action implements Action {
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
        	if (m_TCPane.isExecuting)
        		m_TCPane.stopExecution();
        }

    }
    protected class F6Action implements Action {
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
            	if (m_TCPane.isExecuting)
            		m_TCPane.continueStep();
            }

        }
    protected class F8Action implements Action {
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
            	if (m_TCPane.isExecuting)
            		m_TCPane.continueDebug();
            }

        }
        
    protected class ShiftF5Action implements Action {
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
            	if (m_TCPane.isExecuting)
            		m_TCPane.stopDebug();
            }

        }
}
