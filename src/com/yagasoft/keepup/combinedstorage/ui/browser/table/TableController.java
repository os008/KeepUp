/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.table/TableController.java
 *
 *			Modified: 07-May-2014 (15:53:10)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.table;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.remote.RemoteFile;


/**
 * The Class TableController.
 */
public class TableController implements Observer
{

	/** Controlled view. */
	protected FilesTable	view;

	/** Table. */
	protected JTable		table;

	/**
	 * Instantiates a new table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 */
	public TableController(FilesTable filesTable)
	{
		view = filesTable;
		table = filesTable.getTableFiles();
	}

	/**
	 * Update table with the files passed.
	 *
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(File<?>[] fileArray)
	{
		Arrays.sort(fileArray);

		Object[][] tableData = new Object[fileArray.length][3];

		for (int i = 0; i < fileArray.length; i++)
		{
			tableData[i][0] = fileArray[i];
			tableData[i][1] = App.humanReadableSize(fileArray[i].getSize());
			tableData[i][2] = fileArray[i].getCsp().getName();
		}

		view.updateTable(tableData);
	}

	/**
	 * Gets the selected files.
	 *
	 * @return the selected files
	 */
	public RemoteFile<?>[] getSelectedFiles()
	{
		// TODO refine 'getSelectedFiles'
		Object[] tempArray = view.getSelectedFiles();
		ArrayList<RemoteFile<?>> files = new ArrayList<RemoteFile<?>>();

		for (Object file : tempArray)
		{
			files.add((RemoteFile<?>) file);
		}

		return files.toArray(new RemoteFile<?>[tempArray.length]);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		// selected folder has changed, and so fetch its files and display them.
		if (arg != null)
		{
			updateTable(((CombinedFolder) arg).getFilesArray(false));
		}
	}

}
