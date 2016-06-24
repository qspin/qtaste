package com.qspin.qtaste.javaguifx.server;

import com.qspin.qtaste.testsuite.QTasteException;

public class FocusRequester extends UpdateComponentCommander {

    @Override
    protected void prepareActions() throws QTasteException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doActionsInEventThread() throws QTasteException {
        component.requestFocus();
    }

}
