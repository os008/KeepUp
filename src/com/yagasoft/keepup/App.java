/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup/App.java
 *
 *			Modified: 05-May-2014 (16:03:40)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup;


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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.yagasoft.keepup.combinedstorage.CombinedFolder;
import com.yagasoft.keepup.combinedstorage.ui.BrowserPanel;
import com.yagasoft.keepup.combinedstorage.ui.MainWindow;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.operation.IOperationListener;
import com.yagasoft.overcast.base.container.operation.OperationEvent;
import com.yagasoft.overcast.base.container.operation.OperationState;
import com.yagasoft.overcast.base.container.remote.RemoteFile;
import com.yagasoft.overcast.base.container.remote.RemoteFolder;
import com.yagasoft.overcast.base.csp.CSP;
import com.yagasoft.overcast.exception.AuthorisationException;
import com.yagasoft.overcast.exception.CSPBuildException;
import com.yagasoft.overcast.exception.CreationException;
import com.yagasoft.overcast.exception.OperationException;
import com.yagasoft.overcast.exception.TransferException;
import com.yagasoft.overcast.implement.dropbox.Dropbox;
import com.yagasoft.overcast.implement.google.Google;


/**
 * This class contains the central control for the program. All other classes return to this one to ask for services.
 */
public final class App
{
	
	public static final String					VERSION		= "2.03.0065";
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Fields.
	// ======================================================================================
	
	/** Options file path. */
	private static Path							optionsFile	= Paths.get(System.getProperty("user.dir") + "/etc/options.ser");
	
	/** Options as a map. */
	private static HashMap<String, Object>		options		= null;
	
//	private static String						ubuntuUser;
//	private static String						ubuntuPass;
	
	/** CSPs list currently loaded. */
	public static HashMap<String, CSP<?, ?, ?>>	csps;
	
	/** Constant: Root of the tree. */
	public static final CombinedFolder			root		= new CombinedFolder(null);
	
	/** Main window. */
	public static MainWindow					mainWindow;
	
	/** Last directory used. */
	private static String						lastDirectory;
	
	/** Array of files in copied or moved to memory. */
	private static RemoteFile<?>[]				filesInHand;
	
	/**
	 * FileActions that can be performed on the one "in hand".
	 */
	enum FileActions
	{
		COPY,
		MOVE
	}
	
	/** File action to be performed on filesInHand. */
	private static FileActions	fileAction;
	
	// ======================================================================================
	// #endregion Fields.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Initialisation.
	// ======================================================================================
	
	/**
	 * Prepare the app for usage.
	 * 
	 * @throws CSPBuildException
	 */
	public static void initApp()
	{
		initGUI();
		loadOptions();
		initCSPs();
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
	}
	
