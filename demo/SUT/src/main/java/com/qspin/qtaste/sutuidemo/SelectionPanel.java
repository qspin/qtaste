package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

final class SelectionPanel extends JPanel {

    public SelectionPanel() {
        super();
        setName(COMPONENT_NAME);

        genUI();
    }

    private void genUI() {
        prepareComponent();

        FormLayout layout = new FormLayout("3dlu:grow, right:pref, 3dlu, pref, 3dlu:grow",
              "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.addLabel("JList :", cc.xy(2, 2));
        builder.add(mList, cc.xy(4, 2));
        builder.addLabel("JSpinner :", cc.xy(2, 4));
        builder.add(mSpinner, cc.xy(4, 4));
        builder.addLabel("JSlider :", cc.xy(2, 6));
        builder.add(mSlider, cc.xy(4, 6));
        builder.addLabel("JComboBox :", cc.xy(2, 8));
        builder.add(mCombo, cc.xy(4, 8));

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private void prepareComponent() {
        mSpinner.setName("SPINNER");
        mSlider.setName("SLIDER");
        mCombo.setName("COMBO_BOX");
        mCombo.setRenderer(new CustomListCellRenderer());
        mList.setName("LIST");
        mList.setCellRenderer(new CustomListCellRenderer());
    }

    private JSpinner mSpinner = new JSpinner();
    private JSlider mSlider = new JSlider();
    private JComboBox mCombo = new JComboBox(ModelBuilder.getComboBoxModel());
    private JList mList = new JList(ModelBuilder.getListModel());

    private static final int NUMBER_OF_COMPONENT = 4;
    public static final String COMPONENT_NAME = "SELECTION_PANEL";

}
