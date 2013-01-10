package com.qspin.qtaste.controlscriptbuilder;

import javax.swing.JPanel;

import com.qspin.qtaste.addon.AddOn;
import com.qspin.qtaste.addon.AddOnException;
import com.qspin.qtaste.addon.AddOnMetadata;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.controlscriptbuilder.ui.ControlScriptBuilderPane;
import com.qspin.qtaste.util.Environment;

public class ControlScriptBuilderAddOn extends AddOn {

	public ControlScriptBuilderAddOn(AddOnMetadata pMetaData) {
		super(pMetaData);
	}

	@Override
	public boolean loadAddOn() throws AddOnException {
		mEditorPane = new ControlScriptBuilderPane();
		mChangeHandler = new TestbedConfigurationChangeHandler(mEditorPane);
		TestBedConfiguration.registerConfigurationChangeHandler(mChangeHandler);
		mChangeHandler.onConfigurationChange();
		Environment.getEnvironment().addTestEditor(mEditorPane, getAddOnId());
		return true;
	}

	@Override
	public boolean unloadAddOn() throws AddOnException {
		if ( mChangeHandler != null )
		{
			TestBedConfiguration.unregisterConfigurationChangeHandler(mChangeHandler);
		}
		Environment.getEnvironment().removeTestEditor(mEditorPane);
		return true;
	}

	@Override
	public boolean hasConfiguration() {
		return false;
	}

	@Override
	public JPanel getConfigurationPane() {
		return null;
	}
	
	private ControlScriptBuilderPane mEditorPane;
	private TestbedConfigurationChangeHandler mChangeHandler;

}
