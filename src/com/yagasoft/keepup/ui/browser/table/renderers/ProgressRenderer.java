/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.renderers/ProgressRenderer.java
 *
 *			Modified: 23-Jun-2014 (16:30:01)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser.table.renderers;


import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * Defines how to handle the progress bar inside the table.
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -4106456853733016017L;

	/**
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean,
	 *      boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value
			, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Float v = (Float) value;

		// set the range.
		setMinimum(0);
		setMaximum(100);

		setIndeterminate(false);	// the progress is always known!
		setStringPainted(true);		// force display the percent info.
		setValue((int) (v.floatValue() * 100));

		return this;
	}
}
