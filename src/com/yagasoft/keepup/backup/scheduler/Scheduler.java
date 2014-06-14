/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.scheduler/Scheduler.java
 *
 *			Modified: 13-Jun-2014 (23:18:03)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.scheduler;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.backup.watcher.IWatchListener;
import com.yagasoft.keepup.backup.watcher.State;
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
	protected int					timer		= 20;

	/**
	 * Instantiates a new scheduler.
	 */
	public Scheduler()
	{
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
		Logger.info("starting backup loop.");

		new Thread(backupThread).start();
	}

	/**
	 * Starts uploading.
	 */
	public void startUploading()
	{
		Logger.info("Starting backup upload.");

		stopTimer();

		Container<?> container = null;

		// go through the queue
		while ((container = queue.poll()) != null)
		{
			if ( !container.isFolder())
			{
				// form the path to the parent, using a prefix, and removing ':' and '\' because they are windows based and
				// unsupported.
				String parentPath = "/keepup_backup/" + container.getParent().getPath().replace(":", "").replace("\\", "/");

				// check if file already existsF
				if (App.searchForFile(parentPath + "/" + calculateNewName(container)).isEmpty())
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
			}

			// TODO implement folder upload
		}

		// emptied the queue, but nothing is uploading, reset timer and wait.
		if (uploading.isEmpty())
		{
			resetTimer(timer);
		}
	}

	private String calculateNewName(Container<?> container)
	{
		try
		{
			// refresh container's meta data (not done in real time)
			container.updateFromSource();
		}
		catch (OperationException e)
		{
			Logger.except(e);
			e.printStackTrace();
		}

		// prepare the path hash to be the start of the filename on the server
		String pathHash = App.getMD5(container.getPath());
		// form the new filename as the path hash plus the modified date, this will keep revisions of the file.
		return pathHash + container.getDate();
	}

	/**
	 * Reset timer.
	 *
	 * @param seconds
	 *            Seconds.
	 */
	public void resetTimer(int seconds)
	{
		Logger.info("Timer reset to " + seconds);

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
	 * Watch list changed.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            State.
	 * @see com.yagasoft.keepup.backup.watcher.IWatchListener#watchListChanged(com.yagasoft.overcast.base.container.Container,
	 *      com.yagasoft.keepup.backup.watcher.State)
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
					Logger.info("queueing " + container.getPath());

					queue.add(container);
				}
				break;

			case DELETE:
			case REMOVE:
			case REMOVE_ALL:
			case SYNCED:
				Logger.info("dequeueing " + container.getPath());

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
				break;

			case IN_PROGRESS:
				break;

			case COMPLETED:
				Logger.info(container + " has been backed-up");

				try
				{
					System.out.println(calculateNewName(container));
					System.out.println(container.getPath());
					System.out.println(remoteContainer.getPath());
					// rename to new revision name.
					remoteContainer.rename(calculateNewName(container));
					// remove if everything went fine.
					uploading.remove(container);
				}
				catch (OperationException e)
				{
					Logger.except(e);
					e.printStackTrace();

					try
					{
						// something went wrong, so delete the file.
						remoteContainer.delete();
					}
					catch (OperationException e1)
					{
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
				Logger.info(container + " failed or cancelled its backup");

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
		public int	remainingTime	= timer;

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			// loop forever and start backup when timer runs out
			while (true)
			{
				System.out.println("looping ... " + remainingTime);

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
				}

				// count down expired, start uploading what's in the queue.
				if (remainingTime <= 0)
				{
					System.out.println("timeout! " + remainingTime);
					startUploading();
				}
			}
		}
	}
}
