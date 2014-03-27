/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 18-Mar-2014 (17:46:45)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.yagasoft.keepup.combinedstorage.MainWindow;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.CSP;
import com.yagasoft.overcast.container.File;
import com.yagasoft.overcast.container.Folder;
import com.yagasoft.overcast.container.local.LocalFile;
import com.yagasoft.overcast.container.local.LocalFolder;
import com.yagasoft.overcast.container.operation.IOperationListener;
import com.yagasoft.overcast.container.operation.OperationEvent;
import com.yagasoft.overcast.container.operation.OperationState;
import com.yagasoft.overcast.container.remote.RemoteFile;
import com.yagasoft.overcast.container.remote.RemoteFolder;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;
import com.yagasoft.overcast.google.Google;
import com.yagasoft.overcast.ubuntu.Ubuntu;


public final class App
{
	
	/** Enable debug-related logging throughout the program. */
	public static final boolean					DEBUG	= true;
	
	/** CSPs. */
	public static HashMap<String, CSP<?, ?, ?>>	csps;
	
	/** Main window. */
	public static MainWindow					mainWindow;
	
	private static String						lastDirectory;
	
	/**
	 * Prepare the app for usage.
	 * 
	 * @throws CSPBuildException
	 */
	public static void initApp() throws CSPBuildException
	{
		initCSPs();
		initGUI();
		mainWindow.getStatusBar().updateFreeSpace();
	}
	
	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 * 
	 * @throws CSPBuildException
	 */
	private static void initCSPs() throws CSPBuildException
	{
		csps = new HashMap<String, CSP<?, ?, ?>>();
		
		CSP<?, ?, ?> google = new Google();
		csps.put(google.getName(), google);
		
		CSP<?, ?, ?> ubuntu = new Ubuntu();
		csps.put(ubuntu.getName(), ubuntu);
		
		for (CSP<?, ?, ?> csp : csps.values())
		{
			csp.initTree();
			csp.buildFileTree(1);
		}
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
	public static void initTree()
	{
		// combined root folders list.
		ArrayList<Folder<?>> folders = new ArrayList<Folder<?>>();
		
		// get each CSP list if available.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			folders.addAll(csp.getRemoteFileTree().getFoldersList());
		}
		
		// sort it to appear as contiguous space.
		Collections.sort(folders);
		
		// sent it to the GUI tree to be displayed.
		mainWindow.getBrowserPanel().updateTree(folders.toArray(new Folder<?>[folders.size()]));
	}
	
	public static void refreshTree()
	{
		// get each CSP list if available.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			csp.getRemoteFileTree().buildTree(1);
		}
		
