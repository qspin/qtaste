package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

    /**
     * Retrieve the GUI component base on its name.
     *
     * @param name the GUI component's name.
     * @return the found GUI component.
     * @throws QTasteTestFailException If no GUI component is found.
     */
    protected Component getComponentByName(String name) throws QTasteException {
        mFoundComponent = null;
        mFindWithEqual = false;
        LOGGER.debug("try to find a component with the name : " + name);
        // TODO: Think about several component having the same names!
        for (Window window : getDisplayableWindows()) {
            if (mFindWithEqual) {
                break;
            }
            if (!checkName(name, window) || !mFindWithEqual) {
                LOGGER.debug("parse window");
                lookForComponent(name, window.getComponents());
            }
        }
        if (mFoundComponent != null) {
            requestFocus();
            return mFoundComponent;
        }
        throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
    }

    private void requestFocus()
    {
        if (SwingUtilities.isEventDispatchThread()) {
            mFoundComponent.requestFocus();
            Component parent = mFoundComponent.getParent();
            //active the parent
            while (parent != null && !(parent instanceof Window))
            {
                parent = parent.getParent();
            }
            if (parent != null)
            {
                ((Window) parent).toFront();
            }
        } else {
            // execute method in Swing thread
            try
            {
                SwingUtilities.invokeAndWait(this::requestFocus);
            }
            catch (InterruptedException | InvocationTargetException e)
            {
                // ignore
            }
        }
    }

    /**
     * Browses recursively the components in order to find components with the name.
     *
     * @param name the component's name.
     * @param components components to browse.
     * @return the first component with the name.
     */
    protected Component lookForComponent(String name, Component[] components) {
        for (int i = 0; i < components.length && !mFindWithEqual; i++) {
            //String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
            Component c = components[i];
            checkName(name, c);
            if (!mFindWithEqual) {
                if (c instanceof Container) {
                    Component result = lookForComponent(name, ((Container) c).getComponents());
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    protected boolean checkName(String name, Component c) {
        if (c != null && c.getName() != null) {
            if (c.getName().contains(name)) {
                mFoundComponent = c;
                if (c.getName().equals(name)) {
                    mFindWithEqual = true;
                    LOGGER.debug("Component:" + name + " is found!");
                } else {
                    LOGGER.debug(
                          "Component:" + name + " is (maybe) found! (component's name : " + mFoundComponent.getName() + ")");
                }
                return true;
            }
        }
        return false;
    }

    protected boolean mFindWithEqual;
    protected Component mFoundComponent;

    /**
     * Checks if a component is accessible, i.e. is not inaccessible due to some modal dialog.
     * @param c a component
     * @return true if component is accessible, i.e. is not inaccessible due to some modal dialog, false otherwise
     */
    protected boolean isAccessible(Component c) {
        Window windowAncestor = SwingUtilities.getWindowAncestor(c);
        if (windowAncestor == null) {
            return false;
        }
        if (windowAncestor.getModalExclusionType() != ModalExclusionType.NO_EXCLUDE) {
            return true;
        }
        for (Window w : getDisplayableWindows()) {
            if (w != c && w.isActive() && w instanceof Dialog && ((Dialog) w).isModal() && !w.isAncestorOf(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the displayable windows.
     * @return an array containing the displayable windows
     */
    protected static Window[] getDisplayableWindows()
    {
        return Stream.of(Window.getWindows()).filter(Window::isDisplayable).toArray(Window[]::new);
    }

    /**
     * Finds all popups. A Component is a popup if it's a JDialog, modal and not resizable.
     *
     * @return the list of all found popups.
     */
    protected static List<JDialog> findPopups() {
        //find all popups
        List<JDialog> popupFound = new ArrayList<>();
        for (Window window : getDisplayableWindows()) {
            //			LOGGER.debug("parse window - type : " + window.getClass());
            if (isAPopup(window)) {
                //it's maybe a popup... a popup is modal and not resizable and containt a JOptionPane component.
                JDialog dialog = (JDialog) window;
                LOGGER.trace("Find a popup with the title '" + dialog.getTitle() + "'.");
                popupFound.add(dialog);
            }
        }
        return popupFound;
    }

    protected static boolean isAPopup(Component c) {
        if (c == null) {
            LOGGER.trace("The given component is null!");
            return false;
        }
        if (!(c instanceof JDialog)) {
            LOGGER.trace("The given component is not a JDialog!");
            return false;
        }
        JDialog dialog = (JDialog) c;

        if (!dialog.isShowing()) {
            LOGGER.trace("The given component is not displayed!");
            return false;
        }
        if (!dialog.isModal()) {
            LOGGER.trace("The given component is not modal!");
            return false;
        }
        if (dialog.isResizable()) {
            LOGGER.trace("The given component is rezisable!");
            return false;
        }
        if (getJOptionPane(dialog) == null) {
            LOGGER.trace("The given component does not contain any JOptionPane!");
            return false;
        }
        return true;
    }

    protected static JOptionPane getJOptionPane(Component c) {
        if (c instanceof JOptionPane) {
            return (JOptionPane) c;
        } else if (c instanceof Container) {
            for (Component comp : ((Container) c).getComponents()) {
                JOptionPane jop = getJOptionPane(comp);
                if (jop != null) {
                    return jop;
                }
            }
        }
        return null;
    }

    /**
     * Try to activate and focus the window containing the component.
     *
     * @param c the component contained in the window to active.
     * @return <code>true</code> only if the parent window is active at the end of the activation process.
     */
    protected boolean activateAndFocusComponentWindow(Component c) {
        Component parent = c;
        //active the parent window
        while (!(parent instanceof Window)) {
            parent = parent.getParent();
        }
        final Window window = (Window) parent;

        if (!window.isFocused()) {
            if (!window.isVisible()) {
                LOGGER.trace("cannot activate and focus the window of '" + c.getName() + "' cause its window is not visible");
                return false;
            }
            LOGGER.trace("try to activate and focus the window of '" + c.getName() + "' cause its window is not focused");
            final KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            WindowFocusedListener windowFocusedListener = new WindowFocusedListener(window);
            window.addWindowFocusListener(windowFocusedListener);
            SwingUtilities.invokeLater(() -> {
                // try to activate application if not active
                if (keyboardFocusManager.getActiveWindow() == null) {
                    LOGGER.trace("try to activate application");
                    // create and display a new frame to force application activation
                    JFrame newFrame = new JFrame();
                    newFrame.pack();
                    newFrame.setVisible(true);
                    newFrame.toFront();
                    newFrame.setVisible(false);
                    newFrame.dispose();
                }

                window.toFront();

                LOGGER.trace("current focused window : " + keyboardFocusManager.getFocusedWindow());
                window.requestFocus();
            });

            boolean windowFocused = windowFocusedListener.waitUntilWindowFocused();
            window.removeWindowFocusListener(windowFocusedListener);
            LOGGER.trace("window focused ? " + windowFocused);
            LOGGER.trace("focused window after request: " + keyboardFocusManager.getFocusedWindow());
            if (!windowFocused) {
                LOGGER.warn("The window activation/focus process failed!!!");
                return false;
            }
            LOGGER.trace("The window activation/focus process is completed!!!");
        } else {
            LOGGER.trace("the window of '" + c.getName() + "' is already focused");
        }
        return true;
    }

    /**
     * Window listener to wait for window to be focused.
     */
    private static class WindowFocusedListener extends WindowAdapter {

        public WindowFocusedListener(Window window) {
            mWindowFocused = window.isFocused();
        }

        @Override
        public synchronized void windowGainedFocus(WindowEvent event) {
            mWindowFocused = true;
            notify();
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
