/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/Watcher.java
 *
 *			Modified: 16-Jun-2014 (00:05:59)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.scheduler.ISyncListener;
import com.yagasoft.keepup.backup.ui.IAddRemoveListener;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;


/**
 * The Class Watcher.
 */
public class Watcher implements IAddRemoveListener, ISyncListener
{

	/** Watched folders. */
	protected Map<LocalFolder, State>	watchedFolders	= new HashMap<LocalFolder, State>();

	/** Watched files. */
	protected Map<LocalFile, State>		watchedFiles	= new HashMap<LocalFile, State>();

	/** Watched paths and the files inside being watched. This is used for the watcher itself. */
	protected Map<Path, WatchKey>		watchedPaths	= new HashMap<Path, WatchKey>();

	/** Listeners. */
	protected Set<IWatchListener>		listeners		= new HashSet<IWatchListener>();

	/** Watcher service responsible for watching the folder. */
	protected WatchService				watcher;

	/** Key! */
	protected WatchKey					watckKey;

	/**
	 * Instantiates a new watcher.
	 */
	public Watcher()
	{
		// TODO load all the watched files from the DB and set their status.
		// add the file, then check its current revision (calculate)
		// , if it exists on the server, then it's 'sync'd', if not, then it's modified.
		startWatchLoop();
	}

	/**
	 * Sets the container state.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            State.
	 */
	private void setContainerState(Container<?> container, State state)
	{
		if (state == State.ADD)
		{
			Path path = Paths.get(container.getParent().getPath());

			// register the path of the parent to be watched if it wasn't
			if ( !watchedPaths.containsKey(path))
			{
				try
				{
					// add the parent to the watched paths after registering
					watchedPaths.put(path, path.register(watcher
							, StandardWatchEventKinds.ENTRY_CREATE
							, StandardWatchEventKinds.ENTRY_DELETE
							, StandardWatchEventKinds.ENTRY_MODIFY));
				}
				catch (IOException e)
				{
					Logger.error("KEEPUP: WATCHER: failed to register for folder: " + path);
					Logger.except(e);
					e.printStackTrace();
				}
			}
		}

		if (state == State.REMOVE)
		{
			removeContainer(container, state);
			return;
		}

		State oldState = null;		// if already existing will be saved here.

		// add to watch list, it will replace if already exists and returns old state.
		if (container.isFolder())
		{
			oldState = watchedFolders.put((LocalFolder) container, state);
		}
		else
		{
			oldState = watchedFiles.put((LocalFile) container, state);
		}

		// notify listeners if the container's state changed or the container was modified.
		if ((oldState != state) || (state == State.MODIFY))
		{
			notifyListeners(container, state);
		}
	}

	/**
	 * Removes the container.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            State.
	 */
	private void removeContainer(Container<?> container, State state)
	{
		State oldState = null;		// if already existing will be saved here.

		// remove from watch list.
		if (container.isFolder())
		{
			oldState = watchedFolders.remove(container);
		}
		else
		{
			oldState = watchedFiles.remove(container);
		}

		// notify listeners if the container existed.
		if (oldState != null)
		{
			Path path = Paths.get(container.getParent().getPath());

			// unregister the path of the parent from being watched if no more files are watched inside
			boolean keepKey = false;

			// check if any file has this parent
			for (LocalFile file : watchedFiles.keySet())
			{
				if (watchedPaths.containsKey(Paths.get(file.getParent().getPath())))
				{
					keepKey = true;
					break;
				}
			}

			// cancel the watch key and remove the path from the watch list
			if ( !keepKey)
			{
				watchedPaths.get(path).cancel();
				watchedPaths.remove(path);
			}

			notifyListeners(container, state);
		}
	}

