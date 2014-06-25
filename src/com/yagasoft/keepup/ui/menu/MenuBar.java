/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.menu/MenuBar.java
 *
 *			Modified: 16-Jun-2014 (17:32:15)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.menu;


import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.GUI;
import com.yagasoft.keepup.ui.menu.panels.AboutPanel;
import com.yagasoft.keepup.ui.menu.panels.options.OptionsPanel;


/**
 * The Class MenuBar.
 */
public class MenuBar extends JMenuBar
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -8001930077352279137L;

	/** File menu. */
	private JMenu				fileMenu;

	/** Edit menu. */
	private JMenu				editMenu;

	/** Help menu. */
	private JMenu				helpMenu;

	/** Exit. */
	private JMenuItem			exit;

	/** Options. */
	private JMenuItem			options;

	/** About. */
	private JMenuItem			about;

	/**
	 * Instantiates a new menu bar.
	 */
	public MenuBar()
	{
		initMenu();
	}

	/**
	 * Initialise the menu.
	 */
	private void initMenu()
	{
		// build file menu
		fileMenu = new JMenu("File");
		exit = new JMenuItem("Exit");
		exit.addActionListener(event -> App.mainWindow.getFrame()
				.dispatchEvent(new WindowEvent(App.mainWindow.getFrame(), WindowEvent.WINDOW_CLOSING)));
		fileMenu.add(exit);
		add(fileMenu);

		// build edit menu
		editMenu = new JMenu("Edit");
		options = new JMenuItem("Options");
		options.addActionListener(event ->
		{
			OptionsPanel optionsPanel = new OptionsPanel();
			JFrame frame = GUI.showSubWindow(optionsPanel, "Options");
			optionsPanel.setFrame(frame);
			optionsPanel.addListener(() -> new Thread(() -> App.resetCSPs()).start());
		});
		editMenu.add(options);
		add(editMenu);

		// build help menu
		helpMenu = new JMenu("Help");
		about = new JMenuItem("About");
		about.addActionListener(event -> GUI.showSubWindow(new AboutPanel(), "About"));
		helpMenu.add(about);
		add(helpMenu);
	}
}
