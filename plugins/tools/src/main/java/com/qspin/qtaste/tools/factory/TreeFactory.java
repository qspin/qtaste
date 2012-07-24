package com.qspin.qtaste.tools.factory;

import java.util.List;

import javax.swing.tree.MutableTreeNode;

import com.qspin.qtaste.tools.model.event.Event;

public abstract class TreeFactory {

	public abstract MutableTreeNode buildRootTree(List<Event> pEvents);
}
