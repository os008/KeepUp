/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.search/SearchTableController.java
 * 
 *			Modified: 23-Jun-2014 (16:42:54)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.search;


import java.util.List;
import java.util.function.Function;

import javax.swing.event.TreeSelectionEvent;

import com.yagasoft.keepup.ui.browser.table.FileTable;
import com.yagasoft.keepup.ui.browser.table.FileTableController;
import com.yagasoft.overcast.base.container.File;


/**
 * The Class SearchTableController.
 */
public class SearchTableController extends FileTableController
{
	
	/**
	 * Instantiates a new search table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 * @param columnFunctions
	 *            Column functions.
	 */
	public SearchTableController(FileTable filesTable, List<Function<File<?>, Object>> columnFunctions)
	{
		super(filesTable, columnFunctions);
	}
	
	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{}
	
}
