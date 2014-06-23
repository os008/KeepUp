/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.actions/BrowserToolBar.java
 *
 *			Modified: 28-May-2014 (15:49:12)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.toolbars;


import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup._keepup;


/**
 * The Class BrowserToolBar.
 */
public abstract class BrowserToolBar extends JToolBar implements ActionListener
{

	/**
	 * What actions can be done by buttons on that bar.
	 */
	protected enum Actions
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

	private static final long	serialVersionUID	= 4610918332476400206L;

	/**
	 * Create button.
	 *
	 * @param imageName
	 *            Image file name.
	 * @param actionCommand
	 *            Action to be taken by that button (from the Enum {@link Actions}).
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

}
