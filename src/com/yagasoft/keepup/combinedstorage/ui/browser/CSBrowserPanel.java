/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser/CombinedStorageBrowserPanel.java
 *
 *			Modified: 27-May-2014 (17:40:41)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser;


import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.keepup.ui.BrowserPanel;
import com.yagasoft.overcast.base.container.local.LocalFolder;


/**
 * The folders tree and files list GUI panels.
 */
public class CSBrowserPanel extends BrowserPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 1173503486389973440L;
	
	/** The text field destination. */
	private JTextField			textFieldWorkingFolder;
	
	/**
	 * Instantiates a new browser panel.
	 */
	public CSBrowserPanel()
	{
		super();
	}
	
	/**
	 * Create the panel.
	 *
	 * @param treeView
	 *            Tree view.
	 * @param tableView
	 *            Table view.
	 */
	public CSBrowserPanel(JPanel treeView, JPanel tableView)
	{
		super(treeView, tableView);
	}
	
	/**
	 * Initialises objects, and forms the split pane.
	 */
	@Override
	protected void initGUI()
	{
		super.initGUI();
		
		// --------------------------------------------------------------------------------------
		// #region Working folder.
		
		JPanel panelWorkingFolder = new JPanel(new BorderLayout());
		
		textFieldWorkingFolder = new JTextField();
		textFieldWorkingFolder.setEditable(false);
		panelWorkingFolder.add(textFieldWorkingFolder, BorderLayout.CENTER);
		
		JButton buttonBrowse = new JButton("Browse");
		buttonBrowse.addActionListener(event -> chooseAFolder());
		panelWorkingFolder.add(buttonBrowse, BorderLayout.EAST);
		
		add(panelWorkingFolder, BorderLayout.NORTH);
		
		// #endregion Working folder.
		// --------------------------------------------------------------------------------------
	}
	
	/**
	 * Pops up a windows to choose a folder, and then updates the chosen folder global var.
	 */
	public void chooseAFolder()
	{
		LocalFolder selectedFolder = Browse.chooseFolder();
		
		// if a folder was chosen ...
		if (selectedFolder != null)
		{
			App.setLastDirectory(selectedFolder.getPath());
		}
	}
	
	/**
	 * Update destination folder in the text field.
	 */
	public void updateDestinationFolder(String path)
	{
		textFieldWorkingFolder.setText(path);
	}
	
}
