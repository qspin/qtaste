/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javaguifx.server;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.stage.StageHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

abstract class UpdateComponentCommander extends ComponentCommander implements Runnable {

    @Override
    public Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        this.timeout = timeout;
        this.componentName = componentName;
        setData(data);
        m_maxTime = System.currentTimeMillis() + 1000 * timeout;

        while (System.currentTimeMillis() < m_maxTime) {
            component = getComponentByName(componentName);
            if (component != null && (!component.isDisabled() && checkComponentIsVisible(component))) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.warn("Exception during the component search sleep...");
            }
        }

        if (component == null) {
            throw new QTasteTestFailException("The component \"" + componentName + "\" is not found.");
        }
        if (component.isDisabled()) {
            throw new QTasteTestFailException("The component \"" + componentName + "\" is not enabled.");
        }
        if (!checkComponentIsVisible(component)) {
            throw new QTasteTestFailException("The component \"" + componentName + "\" is not visible!");
        }

        if (!isAccessible(component)) {
            throw new QTasteTestFailException(
                  "The component \"" + componentName + "\" is not reachable as a modal dialog is opened.");
        }

        prepareActions();
        if (!activateAndFocusComponentWindow(component)) {
            LOGGER.error("Unable to activate/focus the parent window!");
            //throw new QTasteException("Unable to activate/focus the parent window!");
        }

        PlatformImpl.runAndWait(this);

        return true;
    }

    protected boolean isAccessible(Node c) {
        //		Window[] windows = Window.getWindows();
        //		if (windows != null ) {
        //			for ( Window w : windows ) {
        //				if ( w.isShowing() && w instanceof Dialog && ((Dialog)w).isModal() && !w.isAncestorOf(c)) {
        //					if ( w.isShowing() &&  w instanceof Dialog && ((Dialog)w).isModal() && !w.isAncestorOf(c)) {
        //						return false;
        //					}
        //				}
        //			}
        //		}
        return true;
    }

    public void run() {
        try {
            doActionsInEventThread();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.fatal(e.getMessage(), e);
        }
    }

    protected Node getComponentByName(String name) throws QTasteTestFailException {
        mFoundComponents = new ArrayList<Node>();
        mFoundComponent = null;
        mFindWithEqual = false;
        LOGGER.debug("try to find a component with the name : " + name);
        // TODO: Think about several component having the same names!
        //search for all components which contains the name
        for (Stage s : StageHelper.getStages()) {
            //			LOGGER.debug("parse window");
            if (checkName(name, s.getScene().getRoot())) {
                mFoundComponents.add(s.getScene().getRoot());
            }
            lookForComponent(name, s.getScene().getRoot().getChildrenUnmodifiable());
        }
        LOGGER.trace(mFoundComponents.size() + " component(s) found with the contains");

        //if equals the remove others
        if (mFindWithEqual) {
            for (int i = 0; i < mFoundComponents.size(); ) {
                if (!mFoundComponents.get(i).getId().equals(name)) {
                    mFoundComponents.remove(i);
                } else {
                    i++;
                }
            }
            LOGGER.trace(mFoundComponents.size() + " component(s) found with the equals");
        }

        //Remove invisible components
        for (int i = 0; i < mFoundComponents.size(); ) {
            if (!checkComponentIsVisible(mFoundComponents.get(i))) {
                mFoundComponents.remove(i);
            } else {
                i++;
            }
        }
        LOGGER.trace(mFoundComponents.size() + " visible component(s) found");

        if (!mFoundComponents.isEmpty()) {
            mFoundComponent = mFoundComponents.get(0);
            //			mFoundComponent.requestFocus();
            //			Node parent = mFoundComponent;
            //active the parent window
            //			while ( parent != null )
            //			{
            //				parent = parent.getParent();
            //			}
            //			if ( parent != null )
            //				((Window)parent).toFront();
            return mFoundComponent;
        }
        return null;
    }

    protected Component lookForComponent(String name, ObservableList<Node> components) {
        for (int i = 0; i < components.size(); i++) {
            //String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
            Node c = components.get(i);
            if (checkName(name, c)) {
                LOGGER.debug("Component " + c.getId() + " added to the list of found components");
                mFoundComponents.add(c);
            }
            if (c instanceof Parent) {
                //				LOGGER.trace("Will parse the container " + c.getName() );
                lookForComponent(name, ((Parent) c).getChildrenUnmodifiable());
            }
        }
        return null;
    }

    protected boolean checkComponentIsVisible(Node c) {
        Node currentComponent = c;
        if (c == null) {
            LOGGER.debug("checkComponentIsVisible on a null component");
            return false;
        }
        while (currentComponent != null) {
            boolean lastRun = currentComponent.getParent() == null; //Dialog can have another window as parent.

            if (!currentComponent.isVisible()) {
                if (c == currentComponent) {
                    LOGGER.debug("The component " + c.getId() + " is not visible.");
                } else {
                    LOGGER.debug(
                          "The parent (" + currentComponent.getId() + ") of the component " + c.getId() + " is not visible.");
                }
                return false;
            }
            if (lastRun) {
                break;
            } else {
                currentComponent = currentComponent.getParent();
            }
        }
        return true;
    }

    protected abstract void prepareActions() throws QTasteException;

    protected abstract void doActionsInEventThread() throws QTasteException;

    protected void setData(Object[] data) {
        this.mData = data;
    }

    private List<Node> mFoundComponents;
    protected Object[] mData;
    protected Node component;
    protected String componentName;
    protected int timeout;
    protected long m_maxTime;
}
