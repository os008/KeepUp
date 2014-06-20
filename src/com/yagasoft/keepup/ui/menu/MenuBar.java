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


import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.yagasoft.keepup.App;


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
		add(fileMenu);

		// build edit menu
		editMenu = new JMenu("Edit");
		options = new JMenuItem("Options");
		options.addActionListener(event -> App.showSubWindow(new Options(), "Options"));
		editMenu.add(options);
		add(editMenu);

		// build help menu
		helpMenu = new JMenu("Help");
		about = new JMenuItem("About");
		about.addActionListener(event -> App.showSubWindow(new About(), "About"));
		helpMenu.add(about);
		add(helpMenu);
	}
}
