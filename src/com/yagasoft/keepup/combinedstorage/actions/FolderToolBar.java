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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
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
		
		/** Delete folder. */
		DELETE
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
		
		button = createButton("delete_folder", Actions.DELETE + "", "Delete selected folder.", "Delete");
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
			
			App.createFolder(App.mainWindow.getBrowserPanel().getSelectedFolder(), folderName);
		}
		
		if (Actions.REFRESH.toString().equals(cmd))
		{
			App.refreshTree();
		}
		
		if (Actions.DELETE.toString().equals(cmd))
		{
			App.deleteFolder(App.mainWindow.getBrowserPanel().getSelectedFolder());
		}
	}
	
}
