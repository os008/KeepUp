/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.actions/ToolBar.java
 *
 *			Modified: 18-Mar-2014 (17:36:44)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.actions;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.container.local.LocalFile;
import com.yagasoft.overcast.container.local.LocalFolder;
import com.yagasoft.overcast.container.remote.RemoteFolder;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class FileToolBar extends JToolBar implements ActionListener
{
	
	private JTextField	textFieldDestination;
	
	/**
	 * What actions can be done by buttons on that bar.
	 */
	private enum Actions
	{
		
		/** Download. */
		DOWNLOAD,
		
		/** Upload. */
		UPLOAD,
		
		/** Choose folder. */
		CHOOSE_FOLDER,
		
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
		addButtons();
	}
	
	/**
	 * Adds the buttons to the bar.
	 */
	protected void addButtons()
	{
		JButton button = null;
		
		button = createButton("download", Actions.DOWNLOAD + "", "Download selected files.", "Download");
		add(button);
		
		textFieldDestination = new JTextField();
		textFieldDestination.setEditable(false);
		textFieldDestination.setMinimumSize(new Dimension(50, textFieldDestination.getMinimumSize().height));
//		textFieldDestination.setPreferredSize(new Dimension(200, textFieldDestination.getPreferredSize().height));
//		textFieldDestination.setMaximumSize(new Dimension(200, textFieldDestination.getMaximumSize().height));
		add(textFieldDestination);
		
		button = createButton("null", Actions.CHOOSE_FOLDER + "", "Select a folder.", "Browse");
		add(button);
		
		button = createButton("upload", Actions.UPLOAD + "", "Upload files to selected folder.", "Upload");
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
			App.downloadFile(App.mainWindow.getBrowserPanel().getSelectedFiles());
		}
		
		if (Actions.UPLOAD.toString().equals(cmd))
		{
			RemoteFolder<?> remoteFolder = App.mainWindow.getBrowserPanel().getSelectedFolder();
			
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
		
		if (Actions.CHOOSE_FOLDER.toString().equals(cmd))
		{
			chooseAFolder();
		}
		
		if (Actions.DELETE.toString().equals(cmd))
		{
			App.deleteFiles(App.mainWindow.getBrowserPanel().getSelectedFiles());
		}
	}
	
	/**
	 * Pops up a windows to choose a folder, and then updates the chosen folder global var.
	 */
	private void chooseAFolder()
	{
		LocalFolder selectedFolder = Browse.chooseFolder();
		
		// if a folder was chosen ...
		if (selectedFolder != null)
		{
			App.setLastDirectory(selectedFolder.getPath());
			updateDestinationFolder();
		}
	}
	
	/**
	 * Update destination folder in the text field.
	 */
	public void updateDestinationFolder()
	{
		textFieldDestination.setText(App.getLastDirectory());
	}
	
}
