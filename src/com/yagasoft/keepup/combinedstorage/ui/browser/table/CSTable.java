/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.table/CSTable.java
 *
 *			Modified: 27-May-2014 (20:21:22)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.table;


import java.awt.BorderLayout;

import javax.swing.JTable;

import com.yagasoft.keepup.combinedstorage.ui.actions.FileToolBar;
import com.yagasoft.keepup.ui.browser.FileTable;


public class CSTable extends FileTable
{
	
	private static final long	serialVersionUID	= -8729490450147401081L;
	
	private FileToolBar			toolBarFiles;
	
	public CSTable(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns)
	{
		super(columnNames, columnsWidthPercent, rightAlignedColumns);
		
		toolBarFiles = new FileToolBar();
		add(toolBarFiles, BorderLayout.NORTH);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================
	
	public FileToolBar getToolBarFiles()
	{
		return toolBarFiles;
	}
	
	public void setToolBarFiles(FileToolBar toolBarFiles)
	{
		this.toolBarFiles = toolBarFiles;
	}
	
	@Override
	public JTable getTable()
	{
		return tableFiles;
	}
	
	@Override
	public void setTableFiles(JTable tableFiles)
	{
		this.tableFiles = tableFiles;
	}
	
	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
}
