package com.qspin.qtaste.addon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestEngineConfiguration;
import com.qspin.qtaste.util.Environment;

/**
 * Class responsible for the {@link AddOn} management.
 * @author simjan
 */
public class AddOnManager {

	/**
	 * Constructor.
	 */
	public AddOnManager()
	{
		mRegisteredAddOns = new HashMap<String, AddOn>();
		mAddons = new ArrayList<AddOnMetadata>();
		loadConfigurationPane();
	}
	
	/**
	 * Loads and registers all add-ons references in the engine configuration file.
	 */
	public void loadAddOns()
	{
		List<String> addonToLoad = getAddOnClasses();
		for (File f : new File(StaticConfiguration.PLUGINS_HOME).listFiles())
		{
			if ( f.isFile() && f.getName().toUpperCase().endsWith(".JAR"))
			{
				AddOnMetadata meta = AddOnMetadata.createAddOnMetadata(f);
				if ( meta != null )
				{
					mAddons.add(meta);
					LOGGER.debug("load " + meta.getMainClass());
					if ( addonToLoad.contains(meta.getMainClass()) )
					{
						loadAddOn(meta);
					}
				}
			}
		}
	}
	
	public void loadAddOn(AddOnMetadata pAddonMetaData)
	{
		try {
            Class<?> addOnClass = Class.forName(pAddonMetaData.getMainClass());
            if (AddOn.class.isAssignableFrom(addOnClass))
            {
                AddOn ao = (AddOn) addOnClass.getConstructors()[0].newInstance(pAddonMetaData);
                ao.loadAddOn();
                registerAddOn(ao);
                pAddonMetaData.setStatus(AddOnMetadata.LOAD);			                
            }
            else
            {
            	pAddonMetaData.setStatus(AddOnMetadata.ERROR);
                throw new AddOnException("The class " + pAddonMetaData.getMainClass() + " is not an add-on implementation.");
            }
        } catch (Exception e) {
        	pAddonMetaData.setStatus(AddOnMetadata.ERROR);
            LOGGER.error("Exception load the add-on: " + pAddonMetaData.getName(), e);
        }
		finally
		{
			Environment.getEnvironment().getMainMenuBar().updateUI();
			Environment.getEnvironment().getMainFrame().validate();
		}
	}
	
	public void unloadAddOn(AddOnMetadata pAddonMetaData)
	{
		try
		{
			LOGGER.debug("try to unload " + pAddonMetaData);
			AddOn ao = getAddOn(pAddonMetaData.getName());
			ao.unloadAddOn();
	        pAddonMetaData.setStatus(AddOnMetadata.NONE);
	        mRegisteredAddOns.remove(pAddonMetaData.getName());
			if ( ao.hasConfiguration() )
			{
				removeConfiguration(ao.getAddOnId(), ao.getConfigurationPane());
			}
		}
		catch (AddOnException pExc)
		{
			LOGGER.error("Unable to unload the add-on " + pAddonMetaData.getName());
	        pAddonMetaData.setStatus(AddOnMetadata.ERROR);
		}
		finally
		{
			Environment.getEnvironment().getMainMenuBar().updateUI();
			Environment.getEnvironment().getMainFrame().validate();
		}
	}
	
	List<String> getAddOnClasses()
	{
		List<String> classes = new ArrayList<String>();
		TestEngineConfiguration config = TestEngineConfiguration.getInstance();
		int reportersCount = config.getMaxIndex("addons.addon") + 1;
        for (int reporterIndex = 0; reporterIndex < reportersCount; reporterIndex++) {
        	LOGGER.debug("Need to load " + config.getString("addons.addon(" + reporterIndex + ")"));
        	classes.add(config.getString("addons.addon(" + reporterIndex + ")"));
        }
        return classes;
	}
	
	private void loadConfigurationPane()
	{
		String rootName = "Plugins";
		mConfigurationRoot = new DefaultMutableTreeNode(rootName);
		
        mConfigurationPane = new AddOnManagerConfigurationPane(rootName, mAddons);

        mConfigurationTree = new ConfigurationTree(mConfigurationRoot, mConfigurationPane);
		
		Environment.getEnvironment().addTreeTabPane(rootName, mConfigurationTree, mConfigurationPane);
	}

	
	public void addConfiguration(String pConfigurationId, JPanel pPanel)
	{
		LOGGER.debug("Add a configuration pane for the addon :" + pConfigurationId);
		mConfigurationTree.addConfiguration(new DefaultMutableTreeNode(pConfigurationId));
		mConfigurationPane.add(pConfigurationId, pPanel);
	}
	
	public void removeConfiguration(String pConfigurationId, JPanel pPanel)
	{
		LOGGER.debug("Remove a configuration pane for the addon :" + pConfigurationId);
		mConfigurationTree.removeConfiguration(new DefaultMutableTreeNode(pConfigurationId));
		mConfigurationTree.invalidate();
		mConfigurationPane.validate();
		mConfigurationPane.remove(pPanel);
		mConfigurationPane.invalidate();
		mConfigurationPane.validate();
	}
	
	/**
	 * Registers the add-on. If the add-on is not loaded, loads it.
	 * @param pAddOn The add-on to register.
	 * 
	 * @return <code>true</code> if the add-on is successfully registered.
	 */
	boolean registerAddOn(AddOn pAddOn)
	{
		if ( !mRegisteredAddOns.containsKey(pAddOn.getAddOnId()))
		{
			mRegisteredAddOns.put(pAddOn.getAddOnId(), pAddOn);
			if ( pAddOn.hasConfiguration() )
			{
				addConfiguration(pAddOn.getAddOnId(), pAddOn.getConfigurationPane());
			}
			LOGGER.info("The add-on " + pAddOn.getAddOnId() + " has been registered." );
			return true;
		}
		else
		{
			LOGGER.warn("The add-on " + pAddOn.getAddOnId() + " is alreadry registered." );
			return false;
		}
	}
	
	/**
	 * Returns the registered add-on identified by the identifier.
	 * 
	 * @param pAddOnId The add-on's identifier.
	 * @return The registered add-on identified by the identifier.
	 */
	public AddOn getAddOn(String pAddOnId)
	{
		
		if ( mRegisteredAddOns.containsKey(pAddOnId) )
		{
			return mRegisteredAddOns.get(pAddOnId);
		}
		else
		{
			LOGGER.warn("Add-on " + pAddOnId + " is not loaded.");
			return null;
		}
	}
	
	public AddOnMetadata getAddonMetaData(String pAddOnId)
	{
		for ( AddOnMetadata aomd : mAddons )
		{
			if (aomd.getName().equals(pAddOnId))
				return aomd;
		}
		LOGGER.warn("Add-on " + pAddOnId + " is not registered.");
		return null;
	}

	protected DefaultMutableTreeNode mConfigurationRoot;
	/** Map containing all registered add-ons. */
	protected Map<String,AddOn> mRegisteredAddOns;
    private ConfigurationTree mConfigurationTree;
    private AddOnManagerConfigurationPane mConfigurationPane;
    private List<AddOnMetadata> mAddons;
	/** Used for logging. */
	protected static final Logger LOGGER = Logger.getLogger(AddOnManager.class);
}
