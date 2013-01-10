package com.qspin.qtaste.tools.filter;


public class RuleOnClass implements FilterRule {

	public RuleOnClass(Class<?> pTestedClass, boolean pExpectedResult)
	{
		mClass = pTestedClass;
		mResult = pExpectedResult;
	}
	
	@Override
	public boolean isRespected(Object o) {
		Class<?> c;
		if ( o instanceof Class) {
			c = (Class<?>) o;
		} else {
			c = o.getClass();
		}
		return  (mClass.isAssignableFrom(c) == mResult);
	}
	
	private Class<?> mClass;
	private boolean mResult;

}
