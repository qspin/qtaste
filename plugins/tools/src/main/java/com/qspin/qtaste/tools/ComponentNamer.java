package com.qspin.qtaste.tools;

import java.awt.Component;
import java.util.HashMap;
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
	public ComponentNamer()
	{
		super();
		mMapChildIndex = new HashMap<Component, Integer>();
		mMapName = new HashMap<Component, String>();
	}
	
	/**
	 * If the component's name is null or empty, set a name on this component.
	 * @param pComponent
	 * @param idx
	 * @return the idx increase by 1 if the component's name has been updated.
	 */
	public synchronized int setNameToComponent(Component pComponent, int idx)
	{
		if (pComponent.getName() == null || pComponent.getName().isEmpty()) {
			pComponent.setName(getChildName(pComponent.getParent()));
			idx += 1;
		}
		return idx;
	}
	
	/**
	 * Returns a name for a child of the component.  
	 * @param pComponent can be <code>null</code>.
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
		return mMapName.get(pComponent)+childIndex;
	}
	
	private Map<Component, Integer> mMapChildIndex;
	private Map<Component, String> mMapName;
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
}