	/**
	 * Notify listeners.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            Change.
	 */
	protected void notifyListeners(Container<?> container, State state)
	{
		Logger.info("KEEPUP: WATCHER: container CHANGED state: " + state + " => " + container.getPath());
		listeners.parallelStream()
				.forEach(listener -> listener.watchListChanged(container, state));
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
	 * Containers added removed.
	 *
	 * @param containers
	 *            Containers.
	 * @param state
	 *            State.
	 * @see com.yagasoft.keepup.backup.ui.IAddRemoveListener#containersAddedRemoved(java.util.List,
	 *      com.yagasoft.keepup.backup.State)
	 */
	@Override
	public void containersAddedRemoved(List<? extends Container<?>> containers, State state)
	{
		containers.stream()
				.forEach(container ->
				{
					switch (state)
					{
						case ADD:
						case MODIFY:
						case DELETE:
						case SYNCED:
							setContainerState(container, state);
							break;

						// TODO delete all revisions and delete empty folder.
						case REMOVE_ALL:
						case REMOVE:
							removeContainer(container, state);
							break;
					}
				});
	}

	/**
	 * @see com.yagasoft.keepup.backup.scheduler.ISyncListener#containerSynced(com.yagasoft.overcast.base.container.Container,
	 *      java.lang.String)
	 */
	@Override
	public void containerSynced(Container<?> container, String revision)
	{
		setContainerState(container, State.SYNCED);
	}

	/**
	 * Start watch loop.
	 */
	public void startWatchLoop()
	{
		try
		{
			Logger.info("KEEPUP: WATCHER: starting file watcher loop ...");
			watcher = FileSystems.getDefault().newWatchService();		// create a watch service.
			new Thread(new WatchThread()).start();
		}
		catch (IOException e)
		{
			Logger.error("KEEPUP: WATCHER: failed to get watch service");
			Logger.except(e);
			e.printStackTrace();
		}
	}

	public State getContainerState(Container<?> container)
	{
		if (container.isFolder())
		{
			return watchedFolders.getOrDefault(container, State.REMOVE);
		}
		else
		{
			return watchedFiles.getOrDefault(container, State.REMOVE);
		}
	}

	/**
	 * The Class WatchThread.
	 */
	private class WatchThread implements Runnable
	{

		/** Events. */
		List<WatchEvent<?>>	events;

		/**
		 * @see java.lang.Runnable#run()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					Thread.sleep(1000);		// cool it for a second!

					watckKey = watcher.take();			// this will pause the loop until the system notifies of a file-change.
					events = watckKey.pollEvents();		// what are the changes?

					// go through all the changes.
					eventsLoop:
					for (WatchEvent<?> event : events)
					{
						System.out.println("WATCHER: " + event.kind() + " event! " + ((Path) watckKey.watchable())
								.resolve(((WatchEvent<Path>) event).context()));

						// don't need these events
						if (event.kind() == StandardWatchEventKinds.OVERFLOW)
						{
							continue;
						}

						// get the file path of the file changed.
						Path path = ((Path) watckKey.watchable())
								.resolve(((WatchEvent<Path>) event).context());
						State state = null;

						Logger.info("KEEPUP: WATCHER: " + event.kind() + " event! " + path);

						// if the file was created or modified then set state as modified
						// when it's created, it just means it returned from a 'deleted' state
						if ((event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
							|| (event.kind() == StandardWatchEventKinds.ENTRY_CREATE))
						{
							state = State.MODIFY;
						}
						// if the file was deleted then set state
						else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
						{
							state = State.DELETE;
						}

						for (LocalFile file : watchedFiles.keySet())
						{
							if (Paths.get(file.getPath()).equals(path) && (state != null))
							{
								setContainerState(file, state);
								break eventsLoop;
							}
						}
					}

					// if there's a problem with the watcher, exit.
//					if ( !watckKey.reset())
//					{
//						Msg.showError("There's a problem with the watcher loop.\nPlease, restart the program.");
//						break;
//					}
				}
				catch (InterruptedException e)
				{
					Logger.error("KEEPUP: WATCHER: loop interrupted.");
					Logger.except(e);
					e.printStackTrace();
				}
			}
		}

	}
}
