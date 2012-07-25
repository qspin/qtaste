package com.qspin.qtaste.tools.ui;

import static com.qspin.qtaste.tools.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.model.EventManager;

public class ConversionConfigurationPane extends JPanel implements PropertyChangeListener {

	public ConversionConfigurationPane()
	{
		super();
		setLayout(new BorderLayout());
		add( genUI(), BorderLayout.CENTER);
	}
	
	public void propertyChange (PropertyChangeEvent pEvt)
	{
		mComponent.resetFor(EventManager.getInstance().getComponentNames());
		mEventType.resetFor(EventManager.getInstance().getEventTypes());
	}
	
	public List<Object> getSelectedComponent()
	{
		return mComponent.getSelectedItems();
	}
	
	public List<Object> getSelectedEventType()
	{
		return mEventType.getSelectedItems();
	}
	
	public String getOutputDirectory()
	{
		return mExportPath.getText();
	}
	
	private JPanel genUI()
	{
		FormLayout layout = new FormLayout( 
				"pref" + COMPONENT_SPACING + "pref",
				"pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		mComponent = new SelectionPane("components");
		builder.add(mComponent, cc.xy(1,1));
		mEventType = new SelectionPane("events");
		builder.add(mEventType, cc.xy(3,1));
		
		builder.add( createExportPathPane(), cc.xyw(1, 3, 3));
		
		return builder.getPanel();
	}
	
	private JPanel createExportPathPane()
	{
		FormLayout layout = new FormLayout("fill:pref:grow" + COMPONENT_SPACING + "pref", "pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		mExportPath = new JTextField();
		builder.add(mExportPath, cc.xy(1,1));
		JButton browse = new JButton("Browse");
		browse.addActionListener(new BrowseAction());
		builder.add(browse, cc.xy(3,1));
		builder.setBorder(BorderFactory.createTitledBorder("Export directory"));
		
		return builder.getPanel();
	}
	
	private class BrowseAction implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvt)
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if ( jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
			{
				mExportPath.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		}
	}

	private SelectionPane mComponent;
	private SelectionPane mEventType;
	private JTextField mExportPath;

}
