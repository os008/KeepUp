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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.swing.event.TreeSelectionEvent;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.DB;
import com.yagasoft.keepup.DB.Table;
import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.ui.recover.RecoverPanel;
import com.yagasoft.keepup.backup.watcher.IWatchListener;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.keepup.ui.browser.table.FileTableController;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;


/**
 * The Class WatcherTableController.
 */
public class WatcherTableController extends FileTableController implements IWatchListener, ActionListener
{

	/**
	 * Instantiates a new watcher table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 */
	public WatcherTableController(WatcherPanel filesTable)
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
	public WatcherTableController(WatcherPanel filesTable, List<Function<File<?>, Object>> columnFunctions)
	{
		super(filesTable, columnFunctions);
		// listen to recover button
		filesTable.addListener(this);
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
	 *      com.yagasoft.keepup.backup.State)
	 */
	@Override
	public void watchListChanged(Container<?> container, State state)
	{
		// TODO remove to support watching folders.
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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<File<?>> result = new ArrayList<File<?>>();
		File<?> file = getSelectedFiles().parallelStream().findFirst().orElse(null);

		if (file == null)
		{
			Msg.showError("No revisions available!");
			return;
		}

		String[][] dbResult = DB.getRecord(Table.backup_revisions
				, new String[] { "revision" }
				, "path = '" + file.getPath() + "'");

		String[][] remoteParent = DB.getRecord(Table.backup_path
				, new String[] { "remote" }
				, "path = '" + file.getPath() + "'");

		if (dbResult.length > 0)
		{
			CombinedFolder backupFolder = App.searchForFolder(remoteParent[0][0]);

			for (String[] revision : dbResult)
			{
				for (Container<?> container : backupFolder.findContainer(revision[0], false, false))
				{
					if ( !container.isFolder())
					{
						result.add((File<?>) container);
					}
				}
			}
		}

		RecoverPanel panel = new RecoverPanel(file, result);
		panel.setFrame(App.showSubWindow(panel, "Revisions of file " + file.getPath()));
	}
}
