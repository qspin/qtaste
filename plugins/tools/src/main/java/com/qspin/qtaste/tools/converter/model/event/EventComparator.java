package com.qspin.qtaste.tools.converter.model.event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

	@Override
	public int compare(Event o1, Event o2) {
		if ( o1 == null && o2 == null )
			return 0;
		if ( o1 == null )
			return -1;
		if ( o2 == null )
			return 1;
		if ( o1.getTimeStamp() == o2.getTimeStamp() )
			return 0;
		if ( o1.getTimeStamp() > o2.getTimeStamp() )
			return 1;
		//if ( o1.getTimeStamp() < o2.getTimeStamp() )
			return -1;
	}

}
