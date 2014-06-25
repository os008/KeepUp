/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/Transfer.java
 *
 *			Modified: 25-Jun-2014 (02:52:41)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.container.transfer.TransferJob;
import com.yagasoft.overcast.base.container.transfer.UploadJob;
import com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;




/**
 * The Class Transfer.
 */
public final class Transfer
{

	/**
	 * Download file.
	 *
	 * @param file File.
	 * @param destination Destination.
	 */
	public static void downloadFile(File<?> file, LocalFolder... destination)
	{
		Transfer.downloadFiles(Collections.singletonList(file), null, destination);
	}

	/**
	 * Download file.
	 *
	 * @param file File.
	 * @param listener Listener.
	 * @param destination Destination.
	 */
	public static void downloadFile(File<?> file, ITransferProgressListener listener, LocalFolder... destination)
	{
		Transfer.downloadFiles(Collections.singletonList(file), listener, destination);
	}

	/**
	 * Download files.
	 *
	 * @param files Files.
	 * @param destination Destination.
	 */
	public static void downloadFiles(List<File<?>> files, LocalFolder... destination)
	{
		Transfer.downloadFiles(files, null, destination);
	}

	/**
	 * Download selected files to the selected folder.
	 *
	 * @param files            Files list.
	 * @param listener Listener.
	 * @param destination Destination.
	 */
	public static void downloadFiles(List<File<?>> files, ITransferProgressListener listener, LocalFolder... destination)
	{
		// no files, then no need to proceed.
		if (files.isEmpty())
		{
			Logger.error("KEEPUP: DOWNLOAD: nothing!");
			Msg.showError("Please, choose a file first from the files list.");
			return;
		}

		if (files.get(0).isLocal())
		{
			return;
		}

		try
		{
			// prepare folder object ...
			LocalFolder parent = (destination.length > 0) ? destination[0] : new LocalFolder(App.getLastDirectory());

			// --------------------------------------------------------------------------------------
			// #region Make sure there's enough space.

			Long filesSize = 0L;

			for (File<?> file : files)
			{
				filesSize += file.getSize();
			}

			// no space, then no need to proceed.
			if (parent.getLocalFreeSpace() <= filesSize)
			{
				Logger.error("KEEPUP: DOWNLOAD: not enough space!");
				Msg.showError("Please, free some space on local disk.");
				return;
			}

			// #endregion Make sure there's enough space.
			// --------------------------------------------------------------------------------------

			// ... download all files passed to that folder.
			for (RemoteFile<?> file : Util.convertListToRemote(files))
			{
				if ( !parent.searchByName(file.getName(), false, false).isEmpty())
				{
					if ( !Msg.askQuestion("Overwrite: '" + file.getPath() + "'?"))
					{
						continue;
					}
				}

				TransferJob<?> job = file.getCsp().download(file, parent, true, App.mainWindow.getQueuePanel());
				App.mainWindow.getQueuePanel().addTransferJob(job, "Download");

				if (listener != null)
				{
					job.addProgressListener(listener);
				}
			}
		}
		catch (TransferException | OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}

	/**
	 * Upload selected file to the selected folder.
	 *
	 * @param file
	 *            File.
	 * @param parent
	 *            Parent remote folder.
	 * @param overwrite
	 *            Overwrite? If nothing passed, then the user will be asked if exists.
	 * @return Upload job
	 */
	public static UploadJob<?, ?> uploadFile(LocalFile file, CombinedFolder parent, boolean... overwrite)
	{
		List<LocalFile> fileList = new ArrayList<LocalFile>();
		fileList.add(file);

		List<UploadJob<?, ?>> uploadJobs = Transfer.uploadFiles(fileList, parent, overwrite);

		if ((uploadJobs != null) && !uploadJobs.isEmpty())
		{
			return uploadJobs.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Upload selected files to the selected folder.
	 *
	 * @param files
	 *            Files.
	 * @param parent
	 *            Parent remote folder.
	 * @param overwrite
	 *            Overwrite? If nothing passed, then the user will be asked if exists.
	 * @return List
	 */
	public static List<UploadJob<?, ?>> uploadFiles(List<LocalFile> files, CombinedFolder parent, boolean... overwrite)
	{
		// choose best fitting to upload to its root.
		if (parent == null)
		{
			parent = App.ROOT;
		}

		List<UploadJob<?, ?>> uploadJobs = new ArrayList<UploadJob<?, ?>>();

		// choose best fitting to upload to.
		RemoteFolder<?> parentRemoteFolder;
		CSP<?, ?, ?> csp;

		try
		{
			if ((csp = App.chooseCsp(files)) == null)
			{
				return null;
			}

			parentRemoteFolder = parent.getCspFolders().get(csp.getName());
		}
		catch (OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
			return uploadJobs;
		}

		try
		{
			// upload each file to the folder.
			outerLoop:
			for (LocalFile file : files)
			{
				// check if file exists on any of the CSPs
				List<Container<?>> existingContainers = parent.findContainer(file.getName(), false, false);

				// found something
				if ( !existingContainers.isEmpty())
				{
					// check each container returned
					for (Container<?> container : existingContainers)
					{
						// if it's a file and has the same name
						if ( !container.isFolder() && (container.getName() == file.getName()))
						{
							// ask to overwrite
							if (((overwrite.length <= 0) && !Msg.askQuestion("Overwrite: '" + file.getPath() + "'?"))
									|| ((overwrite.length > 0) && !overwrite[0]))
							{
								continue outerLoop;		// skip file.
							}
							else
							{	// overwrite
								container.delete();
							}
						}
					}
				}

				// create the remote folder before uploading to it at the csp.
				if (parentRemoteFolder == null)
				{
					parentRemoteFolder = csp.getAbstractFactory().createFolder();
					parentRemoteFolder.setName(parent.getName());
					parentRemoteFolder.create(
							parent.getPath().substring(0, (parent.getPath().lastIndexOf("/" + parent.getName())))
							, event -> {});
				}

				UploadJob<?, ?> uploadJob = parentRemoteFolder.getCsp()
						.upload(file, parentRemoteFolder, true, App.mainWindow.getQueuePanel());

				// add file job to the gui queue.
				App.mainWindow.getQueuePanel().addTransferJob(uploadJob, "Upload");

				uploadJobs.add(uploadJob);
			}
		}
		catch (TransferException | CreationException | OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}

		return uploadJobs;
	}

	/**
	 * Singleton!
	 */
	private Transfer()
	{}

}




