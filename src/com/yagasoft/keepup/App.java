/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 12-Mar-2014 (21:24:59)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;

import com.yagasoft.keepup.combinedstorage.MainWindow;
import com.yagasoft.overcast.container.File;
import com.yagasoft.overcast.container.Folder;
import com.yagasoft.overcast.container.local.LocalFolder;
import com.yagasoft.overcast.container.remote.RemoteFile;
import com.yagasoft.overcast.exception.TransferException;
import com.yagasoft.overcast.google.Google;
import com.yagasoft.overcast.ubuntu.Ubuntu;


public abstract class App
{

	/** Enable debug-related logging throughout the program. */
	public static final boolean	DEBUG	= true;

	/** Google. */
	public static Google		google;

	/** Ubuntu. */
	public static Ubuntu		ubuntu;

	/** Main window. */
	public static MainWindow	mainWindow;

	private static String		lastDirectory;

	/**
	 * Prepare the app for usage.
	 */
	public static void initApp()
	{
		initCSPs();
		initGUI();
	}

	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 */
	private static void initCSPs()
	{
//		google = new Google();
//		google.initTree();
//		google.buildFileTree(1);

		ubuntu = new Ubuntu();
		ubuntu.initTree();
		ubuntu.buildFileTree(1);
	}

	/**
	 * Constructs the main window of the combined storage app, and sets it to be displayed.
	 */
	private static void initGUI()
	{
		mainWindow = new MainWindow();
		mainWindow.getBrowserPanel().resetDivider();

//		Task task = new Task();
//		task.addPropertyChangeListener(mainWindow.getQueuePanel());
//		task.execute();

//		new Thread(new Runnable()
//		{
//
//			@Override
//			public void run()
//			{
//
		// try
		// {
		// LocalFolder parent = new LocalFolder("G:\\Downloads");
		// parent.buildTree(false);
		//
		// mainWindow.getQueuePanel().addTransferJob(
		// ubuntu.download((RemoteFile<?>) ubuntu.getRemoteFileTree().getFilesArray()[0]
		// , parent, true, mainWindow.getQueuePanel(), null));
		//
		// // mainWindow.getQueuePanel().addTransferJob(
		// // ubuntu.download((RemoteFile) ubuntu.getRemoteFileTree().getFilesArray()[1]
		// // , parent, true, mainWindow.getQueuePanel(), null));
		// }
		// catch (TransferException e)
		// {
		// e.printStackTrace();
		// }
//
//			}
//		}).start();
	}

	/**
	 * Rebuild the folders tree.
	 */
	public static void refreshTree()
	{
		// combined root folders list.
		ArrayList<Folder<?>> folders = new ArrayList<Folder<?>>();

		// get Google's list if available.
		if (google != null)
		{
			folders.addAll(google.getRemoteFileTree().getFoldersList());
		}

		// get Ubuntu's list if available.
		if (ubuntu != null)
		{
			folders.addAll(ubuntu.getRemoteFileTree().getFoldersList());
		}

		// sort it to appear as contiguous space.
		Collections.sort(folders);

		// sent it to the GUI tree to be displayed.
		mainWindow.getBrowserPanel().updateTree(folders.toArray(new Folder<?>[folders.size()]));
	}

	/**
	 * Combine the files in all the available CSPs into a single array.
	 *
	 * @return the root files
	 */
	public static File<?>[] getRootFiles()
	{
		// combined root folders list.
		ArrayList<File<?>> files = new ArrayList<File<?>>();

		// get Google's list if available.
		if (google != null)
		{
			files.addAll(google.getRemoteFileTree().getFilesList());
		}

		// get Ubuntu's list if available.
		if (ubuntu != null)
		{
			files.addAll(ubuntu.getRemoteFileTree().getFilesList());
		}

		// sort it to appear as contiguous space.
		Collections.sort(files);

		return files.toArray(new File<?>[files.size()]);
	}

	/**
	 * Download file to selected folder.
	 *
	 * @param files
	 *            Files list.
	 */
	public static void downloadFile(RemoteFile<?>[] files)
	{
		// no files, then no need to proceed.
		if (files.length == 0)
		{
			return;
		}
		
		// only choose directories.
		JFileChooser chooser = new JFileChooser(lastDirectory);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = chooser.showOpenDialog(mainWindow);
		java.io.File selectedFolder = chooser.getSelectedFile();
		
		// if a folder was chosen ...
		if ((result == JFileChooser.APPROVE_OPTION) && selectedFolder != null)
		{
			lastDirectory = selectedFolder.getAbsolutePath();

			// ... prepare its object ...
			LocalFolder parent = new LocalFolder(lastDirectory);
			parent.buildTree(false);

			// ... download all files sent to that folder.
			try
			{
				for (RemoteFile<?> file : files)
				{
					mainWindow.getQueuePanel().addTransferJob(
							ubuntu.download(file, parent, true, mainWindow.getQueuePanel(), null), parent);
				}
			}
			catch (TransferException e)
			{
				e.printStackTrace();
			}
		}
	}
}

//class Task extends SwingWorker<Void, Void> implements ITransferProgressListener
//{
//
//	/*
//	 * Main task. Executed in background thread.
//	 */
//	@Override
//	public Void doInBackground()
//	{
//		// Initialize progress property.
//		setProgress(0);
//
//		try
//		{
//			App.mainWindow.getQueuePanel().addTransferJob(
//					App.ubuntu.download((RemoteFile) App.ubuntu.getRemoteFileTree().getFilesArray()[1], new LocalFolder(
//							"G:\\Downloads"), false, this, null));
//		}
//		catch (TransferException e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public void progressChanged(TransferEvent event)
//	{
//		setProgress((int) (event.getProgress() * 100));
//	}
//}
