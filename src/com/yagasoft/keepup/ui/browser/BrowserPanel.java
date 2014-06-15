/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/BrowserPanel.java
 *
 *			Modified: 27-May-2014 (17:40:27)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;


/**
 * The folders tree and files list GUI panels.
 */
public class BrowserPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 1173503486389973440L;
	
	/** Split pane. */
	protected JSplitPane		splitPane;
	
	/** Tree view. */
	protected JPanel			treeView;
	
	/** Table view. */
	protected JPanel			tableView;
	
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
	public BrowserPanel(JPanel treeView, JPanel tableView)
	{
		this.treeView = treeView;
		this.tableView = tableView;
		initGUI();
	}
	
	/**
	 * Initialises objects, and forms the split pane.
	 */
	protected void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		
		if ((treeView != null) && (tableView != null))
		{
			setTreeView(treeView);
			setTableView(tableView);
		}
		
		add(splitPane, BorderLayout.CENTER);
		
		resetDivider(150);
	}
	
	public void setTreeView(JPanel treeView)
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
	public void setTableView(JPanel tableView)
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
	
}
