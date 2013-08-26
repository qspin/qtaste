package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.SingletonComponent;

public interface Subtitler extends SingletonComponent {

	void setSubtitle(String subtitle);
	void setSubtitle(String subtitle, double displayTimeInSecond);
}
