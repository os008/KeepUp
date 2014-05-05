/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.actions/FolderToolBar.java
 *
 *			Modified: Mar 31, 2014 (9:52:00 AM)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FolderToolBar extends JToolBar implements ActionListener
{

	/**
	 * What actions can be done by buttons on that bar.
	 */
	private enum Actions
	{

		/** Create. */
		CREATE,

		/** Refresh. */
		REFRESH,

		/** The rename. */
		RENAME,

		/** Delete folder. */
		DELETE,

		PASTE
	}

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 6667133528600925976L;

	/**
	 * Instantiates a new tool bar.
	 */
	public FolderToolBar()
	{
		initBar();
	}

	/**
	 * Inits the bar.
	 */
	private void initBar()
	{
		addButtons();
	}

	/**
	 * Adds the buttons to the bar.
	 */
	protected void addButtons()
	{
		JButton button = null;

		button = createButton("create", Actions.CREATE + "", "Create a folder.", "Create");
		add(button);

		button = createButton("refresh", Actions.REFRESH + "", "Refresh folders list.", "Refresh");
		add(button);

		button = createButton("rename", Actions.RENAME + "", "rename selected file.", "Rename");
		add(button);

		button = createButton("delete_folder", Actions.DELETE + "", "Delete selected folder.", "Delete");
		add(button);

		button = createButton("paste", Actions.PASTE + "", "Paste into selected folder.", "Paste");
		add(button);
	}

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
		String imgLocation = "images\\" + imageName + ".gif";
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
//			Logger.post("Resource not found: " + imgLocation);
		}

		return button;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		// Handle each button.

		if (Actions.CREATE.toString().equals(cmd))
		{
			String folderName = Msg.getInput("Please enter the name of the folder:");

			if ((folderName == null) || (folderName.length() <= 0))
			{
				Msg.showError("Try again with a proper name, please.");
				return;
			}

			App.createFolder(App.getSelectedFolder(), folderName);
		}

		if (Actions.REFRESH.toString().equals(cmd))
		{
			App.refreshTree();
		}

		if (Actions.RENAME.toString().equals(cmd))
		{
			String newName = Msg.getInput("Please enter a new name for the file:");

			if ((newName == null) || (newName.length() <= 0))
			{
				Msg.showError("Try again with a proper name, please.");
				return;
			}

			App.renameFolder(App.getSelectedFolder(), newName);
		}

		if (Actions.DELETE.toString().equals(cmd))
		{
			CombinedFolder selectedFolder = App.getSelectedFolder();

			if (Msg.askConfirmation("Are you sure you want to delete '" + selectedFolder.getPath() + "'?"))
			{
				App.deleteFolder(selectedFolder);
			}
		}

		if (Actions.PASTE.toString().equals(cmd))
		{
			App.pasteFiles(App.getSelectedFolder());
		}
	}

}
