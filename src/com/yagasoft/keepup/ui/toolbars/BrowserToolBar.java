/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.toolbars/BrowserToolBar.java
 *
 *			Modified: 24-Jun-2014 (15:03:17)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.toolbars;


import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup._keepup;


/**
 * The Class BrowserToolBar.
 */
public abstract class BrowserToolBar extends JToolBar implements ActionListener
{

	private static final long	serialVersionUID	= 4610918332476400206L;

	/**
	 * What actions can be done by buttons on that bar.
	 */
	protected enum Action
	{
		CREATE,

		/** Download. */
		DOWNLOAD,

		/** Upload. */
		UPLOAD,

		REFRESH,

		COPY,

		MOVE,

		/** The rename. */
		RENAME,

		/** Delete file. */
		DELETE,

		/** Paste. */
		PASTE,

		BACKWARD,

		FORWARD
	}

	protected Map<Action, JButton>	buttons	= new HashMap<Action, JButton>();

	/**
	 * Create button.
	 *
	 * @param imageName
	 *            Image file name.
	 * @param actionCommand
	 *            Action to be taken by that button (from the Enum {@link Action}).
	 * @param toolTipText
	 *            Tool tip text.
	 * @param altText
	 *            Text to be displayed in case the icon is missing.
	 * @return The button.
	 */
	protected JButton createButton(String imageName, String actionCommand, String toolTipText, String altText)
	{
		// Look for the image.
		String imgLocation = "images/" + imageName + ".gif";
		URL imageURL = _keepup.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null)
		{	// image found
			button.setIcon(new ImageIcon(imageURL, altText));
		}
		else
		{	// no image found
			button.setText(altText);
		}

		return button;
	}

	/**
	 * Adds the button to the collection of buttons (for reference), and adds to the bar.
	 *
	 * @param action
	 *            Action.
	 * @param button
	 *            Button.
	 */
	protected void addButton(Action action, JButton button)
	{
		buttons.put(action, button);
		add(button);
	}

	/**
	 * Removes the button from the collection of buttons, and removes it from the bar.
	 *
	 * @param action Action.
	 */
	protected void removeButton(Action action)
	{
		remove(buttons.remove(action));
	}

}
