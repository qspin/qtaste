package com.qspin.qtaste.addon;

import java.awt.CardLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

final class AddOnManagerConfigurationPane extends JPanel implements ListSelectionListener {

    public AddOnManagerConfigurationPane(String pPanelName, List<AddOnMetadata> pAddons) {
        super();
        mPanelName = pPanelName;
        genUI(pAddons);
    }

    private void genUI(List<AddOnMetadata> pAddons) {
        FormLayout layout = new FormLayout("20px, pref:grow, 20px", "20px, fill:pref:grow, 5px, fill:150px, 5px");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        mTable = new JTable(new AddOnTableModel(pAddons));
        mTable.getSelectionModel().addListSelectionListener(this);
        mTable.getColumnModel().getColumn(0).setCellRenderer(mTable.getDefaultRenderer(Boolean.class));
        mTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        mTable.getColumnModel().getColumn(0).setMaxWidth(50);
        mTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane js = new JScrollPane();
        js.getViewport().add(mTable);
        builder.add(js, cc.xy(2, 2));

        mAddOnDescription = new JTextArea();
        mAddOnDescription.setEditable(false);
        builder.add(new JScrollPane(mAddOnDescription), cc.xy(2, 4));

        setLayout(new CardLayout());
        add(builder.getPanel(), mPanelName);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && mTable.getSelectedRow() >= 0) {
            mAddOnDescription.setText(
                  ((AddOnTableModel) mTable.getModel()).getAddOnMetaData(mTable.getSelectedRow()).getDescription());
        } else {
            mAddOnDescription.setText(null);
        }
    }

    private JTable mTable;
    private JTextArea mAddOnDescription;
    private String mPanelName;
}
