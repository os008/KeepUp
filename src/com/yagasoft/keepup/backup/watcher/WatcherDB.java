/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/WatcherDB.java
 *
 *			Modified: 24-Jun-2014 (16:52:18)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


import java.util.List;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.DB;
import com.yagasoft.keepup.DB.Table;
import com.yagasoft.keepup.Util;
import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.scheduler.ISyncListener;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.exception.AccessException;


/**
 * The Class WatcherDB.
 */
public class WatcherDB implements IWatchListener, ISyncListener
{
	
	protected Watcher	watcher;
	
	/**
	 * Instantiates a new watcher db.
	 */
	public WatcherDB(Watcher watcher)
	{
		this.watcher = watcher;
		
		syncRevisions();
		initWatcherFromDB();
		watcher.addListener(this);
	}
	
	/**
	 * Reads the file states from the DB and passes them to the watcher.
	 */
	private void initWatcherFromDB()
	{
		// get all paths stored in DB
		String[][] paths = DB.getRecord(Table.backup_status, DB.backupStatusColumns);
		
		// go through them and add them to the watcher
		for (String[] path : paths)
		{
			LocalFile file = new LocalFile(path[0]);
			
			// add them initially as ADD to inform the GUI to add them to table
			watcher.setContainerState(file, State.ADD);
			
			try
			{
				// check existence and set to 'DELETE' if they don't exist locally.
				if (file.isExist())
				{
					watcher.setContainerState(file, State.MODIFY);
				}
				else
				{
					watcher.setContainerState(file, State.DELETE);
				}
			}
			catch (AccessException e)
			{
				e.printStackTrace();
				watcher.setContainerState(file, State.DELETE);
			}
		}
	}
	
	/**
	 * Removes the obsolete revisions from the server and DB.
	 */
	private void syncRevisions()
	{
		// get all paths from the DB, local file and remove parent
		String[][] paths = DB.getRecord(Table.backup_path, DB.backupPathColumns);
		
		for (String[] path : paths)
		{
			DB.deleteRecord(Table.backup_revisions, "path = '" + path[0] + "'");
			
			// get the current revision name
			String hashedName = Util.getMD5(path[0]);
			
			// search for parent on server
			CombinedFolder folder = App.searchForFolder(path[1]);
			
			// if found
			if (folder != null)
			{
				// get all the revisions related to this file in the parent
				List<Container<?>> result = folder.findContainer(hashedName, true, false);
				
				// if something was found
				if ( !result.isEmpty())
				{
					// add them as a revision for this file to the DB.
					for (Container<?> container : result)
					{
						DB.insertOrUpdate(DB.Table.backup_revisions, DB.backupRevisionsColumns
								, new String[] { path[0], container.getName()
										, container.getName().replace(hashedName, "") }
								, new int[] { 0, 1 });
					}
				}
			}
		}
	}
	
	/**
	 * @see com.yagasoft.keepup.backup.watcher.IWatchListener#watchListChanged(com.yagasoft.overcast.base.container.Container,
	 *      com.yagasoft.keepup.backup.State)
	 */
	@Override
	public void watchListChanged(Container<?> container, State state)
	{
		switch (state)
		{
			case ADD:
				// make sure the path is in the database
				DB.insertOrUpdate(DB.Table.backup_path, DB.backupPathColumns
						, new String[] { container.getPath(), Util.formRemoteBackupParent(container) }
						, new int[] { 0 });
				// set the path status.
				DB.insertOrUpdate(DB.Table.backup_status, DB.backupStatusColumns
						, new String[] { container.getPath(), State.MODIFY.toString() }
						, new int[] { 0 });
				break;
			
			case SYNCED:
			case MODIFY:
			case DELETE:
				// set the path status.
				DB.insertOrUpdate(DB.Table.backup_status, DB.backupStatusColumns
						, new String[] { container.getPath(), state.toString() }
						, new int[] { 0 });
				break;
			
			case REMOVE:
			case REMOVE_ALL:
				// delete all traces of this container from the DB.
				DB.deleteRecord(DB.Table.backup_revisions, DB.backupRevisionsColumns[0] + " = '" + container.getPath() + "'");
				DB.deleteRecord(DB.Table.backup_status, DB.backupStatusColumns[0] + " = '" + container.getPath() + "'");
				DB.deleteRecord(DB.Table.backup_path, DB.backupPathColumns[0] + " = '" + container.getPath() + "'");
				break;
		}
	}
	
	/**
	 * @see com.yagasoft.keepup.backup.scheduler.ISyncListener#containerSynced(com.yagasoft.overcast.base.container.Container,
	 *      java.lang.String)
	 */
	@Override
	public void containerSynced(Container<?> container, String revision)
	{
		// add the new revision to the DB.
		DB.insertOrUpdate(DB.Table.backup_revisions, DB.backupRevisionsColumns
				, new String[] { container.getPath(), revision, container.getDate() + "" }
				, new int[] { 0, 1 });
	}
}
