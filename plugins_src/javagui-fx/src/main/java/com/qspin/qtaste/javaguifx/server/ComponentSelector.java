package com.qspin.qtaste.javaguifx.server;

import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller that simulates the (de)selection of a {@link CheckBox}
 * or a {@link RadioButton} or a {@link ToggleButton}.
 * The change is done through the setSelected method. The method call is done in the Event Thread.
 *
 * @author simjan
 */
class ComponentSelector extends UpdateComponentCommander {

    protected boolean mSelectState;

    /**
     * Takes from the data the selection state.
     *
     * @throws QTasteTestFailException if the component is not a {@link CheckBox}
     * or a {@link RadioButton}
     * or a {@link ToggleButton}.
     */
    @Override
    protected void prepareActions() throws QTasteTestFailException {
        mSelectState = Boolean.parseBoolean(mData[0].toString());
        if (!(component instanceof CheckBox) &&
              !(component instanceof RadioButton) &&
              !(component instanceof ToggleButton)) {
            throw new QTasteTestFailException("Unsupported component.");
        }
    }

    @Override
    protected void doActionsInEventThread() {
        if (component instanceof CheckBox) {
            ((CheckBox) component).selectedProperty().set(mSelectState);
            ((CheckBox) component).indeterminateProperty().set(false);
        } else if (component instanceof RadioButton) {
            ((RadioButton) component).selectedProperty().set(mSelectState);
        } else if (component instanceof ToggleButton) {
            ((ToggleButton) component).selectedProperty().set(mSelectState);
        }
    }

}
