/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.actions/FolderToolBar.java
 *
 *			Modified: 28-May-2014 (18:12:53)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.actions;


import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FolderToolBar extends BrowserToolBar
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

		/** Paste. */
		PASTE,

		BACKWARD,

		FORWARD
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

		button = createButton("backward", Actions.BACKWARD + "", "Go to previous folder.", "Backward");
		add(button);

		button = createButton("forward", Actions.FORWARD + "", "Go to next folder.", "Forward");
		add(button);

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
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		// Handle each button.

		switch (cmd)
		{
			case "CREATE":
			{
				String folderName = Msg.getInput("Please enter the name of the folder:");

				if ((folderName == null) || (folderName.length() <= 0))
				{
					Msg.showError("Try again with a proper name, please.");
					return;
				}

				App.createFolder(App.getSelectedFolder(), folderName);
				break;
			}

			case "REFRESH":
				App.refreshTree();
				break;

			case "RENAME":
			{
				String newName = Msg.getInput("Please enter a new name for the file:");

				if ((newName == null) || (newName.length() <= 0))
				{
					Msg.showError("Try again with a proper name, please.");
					return;
				}

				App.renameFolder(App.getSelectedFolder(), newName);
				break;
			}

			case "DELETE":
			{
				CombinedFolder selectedFolder = App.getSelectedFolder();

				if (Msg.askConfirmation("Are you sure you want to delete '" + selectedFolder.getPath() + "'?"))
				{
					App.deleteFolder(selectedFolder);
				}

				break;
			}

			case "PASTE":
				App.pasteFiles(App.getSelectedFolder());
				break;

			case "BACKWARD":
				App.navigateBackward();
				break;

			case "FORWARD":
				App.navigateForward();
				break;
		}
	}

}
