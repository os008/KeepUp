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

package com.yagasoft.keepup.ui.toolbars;


import java.awt.event.ActionEvent;

import com.yagasoft.keepup.GUI;
import com.yagasoft.keepup.Operation;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FolderToolBar extends BrowserToolBar
{

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
		addButton(Action.BACKWARD, createButton("backward", Action.BACKWARD + "", "Go to previous folder.", "Backward"));
		addButton(Action.FORWARD, createButton("forward", Action.FORWARD + "", "Go to next folder.", "Forward"));
		addButton(Action.CREATE, createButton("create", Action.CREATE + "", "Create a folder.", "Create"));
		addButton(Action.REFRESH, createButton("refresh", Action.REFRESH + "", "Refresh folders list.", "Refresh"));
		addButton(Action.RENAME, createButton("rename", Action.RENAME + "", "rename selected file.", "Rename"));
		addButton(Action.DELETE, createButton("delete", Action.DELETE + "", "Delete selected folder.", "Delete"));
		addButton(Action.PASTE, createButton("paste", Action.PASTE + "", "Paste into selected folder.", "Paste"));
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

				Operation.createFolder(GUI.getSelectedFolder(), folderName);
				break;
			}

			case "REFRESH":
				GUI.refreshTree();
				break;

			case "RENAME":
			{
				String newName = Msg.getInput("Please enter a new name for the file:");

				if ((newName == null) || (newName.length() <= 0))
				{
					Msg.showError("Try again with a proper name, please.");
					return;
				}

				Operation.renameFolder(GUI.getSelectedFolder(), newName);
				break;
			}

			case "DELETE":
			{
				CombinedFolder selectedFolder = GUI.getSelectedFolder();

				if (Msg.askConfirmation("Are you sure you want to delete '" + selectedFolder.getPath() + "'?"))
				{
					Operation.deleteFolder(selectedFolder);
				}

				break;
			}

			case "PASTE":
				Operation.pasteFiles(GUI.getSelectedFolder());
				break;

			case "BACKWARD":
				GUI.navigateBackward();
				break;

			case "FORWARD":
				GUI.navigateForward();
				break;
		}
	}

}
