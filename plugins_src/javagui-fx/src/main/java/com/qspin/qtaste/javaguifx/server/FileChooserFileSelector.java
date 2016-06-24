package com.qspin.qtaste.javaguifx.server;

import com.qspin.qtaste.testsuite.QTasteException;

public class FileChooserFileSelector extends UpdateComponentCommander {

    @Override
    protected void prepareActions() throws QTasteException {
    }

    @Override
    protected void doActionsInEventThread() throws QTasteException {
        //		JFileChooser chooser = (JFileChooser)this.mFoundComponent;
        //		chooser.setSelectedFile(new File(this.mData[0].toString()));
        //		chooser.approveSelection();
    }
}
