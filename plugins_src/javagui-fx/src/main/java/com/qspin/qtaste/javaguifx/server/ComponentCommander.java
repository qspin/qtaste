package com.qspin.qtaste.javaguifx.server;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.stage.StageHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Class to perform an action on a Component.
 *
 * @author simjan
 */
abstract class ComponentCommander {

    /**
     * Executes the a command on a component.
     *
     * @param timeout
     * @param componentName
     * @param data an array of Object
     * @return true if the command is successfully performed.
     * @throws QTasteException
     */
    abstract Object executeCommand(int timeout, String componentName, Object... data) throws QTasteException;

    /**
     * used for logging.
     */
    protected static final Logger LOGGER = Logger.getLogger(ComponentCommander.class);

    @SuppressWarnings("unchecked")
    protected static List<Stage> getStages() throws QTasteTestFailException
    {
        final FutureTask query = new FutureTask(() -> Collections.unmodifiableList(StageHelper.getStages()));
        PlatformImpl.runAndWait(query);
        try {
            return (List<Stage>) query.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new QTasteTestFailException("Cannot get JavaFx stages", e);
        }
    }

    /**
     * Retrieve the GUI component base on its name.
     *
     * @param name the GUI component's name.
     * @return the found GUI component.
     * @throws QTasteTestFailException If no GUI component is found.
     */
    protected Node getComponentByName(String name) throws QTasteException {
        mFoundComponent = null;
        mFindWithEqual = false;
        LOGGER.debug("try to find a component with the name : " + name);
        // TODO: Think about several component having the same names!
        for (Stage s : getStages()) {
            if (mFindWithEqual) {
                break;
            }
            if (!checkName(name, s.getScene().getRoot()) || !mFindWithEqual) {
                LOGGER.debug("parse window");
                lookForComponent(name, s.getScene().getRoot().getChildrenUnmodifiable());
            }
        }
        if (mFoundComponent != null) {
            //			Parent parent = mFoundComponent.getParent();
            //			//active the parent
            //			while ( parent != null && !(parent instanceof Window) )
            //			{
            //				parent = parent.getParent();
            //			}
            //			if ( parent != null )
            //				((Window)parent).toFront();

            return mFoundComponent;
        }
        throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
    }

    /**
     * Browses recursively the components in order to find components with the name.
     *
     * @param name the component's name.
     * @param components components to browse.
     * @return the first component with the name.
     */
    protected Component lookForComponent(String name, ObservableList<Node> components) {
        for (int i = 0; i < components.size() && !mFindWithEqual; i++) {
            //String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
            Node c = components.get(i);
            checkName(name, c);
            if (!mFindWithEqual) {
                if (c instanceof Parent) {
                    Component result = lookForComponent(name, ((Parent) c).getChildrenUnmodifiable());
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    protected boolean checkName(String name, Node c) {
        if (c != null && c.getId() != null) {
            if (c.getId().contains(name)) {
                mFoundComponent = c;
                if (c.getId().equals(name)) {
                    mFindWithEqual = true;
                    LOGGER.debug("Component:" + name + " is found!");
                } else {
                    LOGGER.debug("Component:" + name + " is (maybe) found! (component's name : " + mFoundComponent.getId() + ")");
                }
                return true;
            }
        }
        return false;
    }

    protected boolean mFindWithEqual;
    protected Node mFoundComponent;

    /**
     * Finds all popups. A Component is a popup if it's a DialogPane, modal and not resizable.
     *
     * @return the list of all found popups.
     */
    protected static List<Stage> findPopups() throws QTasteTestFailException {
        //find all popups
        List<Stage> popupFound = new ArrayList<>();
        for (Stage stage : getStages()) {
            Parent root = stage.getScene().getRoot();
            if (isAPopup(stage)) {
                //it's maybe a popup... a popup is modal and not resizable and has a DialogPane root
                DialogPane dialog = (DialogPane) root;
                LOGGER.trace("Find a popup with the title '" + stage.getTitle() + "'.");
                popupFound.add(stage);
            }
        }
        return popupFound;
    }

    protected static boolean isAPopup(Stage stage) {
        if (stage == null) {
            LOGGER.trace("The given stage is null!");
            return false;
        }
        Parent root = stage.getScene().getRoot();
        if (root == null) {
            LOGGER.trace("The given stage's root is null!");
            return false;
        }
        if (!(root instanceof DialogPane)) {
            LOGGER.trace("The given stage's root is not a DialogPane!");
            return false;
        }
        if (!stage.isShowing()) {
            LOGGER.trace("The given component is not displayed!");
            return false;
        }
        if (stage.getModality() == Modality.NONE) {
            LOGGER.trace("The given component is not modal!");
            return false;
        }
        if (stage.isResizable()) {
            LOGGER.trace("The given component is rezisable!");
            return false;
        }
        return true;
    }

    protected static DialogPane getDialogPane(Stage stage) {
        Parent root = stage.getScene().getRoot();
        if (root instanceof DialogPane) {
            return (DialogPane) root;
        }
        return null;
    }

    /**
     * Try to activate and focus the window containing the component.
     *
     * @param c the component contained in the window to activate.
     * @return <code>true</code> only if the parent window is active at the end of the activation process.
     */
    protected boolean activateAndFocusComponentWindow(Node c) {
        Window window = c.getScene().getWindow();
        return window instanceof Stage && activateAndFocusWindow((Stage) window);
    }

    /**
     * Try to activate and focus the stage window.
     *
     * @param window the stage window to activate.
     * @return <code>true</code> only if the stage window is active at the end of the activation process.
     */
    protected boolean activateAndFocusWindow(Stage window) {
        if (!window.isFocused()) {
            if (!window.isShowing()) {
                LOGGER.trace("cannot activate and focus the window '" + window.getTitle() + "' cause its window is not showing");
                return false;
            }
            LOGGER.trace("try to activate and focus the window  '" + window.getTitle() + "' cause its window is not focused");
            StageFocusedListener stageFocusedListener = new StageFocusedListener(window);
            window.focusedProperty().addListener(stageFocusedListener);

            PlatformImpl.runAndWait(() -> {
                window.toFront();
                window.requestFocus();
            });

            boolean windowFocused = stageFocusedListener.waitUntilWindowFocused();
            window.focusedProperty().removeListener(stageFocusedListener);
            LOGGER.trace("window focused ? " + windowFocused);
            if (!window.isFocused()) {
                LOGGER.warn("The window activation/focus process failed!!!");
                return false;
            }
            LOGGER.trace("The window activation/focus process is completed!!!");
        } else {
            LOGGER.trace("the window '" + window.getTitle() + "' is already focused");
        }
        return true;
    }

    private static class StageFocusedListener implements ChangeListener<Boolean> {

        public StageFocusedListener(Stage window) {
            mWindowFocused = window.isFocused();
        }

        @Override
        public synchronized void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                mWindowFocused = true;
                notify();
            }
        }

        /**
         * Waits until window is focused or timeout occurs.
         *
         * @return true if window is focused, false otherwise
         */
        public synchronized boolean waitUntilWindowFocused() {
            if (mWindowFocused) {
                return true;
            }
            try {
                wait(WINDOW_FOCUSED_TIMEOUT_MS);
            } catch (InterruptedException e) {
                // ignore
            }
            return mWindowFocused;
        }

        private static final long WINDOW_FOCUSED_TIMEOUT_MS = 5000; // 5 s

        private volatile boolean mWindowFocused = false;
    }
}
