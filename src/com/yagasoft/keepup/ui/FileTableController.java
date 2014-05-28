/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/FileTableController.java
 *
 *			Modified: 27-May-2014 (20:54:47)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui;


import java.util.Arrays;
import java.util.Observer;
import java.util.function.Function;

import javax.swing.JTable;


/**
 * The Class TableController.
 * The update function of the observer should fetch the files of the selected folder sent to the function's 'arg' parameter.
 * Convert 'arg' to the type of the folders being monitored.
 */
public abstract class FileTableController<FileType> implements Observer
{

	/** Controlled view including the panel. */
	protected FileTable						view;

	/** Table itself. */
	protected JTable						table;

	protected int							columnsCount;

	protected Function<FileType, Object>[]	columnFunctions;

	/**
	 * Instantiates a new table controller.
	 * The functionalities should use each file to produce something to store in the relevent cell under the column.
	 * As you can't create a generic array, so add the functions to a list, and then convert them to array using toArray.
	 *
	 * For example:
	 * <code>
	 * 		functions.add(file -> file);		// stores the file object itself in the first column.
	 * 		functions.add(file -> App.humanReadableSize(file.getSize()));	// stores the size of the file.
	 * 		functions.add(file -> file.getCsp().getName());		// stores the CSP.
	 * </code>
	 *
	 * @param filesTable
	 *            Files table.
	 * @param columnFunctions
	 *            Column functions.
	 */
	public FileTableController(FileTable filesTable, Function<FileType, Object> columnFunctions[])
	{
		view = filesTable;
		table = filesTable.getTable();
		columnsCount = view.getColumnNames().length;
		this.columnFunctions = columnFunctions;
	}

	/**
	 * Update table with the files passed.
	 *
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(FileType[] fileArray)
	{
		Arrays.sort(fileArray);

		Object[][] tableData = new Object[fileArray.length][columnsCount];

		for (int i = 0; i < fileArray.length; i++)
		{
			for (int j = 0; j < columnsCount; j++)
			{
				tableData[i][j] = columnFunctions[j].apply(fileArray[i]);
			}
		}

		view.updateTable(tableData);
	}

	/**
	 * Gets the selected files from the view.
	 * You can use view.getSelectedFiles(), then convert the type.
	 *
	 * @return the selected files
	 */
	public abstract FileType[] getSelectedFiles();

}
