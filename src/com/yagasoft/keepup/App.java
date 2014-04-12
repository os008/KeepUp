/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 * 
 *			Modified: 13-Apr-2014 (00:00:38)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.yagasoft.keepup.combinedstorage.MainWindow;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.Folder;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.operation.IOperationListener;
import com.yagasoft.overcast.base.container.operation.OperationEvent;
import com.yagasoft.overcast.base.container.operation.OperationState;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;
import com.yagasoft.overcast.implement.ubuntu.Ubuntu;


public final class App
{
	
	enum FileActions
	{
		COPY,
		MOVE
	}
	
	/** Enable debug-related logging throughout the program. */
	public static final boolean					DEBUG		= true;
	
	private static Path							optionsFile	= Paths.get(System.getProperty("user.dir") + "/bin/options.ser");
	private static HashMap<String, Object>		options		= null;
	private static String						ubuntuUser;
	private static String						ubuntuPass;
	
	/** CSPs. */
	public static HashMap<String, CSP<?, ?, ?>>	csps;
	
	/** Main window. */
	public static MainWindow					mainWindow;
	
	private static String						lastDirectory;
	
	private static RemoteFile<?>[]				filesInHand;
	
	private static FileActions					fileAction;
	
	/**
	 * Prepare the app for usage.
	 * 
	 * @throws CSPBuildException
	 */
	public static void initApp() throws CSPBuildException
	{
		initGUI();
		loadOptions();
		initCSPs();
	}
	
