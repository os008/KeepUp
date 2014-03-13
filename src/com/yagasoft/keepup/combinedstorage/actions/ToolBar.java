/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.actions/ToolBar.java
 *
 *			Modified: 13-Mar-2014 (13:46:05)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.actions;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.container.local.LocalFile;
import com.yagasoft.overcast.container.remote.RemoteFolder;


/**
 * Tool bar at the top of the main window. It has file operations.
 */
public class ToolBar extends JToolBar implements ActionListener
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
		CHOOSE_FOLDER
	}
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 6667133528600925976L;
	
	/**
	 * Instantiates a new tool bar.
	 */
	public ToolBar()
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
				
				if (JOptionPane.showOptionDialog(this, "Upload to 'root'? This will choose the best fit from all CSPs."
						, "Confirm root upload.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null
						, null, 1) == 0)
				{
					Logger.post("Uploading to root ...");
					
					LocalFile[] files = chooseFiles();
					
					// get the best CSP to store those files, and initiate upload.
					App.uploadFile(files, App.chooseCsp(files));
				}
				
				// done here.
				return;
			}
			
			App.uploadFile(chooseFiles(), remoteFolder);
		}
		
		if (Actions.CHOOSE_FOLDER.toString().equals(cmd))
		{
			chooseAFolder();
		}
	}
	
	/**
	 * Pops up a windows to choose a folder, and then updates the chosen folder global var.
	 */
	private void chooseAFolder()
	{
		// only choose directories.
		JFileChooser chooser = new JFileChooser(App.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = chooser.showOpenDialog(this);
		java.io.File selectedFolder = chooser.getSelectedFile();
		
		// if a folder was chosen ...
		if ((result == JFileChooser.APPROVE_OPTION) && (selectedFolder != null))
		{
			App.setLastDirectory(selectedFolder.getAbsolutePath());
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
	
	/**
	 * Pops up a windows to choose files.
	 * 
	 * @return the local files list.
	 */
	private LocalFile[] chooseFiles()
	{
		// only choose directories.
		JFileChooser chooser = new JFileChooser(App.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		
		chooser.showOpenDialog(this);
		java.io.File[] selectedFiles = chooser.getSelectedFiles();
		
		LocalFile[] files = new LocalFile[selectedFiles.length];
		
		for (int i = 0; i < files.length; i++)
		{
			files[i] = new LocalFile(selectedFiles[i].getAbsolutePath());
		}
		
		return files;
	}
	
}
