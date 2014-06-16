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


import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

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
		editMenu.add(options);
		add(editMenu);

		// build help menu
		helpMenu = new JMenu("Help");
		about = new JMenuItem("About");
		about.addActionListener(createFrameAction(new About(), "About"));
		helpMenu.add(about);
		add(helpMenu);
	}

	/**
	 * Creates an action to be taken when a menu item is pressed
	 * that is related to a panel to be opened.
	 *
	 * @param panel
	 *            the panel.
	 * @return the action listener object
	 */
	private static ActionListener createFrameAction(JPanel panel, String title)
	{
		return event ->
		{
			// create a frame for the panel.
			JFrame frame = new JFrame(title);

			// open the frame relative to the main window.
			Point mainWindowLocation = App.mainWindow.getFrame().getLocation();
			frame.setLocation((int) mainWindowLocation.getX() + 50, (int) mainWindowLocation.getY() + 50);

			// when the frame is closed, dispose of it and return focus to the main window.
			frame.addWindowListener(new WindowAdapter()
			{

				@Override
				public void windowClosing(WindowEvent e)
				{
					frame.dispose();
					App.setMainWindowFocusable(true);
				}
			});

			// add the passed panel to the frame.
			frame.add(panel);
			// show the frame.
			frame.setVisible(true);
			// fit the frame to panel.
			frame.pack();

			// disable the main window.
			App.setMainWindowFocusable(false);
		};
	}
}
