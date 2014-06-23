/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.tree/CSTree.java
 *
 *			Modified: 20-Jun-2014 (22:58:07)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.tree;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.ui.browser.tree.FolderTree;
import com.yagasoft.keepup.ui.panels.SearchBar;
import com.yagasoft.keepup.ui.toolbars.FolderToolBar;


/**
 * The Class FoldersTree.
 */
public class CSTree extends FolderTree<CombinedFolder>
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -427791932709838315L;

	/** Folders tool bar. */
	private FolderToolBar		toolBarFolders;

	/** Search bar. */
	private SearchBar			searchBar;

	/**
	 * Instantiates a new folders tree.
	 *
	 * @param root
	 *            Root.
	 */
	public CSTree(DefaultMutableTreeNode root)
	{
		super(root);

		toolBarFolders = new FolderToolBar();
		add(toolBarFolders, BorderLayout.NORTH);

		searchBar = new SearchBar();
		add(searchBar, BorderLayout.SOUTH);
	}

	/**
	 * Gets the search text.
	 *
	 * @return the search text
	 */
	public String getSearchText()
	{
		return searchBar.getSearchText();
	}

	/**
	 * Adds a search action listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addSearchListener(ActionListener listener)
	{
		searchBar.addSearchListener(listener);
	}
}
