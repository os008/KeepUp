/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser/BrowserPanel.java
 * 
 *			Modified: 26-May-2014 (17:20:41)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser;


import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.ui.browser.table.FilesTable;
import com.yagasoft.keepup.combinedstorage.ui.browser.tree.FoldersTree;
import com.yagasoft.keepup.dialogues.Browse;
import com.yagasoft.overcast.base.container.local.LocalFolder;


/**
 * The folders tree and files list GUI panels.
 */
public class BrowserPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 1173503486389973440L;
	
	/** The text field destination. */
	private JTextField			textFieldWorkingFolder;
	
	/** Split pane. */
	private JSplitPane			splitPane;
	
	/** Tree view. */
	private FoldersTree			treeView;
	
	/** Table view. */
	private FilesTable			tableView;
	
	/**
	 * Instantiates a new browser panel.
	 */
	public BrowserPanel()
	{
		this(null, null);
	}
	
	/**
	 * Create the panel.
	 *
	 * @param treeView
	 *            Tree view.
	 * @param tableView
	 *            Table view.
	 */
	public BrowserPanel(FoldersTree treeView, FilesTable tableView)
	{
		this.treeView = treeView;
		this.tableView = tableView;
		initGUI();
	}
	
	/**
	 * Initialises objects, and forms the split pane.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		
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
		
		splitPane = new JSplitPane();
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(tableView);
		add(splitPane, BorderLayout.CENTER);
		
		resetDivider(150);
	}
	
	public void setTreeView(FoldersTree treeView)
	{
		this.treeView = treeView;
		splitPane.setLeftComponent(treeView);
		resetDivider();
	}
	
	/**
	 * Sets the table view.
	 *
	 * @param tableView
	 *            the new table view
	 */
	public void setTableView(FilesTable tableView)
	{
		this.tableView = tableView;
		splitPane.setRightComponent(tableView);
		resetDivider();
	}
	
	/**
	 * Resets the position of the divider between the tree and the files' list.
	 *
	 * @param position
	 *            position of the divider.
	 */
	public void resetDivider(int... position)
	{
		if (position.length == 0)
		{
			splitPane.setDividerLocation(getWidth() / 3);
		}
		else
		{
			splitPane.setDividerLocation(position[0]);
		}
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
