/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.watcher/WatcherPanel.java
 *
 *			Modified: 24-Jun-2014 (19:09:27)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.watcher;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.ui.browser.table.FileTable;


/**
 * The Class WatcherPanel.
 */
public class WatcherPanel extends FileTable
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 5398517240339678162L;

	/** Button recover. */
	private JButton				buttonRecover;

	/**
	 * @param columnNames
	 * @param columnsWidthPercent
	 * @param rightAlignedColumns
	 * @param renderers
	 */
	public WatcherPanel(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns,
			Map<Class<?>, TableCellRenderer> renderers)
	{
		super(columnNames, columnsWidthPercent, rightAlignedColumns, renderers);
		initGUI();
	}

	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		//
		buttonRecover = new JButton("Recover");
		add(buttonRecover, BorderLayout.SOUTH);
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addListener(ActionListener listener)
	{
		buttonRecover.addActionListener(listener);
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeListener(ActionListener listener)
	{
		buttonRecover.removeActionListener(listener);
	}

}
