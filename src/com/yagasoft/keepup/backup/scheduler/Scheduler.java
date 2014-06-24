/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.scheduler/Scheduler.java
 *
 *			Modified: 25-Jun-2014 (01:05:40)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.scheduler;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.backup.State;
import com.yagasoft.keepup.backup.watcher.IWatchListener;
import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.transfer.UploadJob;
import com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener;
import com.yagasoft.overcast.base.container.transfer.event.TransferEvent;
import com.yagasoft.overcast.exception.OperationException;


/**
 * The Class Scheduler.
 */
public class Scheduler implements IWatchListener, ITransferProgressListener
{
	
	/** Queue. */
	protected Queue<Container<?>>	queue		= new LinkedList<Container<?>>();
	
	/** Uploading. */
	protected List<Container<?>>	uploading	= new ArrayList<Container<?>>();
	
	/** Backup thread. */
	protected BackupThread			backupThread;
	
	/** Timer. */
	protected int					timer		= 60;
	
	/** Listeners. */
	protected Set<ISyncListener>	listeners	= new HashSet<ISyncListener>();
	
	/**
	 * Instantiates a new scheduler.
	 */
	public Scheduler()
	{
		// TODO load the tree of the folder below.
		// TODO remove obsoletes in db and server, and revise empty folders.
		// make sure the root folder for backup exists.
		App.createFolder(App.ROOT, "keepup_backup");
		
		backupThread = new BackupThread();
		startBackupLoop();
	}
	
	/**
	 * Start the backup loop.
	 */
	public void startBackupLoop()
	{
		Logger.info("KEEPUP: SCHEDULER: starting backup loop.");
		new Thread(backupThread).start();
	}
	
	/**
	 * Stop backup loop. Might be used when resetting backup system.
	 */
	public void stopBackupLoop()
	{
		backupThread.stop = true;
	}
	
	/**
	 * Starts uploading.
	 */
	public void startUploading()
	{
		stopTimer();
		
		Container<?> container = null;
		
		// go through the queue
		while ((container = queue.poll()) != null)
		{
			if ( !container.isFolder())
			{
				String parentPath = App.formRemoteBackupParent(container);
				
				// check if file already exists
				if (App.searchForFile(parentPath + "/" + App.calculateHashedName(container)).isEmpty())
				{
					App.createFolder(parentPath.substring(0, parentPath.lastIndexOf('/'))
							, parentPath.substring(parentPath.lastIndexOf('/') + 1, parentPath.length()));
					
					CombinedFolder parent = App.searchForFolder(parentPath);
					
					UploadJob<?, ?> uploadJob = App.uploadFile((LocalFile) container, parent, false);
					
					if (uploadJob != null)
					{
						uploadJob.addProgressListener(this);
						
						// move file to uploading list.
						uploading.add(container);
					}
				}
				else
				{	// container already backed-up.
					notifyListenersOfSync(container, App.calculateHashedName(container));
				}
			}
			
			// TODO implement folder upload
		}
		
		// emptied the queue, but nothing is uploading, reset timer and wait.
		if (uploading.isEmpty())
		{
			resetTimer(timer);
		}
	}
	
	/**
	 * Reset timer.
	 *
	 * @param seconds
	 *            Seconds.
	 */
	public void resetTimer(int seconds)
	{
		Logger.info("KEEPUP: SCHEDULER: timer reset to " + seconds);
		
		backupThread.remainingTime = seconds;
	}
	
	/**
	 * Stop timer.
	 */
	public void stopTimer()
	{
		backupThread.remainingTime = Integer.MAX_VALUE;
	}
	
