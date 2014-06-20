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

package com.yagasoft.keepup.ui.browser;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionListener;

import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;


/**
 * The Class TableController.
 * The update function of the observer should fetch the files of the selected folder sent to the function's 'arg' parameter.
 * Convert 'arg' to the type of the folders being monitored.
 * This object should register as an observer at the tree view.
 */
public abstract class FileTableController implements TreeSelectionListener
{

	/** Controlled view including the panel. */
	protected FileTable						view;

	/** Table itself. */
	protected JTable						table;

	protected int							columnsCount;

	protected List<Function<File<?>, Object>>	columnFunctions;

	/**
	 * Instantiates a new table controller.
	 * The functionalities should use each file to produce something to store in the relevent cell under the column.
	 * As you can't create a generic array, so add the functions to a list, and then convert them to array using toArray.
	 *
	 * For example: <code>
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
	public FileTableController(FileTable filesTable, List<Function<File<?>, Object>> columnFunctions)
	{
		view = filesTable;
		table = filesTable.getTable();
		columnsCount = view.getColumnNames().length;
		this.columnFunctions = columnFunctions;
	}

	/**
	 * Call {@link #updateTable(List, Comparator)} with the path comparator.
	 *
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(List<? extends File<?>> files)
	{
		updateTable(files, Container.getPathComparator());
	}

	/**
	 * Update table with the files passed.
	 *
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(List<? extends File<?>> files, Comparator<Container<?>> comparator)
	{
		Collections.sort(files, comparator);

		Object[][] tableData = new Object[files.size()][columnsCount];

		for (int i = 0; i < files.size(); i++)
		{
			for (int j = 0; j < columnsCount; j++)
			{
				tableData[i][j] = columnFunctions.get(j).apply(files.get(i));
			}
		}

		view.updateTable(tableData);
	}

	/**
	 * Gets the selected files from the view.
	 *
	 * @return the selected files
	 */
	public List<File<?>> getSelectedFiles()
	{
		return view.getSelectedFiles().parallelStream()
				.map(file -> (File<?>) file)
				.collect(Collectors.toList());
	}

	/**
	 * Gets all the files from the view.
	 *
	 * @return all files
	 */
	public List<File<?>> getAllFiles()
	{
		return view.getAllFiles().parallelStream()
				.map(file -> (File<?>) file)
				.collect(Collectors.toList());
	}

}
