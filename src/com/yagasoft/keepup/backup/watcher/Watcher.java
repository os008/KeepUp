/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/Watcher.java
 *
 *			Modified: 12-Jun-2014 (23:23:30)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yagasoft.keepup.backup.ui.IAddRemoveListener;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;


/**
 * The Class Watcher.
 */
public class Watcher implements IAddRemoveListener
{

	/** Watched folders. */
	protected Map<String, LocalFolder>	watchedFolders	= new HashMap<String, LocalFolder>();

	/** Watched files. */
	protected Map<String, LocalFile>	watchedFiles	= new HashMap<String, LocalFile>();

	/** Listeners. */
	protected Set<IWatchListener>		listeners		= new HashSet<IWatchListener>();

	/**
	 * Adds the container.
	 *
	 * @param container
	 *            Container.
	 */
	public void addContainer(Container<?> container)
	{
		Container<?> oldContainer = null;		// if already existing will be saved here.

		// add to watch list.
		if (container.isFolder())
		{
			oldContainer = watchedFolders.put(container.getPath(), (LocalFolder) container);
		}
		else
		{
			oldContainer = watchedFiles.put(container.getPath(), (LocalFile) container);
		}

		// notify listeners if the container is new.
		if (oldContainer == null)
		{
			notifyListeners(container, Change.ADD);
		}
	}

	/**
	 * Removes the container.
	 *
	 * @param container
	 *            Container.
	 */
	public void removeContainer(Container<?> container)
	{
		Container<?> oldContainer = null;		// if already existing will be saved here.

		// remove from watch list.
		if (container.isFolder())
		{
			oldContainer = watchedFolders.remove(container.getPath());
		}
		else
		{
			oldContainer = watchedFiles.remove(container.getPath());
		}

		// notify listeners if the container existed.
		if (oldContainer != null)
		{
			notifyListeners(container, Change.REMOVE);
		}
	}

	/**
	 * Notify listeners.
	 *
	 * @param container
	 *            Container.
	 * @param change
	 *            Change.
	 */
	protected void notifyListeners(Container<?> container, Change change)
	{
		listeners.parallelStream()
				.forEach(listener -> listener.watchListChanged(container, change));
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addListener(IWatchListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeListener(IWatchListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * @see com.yagasoft.keepup.backup.ui.IAddRemoveListener#containersAddedRemoved(java.util.List,
	 *      com.yagasoft.keepup.backup.watcher.Change)
	 */
	@Override
	public void containersAddedRemoved(List<? extends Container<?>> containers, Change change)
	{
		containers.stream()
				.forEach(container ->
				{
					switch (change)
					{
						case ADD:
							addContainer(container);
							break;

						case REMOVE_ALL:
						case REMOVE:
							removeContainer(container);
							break;
					}
				});
	}
}