	/**
	 * Create, authenticate, and initialise the tree of each CSP.
	 * 
	 * @throws CSPBuildException
	 */
	private static void initCSPs()
	{
		csps = new HashMap<String, CSP<?, ?, ?>>();
		
		HashSet<Thread> threads = new HashSet<Thread>();
		
		threads.add(new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					addCSP(Google.getInstance("os1983@gmail.com"));
				}
				catch (AuthorisationException | CSPBuildException e)
				{
					Msg.showError(e.getMessage());
					e.printStackTrace();
				}
			}
		}));
		
		threads.add(new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					addCSP(Dropbox.getInstance("os008@hotmail.com", 65234));
				}
				catch (AuthorisationException | CSPBuildException e)
				{
					Msg.showError(e.getMessage());
					e.printStackTrace();
				}
			}
		}));
		
		// start the CSP threads.
		for (Thread thread : threads)
		{
			thread.start();
		}
		
		// wait for the threads to finish before expanding the root -- caused problems with showing expansion icon of children
		for (Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		// show the root as expanded at the start.
		mainWindow.getBrowserPanel().getTreeFolders().expandPath(new TreePath(root.getNode().getPath()));
		
		// if ((ubuntuUser == null) || (ubuntuPass == null))
		// {
		// ubuntuUser = Msg.getInput("Please, enter Ubuntu One's username:");
		// ubuntuPass = Msg.getPassword("Please, enter Ubuntu One's password:");
		// }
		//
		// addCSP(new Ubuntu(ubuntuUser, ubuntuPass));
	}
	
	/**
	 * Initialises the folders tree's root by using the in-memory folder list.
	 */
	public static void initTree()
	{
		// get each CSP list if available.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			try
			{
				csp.buildFileTree(false);
			}
			catch (OperationException | CreationException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Load options from file.
	 */
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
				
//					case "ubuntuUser":
//						ubuntuUser = (String) options.get("ubuntuUser");
//						break;
//
//					case "ubuntuPass":
//						ubuntuPass = (String) options.get("ubuntuPass");
//						break;
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
	
	/**
	 * Save options to file.
	 */
	public static void saveOptions()
	{
		try
		{
			options.put("lastDirectory", lastDirectory);
//			options.put("ubuntuUser", ubuntuUser);
//			options.put("ubuntuPass", ubuntuPass);
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(optionsFile.toString()));
			out.writeObject(options);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// ======================================================================================
	// #endregion Initialisation.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Update free space, and display it.
	 */
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
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Tree operations.
	// ======================================================================================
	
	/**
	 * Refresh tree by going through the folders already loaded, and making sure they're up to date.
	 * It does NOT load the tree from scratch.
	 */
	public static void refreshTree()
	{
		mainWindow.getBrowserPanel().updateTree();
	}
	
	public static void addNodeToTree(DefaultMutableTreeNode childNode, DefaultMutableTreeNode node)
	{
		mainWindow.getBrowserPanel().addNodeToTree(childNode, node);
	}
	
	public static void removeNodeFromTree(DefaultMutableTreeNode childNode)
	{
		mainWindow.getBrowserPanel().removeNodeFromTree(childNode);
	}
	
	public static void updateNode(DefaultMutableTreeNode node)
	{
		mainWindow.getBrowserPanel().updateNodeName(node);
	}
	
	// ======================================================================================
	// #endregion Tree operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Folders operations.
	// ======================================================================================
	
	public static void createFolder(CombinedFolder parent, String name)
	{
		// no parent, then choose root.
		if (parent == null)
		{
			parent = root;
		}
		
		HashSet<RemoteFolder<?>> newFolders = new HashSet<RemoteFolder<?>>();
		
		// go over the csps list, and create a folder for each.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			try
			{
				// try to find the existing combinedfolder.
				CombinedFolder result = parent.findNode(name);
				
				// if it doesn't exist, or the csp folder isn't added ...
				if ((result == null) || !result.getCspFolders().containsKey(csp.getName()))
				{
					// create the csp folder.
					RemoteFolder<?> newFolder = csp.getAbstractFactory().createFolder();
					newFolders.add(newFolder);
					newFolder.setName(name);
				}
			}
			catch (CreationException e)
			{
				Msg.showError(e.getMessage());
				e.printStackTrace();
			}
		}
		
		final CombinedFolder threadedParent = parent;
		
		// create the folders that don't exist at their csp.
		for (final RemoteFolder<?> newFolder : newFolders)
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						// get the csp-related parent path from the combinedfolder, and create the new folder in it.
						newFolder.create(threadedParent.getPath(),
								new IOperationListener()
								{
									
									@SuppressWarnings("incomplete-switch")
									@Override
									public void operationProgressChanged(OperationEvent event)
									{
										switch (event.getState())
										{
											case COMPLETED:
												threadedParent.addChild(newFolder);		// success, add the new folder.
												break;
											
											case FAILED:
												Msg.showError("Failed to create: '" + event.getContainer().getPath() + "'.");
												break;
										}
									}
								});
					}
					catch (CreationException e)
					{
						Msg.showError(e.getMessage());
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public static void renameFolder(final CombinedFolder folder, final String newName)
	{
		for (final RemoteFolder<?> remoteFolder : folder.getCspFolders().values())
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						remoteFolder.rename(newName, new IOperationListener()
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
	
	public static void deleteFolder(CombinedFolder folder)
	{
		for (final RemoteFolder<?> remoteFolder : folder.getCspFolders().values())
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						remoteFolder.delete(new IOperationListener()
						{
							
							@SuppressWarnings("incomplete-switch")
							@Override
							public void operationProgressChanged(OperationEvent event)
							{
								switch (event.getState())
								{
									case FAILED:
										Msg.showError("Failed to delete: '" + event.getContainer().getPath() + "'.");
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
	}
	
	public static CombinedFolder getSelectedFolder()
	{
		return mainWindow.getBrowserPanel().getSelectedFolder();
	}
	
	// ======================================================================================
	// #endregion Folders operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Updates the selected folder. It grabs the files in the folder, and then passes them to
	 * {@link BrowserPanel#updateTable(File[])}.
	 */
	public static void updateTable()
	{
		CombinedFolder folder = getSelectedFolder();
		
		if (folder != null)
		{
			folder.updateCombinedFolder(true);
			mainWindow.getBrowserPanel().updateTable(folder.getFilesArray(false));
		}
		else
		{
			root.updateCombinedFolder(true);
			mainWindow.getBrowserPanel().updateTable(root.getFilesArray(false));
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files operations.
	// ======================================================================================
	
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
			Logger.error("Nothing to download!");
			Msg.showError("Please, choose a file first from the files list.");
			return;
		}
		
		try
		{
			// prepare folder object ...
			LocalFolder parent = new LocalFolder(getLastDirectory());
			// parent.buildTree(false);
			
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
				Logger.error("Not enough space!");
				Msg.showError("Please, free some space on local disk.");
				return;
			}
			
			// #endregion Make sure there's enough space.
			// --------------------------------------------------------------------------------------
			
			// ... download all files passed to that folder.
			for (RemoteFile<?> file : files)
			{
				if (parent.searchByName(file.getName(), false).length > 0)
				{
					if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") != 0)
					{
						continue;
					}
				}
				
				mainWindow.getQueuePanel().addTransferJob(
						file.getCsp().download(file, parent, true, mainWindow.getQueuePanel()), "Download");
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
	public static void uploadFile(LocalFile[] files, CombinedFolder parent)
	{
		// choose best fitting to upload to its root.
		if (parent == null)
		{
			parent = root;
		}
		
		// choose best fitting to upload to.
		RemoteFolder<?> parentRemoteFolder;
		CSP<?, ?, ?> csp;
		
		try
		{
			csp = chooseCsp(files);
			parentRemoteFolder = parent.getCspFolders().get(csp.getName());
		}
		catch (OperationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
			return;
		}
		
		try
		{
			// upload each file to the folder.
			for (LocalFile file : files)
			{
				// create the remote folder before uploading to it at the csp.
				if (parentRemoteFolder == null)
				{
					parentRemoteFolder = csp.getAbstractFactory().createFolder();
					parentRemoteFolder.setName(parent.getName());
					parentRemoteFolder.create(
							parent.getPath().substring(0, (parent.getPath().lastIndexOf("/" + parent.getName()))),
							new IOperationListener()
							{
								
								@Override
								public void operationProgressChanged(OperationEvent event)
								{}
							});
				}
				else
				{
					// make sure the file doesn't exist, and ask to overwrite if exists.
					if (parentRemoteFolder.searchByName(file.getName(), false).length > 0)
					{
						if (Msg.showQuestion("Overwrite: '" + file.getPath() + "'?") != 0)
						{
							continue;
						}
					}
				}
				
				// add file job to the gui queue.
				mainWindow.getQueuePanel().addTransferJob(
						parentRemoteFolder.getCsp().upload(file, parentRemoteFolder, true, mainWindow.getQueuePanel()), "Upload");
			}
		}
		catch (TransferException | CreationException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
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
	
	public static void pasteFiles(CombinedFolder folder)
	{
		for (RemoteFile<?> file : filesInHand)
		{
			RemoteFolder<?> remoteFolder = folder.getCspFolders().get(file.getCsp().getName());
			
			// create the remote folder before uploading to it at the csp.
			if (remoteFolder == null)
			{
				try
				{
					remoteFolder = file.getCsp().getAbstractFactory().createFolder();
					remoteFolder.setName(folder.getName());
					remoteFolder.create(
							folder.getPath().substring(0, (folder.getPath().lastIndexOf("/" + folder.getName()))),
							new IOperationListener()
							{
								
								@Override
								public void operationProgressChanged(OperationEvent event)
								{}
							});
				}
				catch (CreationException e)
				{
					e.printStackTrace();
					Msg.showError(e.getMessage());
					continue;
				}
				
			}
			
			// make sure the user wants to overwrite if necessary.
			if (remoteFolder.searchById(file.getId(), false) != null)
			{
				if ( !Msg.askConfirmation("Overwrite: " + file.getName() + " in " + folder.getPath()))
				{
					return;
				}
			}
			
			try
			{
				switch (fileAction)
				{
					case COPY:
						file.copy(remoteFolder, true, new IOperationListener()
						{
							
							@Override
							public void operationProgressChanged(OperationEvent event)
							{
								if (event.getState() == OperationState.COMPLETED)
								{
									updateTable();
								}
							}
						});
						break;
					
					case MOVE:
						file.move(remoteFolder, true, new IOperationListener()
						{
							
							@Override
							public void operationProgressChanged(OperationEvent event)
							{
								if (event.getState() == OperationState.COMPLETED)
								{
									updateTable();
								}
							}
						});
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
	}
	
	/**
	 * Combine the files in all the available CSPs into a single array.
	 * 
	 * @param update
	 *            update from soure, or read from memory
	 * @return the root files
	 */
	public static File<?>[] getFilesArray(CombinedFolder folder, boolean update)
	{
		return folder.getFilesArray(update);
	}
	
	public static RemoteFile<?>[] getSelectedFiles()
	{
		return mainWindow.getBrowserPanel().getSelectedFiles();
	}
	
	// ======================================================================================
	// #endregion Files operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////
	// #region CSP operations.
	// ======================================================================================
	
	/**
	 * Adds a csp, and initialise its tree.
	 * 
	 * @param csp
	 *            csp.
	 */
	public static void addCSP(final CSP<?, ?, ?> csp)
	{
		try
		{
			csp.initTree();
			csp.getRemoteFileTree().addContentListener(root);
			root.addCspFolder(csp.getRemoteFileTree());
			csps.put(csp.getName(), csp);
			updateFreeSpace();
			initTree();
		}
		catch (OperationException e)
		{
			e.printStackTrace();
		}
	}
	
	public static CSP<?, ?, ?>[] getCspsArray()
	{
		return csps.values().toArray(new CSP<?, ?, ?>[csps.values().size()]);
	}
	
	/**
	 * Gets the CSP with the least space to fit the files passed.
	 * 
	 * @param files
	 *            Files.
	 * @return The best fit CSP.
	 * @throws OperationException
	 *             the operation exception
	 */
	public static CSP<?, ?, ?> chooseCsp(LocalFile[] files) throws OperationException
	{
		long filesSize = 0L;
		
		// add up all the files' sizes.
		for (LocalFile file : files)
		{
			filesSize += file.getSize();
		}
		
		// get best fitting csp for the files at hand.
		CSP<?, ?, ?> bestFit = bestFit(filesSize);
		
		// if no fit, then not enough space on any csp.
		if (bestFit == null)
		{
			Logger.error("Not enough space!");
			Msg.showError("Please, free some space on any CSP.");
			return null;
		}
		else
		{
			return bestFit;
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
		// get current free space, and divide the file size by it to get percentage of fit.
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
		
		// store each fit value for each csp in the map.
		for (CSP<?, ?, ?> csp : csps.values())
		{
			fits.put(fit(csp, filesSize), csp);
		}
		
		// sort the fits.
		Float[] fitsList = fits.keySet().toArray(new Float[fits.size()]);
		Arrays.sort(fitsList);
		
		// choose the highest fit.
		for (int i = (fitsList.length - 1); i >= 0; i--)
		{
			// 100% fit might cause unpredictable approximation errors.
			if (fitsList[i] < 0.95)
			{
				return fits.get(fitsList[i]);
			}
		}
		
		return null;
	}
	
	// ======================================================================================
	// #endregion CSP operations.
	// //////////////////////////////////////////////////////////////////////////////////////
	
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
}
