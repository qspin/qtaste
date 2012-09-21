package com.qspin.qtaste.tools;

import javax.swing.SwingUtilities;

import com.qspin.qtaste.tools.converter.ui.MainUI;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainUI();
			}
		});

	}

}
