package com.qspin.qtaste.demo.addon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.addon.AddOn;
import com.qspin.qtaste.addon.AddOnException;
import com.qspin.qtaste.addon.AddOnMetadata;
import com.qspin.qtaste.util.Environment;

public class DemoAddOn extends AddOn {

	public DemoAddOn(AddOnMetadata pMetaData) {
		super(pMetaData);
	}

	@Override
	public boolean loadAddOn() throws AddOnException {
		mMenu = new JMenu("Custom Add-On");
		JMenuItem action = new JMenuItem("Add-on action");
		action.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, mText.getText() );
			}
		});
		mMenu.add(action);
		Environment.getEnvironment().getMainMenuBar().add(mMenu);
		return true;
	}

	@Override
	public boolean unloadAddOn() throws AddOnException {
		Environment.getEnvironment().getMainMenuBar().remove(mMenu);
		return true;
	}

	public boolean hasConfiguration()
	{
		return true;
	}

	@Override
	public JPanel getConfigurationPane() {
		if ( mConfigurationPane == null )
		{
			FormLayout layout = new FormLayout("20px, right:pref, 5px, pref:grow, 20px", "20px, pref, 5px:grow");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
	
			builder.addLabel("Displayed message :", cc.xy(2,2));
			mText = new JTextField("Action added through the add-on");
			builder.add(mText, cc.xy(4,2));
			
			mConfigurationPane = builder.getPanel();
		}
		return mConfigurationPane;
	}
	
	private JTextField mText;
	private JMenu mMenu;
	private JPanel mConfigurationPane;
}
