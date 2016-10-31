package com.qspin.qtaste.addon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.util.Environment;

@SuppressWarnings("serial")
public class AddOnTableModel extends AbstractTableModel {

    public AddOnTableModel(List<AddOnMetadata> pAddons) {
        mAddons = pAddons;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return mAddons.size();
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Objects.equals(mAddons.get(rowIndex).getStatus(), AddOnMetadata.LOAD);
            case 1:
                return mAddons.get(rowIndex).getName();
            case 2:
                return mAddons.get(rowIndex).getVersion();
            case 3:
                return mAddons.get(rowIndex).getJarName();
            case 4:
                return mAddons.get(rowIndex).getStatus();
            default:
                return null;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            if (Boolean.parseBoolean(aValue.toString())) {
                getAddonManager().loadAddOn(mAddons.get(rowIndex));
                TestEngineConfiguration.getInstance().addProperty("addons.addon", mAddons.get(rowIndex).getMainClass());
            } else {
                getAddonManager().unloadAddOn(mAddons.get(rowIndex));
                List<String> classes = new ArrayList<>();
                TestEngineConfiguration config = TestEngineConfiguration.getInstance();
                int reportersCount = config.getMaxIndex("addons.addon") + 1;
                for (int reporterIndex = 0; reporterIndex < reportersCount; reporterIndex++) {
                    String addon = config.getString("addons.addon(" + reporterIndex + ")");
                    if (!addon.equals(mAddons.get(rowIndex).getMainClass())) {
                        classes.add(addon);
                    }
                }
                TestEngineConfiguration.getInstance().clearProperty("addons.addon");
                for (String addon : classes) {
                    TestEngineConfiguration.getInstance().addProperty("addons.addon", addon);
                }
            }

            try {
                TestEngineConfiguration.getInstance().save();
            } catch (ConfigurationException pExc) {
                LOGGER.error("Unable to save the configuration: " + pExc.getMessage(), pExc);
            }
        }
        fireTableDataChanged();
    }

    public AddOnMetadata getAddOnMetaData(int pRowIndex) {
        return mAddons.get(pRowIndex);
    }

    protected AddOnManager getAddonManager() {
        return Environment.getEnvironment().getAddOnManager();
    }

    protected List<AddOnMetadata> mAddons;
    private static final String[] COLUMN_NAMES = new String[] {"Active", "Name", "Version", "Jar name", "Status"};
    private static final Logger LOGGER = Logger.getLogger(AddOnTableModel.class);

}
