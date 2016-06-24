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

package com.qspin.qtaste.ui.tools;

/**
 * @author vdubois
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class CheckBoxJList extends JList implements ListSelectionListener {

    static Color listForeground, listBackground, listSelectionForeground, listSelectionBackground;

    static {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        listForeground = uid.getColor("List.foreground");
        listBackground = uid.getColor("List.background");
        listSelectionForeground = uid.getColor("List.selectionForeground");
        listSelectionBackground = uid.getColor("List.selectionBackground");
    }

    HashSet<Integer> selectionCache = new HashSet<Integer>();
    int toggleIndex = -1;
    boolean toggleWasSelected;

    public boolean isIndexSelected(int index) {
        Iterator<Integer> it = selectionCache.iterator();
        while (it.hasNext()) {
            Integer nextInt = it.next();
            if (nextInt.equals(index)) {
                return true;
            }
        }
        return false;
    }

    public CheckBoxJList() {
        super();
        setCellRenderer(new CheckBoxListCellRenderer());
        addListSelectionListener(this);
    }

    // ListSelectionListener implementation
    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            removeListSelectionListener(this);

            // remember everything selected as a result of this action
            HashSet<Integer> newSelections = new HashSet<Integer>();
            int size = getModel().getSize();
            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    newSelections.add(new Integer(i));
                }
            }

            // turn on everything that was previously selected
            Iterator<Integer> it = selectionCache.iterator();
            while (it.hasNext()) {
                int index = it.next().intValue();
                getSelectionModel().addSelectionInterval(index, index);
            }

            // add or remove the delta
            it = newSelections.iterator();
            while (it.hasNext()) {
                Integer nextInt = it.next();
                int index = nextInt.intValue();
                if (selectionCache.contains(nextInt)) {
                    getSelectionModel().removeSelectionInterval(index, index);
                } else {
                    getSelectionModel().addSelectionInterval(index, index);
                }
            }

            // save selections for next time
            selectionCache.clear();
            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    selectionCache.add(new Integer(i));
                }
            }

            addListSelectionListener(this);

        }
    }

    class CheckBoxListCellRenderer extends JComponent implements ListCellRenderer {
        DefaultListCellRenderer defaultComp;
        JCheckBox checkbox;

        public CheckBoxListCellRenderer() {
            setLayout(new BorderLayout());
            defaultComp = new DefaultListCellRenderer();
            checkbox = new JCheckBox();
            add(checkbox, BorderLayout.WEST);
            add(defaultComp, BorderLayout.CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean
              cellHasFocus) {
            defaultComp.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            /*
            checkbox.setSelected (isSelected);
            checkbox.setForeground (isSelected ?
                                    listSelectionForeground :
                                    listForeground);
            checkbox.setBackground (isSelected ?
                                    listSelectionBackground :
                                    listBackground);
            */
            checkbox.setSelected(isSelected);
            Component[] comps = getComponents();
            for (int i = 0; i < comps.length; i++) {
                comps[i].setForeground(listForeground);
                comps[i].setBackground(listBackground);
            }
            return this;
        }
    }
} 
