/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.browser/LocalTable.java
 * 
 *			Modified: 12-Jun-2014 (23:22:27)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.browser;


import com.yagasoft.keepup.ui.FileTable;


/**
 * The Class LocalTable.
 */
public class LocalTable extends FileTable
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 4011038973966364033L;
	
	/**
	 * Instantiates a new local table.
	 *
	 * @param columnNames
	 *            Column names.
	 * @param columnsWidthPercent
	 *            Columns width percent.
	 * @param rightAlignedColumns
	 *            Right aligned columns.
	 */
	public LocalTable(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns)
	{
		super(columnNames, columnsWidthPercent, rightAlignedColumns);
	}
	
}
