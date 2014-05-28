/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.browser.table/CSTableController.java
 *
 *			Modified: 27-May-2014 (22:21:06)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.browser.table;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.function.Function;

import javax.swing.JTable;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.ui.FileTable;
import com.yagasoft.keepup.ui.FileTableController;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.remote.RemoteFile;


/**
 * The Class TableController.
 */
public class CSTableController extends FileTableController<RemoteFile<?>>
{

	/** Controlled view. */
	protected CSTable	view;

	/** Table. */
	protected JTable	table;

	/**
	 * @param filesTable
	 * @param columnFunctions
	 */
	public CSTableController(FileTable filesTable)
	{
		this(filesTable, null);
	}

	/**
	 * Instantiates a new CS table controller.
	 *
	 * @param filesTable Files table.
	 * @param columnFunctions Column functions.
	 *
	 * @see FileTableController#FileTableController(FileTable, Function[])
	 */
	@SuppressWarnings("unchecked")
	public CSTableController(FileTable filesTable, Function<RemoteFile<?>, Object>[] columnFunctions)
	{
		super(filesTable, columnFunctions);

		List<Function<File<?>, Object>> functions = new ArrayList<Function<File<?>, Object>>();
		functions.add(file -> file);
		functions.add(file -> App.humanReadableSize(file.getSize()));
		functions.add(file -> file.getCsp());
		this.columnFunctions = functions.toArray(new Function[functions.size()]);
	}

	/**
	 * @see com.yagasoft.keepup.ui.FileTableController#getSelectedFiles()
	 */
	@Override
	public RemoteFile<?>[] getSelectedFiles()
	{
		return Arrays.stream(view.getSelectedFiles())
				.map(file -> (RemoteFile<?>) file)
				.toArray(size -> new RemoteFile<?>[size]);
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
