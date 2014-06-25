/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.actions/FileToolBar.java
 *
 *			Modified: 20-Jun-2014 (21:11:16)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.toolbars;


import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import com.yagasoft.keepup.GUI;
import com.yagasoft.keepup.Operation;
import com.yagasoft.keepup.Transfer;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.keepup.ui.browser.table.FileTableController;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FileToolBar extends BrowserToolBar
{
	
	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= 6667133528600925976L;
	
	protected FileTableController	tableController;
	
	/**
	 * Instantiates a new tool bar.
	 *
	 * @param tableController
	 *            Table controller that has a 'getSelectedFiles' method.
	 */
	public FileToolBar(FileTableController tableController)
	{
		this.tableController = tableController;
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
		addButton(Action.DOWNLOAD, createButton("download", Action.DOWNLOAD + "", "Download selected files.", "Download"));
		addButton(Action.UPLOAD, createButton("upload", Action.UPLOAD + "", "Upload files to selected folder.", "Upload"));
		addButton(Action.REFRESH, createButton("refresh", Action.REFRESH + ""
				, "Refresh contents of selected folder.", "Refresh"));
		addButton(Action.COPY, createButton("copy", Action.COPY + "", "copy selected file.", "Copy"));
		addButton(Action.MOVE, createButton("move", Action.MOVE + "", "move selected file.", "Move"));
		addButton(Action.RENAME, createButton("rename", Action.RENAME + "", "rename selected file.", "Rename"));
		addButton(Action.DELETE, createButton("delete", Action.DELETE + "", "Delete selected file.", "Delete"));
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		
		// Handle each button.
		
		if (Action.DOWNLOAD.toString().equals(cmd))
		{
			Transfer.downloadFiles(tableController.getSelectedFiles());
		}
		
		if (Action.UPLOAD.toString().equals(cmd))
		{
			CombinedFolder remoteFolder = GUI.getSelectedFolder();
			
			// no folder, so let the user choose either 'root', or stop.
			if (remoteFolder == null)
			{
				Logger.error("KEEPUP: FILE TOOLBAR: nothing to upload to!");
				
				if (Msg.askConfirmation("Upload to 'root'? This will choose the best fit from all CSPs."))
				{
					Logger.info("KEEPUP: FILE TOOLBAR: uploading to root ...");
				}
				else
				{
					return;
				}
			}
			
			List<LocalFile> files = Arrays.asList(Browse.chooseFiles());
			
			// no file, then no need to proceed.
			if (files.size() == 0)
			{
				Logger.error("KEEPUP: FILE TOOLBAR: nothing to upload!");
				Msg.showError("Please, choose a file first.");
				return;
			}
			
			Transfer.uploadFiles(files, remoteFolder);
		}
		
		if (Action.REFRESH.toString().equals(cmd))
		{
			GUI.updateTable();
		}
		
		if (Action.COPY.toString().equals(cmd))
		{
			Operation.copyFiles(tableController.getSelectedFiles());
		}
		
		if (Action.MOVE.toString().equals(cmd))
		{
			Operation.moveFiles(tableController.getSelectedFiles());
		}
		
		if (Action.RENAME.toString().equals(cmd))
		{
			String newName = Msg.getInput("Please enter a new name for the file:");
			
			if ((newName == null) || (newName.length() <= 0))
			{
				Msg.showError("Try again with a proper name, please.");
				return;
			}
			
			Operation.renameFile(tableController.getSelectedFiles(), newName);
		}
		
		if (Action.DELETE.toString().equals(cmd))
		{
			List<File<?>> selectedFiles = tableController.getSelectedFiles();
			String filesNames = "";
			
			// form the names in to a list.
			for (int i = 0; i < ((selectedFiles.size() > 10) ? 10 : selectedFiles.size()); i++)
			{
				filesNames += selectedFiles.get(i).getPath() + "\n";
			}
			
			if (selectedFiles.size() > 10)
			{
				filesNames += "...";
			}
			
			if (Msg.askConfirmation("Are you sure you want to delete the following files:\n" + filesNames))
			{
				Operation.deleteFiles(selectedFiles);
			}
		}
	}
	
}
