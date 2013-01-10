package com.qspin.qtaste.tools.converter.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.converter.ui.MainUI;

public abstract class ImportAction implements ActionListener {

	public ImportAction(MainUI pCaller) {
		mCaller = pCaller;
	}

	public void actionPerformed(ActionEvent pEvent) {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(mCaller) == JFileChooser.APPROVE_OPTION) {
			useSelectedFile(jfc.getSelectedFile());
		}
		else
		{
			LOGGER.debug("Import canceled");
		}
	}
	
	protected abstract void useSelectedFile(File pSelectedFile);

	protected MainUI mCaller;
	protected static final Logger LOGGER = Logger.getLogger(ImportAction.class);
}