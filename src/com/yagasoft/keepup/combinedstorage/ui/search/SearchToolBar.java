/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.search/SearchToolBar.java
 * 
 *			Modified: 23-Jun-2014 (16:42:29)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.search;


import com.yagasoft.keepup.ui.browser.table.FileTableController;
import com.yagasoft.keepup.ui.toolbars.FileToolBar;


/**
 * The Class SearchToolBar.
 */
public class SearchToolBar extends FileToolBar
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -4050551200751478324L;
	
	/**
	 * Instantiates a new search tool bar.
	 *
	 * @param tableController
	 *            Table controller.
	 */
	public SearchToolBar(FileTableController tableController)
	{
		super(tableController);
		remove(1);		// remove upload button
		remove(1);		// remove refresh button
	}
}
