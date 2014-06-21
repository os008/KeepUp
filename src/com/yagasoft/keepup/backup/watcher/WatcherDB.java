/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/WatcherDB.java
 *
 *			Modified: 16-Jun-2014 (00:10:25)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


import com.yagasoft.keepup.App;
import com.yagasoft.keepup.DB;
import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.scheduler.ISyncListener;
import com.yagasoft.overcast.base.container.Container;


/**
 * The Class WatcherDB.
 */
public class WatcherDB implements IWatchListener, ISyncListener
{

	/**
	 * Instantiates a new watcher db.
	 */
	public WatcherDB()
	{}

	// TODO create a 'remove obsoletes' method that accepts the backup root container.

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
						, new String[] { container.getPath(), App.formRemoteBackupParent(container) });
				// set the path status.
				DB.insertOrUpdate(DB.Table.backup_status, DB.backupStatusColumns
						, new String[] { container.getPath(), State.MODIFY.toString() });
				break;

			case SYNCED:
			case MODIFY:
			case DELETE:
				// set the path status.
				DB.updateRecord(DB.Table.backup_status, DB.backupStatusColumns
						, new String[] { container.getPath(), state.toString() });
				break;

			case REMOVE:
			case REMOVE_ALL:
				// delete all traces of this container from the DB.
				DB.deleteRecord(DB.Table.backup_revisions, DB.backupRevisionsColumns[0] + " = " + container.getPath());
				DB.deleteRecord(DB.Table.backup_status, DB.backupStatusColumns[0] + " = " + container.getPath());
				DB.deleteRecord(DB.Table.backup_path, DB.backupPathColumns[0] + " = " + container.getPath());
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
		DB.insertRecord(DB.Table.backup_revisions, new String[] { container.getPath(), revision });
	}
}
