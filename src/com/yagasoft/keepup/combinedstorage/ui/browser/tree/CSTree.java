/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.tree/CSTree.java
 *
 *			Modified: 27-May-2014 (17:44:43)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.tree;


import java.awt.BorderLayout;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.actions.FolderToolBar;
import com.yagasoft.keepup.ui.FolderTree;


/**
 * The Class FoldersTree.
 */
public class CSTree extends FolderTree<CombinedFolder>
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -427791932709838315L;
	
	/** Folders tool bar. */
	private FolderToolBar		toolBarFolders;
	
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
	}
	
}
