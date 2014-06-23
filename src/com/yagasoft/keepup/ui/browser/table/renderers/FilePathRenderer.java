/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.browser/FilePathRenderer.java
 *
 *			Modified: 20-Jun-2014 (20:08:46)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser.table.renderers;


import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.yagasoft.overcast.base.container.File;


/**
 * Renders the file in a table as a path, not its name (toString).
 */
public class FilePathRenderer extends DefaultTableCellRenderer
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -3822550887588091526L;

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
	 *      boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value
			, boolean isSelected, boolean hasFocus, int row, int col)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		setValue(((File<?>) value).getPath());
		return this;
	}

}
