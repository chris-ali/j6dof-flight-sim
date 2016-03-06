package com.chrisali.javaflightsim.utilities.tests;

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.menus.Controller;
import com.chrisali.javaflightsim.menus.MainFrame;

public class GUITest {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}

	private static void runApp() {new MainFrame(new Controller());}
}