	@SuppressWarnings("unchecked")
	public static void loadOptions()
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(optionsFile.toString()));
			options = (HashMap<String, Object>) in.readObject();
			in.close();
			
			for (String option : options.keySet())
			{
				switch (option)
				{
					case "lastDirectory":
						String directory = (String) options.get("lastDirectory");
						
						if (Files.notExists(Paths.get(directory)))
						{
							setLastDirectory(System.getProperty("user.home"));
						}
						else
						{
							setLastDirectory(directory);
						}
						
						break;
					
					case "ubuntuUser":
						ubuntuUser = (String) options.get("ubuntuUser");
						break;
					
					case "ubuntuPass":
						ubuntuPass = (String) options.get("ubuntuPass");
						break;
				}
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			options = new HashMap<String, Object>();
			setLastDirectory(System.getProperty("user.home"));
		}
	}
	
	public static void saveOptions()
	{
		try
		{
			options.put("lastDirectory", lastDirectory);
			options.put("ubuntuUser", ubuntuUser);
			options.put("ubuntuPass", ubuntuPass);
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(optionsFile.toString()));
			out.writeObject(options);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 * 
	 * @throws CSPBuildException
	 */
	private static void initCSPs() throws CSPBuildException
	{
		csps = new HashMap<String, CSP<?, ?, ?>>();
		
//		addCSP(new Google());
		
		if ((ubuntuUser == null) || (ubuntuPass == null))
		{
			ubuntuUser = Msg.getInput("Please, enter Ubuntu One's username:");
			ubuntuPass = Msg.getPassword("Please, enter Ubuntu One's password:");
		}
		
		addCSP(new Ubuntu(ubuntuUser, ubuntuPass));
	}
	
	public static void addCSP(final CSP<?, ?, ?> csp)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					csp.initTree();
					csp.buildFileTree(1);
					csps.put(csp.getName(), csp);
					updateFreeSpace();
					initTree();
				}
				catch (OperationException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void updateFreeSpace()
	{
		HashMap<String, Long> freeSpace = new HashMap<String, Long>();
		
		for (CSP<?, ?, ?> csp : App.getCspsArray())
		{
			try
			{
				freeSpace.put(csp.getName(), new Long(csp.calculateRemoteFreeSpace()));
			}
			catch (OperationException e)
			{
				e.printStackTrace();
			}
		}
		
		mainWindow.getStatusBar().updateFreeSpace(freeSpace);
	}
	
	/**
	 * Constructs the main window of the combined storage app, and sets it to be displayed.
	 */
	private static void initGUI()
	{
		mainWindow = new MainWindow();
		mainWindow.getBrowserPanel().resetDivider();
		
		mainWindow.getFrame().addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveOptions();
			}
		});
		
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
		for (final CSP<?, ?, ?> csp : csps.values())
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						csp.getRemoteFileTree().buildTree(1);
						initTree();
					}
					catch (OperationException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	/**
	 * Combine the files in all the available CSPs into a single array.
	 * 
	 * @param update
	 *            update from soure, or read from memory
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
				try
				{
					csp.getRemoteFileTree().updateFromSource(true, false);
				}
				catch (OperationException e)
				{
					e.printStackTrace();
				}
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
		
		final RemoteFolder<?> folder = parent.getCsp().getAbstractFactory().createFolder();
		folder.setName(name);
		
		final RemoteFolder<?> threadedParent = parent;
		
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					folder.create(threadedParent, new IOperationListener()
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
			}
		}).start();
		
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
		
		try
		{
			// prepare folder object ...
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
			
			// ... download all files passed to that folder.
			for (RemoteFile<?> file : files)
			{
				boolean overwrite = false;
				
				if (parent.searchByName(file.getName(), false) != null)
				{
					if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") == 0)
					{
						overwrite = true;
					}
				}
				
				mainWindow.getQueuePanel().addTransferJob(
						file.getCsp().download(file, parent, overwrite, mainWindow.getQueuePanel()), "Download");
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
				boolean overwrite = false;
				
				if (parent.searchByName(file.getName(), false) != null)
				{
					if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") == 0)
					{
						overwrite = true;
					}
				}
				
				mainWindow.getQueuePanel().addTransferJob(
						parent.getCsp().upload(file, parent, overwrite, mainWindow.getQueuePanel()), "Upload");
			}
		}
		catch (TransferException | OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}
	
	public static void deleteFolder(final RemoteFolder<?> folder)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
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
		}).start();
	}
	
	/**
	 * Delete passed files.
	 * 
	 * @param files
	 *            the files.
	 */
	public static void deleteFiles(RemoteFile<?>[] files)
	{
		for (final RemoteFile<?> file : files)
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
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
						
						mainWindow.getBrowserPanel().updateTable();
					}
					catch (OperationException e)
					{
						e.printStackTrace();
						Msg.showError(e.getMessage());
					}
				}
			}).start();
		}
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
		mainWindow.getBrowserPanel().updateDestinationFolder(lastDirectory);
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
	
	public static RemoteFile<?>[] getSelectedFiles()
	{
		return mainWindow.getBrowserPanel().getSelectedFiles();
	}
	
	public static void renameFolder(final RemoteFolder<?> folder, final String newName)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					folder.rename(newName, new IOperationListener()
					{
						
						@Override
						public void operationProgressChanged(OperationEvent event)
						{
							if (event.getState() == OperationState.FAILED)
							{
								Msg.showError("Failed to rename folder: " + event.getContainer().getName());
							}
						}
					});
					
					refreshTree();
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
				}
			}
		}).start();
	}
	
	public static void renameFile(final RemoteFile<?>[] files, final String newName)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					files[0].rename(newName, new IOperationListener()
					{
						
						@Override
						public void operationProgressChanged(OperationEvent event)
						{
							if (event.getState() == OperationState.FAILED)
							{
								Msg.showError("Failed to rename file: " + event.getContainer().getName());
							}
						}
					});
					
					updateTable();
				}
				catch (OperationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
				}
			}
		}).start();
	}
	
	public static void updateTable()
	{
		mainWindow.getBrowserPanel().updateTable();
	}
	
	public static RemoteFolder<?> getSelectedFolder()
	{
		return mainWindow.getBrowserPanel().getSelectedFolder();
	}
	
	public static void copyFiles(RemoteFile<?>[] files)
	{
		filesInHand = files;
		fileAction = FileActions.COPY;
	}
	
	public static void moveFiles(RemoteFile<?>[] files)
	{
		filesInHand = files;
		fileAction = FileActions.MOVE;
	}
	
	public static void pasteFiles(RemoteFolder<?> folder)
	{
		for (RemoteFile<?> file : filesInHand)
		{
			boolean overwrite = false;
			
			if (folder.searchByName(file, false) != null)
			{
				if (Msg.showQuestion("Overwrite: " + file.getName() + " in " + folder.getPath()) == 0)
				{
					overwrite = true;
				}
			}
			
			try
			{
				switch (fileAction)
				{
					case COPY:
						file.copy(folder, overwrite, new IOperationListener()
						{
							
							@Override
							public void operationProgressChanged(OperationEvent event)
							{}
						});
						break;
					
					case MOVE:
						file.move(folder, overwrite, new IOperationListener()
						{
							
							@Override
							public void operationProgressChanged(OperationEvent event)
							{}
						});
						break;
					
					default:
						break;
				
				}
			}
			catch (OperationException e)
			{
				e.printStackTrace();
				Msg.showError(e.getMessage());
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
