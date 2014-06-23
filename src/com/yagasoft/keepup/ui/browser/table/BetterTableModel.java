/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/BetterTableModel.java
 *
 *			Modified: 20-Jun-2014 (20:08:58)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser.table;


import java.util.Vector;

import javax.swing.table.DefaultTableModel;


/**
 * An extension of {@link DefaultTableModel} , which is used to overcome the limitation of treating all cells as {@link String} to
 * include columns that display components rather than text.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BetterTableModel extends DefaultTableModel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 3302631877543414017L;
	
	/**
	 * Instantiates a new better table model.
	 */
	public BetterTableModel()
	{
		super();
	}
	
	/**
	 * Instantiates a new better table model.
	 *
	 * @param rowCount
	 *            Row count.
	 * @param columnCount
	 *            Column count.
	 */
	public BetterTableModel(int rowCount, int columnCount)
	{
		super(rowCount, columnCount);
	}
	
	/**
	 * Instantiates a new better table model.
	 *
	 * @param columnNames
	 *            Column names.
	 * @param rowCount
	 *            Row count.
	 */
	public BetterTableModel(Object[] columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}
	
	/**
	 * Instantiates a new better table model.
	 *
	 * @param data
	 *            Data.
	 * @param columnNames
	 *            Column names.
	 */
	public BetterTableModel(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);
	}
	
	/**
	 * Instantiates a new better table model.
	 *
	 * @param columnNames
	 *            Column names.
	 * @param rowCount
	 *            Row count.
	 */
	public BetterTableModel(Vector columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}
	
	/**
	 * Instantiates a new better table model.
	 *
	 * @param data
	 *            Data.
	 * @param columnNames
	 *            Column names.
	 */
	public BetterTableModel(Vector data, Vector columnNames)
	{
		super(data, columnNames);
	}
	
	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}
}
