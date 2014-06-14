/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.watcher/WatcherTableController.java
 *
 *			Modified: 12-Jun-2014 (23:23:16)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.watcher;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.swing.event.TreeSelectionEvent;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.backup.watcher.IWatchListener;
import com.yagasoft.keepup.backup.watcher.State;
import com.yagasoft.keepup.ui.FileTable;
import com.yagasoft.keepup.ui.FileTableController;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;


/**
 * The Class WatcherTableController.
 */
public class WatcherTableController extends FileTableController implements IWatchListener
{
	
	/**
	 * Instantiates a new watcher table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 */
	public WatcherTableController(FileTable filesTable)
	{
		this(filesTable, null);
	}
	
	/**
	 * Instantiates a new watcher table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 * @param columnFunctions
	 *            Column functions.
	 */
	@SuppressWarnings("unchecked")
	public WatcherTableController(FileTable filesTable, Function<File<?>, Object>[] columnFunctions)
	{
		super(filesTable, columnFunctions);
		
		List<Function<File<?>, Object>> functions = new ArrayList<Function<File<?>, Object>>();
		functions.add(file -> file);
		functions.add(file -> file.getPath());
		functions.add(file -> App.humanReadableSize(file.getSize()));
		functions.add(file -> file);	// TODO represent the state
		this.columnFunctions = functions.toArray(new Function[functions.size()]);
	}
	
	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		throw new UnsupportedOperationException("This table doesn't have a tree!");
	}
	
	/**
	 * @see com.yagasoft.keepup.backup.watcher.IWatchListener#watchListChanged(com.yagasoft.overcast.base.container.Container,
	 *      com.yagasoft.keepup.backup.watcher.State)
	 */
	@Override
	public void watchListChanged(Container<?> container, State state)
	{
		// this will be removed in the future to support watching folders.
		if (container.isFolder())
		{
			return;
		}
		
		Set<File<?>> files = new HashSet<File<?>>();
		files.addAll(getAllFiles());
		
		switch (state)
		{
			case ADD:
				files.add((File<?>) container);
				break;
			
			case REMOVE_ALL:
			case REMOVE:
				files.remove(container);
				break;
			
			// TODO implement state changes visually
			case DELETE:
				break;
			
			case MODIFY:
				break;
			
			case SYNCED:
				break;
		}
		
		updateTable(new ArrayList<File<?>>(files));
	}
}
