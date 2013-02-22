package com.qspin.qtaste.controlscriptbuilder;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration.ConfigurationChangeHandler;
import com.qspin.qtaste.controlscriptbuilder.ui.ControlScriptBuilderPane;

/**
 * Reload the {@link ControlScriptBuilderPane} when the {@link TestBedConfiguration} is changed.
 * @author simjan
 *
 */
final class TestbedConfigurationChangeHandler implements ConfigurationChangeHandler {

	/**
	 * Constructor.
	 * @param pEditor the {@link ControlScriptBuilderPane} instance to update. 
	 */
	public TestbedConfigurationChangeHandler(ControlScriptBuilderPane pEditor)
	{
		mEditor = pEditor;
	}
	
	@Override
	public void onConfigurationChange() {
		mEditor.reload();
	}

	private ControlScriptBuilderPane mEditor;
}
