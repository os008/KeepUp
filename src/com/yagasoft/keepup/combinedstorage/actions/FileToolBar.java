/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.actions/FileToolBar.java
 *
 *			Modified: 13-Apr-2014 (00:03:22)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FileToolBar extends JToolBar implements ActionListener
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

		if (Actions.DOWNLOAD.toString().equals(cmd))
		{
			App.downloadFile(App.getSelectedFiles());
		}

		if (Actions.UPLOAD.toString().equals(cmd))
		{
			RemoteFolder<?> remoteFolder = App.getSelectedFolder();

			// no folder, so let the user choose either 'root', or stop.
			if (remoteFolder == null)
			{
				Logger.post("Nothing to upload to!");

				if (Msg.showQuestion("Upload to 'root'? This will choose the best fit from all CSPs.") == 0)
				{
					Logger.post("Uploading to root ...");

					LocalFile[] files = Browse.chooseFiles();

					// get the best CSP to store those files, and initiate upload.
					App.uploadFile(files, null);
				}

				// done here.
				return;
			}

			App.uploadFile(Browse.chooseFiles(), remoteFolder);
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
			App.deleteFiles(App.getSelectedFiles());
		}
	}

}
