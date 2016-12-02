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

package com.qspin.qtaste.ui.widget;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.qspin.qtaste.ui.tools.ResourceManager;

@SuppressWarnings("serial")
class InfoCbModel extends DefaultComboBoxModel<InfoCbBox.Info> {
    public void addInfo(InfoCbBox.Info pInfo) {
        addElement(pInfo);
        setSelectedItem(pInfo);
    }
}

@SuppressWarnings("serial")
public class InfoCbBox extends JComboBox<InfoCbBox.Info> implements PopupMenuListener {
    private class InfoRenderer extends JLabel implements ListCellRenderer<Info> {
        public InfoRenderer(InfoCbBox pBox) {
            mBox = pBox;
        }

        public void highlight(boolean pEnable) {
            if (pEnable && !mHighlight) {
                mHighlight = pEnable;

                Thread th = new Thread(() -> {
                    int i = 0;
                    while (i < 10 && mHighlight) {
                        if ((i % 2) == 0) {
                            mForeground = ResourceManager.getInstance().getNormalColor();
                        } else {
                            mForeground = ResourceManager.getInstance().getDarkColor();
                        }

                        mBox.repaint();
                        ++i;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            i = 10;
                        }
                    }
                    mHighlight = false;
                    mForeground = Color.BLACK;
                    mBox.repaint();
                });
                th.start();
            }

            mHighlight = pEnable;
        }

        public Color getForeground() {
            return mForeground;
        }

        public Component getListCellRendererComponent(JList pList, Info pInfo, int pIndex, boolean pIsSelected, boolean
              pCellHasFocus) {
            if (pInfo != null) {
                setText(pInfo.getMessage());
                Icon icon = ResourceManager.getInstance().getImageIcon("main/badsmall");  // Default value plus Error Level
                if (pInfo.getLevel() == Info.Level.WARNING) {
                    icon = ResourceManager.getInstance().getImageIcon("main/beam16");  // TODO: ask for a 'Warning' logo
                } else if (pInfo.getLevel() == Info.Level.INFO) {
                    icon = ResourceManager.getInstance().getImageIcon("main/goodsmall");
                }
                setIcon(icon);
            }
            return this;
        }

        InfoCbBox mBox;
        boolean mHighlight = false;
        private Color mForeground = Color.BLACK;
    }

    public interface Info {
        enum Level {
            /**
             * Information level.
             */
            INFO,
            /**
             * Warning level.
             */
            WARNING,
            /**
             * Error level.
             */
            ERROR
        }

        Level getLevel();

        String getMessage();
    }

    public class DefaultInfo implements Info {
        public DefaultInfo(Level pLevel, String pMsg) {
            mLevel = pLevel;
            mMessage = pMsg;
        }

        public Level getLevel() {
            return mLevel;
        }

        public String getMessage() {
            return mMessage;
        }

        private Level mLevel;
        private String mMessage;
    }

    public InfoCbBox() {
        super(new InfoCbModel());
        mRenderer = new InfoRenderer(this);
        setRenderer(mRenderer);
        this.setFocusable(false);

        addPopupMenuListener(this);
    }

    public void addInfo(Info pInfo) {
        ((InfoCbModel) getModel()).addInfo(pInfo);
        mRenderer.highlight(true);
    }

    public void addInfo(Info.Level pLevel, String pMsg) {
        addInfo(new DefaultInfo(pLevel, pMsg));
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent pE) {
        mRenderer.highlight(false);
        repaint();
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent pE) {

    }

    public void popupMenuCanceled(PopupMenuEvent pE) {

    }

    private InfoRenderer mRenderer;
}
