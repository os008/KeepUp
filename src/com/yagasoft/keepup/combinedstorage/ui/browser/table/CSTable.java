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


import java.util.Map;

import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.ui.browser.table.FileTable;


public class CSTable extends FileTable
{
	
	private static final long	serialVersionUID	= -8729490450147401081L;
	
	public CSTable(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns
			, Map<Class<?>, TableCellRenderer> renderers)
	{
		super(columnNames, columnsWidthPercent, rightAlignedColumns, renderers);
	}
}
