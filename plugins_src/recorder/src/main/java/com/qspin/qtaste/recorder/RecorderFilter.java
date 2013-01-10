package com.qspin.qtaste.recorder;

import java.awt.Component;
import java.util.List;

import com.qspin.qtaste.tools.filter.Filter;

public final class RecorderFilter extends Filter {


	public RecorderFilter(Filter pFilter) {
		super(pFilter);
	}

	@Override
	public boolean accept(Object o) {
		if ( o instanceof List && ((List<?>)o).size() == 2 )
		{
			Component comp = (Component)((List<?>)o).get(0);
			if (concernedComponent(comp))
				return checkEvent((Class<?>)((List<?>)o).get(1));
			
			return true;
		}
		else
		{
			return super.accept(o);
		}
	}
	
	private boolean checkEvent(Class<?> pClass)
	{
		return checkRules(mAcceptedEvents, pClass)
				&& checkRules(mRejectedEvents, pClass);
	}

	private boolean concernedComponent(Component pComponent)
	{
		if (mSourceRule != null)
		{
			return mSourceRule.isRespected(pComponent.getClass());
		}
		return true;
	}
}
