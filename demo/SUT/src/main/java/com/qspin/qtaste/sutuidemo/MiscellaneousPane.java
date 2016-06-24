package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

class MiscellaneousPane extends JPanel {

    public MiscellaneousPane() {
        super();
        genUI();
    }

    private void genUI() {

        FormLayout layout = new FormLayout("3dlu:grow, 150dlu, 3dlu:grow", "3dlu, pref, 3dlu, pref, 3dlu:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.add(mButton, cc.xy(2, 2));
        mButton.setName("VISIBILITY_BUTTON");
        mButton.addActionListener(new MyAction());
        builder.add(mSecondContainer, cc.xy(2, 4));
        mSecondContainer.setBorder(BorderFactory.createTitledBorder("Visibility pane"));
        mSecondContainer.add(mText);
        mText.setName("VISIBILITY_TEXT");

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private JButton mButton = new JButton("Click on me");
    private JPanel mSecondContainer = new JPanel();
    private JTextField mText = new JTextField(30);

    public static final String COMPONENT_NAME = "MISCELLANEOUS";

    private class MyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            mSecondContainer.setVisible(!mSecondContainer.isVisible());
            MiscellaneousPane.this.invalidate();
            MiscellaneousPane.this.validate();
        }
    }
}
