package com.qspin.qtaste.tools;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread which scans all GUI components and give them a name if needed.
 * @author simjan
 *
 */
public final class ComponentNamer extends AbstractGUIAnalyzer {

	/**
	 * Constructor.
	 */
	private ComponentNamer()
	{
		super();
		mMapChildIndex = new HashMap<Component, Integer>();
		mMapName = new HashMap<Component, String>();
		mMapNameIndex = new HashMap<String, Integer>();
		mProccedComponent = new ArrayList<Component>();
		
		mComponentToNameMapping = new HashMap<Component, String>();
		mNametoComponentMapping = new HashMap<String, Component>();
	}
	
	/**
	 * If the component's name is null or empty, set a name on this component.
	 * @param pComponent
	 * @param idx
	 * @return the idx increase by 1 if the component's name has been updated.
	 */
	public synchronized int setNameToComponent(Component pComponent, int idx)
	{
		if ( mProccedComponent.contains(pComponent) )
		{
			return idx;
		}
		String name = null;
		if (pComponent.getName() == null || pComponent.getName().isEmpty()) {
			name = getChildName(pComponent.getParent());
			idx += 1;
		}
		name = pComponent.getName();
		if ( !mMapNameIndex.containsKey(name) )
		{
			mMapNameIndex.put(name, 0);
		}
		else
		{
			String key = name;
			int index = mMapNameIndex.get(key);
			name += "_" + index;
			mMapNameIndex.put(key, index +1);
		}
		mComponentToNameMapping.put(pComponent, name);
		mNametoComponentMapping.put(name, pComponent);
		mProccedComponent.add(pComponent);
		return idx;
	}
	
	/**
	 * Returns a name for a child of the component.  
	 * @param pComponent The component's parent. Can be <code>null</code>.
	 * @return
	 */
	private String getChildName(Component pComponent)
	{
		if ( pComponent == null )
		{
			return "NoParent_"+Integer.toString(mNullParentIndex++);
		}
		if ( !mMapChildIndex.containsKey(pComponent) )
		{
			mMapChildIndex.put(pComponent, 0);
			mMapName.put(pComponent, "parent"+mParentIndex+"_child");
			mParentIndex++;
		}
		int childIndex = mMapChildIndex.get(pComponent);
		mMapChildIndex.put(pComponent, childIndex+1);
		String name = mMapName.get(pComponent)+childIndex;
		
		return name;
	}

	private Map<Component, Integer> mMapChildIndex;
	private Map<String, Integer> mMapNameIndex;
	private Map<Component, String> mMapName;
	private List<Component> mProccedComponent;

	private Map<Component, String> mComponentToNameMapping;
	private Map<String, Component> mNametoComponentMapping;
	
	private int mNullParentIndex = 0;
	private int mParentIndex = 0;
	
	@Override
	protected boolean preProcess() {
		return true;
	}

	@Override
	protected void processComponent(Component pComponent) {
		setNameToComponent(pComponent, 0);
	}

	@Override
	protected boolean postProcess() {
		return true;
	}
	
	private static ComponentNamer INSTANCE;
	
	public static synchronized ComponentNamer getInstance(){
		if ( INSTANCE == null ) {
			INSTANCE = new ComponentNamer();
		}
		return INSTANCE;
	}
	
	public synchronized String getNameForComponent(Component pComponent)
	{
		return mComponentToNameMapping.get(pComponent);
	}
	
	public synchronized Component getComponentForName(String pName)
	{
		return mNametoComponentMapping.get(pName);
	}
}