		initTree();
	}
	
	/**
	 * Combine the files in all the available CSPs into a single array.
	 * 
	 * @return the root files
	 */
	public static File<?>[] getRootFiles(boolean update)
	{
		// combined root folders list.
		ArrayList<File<?>> files = new ArrayList<File<?>>();
		
		// get each CSP list if available.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			if (update)
			{
				csp.getRemoteFileTree().updateFromSource(true, false);
			}
			
			files.addAll(csp.getRemoteFileTree().getFilesList());
		}
		
		// sort it to appear as contiguous space.
		Collections.sort(files);
		
		return files.toArray(new File<?>[files.size()]);
	}
	
	public static void createFolder(RemoteFolder<?> parent, String name)
	{
		// no folder, then choose best fitting to upload to its root.
		if (parent == null)
		{
			try
			{
				parent = bestFit(0).getRemoteFileTree();
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError(e.getMessage());
				return;
			}
		}
		
		RemoteFolder<?> folder = parent.getCsp().getAbstractFactory().createFolder();
		folder.setName(name);
		
		try
		{
			folder.create(parent, new IOperationListener()
			{
				
				@Override
				public void operationProgressChanged(OperationEvent event)
				{
					switch (event.getState())
					{
						case COMPLETED:
							refreshTree();
							break;
						case FAILED:
							Msg.showError("Failed to create: '" + event.getContainer().getPath() + "'.");
							break;
						default:
							break;
					}
				}
			});
		}
		catch (CreationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
		
//		switch (parent.getCsp().getName())
//		{
//			case "Google Drive":
//				((Google) parent.getCsp()).getFactory().crea
//				break;
//
//			case "Ubuntu One":
//				break;
//		}
		
	}
	
	/**
	 * Download selected files to the selected folder.
	 * 
	 * @param files
	 *            Files list.
	 */
	public static void downloadFile(RemoteFile<?>[] files)
	{
		// no files, then no need to proceed.
		if (files.length == 0)
		{
			Logger.post("Nothing to download!");
			Msg.showError("Please, choose a file first from the files list.");
			return;
		}
		
		// if a folder was chosen ...
		if (getLastDirectory() != null)
		{
			// ... prepare its object ...
			LocalFolder parent = new LocalFolder(getLastDirectory());
			parent.buildTree(false);
			
			// --------------------------------------------------------------------------------------
			// #region Make sure there's enough space.
			
			Long filesSize = 0L;
			
			for (RemoteFile<?> file : files)
			{
				filesSize += file.getSize();
			}
			
			// no space, then no need to proceed.
			if (parent.getLocalFreeSpace() <= filesSize)
			{
				Logger.post("Not enough space!");
				Msg.showError("Please, free some space on local disk.");
				return;
			}
			
			// #endregion Make sure there's enough space.
			// --------------------------------------------------------------------------------------
			
			// ... download all files sent to that folder.
			try
			{
				for (RemoteFile<?> file : files)
				{
					mainWindow.getQueuePanel().addTransferJob(
							file.getCsp().download(file, parent, true, mainWindow.getQueuePanel()), "Download");
				}
			}
			catch (TransferException | OperationException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Logger.post("No folder selected!");
			Msg.showError("Please, choose a folder first using the 'browse' button.");
		}
	}
	
	/**
	 * Upload selected file to the selected folder.
	 * 
	 * @param file
	 *            File.
	 * @param parent
	 *            Parent remote folder.
	 */
	public static void uploadFile(LocalFile[] files, RemoteFolder<?> parent)
	{
		// no file, then no need to proceed.
		if (files.length == 0)
		{
			Logger.post("Nothing to upload!");
			Msg.showError("Please, choose a file first.");
			return;
		}
		
		// no folder, then choose best fitting to upload to its root.
		if (parent == null)
		{
			try
			{
				parent = chooseCsp(files);
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError(e.getMessage());
				return;
			}
		}
		
		// --------------------------------------------------------------------------------------
		// #region Make sure there's enough space.
		
		long filesSize = 0L;
		
		for (LocalFile file : files)
		{
			filesSize += file.getSize();
		}
		
		// no space, then no need to proceed.
		try
		{
			if (parent.getCsp().calculateRemoteFreeSpace() <= filesSize)
			{
				Logger.post("Not enough space!");
				Msg.showError("Please, free some space on local disk.");
				return;
			}
		}
		catch (HeadlessException | OperationException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		// #endregion Make sure there's enough space.
		// --------------------------------------------------------------------------------------
		
		try
		{
			for (LocalFile file : files)
			{
				mainWindow.getQueuePanel().addTransferJob(
						parent.getCsp().upload(file, parent, true, mainWindow.getQueuePanel()), "Upload");
			}
		}
		catch (TransferException | OperationException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deleteFolder(RemoteFolder<?> folder)
	{
		try
		{
			folder.delete(new IOperationListener()
			{
				
				@Override
				public void operationProgressChanged(OperationEvent event)
				{
					switch (event.getState())
					{
						case COMPLETED:
							refreshTree();
							break;
						case FAILED:
							Msg.showError("Failed to delete: '" + event.getContainer().getPath() + "'.");
							break;
						default:
							break;
					}
				}
			});
		}
		catch (OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}
	
	/**
	 * Delete passed files.
	 * 
	 * @param files
	 *            the files.
	 */
	public static void deleteFiles(RemoteFile<?>[] files)
	{
		for (RemoteFile<?> file : files)
		{
			try
			{
				file.delete(new IOperationListener()
				{
					
					@Override
					public void operationProgressChanged(OperationEvent event)
					{
						if (event.getState() == OperationState.FAILED)
						{
							Msg.showError("Failed to delete file: " + event.getContainer().getName());
						}
					}
				});
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError(e.getMessage());
			}
		}
		
		mainWindow.getBrowserPanel().updateTable();
	}
	
	/**
	 * @return the lastDirectory
	 */
	public static String getLastDirectory()
	{
		return lastDirectory;
	}
	
	/**
	 * @param lastDirectory
	 *            the lastDirectory to set
	 */
	public static void setLastDirectory(String lastDirectory)
	{
		App.lastDirectory = lastDirectory;
		mainWindow.getBrowserPanel().getToolBarFiles().updateDestinationFolder();
	}
	
	/**
	 * Gets the CSP with the least space to fit the files passed.
	 * 
	 * @param files
	 * 
	 * @return The root of the best fit CSP.
	 */
	public static RemoteFolder<?> chooseCsp(LocalFile[] files) throws OperationException
	{
		long filesSize = 0L;
		
		for (LocalFile file : files)
		{
			filesSize += file.getSize();
		}
		
		// no space, then no need to proceed.
		CSP<?, ?, ?> bestFit = bestFit(filesSize);
		
		if (bestFit == null)
		{
			Logger.post("Not enough space!");
			Msg.showError("Please, free some space on any CSP.");
			return null;
		}
		else
		{
			return bestFit.getRemoteFileTree();
		}
	}
	
	/**
	 * Gets the % of the fit of the files inside the CSP's free space.
	 * 
	 * @param csp
	 *            CSP.
	 * @param filesSize
	 *            Files size.
	 * @return % of the fit.
	 * @throws OperationException
	 *             the operation exception when free space can't be obtained.
	 */
	private static float fit(CSP<?, ?, ?> csp, long filesSize) throws OperationException
	{
		return filesSize / (float) csp.calculateRemoteFreeSpace();
	}
	
	/**
	 * Gets the smallest fit for the files among all the CSPs.
	 * 
	 * @param filesSize
	 *            Files size.
	 * @return Best fit CSP.
	 * @throws OperationException
	 *             the operation exception when free space can't be obtained.
	 */
	private static CSP<?, ?, ?> bestFit(long filesSize) throws OperationException
	{
		HashMap<Float, CSP<?, ?, ?>> fits = new HashMap<Float, CSP<?, ?, ?>>();
		
		for (CSP<?, ?, ?> csp : csps.values())
		{
			fits.put(fit(csp, filesSize), csp);
		}
		
		Float[] fitsList = fits.keySet().toArray(new Float[fits.size()]);
		Arrays.sort(fitsList);
		
		for (int i = (fitsList.length - 1); i >= 0; i--)
		{
			if (fitsList[i] < 0.99)
			{
				return fits.get(fitsList[i]);
			}
		}
		
		return null;
	}
	
	/**
	 * Human readable size conversion.<br/>
	 * <br />
	 * Credit: 'aioobe' at 'StackOverFlow.com'
	 * 
	 * @param bytes
	 *            Size in bytes.
	 * @return Human readable size.
	 */
	public static String humanReadableSize(long bytes)
	{
		int unit = 1024;
		
		if (bytes < unit)
		{
			return bytes + " B";
		}
		
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = ("KMGTPE").charAt(exp - 1) + ("i");
		
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static CSP<?, ?, ?>[] getCspsArray()
	{
		return csps.values().toArray(new CSP<?, ?, ?>[csps.values().size()]);
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