	/**
	 * Notify listeners that a container has been sync'd.
	 *
	 * @param container
	 *            Container.
	 */
	protected void notifyListenersOfSync(Container<?> container, String revision)
	{
		Logger.info("KEEPUP: SCHEDULER: container sync'd: " + container.getPath());
		listeners.parallelStream()
				.forEach(listener -> listener.containerSynced(container, revision));
	}
	
	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addListener(ISyncListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeListener(ISyncListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Watch list changed.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            State.
	 * @see com.yagasoft.keepup.backup.watcher.IWatchListener#watchListChanged(com.yagasoft.overcast.base.container.Container,
	 *      com.yagasoft.keepup.backup.State)
	 */
	@Override
	public void watchListChanged(Container<?> container, State state)
	{
		switch (state)
		{
			case ADD:
			case MODIFY:
				// make sure the container isn't queued.
				if ( !queue.contains(container) && !uploading.contains(container))
				{
					Logger.info("KEEPUP: SCHEDULER: queueing " + container.getPath());
					queue.add(container);
				}
				break;
			
			case DELETE:
			case REMOVE:
			case REMOVE_ALL:
			case SYNCED:
				Logger.info("KEEPUP: SCHEDULER: dequeueing " + container.getPath());
				queue.remove(container);
				uploading.remove(container);
				break;
		}
	}
	
	/**
	 * @see com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener#transferProgressChanged(com.yagasoft.overcast.base.container.transfer.event.TransferEvent)
	 */
	@Override
	public void transferProgressChanged(TransferEvent event)
	{
		Container<?> container = event.getContainer();
		Container<?> remoteContainer = event.getDestination();
		
		switch (event.getState())
		{
			case INITIALISED:
			case IN_PROGRESS:
				// the container has been removed from the watch list, so cancel its upload.
				if ( !uploading.contains(container))
				{
					event.getJob().cancelTransfer();
				}
				
				break;
			
			case COMPLETED:
				Logger.info("KEEPUP: SCHEDULER: " + container + " has been backed-up");
				
				try
				{
					String newName = App.calculateHashedName(container);
					// rename to new revision name.
					remoteContainer.rename(newName);
					// remove if everything went fine.
					uploading.remove(container);
					
					// notify listeners of sync
					notifyListenersOfSync(container, newName);
				}
				catch (OperationException e)
				{
					Logger.error("KEEPUP: SCHEDULER: failed to rename file after upload");
					Logger.except(e);
					e.printStackTrace();
					
					try
					{
						// something went wrong, so delete the file.
						remoteContainer.delete();
					}
					catch (OperationException e1)
					{
						Logger.error("KEEPUP: SCHEDULER: failed to delete uploaded file after failed rename!");
						Logger.except(e1);
						e1.printStackTrace();
					}
					
					queue.add(container);
					uploading.remove(container);
					
					if (uploading.isEmpty())
					{
						// reset the timer to try again in 15 minutes.
						resetTimer(15 * 60);
						return;
					}
				}
				
				// upload queue is empty, reset timer and wait.
				if (uploading.isEmpty())
				{
					// reset the timer to try again in 15 minutes.
					resetTimer(timer);
				}
				
				// TODO notify listeners
				break;
			
			case FAILED:
			case CANCELLED:
				Logger.info("KEEPUP: SCHEDULER: " + container + " failed or cancelled its backup");
				
				// queue the file again
				queue.add(container);
				uploading.remove(container);
				
				if (uploading.isEmpty())
				{
					// reset the timer to try again in 15 minutes.
					resetTimer(15 * 60);
				}
		}
	}
	
	/**
	 * The Class BackupThread.
	 */
	private class BackupThread implements Runnable
	{
		
		/** Remaining time in seconds. */
		public int		remainingTime	= timer;
		public boolean	stop			= false;
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			// loop forever and start backup when timer runs out
			while ( !stop)
			{
				try
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				// slept for 10 seconds, so deduct them from remaining time
				// if queue is not empty only.
				if ( !queue.isEmpty())
				{
					remainingTime -= 10;
					Logger.info("KEEPUP: SCHEDULER: remaining secs => " + remainingTime);
				}
				
				// count down expired, start uploading what's in the queue.
				if (remainingTime <= 0)
				{
					Logger.info("KEEPUP: SCHEDULER: starting queue upload ...");
					startUploading();
				}
			}
		}
	}
}
