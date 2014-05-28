/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.actions/FileToolBar.java
 *
 *			Modified: 28-May-2014 (18:13:02)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.actions;


import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.remote.RemoteFile;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FileToolBar extends BrowserToolBar
{

	/**
	 * What actions can be done by buttons on that bar.
	 */
	private enum Actions
	{

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
		DELETE
	}

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 6667133528600925976L;

	/**
	 * Instantiates a new tool bar.
	 */
	public FileToolBar()
	{
		initBar();
	}

	/**
	 * Inits the bar.
	 */
	private void initBar()
	{
		JButton button = null;

		button = createButton("download", Actions.DOWNLOAD + "", "Download selected files.", "Download");
		add(button);

		button = createButton("upload", Actions.UPLOAD + "", "Upload files to selected folder.", "Upload");
		add(button);

		button = createButton("refresh", Actions.REFRESH + "", "Refresh contents of selected folder.", "Refresh");
		add(button);

		button = createButton("copy", Actions.COPY + "", "copy selected file.", "Copy");
		add(button);

		button = createButton("move", Actions.MOVE + "", "move selected file.", "Move");
		add(button);

		button = createButton("rename", Actions.RENAME + "", "rename selected file.", "Rename");
		add(button);

		button = createButton("delete", Actions.DELETE + "", "Delete selected file.", "Delete");
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

		if (Actions.DOWNLOAD.toString().equals(cmd))
		{
			App.downloadFile(App.getSelectedFiles());
		}

		if (Actions.UPLOAD.toString().equals(cmd))
		{
			CombinedFolder remoteFolder = App.getSelectedFolder();

			// no folder, so let the user choose either 'root', or stop.
			if (remoteFolder == null)
			{
				Logger.error("Nothing to upload to!");

				if (Msg.showQuestion("Upload to 'root'? This will choose the best fit from all CSPs.") == 0)
				{
					Logger.info("Uploading to root ...");
				}
				else
				{
					return;
				}
			}

			LocalFile[] files = Browse.chooseFiles();

			// no file, then no need to proceed.
			if (files.length == 0)
			{
				Logger.error("Nothing to upload!");
				Msg.showError("Please, choose a file first.");
				return;
			}

			App.uploadFile(files, remoteFolder);
		}

		if (Actions.REFRESH.toString().equals(cmd))
		{
			App.updateTable();
		}

		if (Actions.COPY.toString().equals(cmd))
		{
			App.copyFiles(App.getSelectedFiles());
		}

		if (Actions.MOVE.toString().equals(cmd))
		{
			App.moveFiles(App.getSelectedFiles());
		}

		if (Actions.RENAME.toString().equals(cmd))
		{
			String newName = Msg.getInput("Please enter a new name for the file:");

			if ((newName == null) || (newName.length() <= 0))
			{
				Msg.showError("Try again with a proper name, please.");
				return;
			}

			App.renameFile(App.getSelectedFiles(), newName);
		}

		if (Actions.DELETE.toString().equals(cmd))
		{
			RemoteFile<?>[] selectedFiles = App.getSelectedFiles();
			String filesNames = "";

			// form the names in to a list.
			for (int i = 0; i < ((selectedFiles.length > 10) ? 10 : selectedFiles.length); i++)
			{
				filesNames += selectedFiles[i].getPath() + "\n";
			}

			if (selectedFiles.length > 10)
			{
				filesNames += "...";
			}

			if (Msg.askConfirmation("Are you sure you want to delete the following files:\n" + filesNames))
			{
				App.deleteFiles(selectedFiles);
			}
		}
	}

}
