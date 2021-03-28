/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oim.swing.app;

import javax.swing.UIManager;

/**
 * 2013-9-4 15:36:45
 * 
 * @author XiaHui
 */
public class AppStartup implements Runnable {
	
	

	public static void main(String[] args) {
		try {
			//			UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(AppStartup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(new AppStartup());
	}

	@Override
	public void run() {
		 new Startup();
	}
}
